package org.colorcoding.ibas.bobas.mapping;

public enum DbFieldType {
	/**
	 * 未知
	 */
	db_Unknown,
	/**
	 * 字母数字
	 */
	db_Alphanumeric,
	/**
	 * 长字符串
	 */
	db_Memo,
	/**
	 * 数字
	 */
	db_Numeric,
	/**
	 * 日期
	 */
	db_Date,
	/**
	 * 小数
	 */
	db_Decimal,
	/**
	 * 字节
	 */
	db_Bytes;
}
