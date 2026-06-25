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
	 * 字节型：0
	 */
	public final static Byte BYTE_VALUE_ZERO = Byte.valueOf((byte) 0);

	/**
	 * 类型默认值
	 *
	 * @param numberType 数值类型；不支持时抛出IllegalArgumentException
	 * @return 对应类型的零值
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
		} else if (numberType == Byte.class) {
			return BYTE_VALUE_ZERO;
		}
		throw new IllegalArgumentException(Strings.format("type [%s] is not Number.", numberType.getName()));
	}

	/**
	 * 字符串是否为数字
	 *
	 * @param value 字符串；null或空返回false；纯负号或"-."返回false
	 * @return 有效数字格式返回true
	 */
	public static boolean isNumeric(String value) {
		if (value == null || value.isEmpty()) {
			return false;
		}
		boolean hasDot = false;
		boolean hasDigit = false;
		char cValue;
		for (int i = 0; i < value.length(); i++) {
			cValue = value.charAt(i);
			if (cValue == '-' && i == 0) {
				continue;
			}
			if (cValue == '.') {
				if (hasDot) {
					return false;
				}
				hasDot = true;
				continue;
			}
			if (!Character.isDigit(cValue)) {
				return false;
			}
			hasDigit = true;
		}
		return hasDigit;
	}

	/**
	 * 字符串是否为0
	 *
	 * @param value 字符串；null、空或非数字格式视为零返回true
	 * @return 值为零返回true
	 */
	public static boolean isZero(String value) {
		if (value == null || value.isEmpty()) {
			return true;
		}
		if (!isNumeric(value)) {
			return true;
		}
		char cValue;
		for (int i = 0; i < value.length(); i++) {
			cValue = value.charAt(i);
			if (cValue == '-' && i == 0) {
				continue;
			}
			if (cValue == '.') {
				continue;
			}
			if (cValue != '0') {
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
			return 0d;
		}
		return value.doubleValue();
	}

	public static double toDouble(Object value) {
		if (value == null) {
			return 0d;
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
			return 0f;
		}
		return value.floatValue();
	}

	public static float toFloat(Object value) {
		if (value == null) {
			return 0f;
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
			return 0;
		}
		return value.intValue();
	}

	public static int toInteger(Object value) {
		if (value == null) {
			return 0;
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
			return 0L;
		}
		return value.longValue();
	}

	public static long toLong(Object value) {
		if (value == null) {
			return 0L;
		}
		return DataConvert.convert(Long.class, value);
	}

	/**
	 * 判断是否大于（任一为null返回false）
	 *
	 * @param a 对象a；null返回false
	 * @param b 对象b；null返回false
	 * @return a大于b返回true；支持Comparable比较和字符串转换比较
	 */
	public static boolean greaterThan(Object a, Object b) {
		if (a == null || b == null) {
			return false;
		}
		if (a instanceof Comparable<?> && b instanceof Comparable<?>) {
			@SuppressWarnings("unchecked")
			Comparable<Object> A = (Comparable<Object>) a;
			@SuppressWarnings("unchecked")
			Comparable<Object> B = (Comparable<Object>) b;
			return A.compareTo(B) > 0;
		} else {
			String A = Strings.valueOf(a);
			String B = Strings.valueOf(b);
			return A.compareTo(B) > 0;
		}
	}

	/**
	 * 判断是否大于等于（任一为null返回false）
	 *
	 * @param a 对象a；null返回false
	 * @param b 对象b；null返回false
	 * @return a大于等于b返回true；支持Comparable比较和字符串转换比较
	 */
	public static boolean greaterEqual(Object a, Object b) {
		if (a == null || b == null) {
			return false;
		}
		if (a instanceof Comparable<?> && b instanceof Comparable<?>) {
			@SuppressWarnings("unchecked")
			Comparable<Object> A = (Comparable<Object>) a;
			@SuppressWarnings("unchecked")
			Comparable<Object> B = (Comparable<Object>) b;
			return A.compareTo(B) >= 0;
		} else {
			String A = Strings.valueOf(a);
			String B = Strings.valueOf(b);
			return A.compareTo(B) >= 0;
		}
	}

	/**
	 * 判断是否小于（任一为null返回false）
	 *
	 * @param a 对象a；null返回false
	 * @param b 对象b；null返回false
	 * @return a小于b返回true；支持Comparable比较和字符串转换比较
	 */
	public static boolean lessThan(Object a, Object b) {
		if (a == null || b == null) {
			return false;
		}
		if (a instanceof Comparable<?> && b instanceof Comparable<?>) {
			@SuppressWarnings("unchecked")
			Comparable<Object> A = (Comparable<Object>) a;
			@SuppressWarnings("unchecked")
			Comparable<Object> B = (Comparable<Object>) b;
			return A.compareTo(B) < 0;
		} else {
			String A = Strings.valueOf(a);
			String B = Strings.valueOf(b);
			return A.compareTo(B) < 0;
		}
	}

	/**
	 * 判断是否小于等于（任一为null返回false）
	 *
	 * @param a 对象a；null返回false
	 * @param b 对象b；null返回false
	 * @return a小于等于b返回true；支持Comparable比较和字符串转换比较
	 */
	public static boolean lessEqual(Object a, Object b) {
		if (a == null || b == null) {
			return false;
		}
		if (a instanceof Comparable<?> && b instanceof Comparable<?>) {
			@SuppressWarnings("unchecked")
			Comparable<Object> A = (Comparable<Object>) a;
			@SuppressWarnings("unchecked")
			Comparable<Object> B = (Comparable<Object>) b;
			return A.compareTo(B) <= 0;
		} else {
			String A = Strings.valueOf(a);
			String B = Strings.valueOf(b);
			return A.compareTo(B) <= 0;
		}
	}

	/**
	 * 判断是否相等（任一为null返回false）
	 *
	 * @param a 对象a；null返回false
	 * @param b 对象b；null返回false
	 * @return 相等返回true；支持Comparable比较和字符串转换比较
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

	// ==================== 数值计算 ====================

	/**
	 * 取绝对值
	 *
	 * @param value 数值；null返回0
	 * @return 绝对值
	 */
	public static int abs(Integer value) {
		return value == null ? 0 : Math.abs(value);
	}

	/**
	 * 取绝对值
	 *
	 * @param value 数值；null返回0
	 * @return 绝对值
	 */
	public static long abs(Long value) {
		return value == null ? 0L : Math.abs(value);
	}

	/**
	 * 取绝对值
	 *
	 * @param value 数值；null返回0
	 * @return 绝对值
	 */
	public static double abs(Double value) {
		return value == null ? 0d : Math.abs(value);
	}

	/**
	 * 取绝对值
	 *
	 * @param value 数值；null返回0
	 * @return 绝对值
	 */
	public static float abs(Float value) {
		return value == null ? 0f : Math.abs(value);
	}

	/**
	 * 取最大值
	 *
	 * @param values 数值数组；null跳过，全null返回0
	 * @return 最大值
	 */
	public static int max(Integer... values) {
		if (values == null || values.length == 0) {
			return 0;
		}
		Integer result = null;
		for (Integer value : values) {
			if (value == null) {
				continue;
			}
			if (result == null || value > result) {
				result = value;
			}
		}
		return result != null ? result : 0;
	}

	/**
	 * 取最大值
	 *
	 * @param values 数值数组；null跳过，全null返回0
	 * @return 最大值
	 */
	public static long max(Long... values) {
		if (values == null || values.length == 0) {
			return 0L;
		}
		Long result = null;
		for (Long value : values) {
			if (value == null) {
				continue;
			}
			if (result == null || value > result) {
				result = value;
			}
		}
		return result != null ? result : 0L;
	}

	/**
	 * 取最大值
	 *
	 * @param values 数值数组；null跳过，全null返回0
	 * @return 最大值
	 */
	public static double max(Double... values) {
		if (values == null || values.length == 0) {
			return 0d;
		}
		Double result = null;
		for (Double value : values) {
			if (value == null) {
				continue;
			}
			if (result == null || value > result) {
				result = value;
			}
		}
		return result != null ? result : 0d;
	}

	/**
	 * 取最小值
	 *
	 * @param values 数值数组；null跳过，全null返回0
	 * @return 最小值
	 */
	public static int min(Integer... values) {
		if (values == null || values.length == 0) {
			return 0;
		}
		Integer result = null;
		for (Integer value : values) {
			if (value == null) {
				continue;
			}
			if (result == null || value < result) {
				result = value;
			}
		}
		return result != null ? result : 0;
	}

	/**
	 * 取最小值
	 *
	 * @param values 数值数组；null跳过，全null返回0
	 * @return 最小值
	 */
	public static long min(Long... values) {
		if (values == null || values.length == 0) {
			return 0L;
		}
		Long result = null;
		for (Long value : values) {
			if (value == null) {
				continue;
			}
			if (result == null || value < result) {
				result = value;
			}
		}
		return result != null ? result : 0L;
	}

	/**
	 * 取最小值
	 *
	 * @param values 数值数组；null跳过，全null返回0
	 * @return 最小值
	 */
	public static double min(Double... values) {
		if (values == null || values.length == 0) {
			return 0d;
		}
		Double result = null;
		for (Double value : values) {
			if (value == null) {
				continue;
			}
			if (result == null || value < result) {
				result = value;
			}
		}
		return result != null ? result : 0d;
	}

	/**
	 * 取反（正变负，负变正）
	 *
	 * @param value 数值；null返回0
	 * @return 取反后的值
	 */
	public static int negate(Integer value) {
		return value == null ? 0 : -value;
	}

	/**
	 * 取反（正变负，负变正）
	 *
	 * @param value 数值；null返回0
	 * @return 取反后的值
	 */
	public static long negate(Long value) {
		return value == null ? 0L : -value;
	}

	/**
	 * 取反（正变负，负变正）
	 *
	 * @param value 数值；null返回0
	 * @return 取反后的值
	 */
	public static double negate(Double value) {
		return value == null ? 0d : -value;
	}

	// ==================== 判断 ====================

	/**
	 * 判断是否为正数
	 *
	 * @param value 数值
	 * @return 正数返回true
	 */
	public static boolean isPositive(Integer value) {
		return value != null && value > 0;
	}

	/**
	 * 判断是否为正数
	 *
	 * @param value 数值
	 * @return 正数返回true
	 */
	public static boolean isPositive(Long value) {
		return value != null && value > 0;
	}

	/**
	 * 判断是否为正数
	 *
	 * @param value 数值
	 * @return 正数返回true
	 */
	public static boolean isPositive(Double value) {
		return value != null && value > 0;
	}

	/**
	 * 判断是否为负数
	 *
	 * @param value 数值
	 * @return 负数返回true
	 */
	public static boolean isNegative(Integer value) {
		return value != null && value < 0;
	}

	/**
	 * 判断是否为负数
	 *
	 * @param value 数值
	 * @return 负数返回true
	 */
	public static boolean isNegative(Long value) {
		return value != null && value < 0;
	}

	/**
	 * 判断是否为负数
	 *
	 * @param value 数值
	 * @return 负数返回true
	 */
	public static boolean isNegative(Double value) {
		return value != null && value < 0;
	}

	/**
	 * 判断是否为非正数（零或负数）
	 *
	 * @param value 数值
	 * @return 零或负数返回true
	 */
	public static boolean isNotPositive(Integer value) {
		return value == null || value <= 0;
	}

	/**
	 * 判断是否为非正数（零或负数）
	 *
	 * @param value 数值
	 * @return 零或负数返回true
	 */
	public static boolean isNotPositive(Long value) {
		return value == null || value <= 0;
	}

	/**
	 * 判断是否为非正数（零或负数）
	 *
	 * @param value 数值
	 * @return 零或负数返回true
	 */
	public static boolean isNotPositive(Double value) {
		return value == null || value <= 0;
	}

	/**
	 * 判断是否为非负数（零或正数）
	 *
	 * @param value 数值
	 * @return 零或正数返回true
	 */
	public static boolean isNotNegative(Integer value) {
		return value == null || value >= 0;
	}

	/**
	 * 判断是否为非负数（零或正数）
	 *
	 * @param value 数值
	 * @return 零或正数返回true
	 */
	public static boolean isNotNegative(Long value) {
		return value == null || value >= 0;
	}

	/**
	 * 判断是否为非负数（零或正数）
	 *
	 * @param value 数值
	 * @return 零或正数返回true
	 */
	public static boolean isNotNegative(Double value) {
		return value == null || value >= 0;
	}

	/**
	 * 判断是否为零值
	 *
	 * @param value 数值；支持Integer/Long/Double/Float/Short/Byte/BigDecimal
	 * @return 值为零返回true；null返回false；非数值类型返回false
	 */
	public static boolean isZero(Object value) {
		if (value == null) {
			return false;
		}
		if (value instanceof Integer) {
			return ((Integer) value) == 0;
		} else if (value instanceof Long) {
			return ((Long) value) == 0L;
		} else if (value instanceof Double) {
			return ((Double) value) == 0d;
		} else if (value instanceof Float) {
			return ((Float) value) == 0f;
		} else if (value instanceof Short) {
			return ((Short) value) == 0;
		} else if (value instanceof Byte) {
			return ((Byte) value) == 0;
		} else if (value instanceof BigDecimal) {
			return Decimals.isZero((BigDecimal) value);
		}
		return false;
	}

	// ==================== 范围 ====================

	/**
	 * 判断是否在范围内（from <= value <= to）
	 *
	 * @param value 数值
	 * @param from  起始值（含）；null不限制
	 * @param to    截止值（含）；null不限制
	 * @return 在范围内返回true
	 */
	public static boolean between(Integer value, Integer from, Integer to) {
		if (value == null) {
			return false;
		}
		if (from != null && value < from) {
			return false;
		}
		if (to != null && value > to) {
			return false;
		}
		return true;
	}

	/**
	 * 判断是否在范围内（from <= value <= to）
	 *
	 * @param value 数值
	 * @param from  起始值（含）；null不限制
	 * @param to    截止值（含）；null不限制
	 * @return 在范围内返回true
	 */
	public static boolean between(Long value, Long from, Long to) {
		if (value == null) {
			return false;
		}
		if (from != null && value < from) {
			return false;
		}
		if (to != null && value > to) {
			return false;
		}
		return true;
	}

	/**
	 * 判断是否在范围内（from <= value <= to）
	 *
	 * @param value 数值
	 * @param from  起始值（含）；null不限制
	 * @param to    截止值（含）；null不限制
	 * @return 在范围内返回true
	 */
	public static boolean between(Double value, Double from, Double to) {
		if (value == null) {
			return false;
		}
		if (from != null && value < from) {
			return false;
		}
		if (to != null && value > to) {
			return false;
		}
		return true;
	}

	/**
	 * 限制数值范围（小于最小值取最小值，大于最大值取最大值）
	 *
	 * @param value 数值；null返回0
	 * @param min   最小值；null不限制
	 * @param max   最大值；null不限制
	 * @return 限制后的值
	 */
	public static int clamp(Integer value, Integer min, Integer max) {
		if (value == null) {
			return 0;
		}
		int result = value;
		if (min != null && result < min) {
			result = min;
		}
		if (max != null && result > max) {
			result = max;
		}
		return result;
	}

	/**
	 * 限制数值范围（小于最小值取最小值，大于最大值取最大值）
	 *
	 * @param value 数值；null返回0
	 * @param min   最小值；null不限制
	 * @param max   最大值；null不限制
	 * @return 限制后的值
	 */
	public static long clamp(Long value, Long min, Long max) {
		if (value == null) {
			return 0L;
		}
		long result = value;
		if (min != null && result < min) {
			result = min;
		}
		if (max != null && result > max) {
			result = max;
		}
		return result;
	}

	/**
	 * 限制数值范围（小于最小值取最小值，大于最大值取最大值）
	 *
	 * @param value 数值；null返回0
	 * @param min   最小值；null不限制
	 * @param max   最大值；null不限制
	 * @return 限制后的值
	 */
	public static double clamp(Double value, Double min, Double max) {
		if (value == null) {
			return 0d;
		}
		double result = value;
		if (min != null && result < min) {
			result = min;
		}
		if (max != null && result > max) {
			result = max;
		}
		return result;
	}
}
