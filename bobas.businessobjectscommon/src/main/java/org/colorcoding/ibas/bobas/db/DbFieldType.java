package org.colorcoding.ibas.bobas.db;

import java.math.BigDecimal;

import org.colorcoding.ibas.bobas.data.DateTime;

public enum DbFieldType {
	/**
	 * 未知
	 */
	UNKNOWN,
	/**
	 * 字母数字
	 */
	ALPHANUMERIC,
	/**
	 * 长字符串
	 */
	MEMO,
	/**
	 * 数字
	 */
	NUMERIC,
	/**
	 * 日期
	 */
	DATE,
	/**
	 * 小数
	 */
	DECIMAL,
	/**
	 * 字节
	 */
	BYTES;

	public static DbFieldType valueOf(Class<?> type) {
		if (type == BigDecimal.class) {
			return DECIMAL;
		} else if (type == Integer.class || type == Short.class || type == Float.class || type == Double.class
				|| type == Long.class) {
			return NUMERIC;
		} else if (type == DateTime.class) {
			return DATE;
		} else if (type == String.class) {
			return ALPHANUMERIC;
		}
		return UNKNOWN;
	}
}
