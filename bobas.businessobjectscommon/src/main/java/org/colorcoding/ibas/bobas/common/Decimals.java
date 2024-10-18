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

}
