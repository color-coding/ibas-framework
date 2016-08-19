package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.mapping.DbValue;

/**
 * 条件之间关系
 */
@XmlType(name = "ConditionRelationship", namespace = MyConsts.NAMESPACE_BOBAS_COMMON)
public enum ConditionRelationship {
	/**
	 * 没关系
	 */
	@DbValue(value = "N")
	cr_NONE,
	/**
	 * 且
	 */
	@DbValue(value = "A")
	cr_AND,
	/**
	 * 或
	 */
	@DbValue(value = "O")
	cr_OR;

	public int getValue() {
		return this.ordinal();
	}

	public static ConditionRelationship forValue(int value) {
		return values()[value];
	}
}
