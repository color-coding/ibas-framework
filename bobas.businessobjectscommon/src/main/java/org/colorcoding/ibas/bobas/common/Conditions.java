package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.data.ArrayList;

/**
 * 查询条件集合
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Conditions", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
@XmlRootElement(name = "Conditions", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
@XmlSeeAlso({ Condition.class })
public class Conditions extends ArrayList<ICondition> implements IConditions {

	private static final long serialVersionUID = 5496591915962628897L;

	public Conditions() {
	}

	public Conditions(int size) {
		super(size);
	}

	public void Add(ICondition item) {
		super.add(item);
	}

	@Override
	public ICondition create() {
		Condition condition = new Condition();
		this.Add(condition);
		return condition;
	}

}
