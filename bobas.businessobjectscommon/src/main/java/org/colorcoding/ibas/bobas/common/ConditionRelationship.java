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
	NONE,
	/**
	 * 且
	 */
	@DbValue(value = "A")
	AND,
	/**
	 * 或
	 */
	@DbValue(value = "O")
	OR;

	public int getValue() {
		return this.ordinal();
	}

	public static ConditionRelationship forValue(int value) {
		return values()[value];
	}
}
