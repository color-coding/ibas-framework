package org.colorcoding.ibas.bobas.serialization.structure;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;

/**
 * 字段元素
 * 
 * @author Niuren.Zhu
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "ElementField", namespace = MyConfiguration.NAMESPACE_BOBAS_SERIALIZATION)
@XmlRootElement(name = "ElementField", namespace = MyConfiguration.NAMESPACE_BOBAS_SERIALIZATION)
public class ElementField extends Element {

	private static final long serialVersionUID = 6834396939699534564L;

	public ElementField() {
	}

	public ElementField(String name) {
		this();
		this.setName(name);
	}

	public ElementField(String name, Class<?> type) {
		this(name);
		this.setType(type);
	}

	public ElementField(String name, Class<?> type, String wrapper) {
		this(name, type);
		this.setWrapper(wrapper);
	}

}
