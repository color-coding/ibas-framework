package org.colorcoding.ibas.bobas.data.measurement;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.mapping.Value;

/**
 * 长度单位
 */
@XmlType(name = "emLengthUnit", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public enum emLengthUnit {
	// 毫米
	@Value("mm")
	MILLIMETER(0),
	// 厘米
	@Value("cm")
	CENTIMETER(1),
	// 米
	@Value("m")
	METER(2),
	// 千米
	@Value("km")
	KILOMETER(5);

	private int intValue;
	private static java.util.HashMap<Integer, emLengthUnit> mappings;

	private synchronized static java.util.HashMap<Integer, emLengthUnit> getMappings() {
		if (mappings == null) {
			mappings = new java.util.HashMap<Integer, emLengthUnit>(4);
		}
		return mappings;
	}

	private emLengthUnit(int value) {
		intValue = value;
		emLengthUnit.getMappings().put(value, this);
	}

	public int getValue() {
		return intValue;
	}

	public static emLengthUnit valueOf(int value, boolean valueIndex) {
		if (valueIndex) {
			return getMappings().get(value);
		}
		return values()[value];
	}

	public static emLengthUnit valueOf(String value, boolean ignoreCase) {
		if (ignoreCase) {
			for (Object item : emLengthUnit.class.getEnumConstants()) {
				if (item.toString().equalsIgnoreCase(value)) {
					return (emLengthUnit) item;
				}
			}
		}
		return emLengthUnit.valueOf(value);
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
	public static double convert(emLengthUnit toUnit, double value, emLengthUnit fromUnit) {
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
	public static BigDecimal convert(emLengthUnit toUnit, BigDecimal value, emLengthUnit fromUnit) {
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
