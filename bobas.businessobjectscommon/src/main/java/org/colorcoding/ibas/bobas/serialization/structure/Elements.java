package org.colorcoding.ibas.bobas.serialization.structure;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;

import org.colorcoding.ibas.bobas.data.ArrayList;

public class Elements extends ArrayList<Element> {

	public Elements(Element parent) {
		this.setParent(parent);
	}

	private Element parent;

	@XmlElement(name = "Parent")
	protected final Element getParent() {
		return parent;
	}

	private final void setParent(Element parent) {
		this.parent = parent;
	}

	private static final long serialVersionUID = -6116518221345433984L;

	@Override
	public boolean addAll(Element[] c) {
		for (Element element : c) {
			this.add(element);
		}
		return true;
	}

	@Override
	public boolean addAll(Iterable<? extends Element> c) {
		for (Element element : c) {
			this.add(element);
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends Element> c) {
		for (Element element : c) {
			this.add(element);
		}
		return true;
	}

	@Override
	public boolean add(Element e) {
		for (int i = this.size() - 1; i >= 0; i--) {
			Element item = this.get(i);
			if (item.getWrapper() != null && !item.getWrapper().isEmpty()) {
				if (item.getWrapper().equals(e.getWrapper())) {
					this.remove(i);
				}
			} else {
				if (item.getName().equals(e.getName())) {
					this.remove(i);
				}
			}
		}
		e.setParent(this.getParent());
		return super.add(e);
	}

}
