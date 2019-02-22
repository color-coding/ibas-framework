package org.colorcoding.ibas.bobas.data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * 数学计算
 * 
 * @author Niuren.Zhu
 *
 */
public final class Decimal {

	private Decimal() {
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
	public final static BigDecimal ONE = BigDecimal.ONE;

	/**
	 * 数值-0
	 */
	public final static BigDecimal ZERO = BigDecimal.ZERO;

	/**
	 * 数值--1
	 */
	public final static BigDecimal MINUS_ONE = ONE.negate();

	/**
	 * 转换值
	 * 
	 * @return
	 */
	public static BigDecimal valueOf() {
		return valueOf((String) null);
	}

	/**
	 * 转换值
	 * 
	 * @param value 值
	 * @return
	 */
	public static BigDecimal valueOf(double value) {
		if (Double.compare(value, 0d) == 0) {
			return ZERO;
		} else if (Double.compare(value, 1d) == 0) {
			return ONE;
		} else if (Double.compare(value, -1d) == 0) {
			return MINUS_ONE;
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
			return ZERO;
		} else if (Long.compare(value, 1l) == 0) {
			return ONE;
		} else if (Long.compare(value, -1l) == 0) {
			return MINUS_ONE;
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
			return ZERO;
		} else if (Integer.compare(value, 1) == 0) {
			return ONE;
		} else if (Integer.compare(value, -1) == 0) {
			return MINUS_ONE;
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
			return ZERO;
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
					return ZERO;
				} else if (value.equals("1")) {
					return ONE;
				} else if (value.equals("-1")) {
					return MINUS_ONE;
				}
			}
		}
		return new BigDecimal(value);
	}

	private static BigInteger INTEGER_ONE = BigInteger.ONE;
	private static BigInteger INTEGER_ZERO = BigInteger.ZERO;
	private static BigInteger INTEGER_MINUS_ONE = BigInteger.ONE.negate();

	/**
	 * 转换值
	 * 
	 * @param value 值
	 * @return
	 */
	public static BigDecimal valueOf(BigInteger value) {
		if (INTEGER_ZERO.equals(value) || INTEGER_ZERO.compareTo(value) == 0) {
			return ZERO;
		} else if (INTEGER_ONE.equals(value) || INTEGER_ONE.compareTo(value) == 0) {
			return ONE;
		} else if (INTEGER_MINUS_ONE.equals(value) || INTEGER_MINUS_ONE.compareTo(value) == 0) {
			return MINUS_ONE;
		}
		return Decimal.valueOf(value);
	}

	/**
	 * 是否为零
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isZero(BigDecimal value) {
		if (ZERO.equals(value)) {
			return true;
		} else if (ZERO.compareTo(value) == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 是否为零
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isZero(BigInteger value) {
		if (INTEGER_ZERO.equals(value)) {
			return true;
		} else if (INTEGER_ZERO.compareTo(value) == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 截取小数位
	 * 
	 * @param value 值
	 * @param scale 保留位数
	 * @param mode  截取方式
	 * @return
	 */
	public static BigDecimal round(BigDecimal value, int scale, RoundingMode mode) {
		return value.divide(ONE, scale, mode);
	}

	/**
	 * 四舍五入小数位
	 * 
	 * @param value 值
	 * @param scale 保留小数位数
	 * @return
	 */
	public static BigDecimal round(BigDecimal value, int scale) {
		return round(value, scale, ROUNDING_MODE_DEFAULT);
	}

	/**
	 * 四舍五入小数位
	 * 
	 * 保留运行时位数（默认9位）
	 * 
	 * @param value 值
	 * @return
	 */
	public static BigDecimal round(BigDecimal value) {
		return round(value, DECIMAL_PLACES_RUNNING, ROUNDING_MODE_DEFAULT);
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
