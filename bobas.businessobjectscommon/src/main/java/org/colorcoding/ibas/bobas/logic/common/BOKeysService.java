package org.colorcoding.ibas.bobas.logic.common;

import org.colorcoding.ibas.bobas.bo.BusinessObject;
import org.colorcoding.ibas.bobas.bo.IBOCustomKey;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.Numbers;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.FieldedObject;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.IKeyValue;
import org.colorcoding.ibas.bobas.data.KeyValue;
import org.colorcoding.ibas.bobas.data.List;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.db.DbField;
import org.colorcoding.ibas.bobas.db.DbFieldType;
import org.colorcoding.ibas.bobas.db.DbTransaction;
import org.colorcoding.ibas.bobas.db.IDbTableLock;
import org.colorcoding.ibas.bobas.db.MaxValue;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.logic.BusinessLogic;
import org.colorcoding.ibas.bobas.logic.BusinessLogicException;
import org.colorcoding.ibas.bobas.logic.LogicContract;

/**
 * 业务对象主键服务
 *
 */
@LogicContract(IBOKeysContract.class)
public class BOKeysService extends BusinessLogic<IBOKeysContract, BONumbering> {

	@Override
	protected boolean checkDataStatus(Object data) {
		if (data instanceof IBusinessObject && data == this.getHost()) {
			IBusinessObject boData = (IBusinessObject) data;
			if (boData.isSavable() == false) {
				return false;
			}
			if (boData.isNew() == false) {
				return false;
			}
			if (data instanceof IBOCustomKey) {
				// 自定义键，不执行业务逻辑
				return false;
			}
		}
		return true;
	}

	@Override
	protected BONumbering fetchBeAffected(IBOKeysContract contract) {
		try {
			// 获取主编号
			ICriteria criteria = new Criteria();
			ICondition condition = criteria.getConditions().create();
			condition.setAlias(BONumbering.PROPERTY_OBJECTCODE.getName());
			condition.setValue(contract.getObjectCode());
			BONumbering numbering = this.fetchBeAffected(BONumbering.class, criteria);
			if (numbering == null) {
				BONumbering[] numberings = this.getTransaction().fetch(BONumbering.class, criteria);
				if (numberings == null || numberings.length == 0) {
					throw new BusinessLogicException(I18N.prop("not found [%s] keys.", contract.getObjectCode()));
				}
				numbering = numberings[0];
			}
			// 获取系列编号
			if (contract.getSeries() > 0) {
				if (numbering.getSeriesNumberings()
						.contains(c -> Numbers.equals(c.getSeries(), contract.getSeries()))) {
					// 不包含此系列，则从数据库中查询
					criteria = new Criteria();
					condition = criteria.getConditions().create();
					condition.setAlias(BOSeriesNumbering.PROPERTY_OBJECTCODE.getName());
					condition.setValue(contract.getObjectCode());
					condition = criteria.getConditions().create();
					condition.setAlias(BOSeriesNumbering.PROPERTY_SERIES.getName());
					condition.setValue(contract.getSeries());
					condition.setAlias(BOSeriesNumbering.PROPERTY_LOCKED.getName());
					condition.setOperation(ConditionOperation.NOT_EQUAL);
					condition.setValue(emYesNo.YES);
					BOSeriesNumbering[] numberings = this.getTransaction().fetch(BOSeriesNumbering.class, criteria);
					if (numberings == null || numberings.length == 0) {
						throw new BusinessLogicException(I18N.prop("not found [%s]'s series [%s].",
								contract.getObjectCode(), contract.getSeries()));
					}
					numbering.getSeriesNumberings().addAll(numberings);
				}
			}
			// 获取最大值（包含LineId）
			if (contract.getMaxValueField() != null) {
				if (numbering.getMaxValueNumbering().contains(c -> c.getKey().equals(contract.getMaxValueKey()))) {
					if (this.getTransaction() instanceof DbTransaction && contract.getHost() instanceof FieldedObject) {
						DbTransaction dbTransaction = (DbTransaction) this.getTransaction();
						FieldedObject boData = (FieldedObject) contract.getHost();
						MaxValue maxValue = new MaxValue(contract.getHost().getClass());
						maxValue.setKeyField(contract.getMaxValueField());
						for (IPropertyInfo<?> item : contract.getMaxValueConditions()) {
							maxValue.addConditionField(item);
							maxValue.setProperty(item, boData.getProperty(item));
						}
						maxValue = dbTransaction.fetch(maxValue);
						if (!maxValue.isDeleted()) {
							numbering.getMaxValueNumbering().add(new KeyValue<>(contract.getMaxValueKey(), 1));
						} else {
							numbering.getMaxValueNumbering()
									.add(new KeyValue<>(contract.getMaxValueKey(), maxValue.getValue()));
						}
					} else {
						// 非数据库事务，编号为1
						numbering.getMaxValueNumbering().add(new KeyValue<>(contract.getMaxValueKey(), 1));
					}
				}
			}
			return numbering;
		} catch (Exception e) {
			throw new BusinessLogicException(e);
		}
	}

