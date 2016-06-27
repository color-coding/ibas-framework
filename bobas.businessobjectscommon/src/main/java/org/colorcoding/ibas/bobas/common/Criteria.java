package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
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
		if (value == null || value.equals("")) {
			return null;
		}
		if (value.startsWith("{[") && value.endsWith("]}")) {
			// 对象识别码
			return fromIdentifiers(value);
		} else if (value.startsWith("<?xml")) {
			// xml
			return (ICriteria) Serializer.fromXmlString(value, Criteria.class, Conditions.class, Condition.class,
					Sorts.class, Sort.class, ChildCriterias.class, ChildCriteria.class);
		} else if (value.startsWith("{") && value.endsWith("}")) {
			// json
			return (ICriteria) Serializer.fromJsonString(value, Criteria.class, Conditions.class, Condition.class,
					Sorts.class, Sort.class, ChildCriterias.class, ChildCriteria.class);
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
		return (ICriteria) Serializer.Clone(this);
	}

	@Override
	public String toString(String type) {
		return Serializer.toString(type, this, true);
	}

	@Override
	public final ICriteria nextResultCriteria(IBusinessObjectBase lastBO) {
		return null;
	}

	@Override
	public final ICriteria previousResultCriteria(IBusinessObjectBase firstBO) {
		return null;
	}

	@Override
	public final ICriteria copyFrom(ICriteria criteria) {
		return null;
	}

}
