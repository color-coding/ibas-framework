package org.colorcoding.ibas.bobas.approval;

/**
 * 属性取值方式
 * 
 * @author Niuren.Zhu
 *
 */
public enum ValueMode {
	/**
	 * 属性取值
	 */
	PROPERTY,
	/**
	 * 数据库字段取值
	 */
	DB_FIELD,
	/**
	 * 输入值
	 */
	INPUT,
	/**
	 * sql脚本取值
	 */
	SQL_SCRIPT;

	public static ValueMode valueOf(int value) {
		return values()[value];
	}

	public static ValueMode valueOf(String value, boolean ignoreCase) {
		if (ignoreCase) {
			for (Object item : ValueMode.class.getEnumConstants()) {
				if (item.toString().equalsIgnoreCase(value)) {
					return (ValueMode) item;
				}
			}
		}
		return ValueMode.valueOf(value);
	}
}
