package org.colorcoding.ibas.bobas.core;

/**
 * 跟踪状态操作员
 */
public interface ITrackStatusOperator {
	/**
	 * 标记为未修改
	 */
	void markOld();

	/**
	 * 标记为新
	 */
	void markNew();

	/**
	 * 对象置为实际删除
	 */
	void markDeleted();

	/**
	 * 对象置为脏
	 */
	void markDirty();

	/**
	 * 对象置为未删除
	 */
	void markUnDeleted();

	/**
	 * 标记为未修改
	 * 
	 * @param 包括子项
	 */
	void markOld(boolean forced);

	/**
	 * 对象置为实际删除
	 * 
	 * @param 真是删除
	 */
	void markDeleted(boolean forced);

	/**
	 * 清理标记删除的数据
	 */
	void clearDeleted();
}
