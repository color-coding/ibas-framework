package org.colorcoding.ibas.bobas.common;

public class Booleans {

	private Booleans() {
	}

	public static final boolean VALUE_TRUE = true;
	public static final boolean VALUE_FALSE = false;

	/**
	 * 类型默认值
	 * 
	 * @return
	 */
	public static Boolean defaultValue() {
		return false;
	}

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
