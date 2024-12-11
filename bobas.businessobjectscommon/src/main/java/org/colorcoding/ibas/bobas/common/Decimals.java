package org.colorcoding.ibas.bobas.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Decimals {

	private Decimals() {
	}

	/**
	 * 小数位数-存储时
	 */
	public static int DECIMAL_PLACES_STORAGE = 6;

	/**
	 * 小数位数-运行时
	 */
	public static int DECIMAL_PLACES_RUNNING = 9;

	/**
	 * 截取小数方式-运行时
	 */
	public static RoundingMode ROUNDING_MODE_DEFAULT = RoundingMode.HALF_UP;

	/**
	 * 数值-1
	 */
	public final static BigDecimal VALUE_ONE = BigDecimal.ONE;

	/**
	 * 数值-0
	 */
	public final static BigDecimal VALUE_ZERO = BigDecimal.ZERO;

	/**
	 * 数值--1
	 */
	public final static BigDecimal VALUE_MINUS_ONE = VALUE_ONE.negate();

	/**
	 * 类型默认值
	 * 
	 * @return
	 */
	public static BigDecimal defaultValue() {
		return VALUE_ZERO;
	}

	/**
	 * 转换值
	 * 
	 * @param value 值
	 * @return
	 */
	public static BigDecimal valueOf(double value) {
		if (Double.compare(value, 0d) == 0) {
			return VALUE_ZERO;
		} else if (Double.compare(value, 1d) == 0) {
			return VALUE_ONE;
		} else if (Double.compare(value, -1d) == 0) {
			return VALUE_MINUS_ONE;
		}
		return new BigDecimal(value);
	}

	/**
	 * 转换值
	 * 
	 * @param value 值
	 * @return
	 */
	public static BigDecimal valueOf(long value) {
		if (Long.compare(value, 0l) == 0) {
			return VALUE_ZERO;
		} else if (Long.compare(value, 1l) == 0) {
			return VALUE_ONE;
		} else if (Long.compare(value, -1l) == 0) {
			return VALUE_MINUS_ONE;
		}
		return new BigDecimal(value);
	}

	/**
	 * 转换值
	 * 
	 * @param value 值
	 * @return
	 */
	public static BigDecimal valueOf(int value) {
		if (Integer.compare(value, 0) == 0) {
			return VALUE_ZERO;
		} else if (Integer.compare(value, 1) == 0) {
			return VALUE_ONE;
		} else if (Integer.compare(value, -1) == 0) {
			return VALUE_MINUS_ONE;
		}
		return new BigDecimal(value);
	}

	/**
	 * 转换值
	 * 
	 * @param value 值
	 * @return
	 */
	public static BigDecimal valueOf(short value) {
		return valueOf((int) value);
	}

	/**
	 * 转换值
	 * 
	 * @param value 值
	 * @return
	 */
	public static BigDecimal valueOf(String value) {
		if (value == null || value.isEmpty()) {
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
				if (value.equals("0") || value.isEmpty()) {
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
	 * 是否相等
	 * 
	 * @param a
	 * @param b
	 * @return
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
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean graterThan(BigDecimal a, BigDecimal b) {
		if (a == null || b == null) {
			return false;
		}
		return a.compareTo(b) > 0;
	}

	/**
	 * 是否小于(a < b)
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean lessThan(BigDecimal a, BigDecimal b) {
		if (a == null || b == null) {
			return false;
		}
		return a.compareTo(b) < 0;
	}

	/**
	 * 是否为零
	 * 
	 * @param value
	 * @return
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
	 * 除法
	 * 
	 * @param dividend 被除数
	 * @param divisor  除数
	 * @return
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
	 * @return
	 */
	public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor, int scale) {
		return divide(dividend, divisor, scale, ROUNDING_MODE_DEFAULT);
	}

	/**
	 * 除法
	 * 
	 * @param dividend 被除数
	 * @param divisor  除数
	 * @param scale    保留小数位
	 * @param rounding 进位方式
	 * @return
	 */
	public static BigDecimal divide(BigDecimal dividend, BigDecimal divisor, int scale, RoundingMode roundingMode) {
		return dividend.divide(divisor, scale, roundingMode);
	}

	/**
	 * 除法
	 * 
	 * @param dividend 被除数
	 * @param divisors 乘除数组（0个，返回被除数）
	 * @return
	 */
	public static BigDecimal divide(BigDecimal dividend, BigDecimal... divisors) {
		if (divisors == null || divisors.length == 0) {
			return dividend;
		}
		for (BigDecimal divisor : divisors) {
			dividend = divide(dividend, divisor);
		}
		return dividend;
	}

	/**
	 * 乘法
	 * 
	 * @param multiplicand 被乘数
	 * @param multipliers  乘数数组（0个，返回被乘数）
	 * @return
	 */
	public static BigDecimal multiply(BigDecimal multiplicand, BigDecimal... multipliers) {
		if (multipliers == null || multipliers.length == 0) {
			return multiplicand;
		}
		for (BigDecimal multiplier : multipliers) {
			multiplicand = multiplicand.multiply(multiplier);
		}
		return multiplicand;
	}

	/**
	 * 加法
	 * 
	 * @param augend  被加数
	 * @param addends 加数组（0个，返回被加数）
	 * @return
	 */
	public static BigDecimal add(BigDecimal augend, BigDecimal... addends) {
		if (addends == null || addends.length == 0) {
			return augend;
		}
		for (BigDecimal addend : addends) {
			augend = augend.add(addend);
		}
		return augend;
	}

	/**
	 * 减法
	 * 
	 * @param subtrahend 被减数
	 * @param addends    减数组（0个，返回被减数）
	 * @return
	 */
	public static BigDecimal subtract(BigDecimal subtrahend, BigDecimal... subtractors) {
		if (subtractors == null || subtractors.length == 0) {
			return subtrahend;
		}
		for (BigDecimal subtractor : subtractors) {
			subtrahend = subtrahend.subtract(subtractor);
		}
		return subtrahend;
	}
}
