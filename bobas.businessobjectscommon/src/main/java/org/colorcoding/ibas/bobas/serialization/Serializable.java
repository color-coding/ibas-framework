package org.colorcoding.ibas.bobas.serialization;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Serializable", namespace = MyConfiguration.NAMESPACE_BOBAS_SERIALIZATION)
public abstract class Serializable implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

}
