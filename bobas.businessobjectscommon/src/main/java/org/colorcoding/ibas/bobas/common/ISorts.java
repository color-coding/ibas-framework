package org.colorcoding.ibas.bobas.common;

import org.colorcoding.ibas.bobas.data.List;

/**
 * 查询排序集合
 * 
 * @author Niuren.Zhu
 *
 */
public interface ISorts extends List<ISort> {
	/**
	 * 新建排序
	 *
	 * @return 新建的排序
	 */
	ISort create();

	/**
	 * 获取排序
	 *
	 * @param index 索引
	 * @return 排序
	 */
	ISort get(int index);
}