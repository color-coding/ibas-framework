package org.colorcoding.ibas.bobas.common;

import org.colorcoding.ibas.bobas.core.ICloneable;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;

/**
 * 查询排序
 * 
 * @author Niuren.Zhu
 *
 */
public interface ISort extends ICloneable {
	/**
	 * 获取-排序的字段（属性）名
	 */
	String getAlias();

	/**
	 * 设置-排序的字段（属性）名
	 *
	 * @param value 字段名
	 */
	void setAlias(String value);

	/**
	 * 设置-排序的字段（属性）
	 *
	 * @param value 属性信息
	 */
	void setAlias(IPropertyInfo<?> property);

	/**
	 * 获取-排序方式
	 */
	SortType getSortType();

	/**
	 * 设置-排序方式
	 *
	 * @param value 排序类型
	 */
	void setSortType(SortType value);

	/**
	 * 克隆
	 *
	 * @return 克隆后的排序
	 */
	ISort clone();
}