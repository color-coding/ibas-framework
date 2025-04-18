package org.colorcoding.ibas.bobas.common;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;

/**
 * 查询排序
 * 
 * @author Niuren.Zhu
 *
 */
public interface ISort {
	/**
	 * 获取-排序的字段（属性）名
	 */
	String getAlias();

	/**
	 * 设置-排序的字段（属性）名
	 * 
	 * @param value
	 */
	void setAlias(String value);

	/**
	 * 设置-排序的字段（属性）
	 * 
	 * @param value 名
	 */
	void setAlias(IPropertyInfo<?> property);

	/**
	 * 获取-排序方式
	 */
	SortType getSortType();

	/**
	 * 设置-排序方式
	 * 
	 * @param value
	 */
	void setSortType(SortType value);

	/**
	 * 克隆
	 * 
	 * @return
	 */
	ISort clone();
}