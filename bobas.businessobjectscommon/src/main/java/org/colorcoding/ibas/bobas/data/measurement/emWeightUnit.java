package org.colorcoding.ibas.bobas.data.measurement;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.mapping.Value;

/**
 * 重量单位
 */
@XmlType(name = "emWeightUnit", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public enum emWeightUnit {
	/**
	 * 克
	 */
	@Value("g")
	GRAM(0),
	/**
	 * 千克
	 */
	@Value("kg")
	KILOGRAM(1),
	/**
	 * 吨
	 */
	@Value("t")
	TON(2);

	private int intValue;
	private static java.util.HashMap<Integer, emWeightUnit> mappings;

	private synchronized static java.util.HashMap<Integer, emWeightUnit> getMappings() {
		if (mappings == null) {
			mappings = new java.util.HashMap<Integer, emWeightUnit>(3);
		}
		return mappings;
	}

	private emWeightUnit(int value) {
		intValue = value;
		emWeightUnit.getMappings().put(value, this);
	}

	public int getValue() {
		return intValue;
	}

	public static emWeightUnit valueOf(int value, boolean valueIndex) {
		if (valueIndex) {
			return getMappings().get(value);
		}
		return values()[value];
	}

	public static emWeightUnit valueOf(String value, boolean ignoreCase) {
		if (ignoreCase) {
			for (Object item : emWeightUnit.class.getEnumConstants()) {
				if (item.toString().equalsIgnoreCase(value)) {
					return (emWeightUnit) item;
				}
			}
		}
		return emWeightUnit.valueOf(value);
	}

	/**
	 * 基本转换率
	 */
	public final static int getRate(int level) {
		int rate = 1;
		for (int i = 0; i < Math.abs(level); i++) {
			// 每级别进率
			rate = rate * 1000;
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
	public static double convert(emWeightUnit toUnit, double value, emWeightUnit fromUnit) {
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
	public static BigDecimal convert(emWeightUnit toUnit, BigDecimal value, emWeightUnit fromUnit) {
		int level = toUnit.getValue() - fromUnit.getValue();
		if (level > 0) {
			// 目标单位大
			return Decimal.divide(value, Decimal.valueOf(level));
		} else if (level < 0) {
			// 目标单位小
			return Decimal.multiply(value, Decimal.valueOf(getRate(level)));
		}
		// 单位相同
		return value;
	}
}
