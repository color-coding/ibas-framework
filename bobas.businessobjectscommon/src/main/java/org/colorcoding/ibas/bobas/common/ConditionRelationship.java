package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.mapping.Value;

/**
 * 条件之间关系
 */
@XmlType(name = "ConditionRelationship", namespace = MyConsts.NAMESPACE_BOBAS_COMMON)
public enum ConditionRelationship {
	/**
	 * 没关系
	 */
	@Value("N")
	NONE,
	/**
	 * 且
	 */
	@Value("A")
	AND,
	/**
	 * 或
	 */
	@Value("O")
	OR;

	public int getValue() {
		return this.ordinal();
	}

	public static ConditionRelationship forValue(int value) {
		return values()[value];
	}
}
