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

	public static double doubleOf(float value) {
		return value;
	}

	public static double doubleOf(int value) {
		return value;
	}

	public static double doubleOf(long value) {
		return value;
	}

	public static double doubleOf(short value) {
		return value;
	}

	public static double doubleOf(BigDecimal value) {
		if (value == null) {
			return Double.valueOf(Strings.VALUE_ZERO);
		}
		return value.doubleValue();
	}

	public static float floatOf(double value) {
		return (float) value;
	}

	public static float floatOf(int value) {
		return value;
	}

	public static float floatOf(long value) {
		return value;
	}

	public static float floatOf(short value) {
		return value;
	}

	public static float floatOf(BigDecimal value) {
		if (value == null) {
			return Float.valueOf(Strings.VALUE_ZERO);
		}
		return value.floatValue();
	}

	public static int intOf(double value) {
		return (int) value;
	}

	public static int intOf(float value) {
		return (int) value;
	}

	public static int intOf(long value) {
		return (int) value;
	}

	public static int intOf(short value) {
		return value;
	}

	public static int intOf(BigDecimal value) {
		if (value == null) {
			return Integer.valueOf(Strings.VALUE_ZERO);
		}
		return value.intValue();
	}

	public static long longOf(double value) {
		return (long) value;
	}

	public static long longOf(float value) {
		return (long) value;
	}

	public static long longOf(int value) {
		return value;
	}

	public static long longOf(short value) {
		return value;
	}

	public static long longOf(BigDecimal value) {
		if (value == null) {
			return Long.valueOf(Strings.VALUE_ZERO);
		}
		return value.longValue();
	}
}
