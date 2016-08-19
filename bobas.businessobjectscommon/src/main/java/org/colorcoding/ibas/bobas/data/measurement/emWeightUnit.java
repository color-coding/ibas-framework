package org.colorcoding.ibas.bobas.data.measurement;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.mapping.DbValue;

/**
 * 重量单位
 */
@XmlType(name = "emWeightUnit", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public enum emWeightUnit {
	/**
	 * 克
	 */
	@DbValue(value = "g")
	gram(0),
	/**
	 * 千克
	 */
	@DbValue(value = "kg")
	kilogram(1),
	/**
	 * 吨
	 */
	@DbValue(value = "t")
	ton(2);

	private int intValue;
	private static java.util.HashMap<Integer, emWeightUnit> mappings;

	private synchronized static java.util.HashMap<Integer, emWeightUnit> getMappings() {
		if (mappings == null) {
			mappings = new java.util.HashMap<Integer, emWeightUnit>();
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

	public static emWeightUnit forValue(int value) {
		return getMappings().get(value);
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
	 * @param toUnit
	 *            换算到单位
	 * @param value
	 *            值
	 * @param fromUnit
	 *            原始单位
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
	 * @param toUnit
	 *            换算到单位
	 * @param value
	 *            值
	 * @param fromUnit
	 *            原始单位
	 * @return 目标单位的值
	 */
	public static Decimal convert(emWeightUnit toUnit, Decimal value, emWeightUnit fromUnit) {
		int level = toUnit.getValue() - fromUnit.getValue();
		if (level > 0) {
			// 目标单位大
			return value.divide(level);
		} else if (level < 0) {
			// 目标单位小
			return value.multiply(getRate(level));
		}
		// 单位相同
		return value;
	}
}
