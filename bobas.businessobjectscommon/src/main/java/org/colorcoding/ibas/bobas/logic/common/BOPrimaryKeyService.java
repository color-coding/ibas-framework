package org.colorcoding.ibas.bobas.logic.common;

import java.beans.PropertyChangeEvent;

import org.colorcoding.ibas.bobas.bo.BusinessObject;
import org.colorcoding.ibas.bobas.bo.BusinessObjects;
import org.colorcoding.ibas.bobas.bo.IBODocumentLine;
import org.colorcoding.ibas.bobas.bo.IBOLine;
import org.colorcoding.ibas.bobas.bo.IBOMasterDataLine;
import org.colorcoding.ibas.bobas.bo.IBOMaxValueKey;
import org.colorcoding.ibas.bobas.bo.IBOSeriesKey;
import org.colorcoding.ibas.bobas.bo.IBOSimpleLine;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.Numbers;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.db.DbField;
import org.colorcoding.ibas.bobas.db.DbFieldType;
import org.colorcoding.ibas.bobas.db.IDbTableLock;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.logic.BusinessLogic;
import org.colorcoding.ibas.bobas.logic.BusinessLogicException;
import org.colorcoding.ibas.bobas.logic.LogicContract;

/**
 * 业务对象主键服务
 *
 */
@LogicContract(IBOPrimaryKeyContract.class)
public class BOPrimaryKeyService extends BusinessLogic<IBOPrimaryKeyContract, BONumbering> {

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
		}
		return true;
	}

	@Override
	protected BONumbering fetchBeAffected(IBOPrimaryKeyContract contract) {
		try {
			// 获取主编号
			ICriteria criteria = new Criteria();
			ICondition condition = criteria.getConditions().create();
			condition.setAlias(BONumbering.PROPERTY_OBJECTCODE.getName());
			condition.setValue(contract.getObjectCode());
			BONumbering numbering = this.fetchBeAffected(criteria, BONumbering.class);
			if (numbering == null) {
				BONumbering[] numberings = this.getTransaction().fetch(criteria, BONumbering.class);
				if (numberings == null || numberings.length == 0) {
					throw new BusinessLogicException(I18N.prop("not found [%s] keys.", contract.getObjectCode()));
				}
				numbering = numberings[0];
			}
			// 获取系列编号
			if (contract.getHost() instanceof IBOSeriesKey && contract.getSeries() > 0) {
				if (numbering.getBOSeriesNumberings()
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
					BOSeriesNumbering[] numberings = this.getTransaction().fetch(criteria, BOSeriesNumbering.class);
					if (numberings == null || numberings.length == 0) {
						throw new BusinessLogicException(I18N.prop("not found [%s]'s series [%s].",
								contract.getObjectCode(), contract.getSeries()));
					}
					numbering.getBOSeriesNumberings().addAll(numberings);
				}
			}
			// 获取子项编号
			if (contract.getHost() instanceof IBOLine) {
				String masterKey = null;
				if (contract.getHost() instanceof IBODocumentLine) {
					masterKey = Strings.valueOf(((IBODocumentLine) contract.getHost()).getDocEntry());
				} else if (contract.getHost() instanceof IBOSimpleLine) {
					masterKey = Strings.valueOf(((IBOSimpleLine) contract.getHost()).getObjectKey());
				} else if (contract.getHost() instanceof IBOMasterDataLine) {
					masterKey = ((IBOMasterDataLine) contract.getHost()).getCode();
				}
				if (masterKey != null) {

				}
			} else if (contract.getHost() instanceof IBOMaxValueKey) {

			}
			return numbering;
		} catch (Exception e) {
			throw new BusinessLogicException(e);
		}
	}

	@Override
	protected void impact(IBOPrimaryKeyContract contract) {
		int key = 0;
		if (contract.getHost() instanceof IBOLine) {
			BOLineNumbering numbering = this.getBeAffected().getBOLineNumberings().firstOrDefault();
			if (numbering == null) {
				throw new BusinessLogicException(
						I18N.prop("not found [%s]'s max line id.", this.getHost().getClass().getSimpleName()));
			}
			key = numbering.getLineId() + 1;
			contract.setPrimaryKey(key);
			numbering.setLineId(key);
		} else {
			key = this.getBeAffected().getAutoKey();
			contract.setPrimaryKey(key);
			this.getBeAffected().setAutoKey(key + 1);
		}
		if (contract.getSeries() > 0) {
			BOSeriesNumbering numbering = this.getBeAffected().getBOSeriesNumberings()
					.firstOrDefault(c -> Numbers.equals(c.getSeries(), contract.getSeries()));
			if (numbering == null) {
				throw new BusinessLogicException(
						I18N.prop("not found [%s]'s series [%s].", contract.getObjectCode(), contract.getSeries()));
			}
			key = numbering.getNextNumber();
			contract.setSeriesKey(Strings.format(numbering.getTemplate(), key));
			numbering.setNextNumber(key + 1);
		}
	}

	@Override
	protected void revoke(IBOPrimaryKeyContract contract) {
	}

}

