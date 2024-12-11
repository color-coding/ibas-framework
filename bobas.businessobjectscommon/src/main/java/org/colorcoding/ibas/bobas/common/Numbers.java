package org.colorcoding.ibas.bobas.common;

import java.math.BigDecimal;
import java.util.Objects;

public class Numbers {

	private Numbers() {
	}

	/**
	 * 类型默认值
	 * 
	 * @return
	 */
	public static Number defaultValue(Class<?> numberType) {
		Objects.requireNonNull(numberType);
		if (numberType == Integer.class) {
			return Integer.valueOf(Strings.VALUE_ZERO);
		} else if (numberType == Short.class) {
			return Short.valueOf(Strings.VALUE_ZERO);
		} else if (numberType == Long.class) {
			return Short.valueOf(Strings.VALUE_ZERO);
		} else if (numberType == Double.class) {
			return Short.valueOf(Strings.VALUE_ZERO);
		} else if (numberType == Float.class) {
			return Short.valueOf(Strings.VALUE_ZERO);
		}
		throw new ClassCastException("is not Number.");
	}

	/**
	 * 判断字符串是否为数字
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isNumeric(String value) {
		if (value == null || value.isEmpty()) {
			return false;
		}
		for (int i = 0; i < value.length(); i++) {
			if (value.charAt(i) == '-' && i == 0) {
				continue;
			}
			if (value.charAt(i) == '.') {
				continue;
			}
			if (!Character.isDigit(value.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static double toDouble(float value) {
		return value;
	}

	public static double toDouble(int value) {
		return value;
	}

	public static double toDouble(long value) {
		return value;
	}

	public static double toDouble(short value) {
		return value;
	}

	public static double toDouble(BigDecimal value) {
		if (value == null) {
			return Double.valueOf(Strings.VALUE_ZERO);
		}
		return value.doubleValue();
	}

	public static float toFloat(double value) {
		return (float) value;
	}

	public static float toFloat(int value) {
		return value;
	}

	public static float toFloat(long value) {
		return value;
	}

	public static float toFloat(short value) {
		return value;
	}

	public static float toFloat(BigDecimal value) {
		if (value == null) {
			return Float.valueOf(Strings.VALUE_ZERO);
		}
		return value.floatValue();
	}

	public static int toInteger(double value) {
		return (int) value;
	}

	public static int toInteger(float value) {
		return (int) value;
	}

	public static int toInteger(long value) {
		return (int) value;
	}

	public static int toInteger(short value) {
		return value;
	}

	public static int toInteger(BigDecimal value) {
		if (value == null) {
			return Integer.valueOf(Strings.VALUE_ZERO);
		}
		return value.intValue();
	}

	public static long toLong(double value) {
		return (long) value;
	}

	public static long toLong(float value) {
		return (long) value;
	}

	public static long toLong(int value) {
		return value;
	}

	public static long toLong(short value) {
		return value;
	}

	public static long toLong(BigDecimal value) {
		if (value == null) {
			return Long.valueOf(Strings.VALUE_ZERO);
		}
		return value.longValue();
	}

	/**
	 * 判断是否相等（任意空值则不等）
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean equals(Object a, Object b) {
		if (a == null || b == null) {
			return false;
		}
		if (a == b) {
			return true;
		}
		if (a.equals(b)) {
			return true;
		}
		if (a instanceof Comparable<?> && b instanceof Comparable<?>) {
			@SuppressWarnings("unchecked")
			Comparable<Object> A = (Comparable<Object>) a;
			@SuppressWarnings("unchecked")
			Comparable<Object> B = (Comparable<Object>) b;
			if (A.compareTo(B) == 0) {
				return true;
			}
		} else {
			String A = Strings.valueOf(a);
			String B = Strings.valueOf(b);
			if (A.equals(B)) {
				return true;
			}
		}
		return false;
	}
}