	@Override
	protected void impact(IBOKeysContract contract) {
		int key = 0;
		// 主键赋值
		key = this.getBeAffected().getAutoKey();
		if (contract.setPrimaryKey(key)) {
			this.getBeAffected().setAutoKey(key + 1);
		}
		// 系列号赋值
		if (contract.getSeries() > 0) {
			BOSeriesNumbering numbering = this.getBeAffected().getSeriesNumberings()
					.firstOrDefault(c -> Numbers.equals(c.getSeries(), contract.getSeries()));
			if (numbering == null) {
				throw new BusinessLogicException(
						I18N.prop("not found [%s]'s series [%s].", contract.getObjectCode(), contract.getSeries()));
			}
			key = numbering.getNextNumber();
			if (contract.setSeriesKey(Strings.format(numbering.getTemplate(), key))) {
				numbering.setNextNumber(key + 1);
			}
		}
		// 最大编号赋值（含LineId）
		IPropertyInfo<?> keyProperty = contract.getMaxValueField();
		if (keyProperty != null) {
			IKeyValue<Integer> keyValue = this.getBeAffected().getMaxValueNumbering()
					.firstOrDefault(c -> c.getKey().equals(contract.getMaxValueKey()));
			if (keyValue == null) {
				throw new BusinessLogicException(I18N.prop("not found [%s]'s max [%s] value.",
						this.getHost().getClass().getSimpleName(), keyProperty.getName()));
			}
			key = keyValue.getValue();
			if (contract.setMaxValue(key)) {
				keyValue.setValue(key + contract.getMaxValueStep());
			}
		}
	}

	@Override
	protected void revoke(IBOKeysContract contract) {
	}

}

/**
 * 业务对象编号方式
 * 
 */
class BONumbering extends BusinessObject<BONumbering> implements IDbTableLock, IBOCustomKey {

	private static final long serialVersionUID = 1L;

	/**
	 * 当前类型
	 */
	private static final Class<?> MY_CLASS = BONumbering.class;

	/**
	 * 数据库表
	 */
	public static final String DB_TABLE_NAME = "${Company}_SYS_ONNM";

	/**
	 * 属性名称-对象编码
	 */
	private static final String PROPERTY_OBJECTCODE_NAME = "ObjectCode";

	/**
	 * 对象编码 属性
	 */
	@DbField(name = "ObjectCode", type = DbFieldType.ALPHANUMERIC, table = DB_TABLE_NAME, primaryKey = true)
	public static final IPropertyInfo<String> PROPERTY_OBJECTCODE = registerProperty(PROPERTY_OBJECTCODE_NAME,
			String.class, MY_CLASS);

	/**
	 * 获取-对象编码
	 * 
	 * @return 值
	 */
	public final String getObjectCode() {
		return this.getProperty(PROPERTY_OBJECTCODE);
	}

