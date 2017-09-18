package org.colorcoding.ibas.bobas.core;

/**
 * 事务类型
 * 
 * @author Niuren.Zhu
 *
 */
public enum SaveActionType {
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
}
