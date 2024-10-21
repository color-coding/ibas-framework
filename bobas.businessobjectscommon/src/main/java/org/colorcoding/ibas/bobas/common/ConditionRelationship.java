package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;

/**
 * 条件之间关系
 */
@XmlType(name = "ConditionRelationship", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
public enum ConditionRelationship {
	/**
	 * 没关系
	 */
	NONE,
	/**
	 * 且
	 */
	AND,
	/**
	 * 或
	 */
	OR;

}
