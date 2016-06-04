package org.colorcoding.bobas.core;

import java.util.List;
import java.util.function.Predicate;

public interface IBusinessObjectListBase<E extends IBusinessObjectBase> extends List<E> {
	/**
	 * 创建新的元素实例
	 * 
	 * @return 新的元素实例
	 */
	E create();

	/**
	 * 第一个或默认值
	 * 
	 * @return 元素实例
	 */
	E firstOrDefault();

	/**
	 * 最后一个或默认值
	 * 
	 * @return 元素实例
	 */
	E lastOrDefault();

	/**
	 * 第一个或默认值
	 * 
	 * @return 元素实例
	 */
	E firstOrDefault(Predicate<? super E> filter);

	/**
	 * 最后一个或默认值
	 * 
	 * @return 元素实例
	 */
	E lastOrfault(Predicate<? super E> filter);

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
