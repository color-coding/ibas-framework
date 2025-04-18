package org.colorcoding.ibas.bobas.logic.common;

import org.colorcoding.ibas.bobas.bo.BOFactory;
import org.colorcoding.ibas.bobas.bo.IBOCustomKey;
import org.colorcoding.ibas.bobas.bo.IBODocument;
import org.colorcoding.ibas.bobas.bo.IBOLine;
import org.colorcoding.ibas.bobas.bo.IBOMasterData;
import org.colorcoding.ibas.bobas.bo.IBOSimple;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.Numbers;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.FieldedObject;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.IKeyValue;
import org.colorcoding.ibas.bobas.data.KeyValue;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.db.DbTransaction;
import org.colorcoding.ibas.bobas.db.MaxValue;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.logging.Logger;
import org.colorcoding.ibas.bobas.logging.LoggingLevel;
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
				Logger.log(LoggingLevel.DEBUG, MSG_LOGICS_SKIP_LOGIC_EXECUTION, this.getClass().getName(), "isSavable",
						"false");
				return false;
			}
			if (boData.isNew() == false) {
				Logger.log(LoggingLevel.DEBUG, MSG_LOGICS_SKIP_LOGIC_EXECUTION, this.getClass().getName(), "isNew",
						"false");
				return false;
			}
			if (data instanceof IBOCustomKey) {
				// 自定义键，不执行业务逻辑
				Logger.log(LoggingLevel.DEBUG, MSG_LOGICS_SKIP_LOGIC_EXECUTION, this.getClass().getName(),
						"isCustomKey", "true");
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
				if (contract.getHost() instanceof IBOLine) {
					// 行对象获取第二主键
					numbering = new BONumbering();
					numbering.setSavable(false);
					numbering.setObjectCode(contract.getObjectCode());
					numbering.setAutoKey(1);
					if (this.getTransaction() instanceof DbTransaction && contract.getHost() instanceof FieldedObject) {
						DbTransaction dbTransaction = (DbTransaction) this.getTransaction();
						FieldedObject boData = (FieldedObject) contract.getHost();
						MaxValue maxValue = new MaxValue(contract.getHost().getClass());
						for (IPropertyInfo<?> item : BOFactory.propertyInfos(maxValue.getType())) {
							if (IBOLine.SECONDARY_PRIMARY_KEY_NAME.equalsIgnoreCase(item.getName())) {
								maxValue.setKeyField(item);
							} else if (IBOMasterData.MASTER_PRIMARY_KEY_NAME.equalsIgnoreCase(item.getName())
									|| IBODocument.MASTER_PRIMARY_KEY_NAME.equalsIgnoreCase(item.getName())
									|| IBOSimple.MASTER_PRIMARY_KEY_NAME.equalsIgnoreCase(item.getName())) {
								maxValue.addConditionField(item);
								maxValue.setProperty(item, boData.getProperty(item));
							}
							if (maxValue.getKeyField() != null && !maxValue.getConditionFields().isEmpty()) {
								break;
							}
						}
						maxValue = dbTransaction.fetch(maxValue);
						numbering.setAutoKey((int) maxValue.getValue() + 1);
					}
				} else {
					// 主对象获取主键
					BONumbering[] numberings = this.getTransaction().fetch(BONumbering.class, criteria);
					if (numberings == null || numberings.length == 0) {
						throw new BusinessLogicException(
								I18N.prop("msg_bobas_not_found_bo_primary_key", contract.getObjectCode()));
					}
					numbering = numberings[0];
				}
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
						throw new BusinessLogicException(I18N.prop("msg_bobas_not_found_bo_series_key",
								contract.getObjectCode(), contract.getSeries()));
					}
					numbering.getSeriesNumberings().addAll(numberings);
				}
			}
			// 获取最大值
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
		} else {
			Logger.log(LoggingLevel.DEBUG, "%s: not set [%s]'s primarkey.", this.getClass().getSimpleName(),
					contract.getIdentifiers());
		}
		// 系列号赋值
		if (contract.getSeries() > 0) {
			BOSeriesNumbering numbering = this.getBeAffected().getSeriesNumberings()
					.firstOrDefault(c -> Numbers.equals(c.getSeries(), contract.getSeries()));
			if (numbering == null) {
				throw new BusinessLogicException(I18N.prop("msg_bobas_not_found_bo_series_key.",
						contract.getObjectCode(), contract.getSeries()));
			}
			key = numbering.getNextNumber();
			if (contract.setSeriesKey(Strings.format(numbering.getTemplate(), key))) {
				numbering.setNextNumber(key + 1);
			}
		}
		// 最大编号赋值
		IPropertyInfo<?> keyProperty = contract.getMaxValueField();
		if (keyProperty != null) {
			IKeyValue<Integer> keyValue = this.getBeAffected().getMaxValueNumbering()
					.firstOrDefault(c -> c.getKey().equals(contract.getMaxValueKey()));
			if (keyValue == null) {
				throw new BusinessLogicException(I18N.prop("msg_bobas_not_found_bo_max_values",
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