/**
 * 业务对象编号方式
 * 
 */
class BONumbering extends BusinessObject<BONumbering> implements IDbTableLock {

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

	/**
	 * 属性名称-业务对象序列编号方式
	 */
	private static final String PROPERTY_BOSERIESNUMBERINGS_NAME = "BOSeriesNumberings";

	/**
	 * 业务对象序列编号方式属性
	 * 
	 */
	public static final IPropertyInfo<BOSeriesNumberings> PROPERTY_BOSERIESNUMBERINGS = registerProperty(
			PROPERTY_BOSERIESNUMBERINGS_NAME, BOSeriesNumberings.class, MY_CLASS);

	/**
	 * 获取-业务对象序列编号方式
	 * 
	 * @return 值
	 */
	public final BOSeriesNumberings getBOSeriesNumberings() {
		return this.getProperty(PROPERTY_BOSERIESNUMBERINGS);
	}

	/**
	 * 设置-业务对象序列编号方式
	 * 
	 * @param value 值
	 */
	public final void setBOSeriesNumberings(BOSeriesNumberings value) {
		this.setProperty(PROPERTY_BOSERIESNUMBERINGS, value);
	}

	/**
	 * 属性名称-业务对象行编号
	 */
	private static final String PROPERTY_BOLINENUMBERINGS_NAME = "BOLineNumberings";

	/**
	 * 业务对象行编号属性
	 * 
	 */
	public static final IPropertyInfo<BOLineNumberings> PROPERTY_BOLINENUMBERINGS = registerProperty(
			PROPERTY_BOLINENUMBERINGS_NAME, BOLineNumberings.class, MY_CLASS);

	/**
	 * 获取-业务对象行编号
	 * 
	 * @return 值
	 */
	public final BOLineNumberings getBOLineNumberings() {
		return this.getProperty(PROPERTY_BOLINENUMBERINGS);
	}

	/**
	 * 设置-业务对象行编号
	 * 
	 * @param value 值
	 */
	public final void setBOLineNumberings(BOLineNumberings value) {
		this.setProperty(PROPERTY_BOLINENUMBERINGS, value);
	}

	/**
	 * 初始化数据
	 */
	@Override
	protected void initialize() {
		super.initialize();
		this.setBOSeriesNumberings(new BOSeriesNumberings(this));
		this.setBOLineNumberings(new BOLineNumberings(this));
	}
}

/**
 * 业务对象系列编号 集合
 */
class BOSeriesNumberings extends BusinessObjects<BOSeriesNumbering, BONumbering> {

	private static final long serialVersionUID = 1L;

	/**
	 * 构造方法
	 */
	public BOSeriesNumberings() {
		super();
	}

	/**
	 * 构造方法
	 * 
	 * @param parent 父项对象
	 */
	public BOSeriesNumberings(BONumbering parent) {
		super(parent);
	}

	/**
	 * 元素类型
	 */
	public Class<BOSeriesNumbering> getElementType() {
		return BOSeriesNumbering.class;
	}

