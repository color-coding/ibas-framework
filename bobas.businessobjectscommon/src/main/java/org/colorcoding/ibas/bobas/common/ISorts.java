package org.colorcoding.ibas.bobas.common;

import org.colorcoding.ibas.bobas.data.IArrayList;

/**
 * 查询排序集合
 * 
 * @author Niuren.Zhu
 *
 */
public interface ISorts extends IArrayList<ISort> {
	/**
	 * 新建Sort
	 *
	 * @return
	 */
	ISort create();

	/**
	 * 获取查询排序
	 * 
	 * @param index 索引
	 * @return
	 */
	ISort get(int index);
}