package org.colorcoding.ibas.bobas.common;

import java.util.Collection;

/**
 * 查询排序集合
 * 
 * @author Niuren.Zhu
 *
 */
public interface ISorts extends Collection<ISort> {
	/**
	 * 新建Sort
	 *
	 * @return
	 */
	ISort create();

	/**
	 * 添加集合
	 *
	 * @param collection
	 */
	// void addRange(Iterable<ISort> collection);

}