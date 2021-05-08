package org.colorcoding.ibas.bobas.common;

public enum ConditionAliasDataType {
	/**
	 * 字母数字
	 */
	ALPHANUMERIC,
	/**
	 * 数字
	 */
	NUMERIC,
	/**
	 * 小数
	 */
	DECIMAL,
	/**
	 * 日期
	 */
	DATE,
	/**
	 * 自定义文本
	 */
	FREE_TEXT;

	public static ConditionAliasDataType valueOf(int value) {
		return values()[value];
	}

	public static ConditionAliasDataType valueOf(String value, boolean ignoreCase) {
		if (ignoreCase) {
			for (Object item : ConditionAliasDataType.class.getEnumConstants()) {
				if (item.toString().equalsIgnoreCase(value)) {
					return (ConditionAliasDataType) item;
				}
			}
		}
		return ConditionAliasDataType.valueOf(value);
	}
}
