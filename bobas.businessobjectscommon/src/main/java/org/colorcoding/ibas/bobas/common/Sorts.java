package org.colorcoding.ibas.bobas.common;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.data.ArrayList;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Sorts", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
@XmlRootElement(name = "Sorts", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
@XmlSeeAlso({ Sort.class })
public class Sorts extends ArrayList<ISort> implements ISorts {

	private static final long serialVersionUID = 3748654397825099641L;

	public Sorts() {
	}

	public Sorts(int size) {
		super(size);
	}

	@Override
	public ISort create() {
		Sort sort = new Sort();
		this.Add(sort);
		return sort;
	}

	public void Add(ISort item) {
		super.add(item);
	}
}
