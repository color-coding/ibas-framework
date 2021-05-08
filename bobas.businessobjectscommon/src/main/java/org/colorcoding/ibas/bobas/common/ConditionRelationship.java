package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.mapping.Value;

/**
 * 条件之间关系
 */
@XmlType(name = "ConditionRelationship", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
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

	public static ConditionRelationship valueOf(int value) {
		return values()[value];
	}

	public static ConditionRelationship valueOf(String value, boolean ignoreCase) {
		if (ignoreCase) {
			for (Object item : ConditionRelationship.class.getEnumConstants()) {
				if (item.toString().equalsIgnoreCase(value)) {
					return (ConditionRelationship) item;
				}
			}
		}
		return ConditionRelationship.valueOf(value);
	}
}
