package org.colorcoding.ibas.bobas.common;

import java.math.BigDecimal;
import java.util.Objects;

import org.colorcoding.ibas.bobas.data.DataConvert;

public class Numbers {

	private Numbers() {
	}

	/**
	 * 整型：1
	 */
	public final static Integer INTEGER_VALUE_ONE = Integer.valueOf(1);

	/**
	 * 整型：0
	 */
	public final static Integer INTEGER_VALUE_ZERO = Integer.valueOf(0);

	/**
	 * 整型：-1
	 */
	public final static Integer INTEGER_VALUE_MINUS_ONE = Integer.valueOf(-1);

	/**
	 * 短整型：1
	 */
	public final static Short SHORT_VALUE_ONE = Short.valueOf((short) 1);

	/**
	 * 短整型：0
	 */
	public final static Short SHORT_VALUE_ZERO = Short.valueOf((short) 0);
	/**
	 * 长整型：0
	 */
	public final static Long LONG_VALUE_ZERO = Long.valueOf(0l);
	/**
	 * 双精度型：0
	 */
	public final static Double DOUBLE_VALUE_ZERO = Double.valueOf(0d);
	/**
	 * 浮点型：0
	 */
	public final static Float FLOAT_VALUE_ZERO = Float.valueOf(0f);

	/**
	 * 类型默认值
	 * 
	 * @return
	 */
	public static Number defaultValue(Class<?> numberType) {
		Objects.requireNonNull(numberType);
		if (numberType == Integer.class) {
			return INTEGER_VALUE_ZERO;
		} else if (numberType == Short.class) {
			return SHORT_VALUE_ZERO;
		} else if (numberType == Long.class) {
			return LONG_VALUE_ZERO;
		} else if (numberType == Double.class) {
			return DOUBLE_VALUE_ZERO;
		} else if (numberType == Float.class) {
			return FLOAT_VALUE_ZERO;
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

	public static double toDouble(Object value) {
		if (value == null) {
			return Double.valueOf(Strings.VALUE_ZERO);
		}
		return DataConvert.convert(Double.class, value);
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

	public static float toFloat(Object value) {
		if (value == null) {
			return Float.valueOf(Strings.VALUE_ZERO);
		}
		return DataConvert.convert(Float.class, value);
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

	public static int toInteger(Object value) {
		if (value == null) {
			return Integer.valueOf(Strings.VALUE_ZERO);
		}
		return DataConvert.convert(Integer.class, value);
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

	public static long toLong(Object value) {
		if (value == null) {
			return Long.valueOf(Strings.VALUE_ZERO);
		}
		return DataConvert.convert(Long.class, value);
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
