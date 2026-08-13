package org.colorcoding.ibas.bobas.serialization.structure;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;

/**
 * 根元素
 * 
 * @author Niuren.Zhu
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "ElementRoot", namespace = MyConfiguration.NAMESPACE_BOBAS_SERIALIZATION)
@XmlRootElement(name = "ElementRoot", namespace = MyConfiguration.NAMESPACE_BOBAS_SERIALIZATION)
public class ElementRoot extends Element {

	private static final long serialVersionUID = 6834396939699534564L;

	public ElementRoot() {
	}

	public ElementRoot(String name) {
		this();
		this.setName(name);
	}

	public ElementRoot(String name, Class<?> type) {
		this(name);
		this.setType(type);
	}

	private String namespace;

	public final String getNamespace() {
		return namespace;
	}

	public final void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public Iterable<Element> allElements() {
		return this.allElements(this);
	}

	private List<Element> allElements(Element element) {
		List<Element> elements = new ArrayList<>();
		elements.add(element);
		for (Element item : element.getChilds()) {
			elements.addAll(this.allElements(item));
		}
		return elements;
	}
}