	/**
	 * 设置-对象编码
	 * 
	 * @param value 值
	 */
	public final void setObjectCode(String value) {
		this.setProperty(PROPERTY_OBJECTCODE, value);
	}

	/**
	 * 属性名称-自动序号
	 */
	private static final String PROPERTY_AUTOKEY_NAME = "AutoKey";

	/**
	 * 自动序号 属性
	 */
	@DbField(name = "AutoKey", type = DbFieldType.NUMERIC, table = DB_TABLE_NAME)
	public static final IPropertyInfo<Integer> PROPERTY_AUTOKEY = registerProperty(PROPERTY_AUTOKEY_NAME, Integer.class,
			MY_CLASS);

	/**
	 * 获取-自动序号
	 * 
	 * @return 值
	 */
	public final Integer getAutoKey() {
		return this.getProperty(PROPERTY_AUTOKEY);
	}

	/**
	 * 设置-自动序号
	 * 
	 * @param value 值
	 */
	public final void setAutoKey(Integer value) {
		this.setProperty(PROPERTY_AUTOKEY, value);
	}

	private List<BOSeriesNumbering> seriesNumbering;

	/**
	 * 获取-业务对象序列编号方式
	 * 
	 * @return 值
	 */
	public final List<BOSeriesNumbering> getSeriesNumberings() {
		if (this.seriesNumbering == null) {
			this.seriesNumbering = new ArrayList<>();
		}
		return this.seriesNumbering;
	}

	private List<IKeyValue<Integer>> maxValueNumberings;

	/**
	 * 获取-业务最大编号
	 * 
	 * @return 值
	 */
	public final List<IKeyValue<Integer>> getMaxValueNumbering() {
		if (this.maxValueNumberings == null) {
			this.maxValueNumberings = new ArrayList<>();
		}
		return this.maxValueNumberings;
	}

	/**
	 * 初始化数据
	 */
	@Override
	protected void initialize() {
		super.initialize();
	}
}

/**
 * 业务对象序列编号方式
 * 
 */
class BOSeriesNumbering extends BusinessObject<BOSeriesNumbering> implements IDbTableLock, IBOCustomKey {

	private static final long serialVersionUID = 1L;

	/**
	 * 当前类型
	 */
	private static final Class<?> MY_CLASS = BOSeriesNumbering.class;

	/**
	 * 数据库表
	 */
	public static final String DB_TABLE_NAME = "${Company}_SYS_NNM1";

	/**
	 * 属性名称-对象编码
	 */
	private static final String PROPERTY_OBJECTCODE_NAME = "ObjectCode";

	/**
	 * 对象编码 属性
	 */
	@DbField(name = "ObjectCode", type = DbFieldType.ALPHANUMERIC, table = DB_TABLE_NAME, primaryKey = true, uniqueKey = true)
	public static final IPropertyInfo<String> PROPERTY_OBJECTCODE = registerProperty(PROPERTY_OBJECTCODE_NAME,
			String.class, MY_CLASS);

	/**
	 * 获取-对象编码
	 * 
	 * @return 值
	 */
	public final String getObjectCode() {
		return this.getProperty(PROPERTY_OBJECTCODE);
	}

	/**
	 * 设置-对象编码
	 * 
	 * @param value 值
	 */
	public final void setObjectCode(String value) {
		this.setProperty(PROPERTY_OBJECTCODE, value);
	}

	/**
	 * 属性名称-序列
	 */
	private static final String PROPERTY_SERIES_NAME = "Series";

	/**
	 * 序列 属性
	 */
	@DbField(name = "Series", type = DbFieldType.NUMERIC, table = DB_TABLE_NAME, primaryKey = true)
	public static final IPropertyInfo<Integer> PROPERTY_SERIES = registerProperty(PROPERTY_SERIES_NAME, Integer.class,
			MY_CLASS);

