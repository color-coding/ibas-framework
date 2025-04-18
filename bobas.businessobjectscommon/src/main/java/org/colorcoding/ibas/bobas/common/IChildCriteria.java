package org.colorcoding.ibas.bobas.common;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;

/**
 * 子项查询
 * 
 * @author Niuren.Zhu
 *
 */
public interface IChildCriteria extends ICriteria {

	/**
	 * 获取-属性路径
	 * 
	 * @return
	 */
	String getPropertyPath();

	/**
	 * 设置-属性路径
	 * 
	 * @param value
	 */
	void setPropertyPath(String value);

	/**
	 * 设置-属性路径
	 * 
	 * @param value
	 */
	void setPropertyPath(IPropertyInfo<?> property);

	/**
	 * 获取-是否入口
	 * 
	 * 先查子项，再根据子项查父项
	 * 
	 * @return
	 */
	boolean isEntry();

	/**
	 * 设置-是否入口
	 */
	void setEntry(boolean value);

	/**
	 * 获取-是否父要求结果
	 * 
	 * 如果子项没有结果，则父项也不返回结果
	 * 
	 * @return
	 */
	boolean isOnlyHasChilds();

	/**
	 * 设置-是否父要求结果
	 * 
	 * @param value
	 */
	void setOnlyHasChilds(boolean value);

	/**
	 * 获取-是否包含不符合条件子项（仅查到结果时有效）
	 * 
	 * @return
	 */
	boolean isIncludingOtherChilds();

	/**
	 * 设置-是否包含不符合条件子项（仅查到结果时有效）
	 * 
	 * @param value
	 */
	void setIncludingOtherChilds(boolean value);

	/**
	 * 克隆
	 * 
	 * @return
	 */
	IChildCriteria clone();
}