package org.colorcoding.ibas.bobas.common;

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
	 * 获取-排序方式
	 */
	SortType getSortType();

	/**
	 * 设置-排序方式
	 * 
	 * @param value
	 */
	void setSortType(SortType value);
}