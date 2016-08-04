package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;

/**
 * 子项查询
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "ChildCriteria", namespace = MyConsts.NAMESPACE_BOBAS_COMMON)
@XmlRootElement(name = "ChildCriteria", namespace = MyConsts.NAMESPACE_BOBAS_COMMON)
public class ChildCriteria extends Criteria implements IChildCriteria {

	private String propertyPath = "";

	@Override
	@XmlElement(name = "PropertyPath")
	public final String getPropertyPath() {
		return this.propertyPath;
	}

	@Override
	public final void setPropertyPath(String value) {
		this.propertyPath = value;
	}

	private boolean fatherMustHasResluts = true;

	@Override
	@XmlElement(name = "FatherMustHasResluts")
	public final boolean getFatherMustHasResluts() {
		return this.fatherMustHasResluts;
	}

	@Override
	public final void setFatherMustHasResluts(boolean value) {
		this.fatherMustHasResluts = value;
	}

}
