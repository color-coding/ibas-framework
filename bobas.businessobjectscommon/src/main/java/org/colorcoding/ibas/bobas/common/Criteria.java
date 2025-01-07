package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.IBODocument;
import org.colorcoding.ibas.bobas.bo.IBODocumentLine;
import org.colorcoding.ibas.bobas.bo.IBOMasterData;
import org.colorcoding.ibas.bobas.bo.IBOMasterDataLine;
import org.colorcoding.ibas.bobas.bo.IBOSimple;
import org.colorcoding.ibas.bobas.bo.IBOSimpleLine;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.core.Serializable;

/**
 * 查询
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Criteria", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
@XmlRootElement(name = "Criteria", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
public class Criteria extends Serializable implements ICriteria, Cloneable {

	private static final long serialVersionUID = -2340536835606017565L;

	/**
	 * 创建实例
	 * 
	 * @return
	 */
	public static ICriteria create() {
		return new Criteria();
	}

	/**
	 * 创建实例
	 * 
	 * @param value 值
	 * @return
	 */
	public static ICriteria create(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}
		if (value.startsWith("{[") && value.endsWith("]}")) {
			// 对象识别码
			return fromIdentifiers(value);
		}
		return null;
	}

	/**
	 * 根据识别码创建查询
	 * 
	 * {[CC_TT_SALESORDER].[DocEntry = 1]&[LineId = 2]}
	 * 
	 * @param identifiers
	 * @return
	 */
	private static ICriteria fromIdentifiers(String identifiers) {
		Criteria criteria = new Criteria();
		String[] tmps = identifiers.split("\\]\\.\\[");
		criteria.setBusinessObject(tmps[0].replace("{[", ""));
		tmps = tmps[1].split("\\]\\&\\[");
		for (int i = 0; i < tmps.length; i++) {
			String[] tmpFields = tmps[i].split("=");
			if (tmpFields.length != 2) {
				continue;
			}
			ICondition condition = criteria.getConditions().create();
			condition.setAlias(tmpFields[0].replace("[", "").trim());
			condition.setValue(tmpFields[1].replace("]", "").replace("}", "").trim());
		}
		return criteria;
	}

	@XmlElement(name = "BusinessObject")
	private String businessObject;

	@Override
	public final String getBusinessObject() {
		if (this.businessObject == null) {
			this.businessObject = Strings.VALUE_EMPTY;
		}
		return this.businessObject;
	}

	@Override
	public final void setBusinessObject(String value) {
		this.businessObject = value;
	}

	@XmlElement(name = "ResultCount")
	private int resultCount = -1;

	@Override
	public final int getResultCount() {
		return this.resultCount;
	}

	@Override
	public final void setResultCount(int value) {
		this.resultCount = value;
	}

	@XmlElement(name = "NoChilds")
	private boolean noChilds = false;

	@Override
	public final boolean isNoChilds() {
		return this.noChilds;
	}

	@Override
	public final void setNoChilds(boolean value) {
		this.noChilds = value;
	}

	@XmlElement(name = "Remarks")
	private String remarks;

	@Override
	public final String getRemarks() {
		if (this.remarks == null) {
			this.remarks = Strings.VALUE_EMPTY;
		}
		return this.remarks;
	}

	@Override
	public final void setRemarks(String value) {
		this.remarks = value;
	}

	@XmlElementWrapper(name = "Conditions")
	@XmlElement(name = "Condition", type = Condition.class)
	private IConditions conditions = null;

	@Override
	public final IConditions getConditions() {
		if (this.conditions == null) {
			this.conditions = new Conditions();
		}
		return this.conditions;
	}

	public final void setConditions(IConditions value) {
		this.conditions = value;
	}

	@XmlElementWrapper(name = "ChildCriterias")
	@XmlElement(name = "ChildCriteria", type = ChildCriteria.class, nillable = true)
	private IChildCriterias childCriterias = null;

	@Override
	public final IChildCriterias getChildCriterias() {
		if (this.childCriterias == null) {
			this.childCriterias = new ChildCriterias();
		}
		return this.childCriterias;
	}

	public final void setChildCriterias(IChildCriterias value) {
		this.childCriterias = value;
	}

	@XmlElementWrapper(name = "Sorts")
	@XmlElement(name = "Sort", type = Sort.class, nillable = true)
	private ISorts sorts = null;

	@Override
	public final ISorts getSorts() {
		if (this.sorts == null) {
			this.sorts = new Sorts();
		}
		return this.sorts;
	}

	public final void setSorts(ISorts value) {
		this.sorts = value;
	}

	@Override
	public ICriteria clone() {
		try {
			Criteria criteria = (Criteria) super.clone();
			// 重置数组内容
			criteria.childCriterias = new ChildCriterias();
			for (IChildCriteria item : this.getChildCriterias()) {
				criteria.getChildCriterias().add(item.clone());
			}
			criteria.conditions = new Conditions();
			for (ICondition item : this.getConditions()) {
				criteria.getConditions().add(item.clone());
			}
			criteria.sorts = new Sorts();
			for (ISort item : this.getSorts()) {
				criteria.getSorts().add(item.clone());
			}
			return criteria;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{criteria: ");
		if (this.getBusinessObject() != null && !this.getBusinessObject().isEmpty()) {
			stringBuilder.append(this.getBusinessObject());
			stringBuilder.append(", ");
		}
		stringBuilder.append("result|");
		stringBuilder.append(this.getResultCount());
		stringBuilder.append("}");
		return stringBuilder.toString();
	}

	protected ICriteria boCriteria(IBusinessObject bo, ConditionOperation operation) {
		ICriteria boCriteria = null;
		// 判断BO类型，添加下个集合条件，尽量使用数值字段
		if (bo instanceof IBOSimple) {
			IBOSimple simple = (IBOSimple) bo;
			boCriteria = new Criteria();
			boCriteria.setBusinessObject(simple.getObjectCode());
			ICondition condition = boCriteria.getConditions().create();
			condition.setAlias(IBOSimple.MASTER_PRIMARY_KEY_NAME);
			condition.setValue(simple.getObjectKey());
			condition.setOperation(operation);
		} else if (bo instanceof IBODocument) {
			IBODocument document = (IBODocument) bo;
			boCriteria = new Criteria();
			boCriteria.setBusinessObject(document.getObjectCode());
			ICondition condition = boCriteria.getConditions().create();
			condition.setAlias(IBODocument.MASTER_PRIMARY_KEY_NAME);
			condition.setValue(document.getDocEntry());
			condition.setOperation(operation);
		} else if (bo instanceof IBOMasterData) {
			IBOMasterData masterData = (IBOMasterData) bo;
			boCriteria = new Criteria();
			boCriteria.setBusinessObject(masterData.getObjectCode());
			ICondition condition = boCriteria.getConditions().create();
			condition.setAlias(IBOMasterData.SERIAL_NUMBER_KEY_NAME);
			condition.setValue(masterData.getDocEntry());
			condition.setOperation(operation);
		} else if (bo instanceof IBOSimpleLine) {
			IBOSimpleLine line = (IBOSimpleLine) bo;
			boCriteria = new Criteria();
			boCriteria.setBusinessObject(line.getObjectCode());
			// 父项相等时，lineId比较方式
			ICondition condition = boCriteria.getConditions().create();
			condition.setBracketOpen(2);
			condition.setAlias(IBOSimpleLine.MASTER_PRIMARY_KEY_NAME);
			condition.setValue(line.getObjectKey());
			condition = boCriteria.getConditions().create();
			condition.setBracketClose(1);
			condition.setAlias(IBOSimpleLine.SECONDARY_PRIMARY_KEY_NAME);
			condition.setValue(line.getLineId());
			condition.setOperation(operation);
			// 父项不等时
			condition = boCriteria.getConditions().create();
			condition.setBracketClose(1);
			condition.setAlias(IBOSimpleLine.MASTER_PRIMARY_KEY_NAME);
			condition.setValue(line.getObjectKey());
			condition.setOperation(operation);
		} else if (bo instanceof IBODocumentLine) {
			IBODocumentLine line = (IBODocumentLine) bo;
			boCriteria = new Criteria();
			boCriteria.setBusinessObject(line.getObjectCode());
			// 父项相等时，lineId比较方式
			ICondition condition = boCriteria.getConditions().create();
			condition.setBracketOpen(2);
			condition.setAlias(IBOSimpleLine.MASTER_PRIMARY_KEY_NAME);
			condition.setValue(line.getDocEntry());
			condition = boCriteria.getConditions().create();
			condition.setBracketClose(1);
			condition.setAlias(IBOSimpleLine.SECONDARY_PRIMARY_KEY_NAME);
			condition.setValue(line.getLineId());
			condition.setOperation(operation);
			// 父项不等时
			condition = boCriteria.getConditions().create();
			condition.setBracketClose(1);
			condition.setAlias(IBOSimpleLine.MASTER_PRIMARY_KEY_NAME);
			condition.setValue(line.getDocEntry());
			condition.setOperation(operation);
		} else if (bo instanceof IBOMasterDataLine) {
			IBOMasterDataLine line = (IBOMasterDataLine) bo;
			boCriteria = new Criteria();
			boCriteria.setBusinessObject(line.getObjectCode());
			// 父项相等时，lineId比较方式
			ICondition condition = boCriteria.getConditions().create();
			condition.setBracketOpen(2);
			condition.setAlias(IBOSimpleLine.MASTER_PRIMARY_KEY_NAME);
			condition.setValue(line.getCode());
			condition = boCriteria.getConditions().create();
			condition.setBracketClose(1);
			condition.setAlias(IBOSimpleLine.SECONDARY_PRIMARY_KEY_NAME);
			condition.setValue(line.getLineId());
			condition.setOperation(operation);
			// 父项不等时
			condition = boCriteria.getConditions().create();
			condition.setBracketClose(1);
			condition.setAlias(IBOSimpleLine.MASTER_PRIMARY_KEY_NAME);
			condition.setValue(line.getCode());
			condition.setOperation(operation);
		}
		if (boCriteria == null) {
			boCriteria = bo.getCriteria();
			for (ICondition condition : boCriteria.getConditions()) {
				condition.setOperation(operation);
			}
		}
		return boCriteria;
	}

	@Override
	public final ICriteria next(IBusinessObject lastBO) {
		if (lastBO != null) {
			SortType sortType = SortType.ASCENDING;
			ConditionOperation operation = ConditionOperation.GRATER_THAN;
			if (!this.getSorts().isEmpty()) {
				sortType = this.getSorts().firstOrDefault().getSortType();
			}
			if (sortType == SortType.DESCENDING) {
				operation = ConditionOperation.LESS_THAN;
			}
			ICriteria boCriteria = this.boCriteria(lastBO, operation);
			if (boCriteria == null) {
				return null;
			}
			return this.copyFrom(boCriteria);
		}
		return null;
	}

	@Override
	public final ICriteria previous(IBusinessObject firstBO) {
		if (firstBO != null) {
			SortType sortType = SortType.ASCENDING;
			ConditionOperation operation = ConditionOperation.LESS_THAN;
			if (!this.getSorts().isEmpty()) {
				sortType = this.getSorts().firstOrDefault().getSortType();
			}
			if (sortType == SortType.DESCENDING) {
				operation = ConditionOperation.GRATER_THAN;
			}
			ICriteria boCriteria = this.boCriteria(firstBO, operation);
			if (boCriteria == null) {
				return null;
			}
			return this.copyFrom(boCriteria);
		}
		return null;
	}

	@Override
	public final ICriteria copyFrom(ICriteria criteria) {
		ICriteria nCriteria = this.clone();
		if (criteria != null) {
			ICriteria tmpCriteria = criteria.clone();
			// 复制子项查询
			for (IChildCriteria tmpChildCriteria : tmpCriteria.getChildCriterias()) {
				if (tmpChildCriteria.getPropertyPath() == null) {
					continue;
				}
				boolean isNew = true;
				for (int i = 0; i < nCriteria.getChildCriterias().size(); i++) {
					IChildCriteria myChildCriteria = nCriteria.getChildCriterias().get(i);
					if (myChildCriteria.getPropertyPath() == null) {
						continue;
					}
					if (!myChildCriteria.getPropertyPath().equals(tmpChildCriteria.getPropertyPath())) {
						continue;
					}
					isNew = false;
					nCriteria.getChildCriterias().set(i, (IChildCriteria) myChildCriteria.copyFrom(tmpChildCriteria));
					break;
				}
				if (isNew) {
					nCriteria.getChildCriterias().add(tmpChildCriteria);
				}
			}
			// 复制查询条件
			if (nCriteria.getConditions().size() > 1) {
				// 原始条件括号括起
				ICondition condition = nCriteria.getConditions().firstOrDefault();
				condition.setBracketOpen(condition.getBracketOpen() + 1);
				condition = nCriteria.getConditions().lastOrDefault();
				condition.setBracketClose(condition.getBracketClose() + 1);
			}
			if (tmpCriteria.getConditions().size() > 1) {
				// 拷贝条件括号括起
				ICondition condition = tmpCriteria.getConditions().firstOrDefault();
				condition.setBracketOpen(condition.getBracketOpen() + 1);
				condition = tmpCriteria.getConditions().lastOrDefault();
				condition.setBracketClose(condition.getBracketClose() + 1);
			}
			nCriteria.getConditions().addAll(tmpCriteria.getConditions());
			// 复制排序条件
			for (ISort tmpSort : tmpCriteria.getSorts()) {
				if (tmpSort.getAlias() == null) {
					continue;
				}
				boolean isNew = true;
				for (ISort mySort : nCriteria.getSorts()) {
					if (mySort.getAlias() == null) {
						continue;
					}
					if (mySort.getAlias().equals(tmpSort.getAlias())) {
						isNew = false;
						break;
					}
				}
				if (isNew) {
					nCriteria.getSorts().add(tmpSort);
				}
			}
		}
		return nCriteria;
	}

}