	/**
	 * 创建库存发货-行
	 * 
	 * @return 库存发货-行
	 */
	public BOSeriesNumbering create() {
		BOSeriesNumbering item = new BOSeriesNumbering();
		if (this.add(item)) {
			return item;
		}
		return null;
	}

	@Override
	public ICriteria getElementCriteria() {
		ICriteria criteria = new Criteria();
		ICondition condition = criteria.getConditions().create();
		condition.setAlias(BOSeriesNumbering.PROPERTY_OBJECTCODE.getName());
		condition.setValue(this.getParent().getObjectCode());
		return criteria;
	}

	@Override
	protected void onParentPropertyChanged(PropertyChangeEvent evt) {
		super.onParentPropertyChanged(evt);
		if (BONumbering.PROPERTY_OBJECTCODE.getName().equals(evt.getPropertyName())) {
			this.forEach(c -> c.setObjectCode(this.getParent().getObjectCode()));
		}
	}
}

/**
 * 业务对象序列编号方式
 * 
 */
class BOSeriesNumbering extends BusinessObject<BOSeriesNumbering> implements IDbTableLock {

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

/**
 * 业务对象行号 集合
 */
class BOLineNumberings extends BusinessObjects<BOLineNumbering, BONumbering> {

	private static final long serialVersionUID = 1L;

	/**
	 * 构造方法
	 */
	public BOLineNumberings() {
		super();
	}

	/**
	 * 构造方法
	 * 
	 * @param parent 父项对象
	 */
	public BOLineNumberings(BONumbering parent) {
		super(parent);
	}

	/**
	 * 元素类型
	 */
	public Class<BOLineNumbering> getElementType() {
		return BOLineNumbering.class;
	}

	/**
	 * 创建库存发货-行
	 * 
	 * @return 库存发货-行
	 */
	public BOLineNumbering create() {
		BOLineNumbering item = new BOLineNumbering();
		if (this.add(item)) {
			return item;
		}
		return null;
	}

	@Override
	public ICriteria getElementCriteria() {
		ICriteria criteria = new Criteria();

		return criteria;
	}

}

/**
 * 业务对象序列编号方式
 * 
 */
class BOLineNumbering extends BusinessObject<BOLineNumbering> {

	private static final long serialVersionUID = 1L;

	/**
	 * 当前类型
	 */
	private static final Class<?> MY_CLASS = BOLineNumbering.class;

	/**
	 * 属性名称-主序号
	 */
	private static final String PROPERTY_MASTERKEY_NAME = "MasterKey";

	/**
	 * 主序号 属性
	 */
	@DbField(name = "MasterKey", type = DbFieldType.NUMERIC)
	public static final IPropertyInfo<String> PROPERTY_MASTERKEY = registerProperty(PROPERTY_MASTERKEY_NAME,
			String.class, MY_CLASS);

	/**
	 * 获取-主序号
	 * 
	 * @return 值
	 */
	public final String getMasterKey() {
		return this.getProperty(PROPERTY_MASTERKEY);
	}

	/**
	 * 设置-主序号
	 * 
	 * @param value 值
	 */
	public final void setMasterKey(String value) {
		this.setProperty(PROPERTY_MASTERKEY, value);
	}

	/**
	 * 属性名称-最大序号
	 */
	private static final String PROPERTY_LINEID_NAME = "LineId";

	/**
	 * 最大序号 属性
	 */
	@DbField(name = "LineId", type = DbFieldType.NUMERIC)
	public static final IPropertyInfo<Integer> PROPERTY_LINEID = registerProperty(PROPERTY_LINEID_NAME, Integer.class,
			MY_CLASS);

	/**
	 * 获取-最大序号
	 * 
	 * @return 值
	 */
	public final Integer getLineId() {
		return this.getProperty(PROPERTY_LINEID);
	}

	/**
	 * 设置-最大序号
	 * 
	 * @param value 值
	 */
	public final void setLineId(Integer value) {
		this.setProperty(PROPERTY_LINEID, value);
	}

	/**
	 * 初始化数据
	 */
	@Override
	protected void initialize() {
		super.initialize();
	}

}
