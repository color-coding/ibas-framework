package org.colorcoding.ibas.bobas.common;

public class Booleans {

	private Booleans() {
	}

	public static final boolean VALUE_TRUE = true;
	public static final boolean VALUE_FALSE = false;

	/**
	 * 类型默认值（false）
	 *
	 * @return Boolean.FALSE
	 */
	public static Boolean defaultValue() {
		return false;
	}

	/**
	 * 是否相等
	 *
	 * @param a 布尔值a；null返回false
	 * @param b 布尔值b；null返回false
	 * @return 相等返回true；任一为null返回false
	 */
	public static boolean equals(Boolean a, Boolean b) {
		if (a == null || b == null) {
			return false;
		}
		if (a == b) {
			return true;
		}
		return false;
	}
}
