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
	 * @return 属性路径
	 */
	String getPropertyPath();

	/**
	 * 设置-属性路径
	 *
	 * @param value 属性路径
	 */
	void setPropertyPath(String value);

	/**
	 * 设置-属性路径
	 *
	 * @param value 属性信息
	 */
	void setPropertyPath(IPropertyInfo<?> property);

	/**
	 * 获取-是否入口查询
	 *
	 * 入口查询：先查子项，再根据子项查父项
	 *
	 * @return true表示入口查询
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
	 * 设置-是否仅返回有子项的结果
	 *
	 * @param value true表示子项无结果时父项也不返回
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
	 * @return 克隆后的子项查询
	 */
	IChildCriteria clone();
}