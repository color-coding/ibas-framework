package org.colorcoding.ibas.bobas.core;

import org.colorcoding.ibas.bobas.data.List;

public interface IBusinessObjectsBase<E extends IBusinessObjectBase> extends List<E> {
	/**
	 * 创建新的元素实例
	 * 
	 * @return 新的元素实例
	 */
	E create();

	/**
	 * 添加元素
	 * 
	 * @param item
	 * @return
	 */
	boolean add(E item);

	/**
	 * 添加集合
	 * 
	 * @param items
	 * @return
	 */
	boolean addAll(Iterable<? extends E> items);
}
