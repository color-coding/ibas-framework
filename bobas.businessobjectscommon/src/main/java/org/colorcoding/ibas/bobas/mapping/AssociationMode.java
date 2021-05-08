package org.colorcoding.ibas.bobas.mapping;

/**
 * 关联方式
 * 
 * @author Niuren.Zhu
 *
 */
public enum AssociationMode {
	/**
	 * 一对〇
	 */
	ONE_TO_ZERO,
	/**
	 * 一对一
	 */
	ONE_TO_ONE,
	/**
	 * 一对多
	 */
	ONE_TO_MANY;

	public static AssociationMode valueOf(int value) {
		return values()[value];
	}

	public static AssociationMode valueOf(String value, boolean ignoreCase) {
		if (ignoreCase) {
			for (Object item : AssociationMode.class.getEnumConstants()) {
				if (item.toString().equalsIgnoreCase(value)) {
					return (AssociationMode) item;
				}
			}
		}
		return AssociationMode.valueOf(value);
	}
}
