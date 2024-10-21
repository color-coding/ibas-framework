package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;

/**
 * 子项查询
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "ChildCriteria", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
@XmlRootElement(name = "ChildCriteria", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
public class ChildCriteria extends Criteria implements IChildCriteria {

	private static final long serialVersionUID = -4641570686942910575L;

	@XmlElement(name = "PropertyPath")
	private String propertyPath;

	@Override
	public final String getPropertyPath() {
		return this.propertyPath;
	}

	@Override
	public final void setPropertyPath(String value) {
		this.propertyPath = value;
	}

	@XmlElement(name = "OnlyHasChilds")
	private boolean onlyHasChilds = true;

	@Override
	public final boolean isOnlyHasChilds() {
		return this.onlyHasChilds;
	}

	@Override
	public final void setOnlyHasChilds(boolean value) {
		this.onlyHasChilds = value;
	}

	@XmlElement(name = "IncludingOtherChilds")
	private boolean includingOtherChilds = false;

	@Override
	public final boolean isIncludingOtherChilds() {
		return includingOtherChilds;
	}

	@Override
	public final void setIncludingOtherChilds(boolean value) {
		this.includingOtherChilds = value;
	}

	@Override
	public IChildCriteria clone() {
		return (IChildCriteria) super.clone();
	}
}