	/**
	 * 获取-序列
	 * 
	 * @return 值
	 */
	public final Integer getSeries() {
		return this.getProperty(PROPERTY_SERIES);
	}

	/**
	 * 设置-序列
	 * 
	 * @param value 值
	 */
	public final void setSeries(Integer value) {
		this.setProperty(PROPERTY_SERIES, value);
	}

	/**
	 * 属性名称-序列名称
	 */
	private static final String PROPERTY_SERIESNAME_NAME = "SeriesName";

	/**
	 * 序列名称 属性
	 */
	@DbField(name = "SeriesName", type = DbFieldType.ALPHANUMERIC, table = DB_TABLE_NAME, uniqueKey = true)
	public static final IPropertyInfo<String> PROPERTY_SERIESNAME = registerProperty(PROPERTY_SERIESNAME_NAME,
			String.class, MY_CLASS);

	/**
	 * 获取-序列名称
	 * 
	 * @return 值
	 */
	public final String getSeriesName() {
		return this.getProperty(PROPERTY_SERIESNAME);
	}

	/**
	 * 设置-序列名称
	 * 
	 * @param value 值
	 */
	public final void setSeriesName(String value) {
		this.setProperty(PROPERTY_SERIESNAME, value);
	}

	/**
	 * 属性名称-下一个序号
	 */
	private static final String PROPERTY_NEXTNUMBER_NAME = "NextNumber";

	/**
	 * 下一个序号 属性
	 */
	@DbField(name = "NextNum", type = DbFieldType.NUMERIC, table = DB_TABLE_NAME)
	public static final IPropertyInfo<Integer> PROPERTY_NEXTNUMBER = registerProperty(PROPERTY_NEXTNUMBER_NAME,
			Integer.class, MY_CLASS);

	/**
	 * 获取-下一个序号
	 * 
	 * @return 值
	 */
	public final Integer getNextNumber() {
		return this.getProperty(PROPERTY_NEXTNUMBER);
	}

	/**
	 * 设置-下一个序号
	 * 
	 * @param value 值
	 */
	public final void setNextNumber(Integer value) {
		this.setProperty(PROPERTY_NEXTNUMBER, value);
	}

	/**
	 * 属性名称-已锁定
	 */
	private static final String PROPERTY_LOCKED_NAME = "Locked";

	/**
	 * 已锁定 属性
	 */
	@DbField(name = "Locked", type = DbFieldType.ALPHANUMERIC, table = DB_TABLE_NAME)
	public static final IPropertyInfo<emYesNo> PROPERTY_LOCKED = registerProperty(PROPERTY_LOCKED_NAME, emYesNo.class,
			MY_CLASS);

	/**
	 * 获取-已锁定
	 * 
	 * @return 值
	 */
	public final emYesNo getLocked() {
		return this.getProperty(PROPERTY_LOCKED);
	}

	/**
	 * 设置-已锁定
	 * 
	 * @param value 值
	 */
	public final void setLocked(emYesNo value) {
		this.setProperty(PROPERTY_LOCKED, value);
	}

	/**
	 * 属性名称-模板
	 */
	private static final String PROPERTY_TEMPLATE_NAME = "Template";

	/**
	 * 模板 属性
	 */
	@DbField(name = "Template", type = DbFieldType.ALPHANUMERIC, table = DB_TABLE_NAME)
	public static final IPropertyInfo<String> PROPERTY_TEMPLATE = registerProperty(PROPERTY_TEMPLATE_NAME, String.class,
			MY_CLASS);

	/**
	 * 获取-模板
	 * 
	 * @return 值
	 */
	public final String getTemplate() {
		return this.getProperty(PROPERTY_TEMPLATE);
	}

	/**
	 * 设置-模板
	 * 
	 * @param value 值
	 */
	public final void setTemplate(String value) {
		this.setProperty(PROPERTY_TEMPLATE, value);
	}

	/**
	 * 初始化数据
	 */
	@Override
	protected void initialize() {
		super.initialize();
	}

}
