package org.colorcoding.ibas.bobas.core;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Serializable", namespace = MyConfiguration.NAMESPACE_BOBAS_CORE)
public abstract class Serializable implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

}
