package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.util.ArrayList;

/**
 * 子项查询集合
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "ChildCriterias", namespace = MyConsts.NAMESPACE_BOBAS_COMMON)
@XmlRootElement(name = "ChildCriterias", namespace = MyConsts.NAMESPACE_BOBAS_COMMON)
@XmlSeeAlso({ ChildCriteria.class })
public final class ChildCriterias extends ArrayList<IChildCriteria> implements IChildCriterias {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2702445645664575985L;

	@Override
	public IChildCriteria create() {
		ChildCriteria childCriteria = new ChildCriteria();
		this.Add(childCriteria);
		return childCriteria;
	}

	@Override
	public IChildCriteria getCriteria(String propertyPath) {
		for (IChildCriteria item : this) {
			if (item.getPropertyPath() == null) {
				continue;
			}
			if (item.getPropertyPath().equals(propertyPath)) {
				return item;
			}
		}
		return null;
	}

	public void Add(IChildCriteria item) {
		super.add(item);
	}
}
