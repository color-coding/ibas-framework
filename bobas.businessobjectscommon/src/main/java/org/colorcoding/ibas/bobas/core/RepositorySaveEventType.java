package org.colorcoding.ibas.bobas.core;

/**
 * 仓库保存事件类型
 * 
 * @author Niuren.Zhu
 *
 */
public enum RepositorySaveEventType {
	/**
	 * 添加前
	 */
	BEFORE_ADDING,
	/**
	 * 添加后
	 */
	ADDED,
	/**
	 * 更新前
	 */
	BEFORE_UPDATING,
	/**
	 * 更新后
	 */
	UPDATED,
	/**
	 * 删除前
	 */
	BEFORE_DELETING,
	/**
	 * 删除后
	 */
	DELETED;

	public static RepositorySaveEventType valueOf(int value) {
		return values()[value];
	}

	public static RepositorySaveEventType valueOf(String value, boolean ignoreCase) {
		if (ignoreCase) {
			for (Object item : RepositorySaveEventType.class.getEnumConstants()) {
				if (item.toString().equalsIgnoreCase(value)) {
					return (RepositorySaveEventType) item;
				}
			}
		}
		return RepositorySaveEventType.valueOf(value);
	}
}
