package org.colorcoding.ibas.bobas.mapping;

/**
 * 复制字段类型
 * 
 * @author Niuren.Zhu
 *
 */
public enum ComplexFieldType {
	/**
	 * 度量（数值与单位的组合）
	 */
	MEASUREMENT;

	public static ComplexFieldType valueOf(int value) {
		return values()[value];
	}

	public static ComplexFieldType valueOf(String value, boolean ignoreCase) {
		if (ignoreCase) {
			for (Object item : ComplexFieldType.class.getEnumConstants()) {
				if (item.toString().equalsIgnoreCase(value)) {
					return (ComplexFieldType) item;
				}
			}
		}
		return ComplexFieldType.valueOf(value);
	}
}
