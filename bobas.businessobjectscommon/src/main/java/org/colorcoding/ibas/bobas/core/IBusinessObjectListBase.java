package org.colorcoding.ibas.bobas.core;

import java.util.List;

import org.colorcoding.ibas.bobas.util.Collection;

public interface IBusinessObjectListBase<E extends IBusinessObjectBase> extends List<E>, Collection<E> {
	/**
	 * 是否智能处理子项主键
	 * 
	 * @return
	 */
	boolean isSmartPrimaryKeys();

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
