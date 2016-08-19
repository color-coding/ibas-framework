package org.colorcoding.ibas.bobas.data.measurement;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.mapping.DbValue;

/**
 * 体积单位
 */
@XmlType(name = "emVolumeUnit", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public enum emVolumeUnit {
	/**
	 * 立方毫米
	 */
	@DbValue(value = "mm³")
	cubic_millimeter(0),
	/**
	 * 立方厘米
	 */
	@DbValue(value = "cm³")
	cubic_centimeter(1),
	/**
	 * 立方米
	 */
	@DbValue(value = "m³")
	cubic_metre(2),
	/**
	 * 立方千米
	 */
	@DbValue(value = "km³")
	cubic_kilometer(5);

	private int intValue;
	private static java.util.HashMap<Integer, emVolumeUnit> mappings;

	private synchronized static java.util.HashMap<Integer, emVolumeUnit> getMappings() {
		if (mappings == null) {
			mappings = new java.util.HashMap<Integer, emVolumeUnit>();
		}
		return mappings;
	}

	private emVolumeUnit(int value) {
		intValue = value;
		emVolumeUnit.getMappings().put(value, this);
	}

	public int getValue() {
		return intValue;
	}

	public static emVolumeUnit forValue(int value) {
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
	 * @param toUnit
	 *            换算到单位
	 * @param value
	 *            值
	 * @param fromUnit
	 *            原始单位
	 * @return 目标单位的值
	 */
	public static double convert(emVolumeUnit toUnit, double value, emVolumeUnit fromUnit) {
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
	public static Decimal convert(emVolumeUnit toUnit, Decimal value, emVolumeUnit fromUnit) {
		int level = toUnit.getValue() - fromUnit.getValue();
		if (level > 0) {
			// 目标单位大
			return value.divide(getRate(level));
		} else if (level < 0) {
			// 目标单位小
			return value.multiply(getRate(level));
		}
		// 单位相同
		return value;
	}
}
