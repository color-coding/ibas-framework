package org.colorcoding.ibas.bobas.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Decimals {

	private Decimals() {
	}

	/**
	 * 截取小数方式-运行时
	 */
	public static RoundingMode ROUNDING_MODE_DEFAULT = RoundingMode.HALF_UP;
	/**
	 * 小数位数-存储时
	 */
	public static int DECIMAL_PLACES_STORAGE = 6;

	/**
	 * 小数位数-运行时
	 */
	public static int DECIMAL_PLACES_RUNNING = 9;

	private static final BigDecimal zeroThroughTen[] = {
			// 0 - 10
			BigDecimal.valueOf(0l).setScale(DECIMAL_PLACES_STORAGE),
			BigDecimal.valueOf(1l).setScale(DECIMAL_PLACES_STORAGE),
			BigDecimal.valueOf(2l).setScale(DECIMAL_PLACES_STORAGE),
			BigDecimal.valueOf(3l).setScale(DECIMAL_PLACES_STORAGE),
			BigDecimal.valueOf(4l).setScale(DECIMAL_PLACES_STORAGE),
			BigDecimal.valueOf(5l).setScale(DECIMAL_PLACES_STORAGE),
			BigDecimal.valueOf(6l).setScale(DECIMAL_PLACES_STORAGE),
			BigDecimal.valueOf(7l).setScale(DECIMAL_PLACES_STORAGE),
			BigDecimal.valueOf(8l).setScale(DECIMAL_PLACES_STORAGE),
			BigDecimal.valueOf(9l).setScale(DECIMAL_PLACES_STORAGE),
			BigDecimal.valueOf(10l).setScale(DECIMAL_PLACES_STORAGE),

	};
	/**
	 * 数值-1
	 */
	public final static BigDecimal VALUE_ONE = zeroThroughTen[1];

	/**
	 * 数值-0
	 */
	public final static BigDecimal VALUE_ZERO = zeroThroughTen[0];

	/**
	 * 数值--1
	 */
	public final static BigDecimal VALUE_MINUS_ONE = VALUE_ONE.negate();

	/**
	 * 数值-整数型最大
	 */
	public final static BigDecimal VALUE_INTEGER_MAX_VALUE = valueOf(Integer.MAX_VALUE);
	/**
	 * 数值-整数型最小
	 */
	public final static BigDecimal VALUE_INTEGER_MIN_VALUE = valueOf(Integer.MIN_VALUE);
	/**
	 * 数值-长整数型最大
	 */
	public final static BigDecimal VALUE_LONG_MAX_VALUE = valueOf(Long.MAX_VALUE);
	/**
	 * 数值-长整数型最小
	 */
	public final static BigDecimal VALUE_LONG_MIN_VALUE = valueOf(Long.MIN_VALUE);
	/**
	 * 数值-短整数型最大
	 */
	public final static BigDecimal VALUE_SHORT_MAX_VALUE = valueOf(Short.MAX_VALUE);
	/**
	 * 数值-短整数型最小
	 */
	public final static BigDecimal VALUE_SHORT_MIN_VALUE = valueOf(Short.MIN_VALUE);

	/**
	 * 类型默认值（VALUE_ZERO）
	 *
	 * @return 零值BigDecimal
	 */
	public static BigDecimal defaultValue() {
		return VALUE_ZERO;
	}

	/**
	 * 转换值（0-10及-1使用缓存，其余保留小数点后9位）
	 *
	 * @param value 值
	 * @return BigDecimal；0-10及-1返回缓存实例
	 */
	public static BigDecimal valueOf(double value) {
		if (value >= 0 && value < zeroThroughTen.length) {
			return zeroThroughTen[(int) value];
		} else if (Double.compare(value, -1d) == 0) {
			return VALUE_MINUS_ONE;
		}
		BigDecimal decimal = new BigDecimal(value);
		if (decimal.scale() > DECIMAL_PLACES_RUNNING) {
			return decimal.setScale(DECIMAL_PLACES_RUNNING, ROUNDING_MODE_DEFAULT);
		}
		return decimal;
	}

	/**
	 * 转换值（0-10及-1使用缓存，其余保留小数点后9位）
	 *
	 * @param value 值
	 * @return BigDecimal
	 */
	public static BigDecimal valueOf(float value) {
		if (value >= 0 && value < zeroThroughTen.length) {
			return zeroThroughTen[(int) value];
		} else if (Float.compare(value, -1f) == 0) {
			return VALUE_MINUS_ONE;
		}
		BigDecimal decimal = new BigDecimal(value);
		if (decimal.scale() > DECIMAL_PLACES_RUNNING) {
			return decimal.setScale(DECIMAL_PLACES_RUNNING, ROUNDING_MODE_DEFAULT);
		}
		return decimal;
	}

	/**
	 * 转换值（0-10及-1使用缓存）
	 *
	 * @param value 值
	 * @return BigDecimal
	 */
	public static BigDecimal valueOf(long value) {
		if (value >= 0 && value < zeroThroughTen.length) {
			return zeroThroughTen[(int) value];
		} else if (Long.compare(value, -1l) == 0) {
			return VALUE_MINUS_ONE;
		}
		return new BigDecimal(value);
	}

	/**
	 * 转换值（0-10及-1使用缓存）
	 *
	 * @param value 值
	 * @return BigDecimal
	 */
	public static BigDecimal valueOf(int value) {
		if (value >= 0 && value < zeroThroughTen.length) {
			return zeroThroughTen[value];
		} else if (Integer.compare(value, -1) == 0) {
			return VALUE_MINUS_ONE;
		}
		return new BigDecimal(value);
	}

	/**
	 * 转换值（0-10使用缓存）
	 *
	 * @param value 值
	 * @return BigDecimal
	 */
	public static BigDecimal valueOf(short value) {
		if (value >= 0 && value < zeroThroughTen.length) {
			return zeroThroughTen[(int) value];
		} else {
			return valueOf((int) value);
		}
	}

	/**
	 * 转换值（字符串）
	 *
	 * @param value 字符串；null或空返回VALUE_ZERO
	 * @return BigDecimal
	 */
	public static BigDecimal valueOf(String value) {
		if (value == null || value.isEmpty()) {
			return VALUE_ZERO;
		}
		if (Numbers.isZero(value)) {
			return VALUE_ZERO;
		}
		// 分拆小数位
		int index = value.indexOf(".");
		if (index >= 0) {
			boolean zero = true;
			for (char item : value.substring(index + 1).toCharArray()) {
				if (item != '0') {
					zero = false;
					break;
				}
			}
			if (zero) {
				value = value.substring(0, index);
				// 小数点后都为零
				if (value.equals("0") || value.equals("-0") || value.isEmpty()) {
					return VALUE_ZERO;
				} else if (value.equals("1")) {
					return VALUE_ONE;
				} else if (value.equals("-1")) {
					return VALUE_MINUS_ONE;
				}
			}
		}
		return new BigDecimal(value);
	}

	/**
	 * 转换值（0-10使用缓存匹配）
	 *
	 * @param value 值；不在缓存范围则原样返回
	 * @return 缓存匹配的实例或原值
	 */
	public static BigDecimal valueOf(BigDecimal value) {
		for (BigDecimal item : zeroThroughTen) {
			if (equals(item, value)) {
				return item;
			}
		}
		return value;
	}

	/**
	 * 是否相等
	 *
	 * @param a 数值a；null返回false
	 * @param b 数值b；null返回false
	 * @return 值相等返回true；任一为null返回false
	 */
	public static boolean equals(BigDecimal a, BigDecimal b) {
		if (a == null || b == null) {
			return false;
		}
		if (a == b) {
			return true;
		}
		if (a.compareTo(b) == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 是否大于(a > b)
	 *
	 * @param a 数值a；null返回false
	 * @param b 数值b；null返回false
	 * @return a大于b返回true
	 */
	public static boolean greaterThan(BigDecimal a, BigDecimal b) {
		if (a == null || b == null) {
			return false;
		}
		return a.compareTo(b) > 0;
	}

	/**
	 * 是否大于等于(a >= b)
	 *
	 * @param a 数值a；null返回false
	 * @param b 数值b；null返回false
	 * @return a大于等于b返回true
	 */
	public static boolean greaterEqual(BigDecimal a, BigDecimal b) {
		if (a == null || b == null) {
			return false;
		}
		return a.compareTo(b) >= 0;
	}

	/**
	 * 是否小于(a < b)
	 *
	 * @param a 数值a；null返回false
	 * @param b 数值b；null返回false
	 * @return a小于b返回true
	 */
	public static boolean lessThan(BigDecimal a, BigDecimal b) {
		if (a == null || b == null) {
			return false;
		}
		return a.compareTo(b) < 0;
	}

	/**
	 * 是否小于等于(a <= b)
	 *
	 * @param a 数值a；null返回false
	 * @param b 数值b；null返回false
	 * @return a小于等于b返回true
	 */
	public static boolean lessEqual(BigDecimal a, BigDecimal b) {
		if (a == null || b == null) {
			return false;
		}
		return a.compareTo(b) <= 0;
	}

	/**
	 * 是否为零
	 *
	 * @param value 数值；不允许为null
	 * @return 值为零返回true
	 */
	public static boolean isZero(BigDecimal value) {
		if (VALUE_ZERO.equals(value)) {
			return true;
		} else if (VALUE_ZERO.compareTo(value) == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 截取小数位
	 *
	 * @param value 值；不允许为null
	 * @param scale 保留位数
	 * @param mode  截取方式
	 * @return 截取后的值
	 */
	public static BigDecimal round(BigDecimal value, int scale, RoundingMode mode) {
		return value.divide(VALUE_ONE, scale, mode);
	}

	/**
	 * 四舍五入小数位
	 *
	 * @param value 值
	 * @param scale 保留小数位数
	 * @return 截取后的值
	 */
	public static BigDecimal round(BigDecimal value, int scale) {
		return round(value, scale, ROUNDING_MODE_DEFAULT);
	}

	/**
	 * 四舍五入小数位（保留运行时位数，默认9位）
	 *
	 * @param value 值
	 * @return 截取后的值
	 */
	public static BigDecimal round(BigDecimal value) {
		return round(value, DECIMAL_PLACES_RUNNING, ROUNDING_MODE_DEFAULT);
	}

	/**
	 * 除法
	 *
	 * @param dividend 被除数
	 * @param divisor  除数
	 * @return 商（保留运行时小数位）
	 */
	public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
		return divide(dividend, divisor, DECIMAL_PLACES_RUNNING, ROUNDING_MODE_DEFAULT);
	}

	/**
	 * 除法
	 *
	 * @param dividend 被除数
	 * @param divisor  除数
	 * @param scale    保留小数位
	 * @return 商
	 */
	public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor, int scale) {
		return divide(dividend, divisor, scale, ROUNDING_MODE_DEFAULT);
	}

	/**
	 * 除法
	 *
	 * @param dividend      被除数
	 * @param divisor       除数
	 * @param scale         保留小数位
	 * @param roundingMode  进位方式
	 * @return 商
	 */
	public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor, int scale, RoundingMode roundingMode) {
		return dividend.divide(divisor, scale, roundingMode);
	}

	/**
	 * 除法（连除）
	 *
	 * @param dividend 被除数；null视为VALUE_ZERO
	 * @param divisors 除数数组（0个返回被除数；null跳过）
	 * @return 计算结果
	 */
	public static BigDecimal divide(BigDecimal dividend, BigDecimal... divisors) {
		if (dividend == null) {
			dividend = VALUE_ZERO;
		}
		if (divisors == null || divisors.length == 0) {
			return dividend;
		}
		for (BigDecimal divisor : divisors) {
			if (divisor == null) {
				continue;
			}
			dividend = divide(dividend, divisor);
		}
		return dividend;
	}

	/**
	 * 乘法（连乘）
	 *
	 * @param multiplicand 被乘数；null视为VALUE_ZERO
	 * @param multipliers  乘数数组（0个返回被乘数；null跳过）
	 * @return 计算结果
	 */
	public static BigDecimal multiply(BigDecimal multiplicand, BigDecimal... multipliers) {
		if (multiplicand == null) {
			multiplicand = VALUE_ZERO;
		}
		if (multipliers == null || multipliers.length == 0) {
			return multiplicand;
		}
		for (BigDecimal multiplier : multipliers) {
			if (multiplier == null) {
				continue;
			}
			multiplicand = multiplicand.multiply(multiplier);
		}
		return multiplicand;
	}

	/**
	 * 加法（连加）
	 *
	 * @param augend  被加数；null视为VALUE_ZERO
	 * @param addends 加数数组（0个返回被加数；null跳过）
	 * @return 计算结果
	 */
	public static BigDecimal add(BigDecimal augend, BigDecimal... addends) {
		if (augend == null) {
			augend = VALUE_ZERO;
		}
		if (addends == null || addends.length == 0) {
			return augend;
		}
		for (BigDecimal addend : addends) {
			if (addend == null) {
				continue;
			}
			augend = augend.add(addend);
		}
		return augend;
	}

	/**
	 * 减法（连减）
	 *
	 * @param subtrahend  被减数；null视为VALUE_ZERO
	 * @param subtractors 减数数组（0个返回被减数；null跳过）
	 * @return 计算结果
	 */
	public static BigDecimal subtract(BigDecimal subtrahend, BigDecimal... subtractors) {
		if (subtrahend == null) {
			subtrahend = VALUE_ZERO;
		}
		if (subtractors == null || subtractors.length == 0) {
			return subtrahend;
		}
		for (BigDecimal subtractor : subtractors) {
			if (subtractor == null) {
				continue;
			}
			subtrahend = subtrahend.subtract(subtractor);
		}
		return subtrahend;
	}

	/**
	 * 取绝对值
	 *
	 * @param value 数值；null返回VALUE_ZERO
	 * @return 绝对值
	 */
	public static BigDecimal abs(BigDecimal value) {
		if (value == null) {
			return VALUE_ZERO;
		}
		return value.abs();
	}

	/**
	 * 取反（正变负，负变正）
	 *
	 * @param value 数值；null返回VALUE_ZERO
	 * @return 取反后的值
	 */
	public static BigDecimal negate(BigDecimal value) {
		if (value == null) {
			return VALUE_ZERO;
		}
		return value.negate();
	}

	/**
	 * 取最大值
	 *
	 * @param values 数值数组；null跳过，全部为null返回VALUE_ZERO
	 * @return 最大值
	 */
	public static BigDecimal max(BigDecimal... values) {
		if (values == null || values.length == 0) {
			return VALUE_ZERO;
		}
		BigDecimal result = null;
		for (BigDecimal value : values) {
			if (value == null) {
				continue;
			}
			if (result == null || value.compareTo(result) > 0) {
				result = value;
			}
		}
		return result != null ? result : VALUE_ZERO;
	}

	/**
	 * 取最小值
	 *
	 * @param values 数值数组；null跳过，全部为null返回VALUE_ZERO
	 * @return 最小值
	 */
	public static BigDecimal min(BigDecimal... values) {
		if (values == null || values.length == 0) {
			return VALUE_ZERO;
		}
		BigDecimal result = null;
		for (BigDecimal value : values) {
			if (value == null) {
				continue;
			}
			if (result == null || value.compareTo(result) < 0) {
				result = value;
			}
		}
		return result != null ? result : VALUE_ZERO;
	}

	/**
	 * 取余（a % b）
	 *
	 * @param dividend 被除数；null视为VALUE_ZERO
	 * @param divisor  除数；null或为零返回被除数
	 * @return 余数
	 */
	public static BigDecimal remainder(BigDecimal dividend, BigDecimal divisor) {
		if (dividend == null) {
			return VALUE_ZERO;
		}
		if (divisor == null || isZero(divisor)) {
			return dividend;
		}
		return dividend.remainder(divisor);
	}

	/**
	 * 百分比（a占b的百分比，保留运行时小数位）
	 *
	 * @param value   数值
	 * @param total   总数；null或为零返回VALUE_ZERO
	 * @return 百分比值（0~1之间）
	 */
	public static BigDecimal percent(BigDecimal value, BigDecimal total) {
		if (value == null || total == null || isZero(total)) {
			return VALUE_ZERO;
		}
		return divide(value, total);
	}

	/**
	 * 按比例计算（value × rate）
	 *
	 * @param value 数值
	 * @param rate  比例
	 * @return 计算结果
	 */
	public static BigDecimal rate(BigDecimal value, BigDecimal rate) {
		if (value == null || rate == null) {
			return VALUE_ZERO;
		}
		return multiply(value, rate);
	}

	/**
	 * 判断是否为正数
	 *
	 * @param value 数值
	 * @return 正数返回true
	 */
	public static boolean isPositive(BigDecimal value) {
		return value != null && value.compareTo(VALUE_ZERO) > 0;
	}

	/**
	 * 判断是否为负数
	 *
	 * @param value 数值
	 * @return 负数返回true
	 */
	public static boolean isNegative(BigDecimal value) {
		return value != null && value.compareTo(VALUE_ZERO) < 0;
	}

	/**
	 * 判断是否为非正数（零或负数）
	 *
	 * @param value 数值
	 * @return 零或负数返回true
	 */
	public static boolean isNotPositive(BigDecimal value) {
		return value == null || value.compareTo(VALUE_ZERO) <= 0;
	}

	/**
	 * 判断是否为非负数（零或正数）
	 *
	 * @param value 数值
	 * @return 零或正数返回true
	 */
	public static boolean isNotNegative(BigDecimal value) {
		return value == null || value.compareTo(VALUE_ZERO) >= 0;
	}

	/**
	 * 判断数值是否近似（忽略微小差异）
	 * <p>
	 * 采用绝对误差与相对误差相结合的策略：
	 * <ul>
	 * <li>绝对误差阈值：10^(degree - digits)，用于接近0的情况（相对误差在接近0时无意义）</li>
	 * <li>相对误差阈值：10^(-digits)，用于常规比较</li>
	 * </ul>
	 * 两个阈值任一满足即视为近似。
	 *
	 * @param value1 值1；null返回false
	 * @param value2 值2；null返回false
	 * @param digits 小数位数（精度）
	 * @param degree 精确度（绝对误差比相对误差宽松的数量级，默认为1）
	 * @return 近似返回true
	 */
	public static boolean isApproximated(BigDecimal value1, BigDecimal value2, int digits, int degree) {
		if (value1 == null || value2 == null) {
			return false;
		}
		// 计算绝对差值
		BigDecimal diff = value1.subtract(value2).abs();
		// 绝对误差阈值：10^(degree - digits)，用于接近0的情况
		BigDecimal absEpsilon;
		int exponent = degree - digits;
		if (exponent >= 0) {
			absEpsilon = BigDecimal.TEN.pow(exponent);
		} else {
			absEpsilon = BigDecimal.ONE.divide(BigDecimal.TEN.pow(-exponent), DECIMAL_PLACES_RUNNING, ROUNDING_MODE_DEFAULT);
		}
		if (diff.compareTo(absEpsilon) <= 0) {
			return true;
		}
		// 相对误差阈值：10^(-digits)
		BigDecimal relEpsilon = BigDecimal.ONE.divide(BigDecimal.TEN.pow(digits), DECIMAL_PLACES_RUNNING, ROUNDING_MODE_DEFAULT);
		// 相对误差判断：diff / max(|value1|, |value2|) <= relEpsilon
		BigDecimal maxAbs = value1.abs().max(value2.abs());
		if (isZero(maxAbs)) {
			// maxAbs为零说明value1和value2均为零，此时diff也为零，已被绝对误差判断覆盖
			return false;
		}
		return diff.divide(maxAbs, DECIMAL_PLACES_RUNNING, ROUNDING_MODE_DEFAULT).compareTo(relEpsilon) <= 0;
	}

	/**
	 * 判断数值是否近似（忽略微小差异）
	 * <p>
	 * 默认精确度为1（绝对误差比相对误差宽松1个数量级）
	 *
	 * @param value1 值1；null返回false
	 * @param value2 值2；null返回false
	 * @param digits 小数位数（精度）
	 * @return 近似返回true
	 */
	public static boolean isApproximated(BigDecimal value1, BigDecimal value2, int digits) {
		return isApproximated(value1, value2, digits, 1);
	}

	/**
	 * 判断数值是否近似（忽略微小差异）
	 * <p>
	 * 默认小数位数为6（存储精度），精确度为1（绝对误差比相对误差宽松1个数量级）
	 *
	 * @param value1 值1；null返回false
	 * @param value2 值2；null返回false
	 * @return 近似返回true
	 */
	public static boolean isApproximated(BigDecimal value1, BigDecimal value2) {
		return isApproximated(value1, value2, DECIMAL_PLACES_STORAGE, 1);
	}
}