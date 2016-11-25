package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.bo.IBODocument;
import org.colorcoding.ibas.bobas.bo.IBOMasterData;
import org.colorcoding.ibas.bobas.bo.IBOSimple;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.Serializer;

/**
 * 查询
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Criteria", namespace = MyConsts.NAMESPACE_BOBAS_COMMON)
@XmlRootElement(name = "Criteria", namespace = MyConsts.NAMESPACE_BOBAS_COMMON)
public class Criteria implements ICriteria {
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
	 * @param value
	 *            值
	 * @return
	 */
	public static ICriteria create(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}
		if (value.startsWith("{[") && value.endsWith("]}")) {
			// 对象识别码
			return fromIdentifiers(value);
		} else if (value.startsWith("<?xml")) {
			// xml
			return (ICriteria) Serializer.deserializeString(Serializer.DATA_TYPE_XML, value, Criteria.class,
					Conditions.class, Condition.class, Sorts.class, Sort.class, ChildCriterias.class,
					ChildCriteria.class);
		} else if (value.startsWith("{") && value.endsWith("}")) {
			// json
			return (ICriteria) Serializer.deserializeString(Serializer.DATA_TYPE_JSON, value, Criteria.class,
					Conditions.class, Condition.class, Sorts.class, Sort.class, ChildCriterias.class,
					ChildCriteria.class);
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
		criteria.setBusinessObjectCode(tmps[0].replace("{[", ""));
		tmps = tmps[1].split("\\]\\&\\[");
		for (int i = 0; i < tmps.length; i++) {
			String[] tmpFields = tmps[i].split("=");
			if (tmpFields.length != 2) {
				continue;
			}
			ICondition condition = criteria.getConditions().create();
			condition.setAlias(tmpFields[0].replace("[", "").trim());
			condition.setCondVal(tmpFields[1].replace("]", "").replace("}", "").trim());
		}
		return criteria;
	}

	private String businessObjectCode = "";

	@Override
	@XmlElement(name = "BusinessObjectCode")
	public final String getBusinessObjectCode() {
		if (this.businessObjectCode == null) {
			this.businessObjectCode = "";
		}
		return this.businessObjectCode;
	}

	@Override
	public final void setBusinessObjectCode(String businessObjectCode) {
		this.businessObjectCode = businessObjectCode;
	}

	private int resultCount = -1;

	@Override
	@XmlElement(name = "ResultCount")
	public final int getResultCount() {
		return this.resultCount;
	}

	@Override
	public final void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}

	private boolean notLoadedChildren = false;

	@Override
	@XmlElement(name = "NotLoadedChildren")
	public final boolean getNotLoadedChildren() {
		return this.notLoadedChildren;
	}

	@Override
	public final void setNotLoadedChildren(boolean notLoadedChildren) {
		this.notLoadedChildren = notLoadedChildren;
	}

	private String remarks = "";

	@Override
	@XmlElement(name = "Remarks")
	public final String getRemarks() {
		return this.remarks;
	}

	@Override
	public final void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	private IConditions conditions = null;

	@Override
	@XmlElementWrapper(name = "Conditions")
	@XmlElement(name = "Condition", type = Condition.class)
	public final IConditions getConditions() {
		if (this.conditions == null) {
			this.conditions = new Conditions();
		}
		;
		return this.conditions;
	}

	public final void setConditions(IConditions value) {
		this.conditions = value;
	}

	private IChildCriterias childCriterias = null;

	@Override
	@XmlElementWrapper(name = "ChildCriterias")
	@XmlElement(name = "ChildCriteria", type = ChildCriteria.class, nillable = true)
	public final IChildCriterias getChildCriterias() {
		if (this.childCriterias == null) {
			this.childCriterias = new ChildCriterias();
		}
		return this.childCriterias;
	}

	public final void setChildCriterias(IChildCriterias childCriterias) {
		this.childCriterias = childCriterias;
	}

	private ISorts sorts = null;

	@Override
	@XmlElementWrapper(name = "Sorts")
	@XmlElement(name = "Sort", type = Sort.class, nillable = true)
	public final ISorts getSorts() {
		if (this.sorts == null) {
			this.sorts = new Sorts();
		}
		return this.sorts;
	}

	public final void setSorts(ISorts sorts) {
		this.sorts = sorts;
	}

	@Override
	public final ICriteria clone() {
		return (ICriteria) Serializer.clone(this);
	}

	@Override
	public String toString(String type) {
		return Serializer.serializeString(type, this, true);
	}

	protected ICriteria getBOCriteria(IBusinessObjectBase bo) {
		ICriteria boCriteria = null;
		// 判断BO类型，添加下个集合条件，尽量使用数值字段
		if (bo instanceof IBOSimple) {
			IBOSimple simple = (IBOSimple) bo;
			boCriteria = new Criteria();
			boCriteria.setBusinessObjectCode(simple.getObjectCode());
			ICondition condition = boCriteria.getConditions().create();
			condition.setAlias(IBOSimple.MASTER_PRIMARY_KEY_NAME);
			condition.setCondVal(simple.getObjectKey());
		} else if (bo instanceof IBODocument) {
			IBODocument document = (IBODocument) bo;
			boCriteria = new Criteria();
			boCriteria.setBusinessObjectCode(document.getObjectCode());
			ICondition condition = boCriteria.getConditions().create();
			condition.setAlias(IBODocument.MASTER_PRIMARY_KEY_NAME);
			condition.setCondVal(document.getDocEntry());

		} else if (bo instanceof IBOMasterData) {
			IBOMasterData master = (IBOMasterData) bo;
			boCriteria = new Criteria();
			boCriteria.setBusinessObjectCode(master.getObjectCode());
			ICondition condition = boCriteria.getConditions().create();
			condition.setAlias(IBOMasterData.SERIAL_NUMBER_KEY_NAME);
			condition.setCondVal(master.getDocEntry());
		}
		if (boCriteria == null) {
			boCriteria = bo.getCriteria();
		}
		return boCriteria;
	}

	@Override
	public final ICriteria nextResultCriteria(IBusinessObjectBase lastBO) {
		if (lastBO != null) {
			ICriteria boCriteria = this.getBOCriteria(lastBO);
			if (boCriteria == null) {
				return null;
			}
			for (ICondition condition : boCriteria.getConditions()) {
				if (this.getSorts().size() > 0 && this.getSorts().get(0).getSortType() == SortType.st_Descending) {
					// 降序排序，则下一个数据集为小于条件
					condition.setOperation(ConditionOperation.co_LESS_THAN);
					continue;
				}
				condition.setOperation(ConditionOperation.co_GRATER_THAN);
			}
			return this.copyFrom(boCriteria);
		}
		return null;
	}

	@Override
	public final ICriteria previousResultCriteria(IBusinessObjectBase firstBO) {
		if (firstBO != null) {
			ICriteria boCriteria = this.getBOCriteria(firstBO);
			if (boCriteria == null) {
				return null;
			}
			for (ICondition condition : boCriteria.getConditions()) {
				if (this.getSorts().size() > 0 && this.getSorts().get(0).getSortType() == SortType.st_Descending) {
					// 降序排序，则下一个数据集为大于条件
					condition.setOperation(ConditionOperation.co_GRATER_THAN);
					continue;
				}
				condition.setOperation(ConditionOperation.co_LESS_THAN);
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
				for (IChildCriteria myChildCriteria : nCriteria.getChildCriterias()) {
					if (myChildCriteria.getPropertyPath() == null) {
						continue;
					}
					if (myChildCriteria.getPropertyPath().equals(tmpChildCriteria.getPropertyPath())) {
						isNew = false;
						break;
					}
				}
				if (isNew) {
					nCriteria.getChildCriterias().add(tmpChildCriteria);
				}
			}
			// 复制查询条件
			if (nCriteria.getConditions().size() > 0) {
				// 原始条件括号括起
				ICondition condition = nCriteria.getConditions().get(0);
				condition.setBracketOpenNum(condition.getBracketOpenNum() + 1);
				condition = nCriteria.getConditions().get(nCriteria.getConditions().size() - 1);
				condition.setBracketCloseNum(condition.getBracketCloseNum() + 1);
			}
			if (tmpCriteria.getConditions().size() > 0) {
				// 拷贝条件括号括起
				ICondition condition = tmpCriteria.getConditions().get(0);
				condition.setBracketOpenNum(condition.getBracketOpenNum() + 1);
				condition = tmpCriteria.getConditions().get(tmpCriteria.getConditions().size() - 1);
				condition.setBracketCloseNum(condition.getBracketCloseNum() + 1);
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
