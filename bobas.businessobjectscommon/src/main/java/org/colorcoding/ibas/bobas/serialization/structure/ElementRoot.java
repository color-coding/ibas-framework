package org.colorcoding.ibas.bobas.serialization.structure;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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

	private Object source;

	public final Object getSource() {
		return source;
	}

	public final void setSource(Object source) {
		this.source = source;
	}

	@Override
	public Object getValue() {
		return this.getSource();
	}

	@Override
	public void setValue(Object value) {
		this.setSource(value);
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
