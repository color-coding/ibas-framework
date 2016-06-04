package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.util.ArrayList;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Sorts", namespace = MyConsts.NAMESPACE_BOBAS_COMMON)
@XmlRootElement(name = "Sorts", namespace = MyConsts.NAMESPACE_BOBAS_COMMON)
@XmlSeeAlso({ Sort.class })
public class Sorts extends ArrayList<ISort> implements ISorts {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3748654397825099641L;

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
