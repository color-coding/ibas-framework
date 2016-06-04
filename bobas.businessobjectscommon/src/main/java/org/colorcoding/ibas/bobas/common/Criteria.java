package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.ObjectCloner;

/**
 * 查询
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Criteria", namespace = MyConsts.NAMESPACE_BOBAS_COMMON)
@XmlRootElement(name = "Criteria", namespace = MyConsts.NAMESPACE_BOBAS_COMMON)
public class Criteria implements ICriteria {
	/**
	 * 输出类型，xml
	 */
	public final static String OUT_TYPE_XML = "xml";
	/**
	 * 输出类型，json
	 */
	public final static String OUT_TYPE_JSON = "json";

	public static ICriteria create() {
		return new Criteria();
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
		return (ICriteria) ObjectCloner.Clone(this);
	}

	@Override
	public String toString(String type) {
		if (OUT_TYPE_XML.equalsIgnoreCase(type)) {
			return ObjectCloner.toXmlString(this, true);
		} else if (OUT_TYPE_JSON.equalsIgnoreCase(type)) {

		}
		return this.toString();
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
