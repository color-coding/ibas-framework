package org.colorcoding.ibas.bobas.core;

/**
 * 状态跟踪
 */
public interface ITrackable {

	/**
	 * 是否为可保存的
	 *
	 * @return true表示可保存
	 */
	boolean isSavable();

	/**
	 * 是否修改过
	 *
	 * @return true表示已修改
	 */
	boolean isDirty();

	/**
	 * 是否删除
	 *
	 * @return true表示已标记删除
	 */
	boolean isDeleted();

	/**
	 * 是否为新建
	 *
	 * @return true表示新建对象
	 */
	boolean isNew();

	/**
	 * 是否加载中
	 *
	 * @return true表示正在加载数据
	 */
	boolean isLoading();

	/**
	 * 设置是否加载中
	 *
	 * @param value true表示正在加载数据
	 */
	void setLoading(boolean value);
}
