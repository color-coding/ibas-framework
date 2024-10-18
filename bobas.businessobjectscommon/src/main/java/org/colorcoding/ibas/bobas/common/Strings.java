package org.colorcoding.ibas.bobas.common;

public class Strings {

	private Strings() {
	}

	public static final String VALUE_ZERO = "0";

	public static final String VALUE_EMPTY = "";

	/**
	 * 类型默认值
	 * 
	 * @return
	 */
	public static String defaultValue() {
		return VALUE_EMPTY;
	}

	/**
	 * 判断字符串是否为空值
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isNullOrEmpty(String value) {
		if (value == null) {
			return true;
		}
		if (value.isEmpty()) {
			return true;
		}
		return false;
	}

	public static String valueOf(Object value) {
		return toString(value);
	}

	public static String toString(Object value) {
		if (value == null) {
			return VALUE_EMPTY;
		}
		return String.valueOf(value);
	}
}
