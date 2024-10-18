package org.colorcoding.ibas.bobas.db;

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

	public static DbFieldType valueOf(int value) {
		return values()[value];
	}

	public static DbFieldType valueOf(String value, boolean ignoreCase) {
		if (ignoreCase) {
			for (Object item : DbFieldType.class.getEnumConstants()) {
				if (item.toString().equalsIgnoreCase(value)) {
					return (DbFieldType) item;
				}
			}
		}
		return DbFieldType.valueOf(value);
	}
}
