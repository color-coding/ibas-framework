package org.colorcoding.ibas.bobas.db;

import java.math.BigDecimal;

import org.colorcoding.ibas.bobas.data.DateTime;

public enum DataType {
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

	/**
	 * 根据Java类型获取对应的数据库字段类型（null或未识别类型返回UNKNOWN）
	 *
	 * @param type Java类型
	 * @return 数据库字段类型
	 */
	public static DataType valueOf(Class<?> type) {
		if (type == BigDecimal.class || type == Float.class || type == Double.class || type == float.class
				|| type == double.class) {
			return DECIMAL;
		} else if (type == Integer.class || type == Short.class || type == Long.class || type == int.class
				|| type == short.class || type == long.class) {
			return NUMERIC;
		} else if (type == DateTime.class) {
			return DATE;
		} else if (type == String.class || (type != null && type.isEnum())) {
			return ALPHANUMERIC;
		}
		return UNKNOWN;
	}

}
