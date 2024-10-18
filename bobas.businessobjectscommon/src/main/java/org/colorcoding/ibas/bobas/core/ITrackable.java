package org.colorcoding.ibas.bobas.core;

/**
 * 状态跟踪
 */
public interface ITrackable {

	/**
	 * 是否修改过
	 * 
	 */
	boolean isDirty();

	/**
	 * 是否删除
	 * 
	 */
	boolean isDeleted();

	/**
	 * 是否为新建
	 * 
	 */
	boolean isNew();

	/**
	 * 是否为可保存的
	 * 
	 */
	boolean isSavable();

	/**
	 * 是否加载中
	 * 
	 * @return
	 */
	boolean isLoading();
}
