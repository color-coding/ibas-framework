package org.colorcoding.ibas.bobas.core;

/**
 * 事务类型
 * 
 * @author Niuren.Zhu
 *
 */
public enum SaveActionsType {
	/**
	 * 添加前
	 */
	before_adding,
	/**
	 * 添加后
	 */
	added,
	/**
	 * 更新前
	 */
	before_updating,
	/**
	 * 更新后
	 */
	updated,
	/**
	 * 删除前
	 */
	before_deleting,
	/**
	 * 删除后
	 */
	deleted;
}
