package org.colorcoding.ibas.bobas.data.measurement;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.mapping.Value;

/**
 * 比例单位
 */
@XmlType(name = "emPercentUnit", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public enum emPercentUnit {
	// 百分百
	@Value("%")
	HUNDRED_PERCENT(0);

	private int intValue;
	private static java.util.HashMap<Integer, emPercentUnit> mappings;

	private synchronized static java.util.HashMap<Integer, emPercentUnit> getMappings() {
		if (mappings == null) {
			mappings = new java.util.HashMap<Integer, emPercentUnit>();
		}
		return mappings;
	}

	private emPercentUnit(int value) {
		intValue = value;
		emPercentUnit.getMappings().put(value, this);
	}

	public int getValue() {
		return intValue;
	}

	public static emPercentUnit forValue(int value) {
		return getMappings().get(value);
	}

	/**
	 * 基本转换率
	 */
	public final static int getRate(int level) {
		int rate = 1;
		for (int i = 0; i < Math.abs(level); i++) {
			// 每级别进率
			rate = rate * 10;
		}
		return rate;
	}

	/**
	 * 换算
	 * 
	 * @param toUnit   换算到单位
	 * @param value    值
	 * @param fromUnit 原始单位
	 * @return 目标单位的值
	 */
	public static double convert(emPercentUnit toUnit, double value, emPercentUnit fromUnit) {
		int level = toUnit.getValue() - fromUnit.getValue();
		if (level > 0) {
			// 目标单位大
			return value / getRate(level);
		} else if (level < 0) {
			// 目标单位小
			return value * getRate(level);
		}
		// 单位相同
		return value;
	}

	/**
	 * 换算
	 * 
	 * @param toUnit   换算到单位
	 * @param value    值
	 * @param fromUnit 原始单位
	 * @return 目标单位的值
	 */
	public static BigDecimal convert(emPercentUnit toUnit, BigDecimal value, emPercentUnit fromUnit) {
		int level = toUnit.getValue() - fromUnit.getValue();
		if (level > 0) {
			// 目标单位大
			return Decimal.divide(value, Decimal.valueOf(getRate(level)));
		} else if (level < 0) {
			// 目标单位小
			return Decimal.multiply(value, Decimal.valueOf(getRate(level)));
		}
		// 单位相同
		return value;
	}
}
