package org.colorcoding.ibas.bobas.data;

import java.util.function.Predicate;

/**
 * 列表
 * 
 * @author Niuren.Zhu
 *
 * @param <E>
 */
public interface IArrayList<E> extends java.util.List<E> {

	/***
	 * 返回第一个，不存在则返回默认
	 * 
	 * @return 元素实例
	 * 
	 */
	E firstOrDefault();

	/**
	 * 返回最后一个，不存在则返回默认
	 * 
	 * @return 元素实例
	 */
	E lastOrDefault();

	/***
	 * 返回第一个，不存在则返回默认
	 * 
	 * @param defalutValue 默认值
	 * @return
	 */
	E firstOrDefault(E defalutValue);

	/**
	 * 返回最后一个，不存在则返回默认
	 * 
	 * @param defalutValue 默认值
	 * @return 元素实例
	 */
	E lastOrDefault(E defalutValue);

	/**
	 * 返回第一个，不存在则返回默认
	 * 
	 * @param filter 过滤条件
	 * @return 元素实例
	 */
	E firstOrDefault(Predicate<? super E> filter);

	/**
	 * 返回最后一个，不存在则返回默认
	 * 
	 * @param filter 过滤条件
	 * @return 元素实例
	 */
	E lastOrDefault(Predicate<? super E> filter);

	/**
	 * 返回第一个，不存在则返回默认
	 * 
	 * @param filter       过滤条件
	 * @param defalutValue 默认值
	 * @return 元素实例
	 */
	E firstOrDefault(Predicate<? super E> filter, E defalutValue);

	/**
	 * 返回最后一个，不存在则返回默认
	 * 
	 * @param filter       过滤条件
	 * @param defalutValue 默认值
	 * @return 元素实例
	 */
	E lastOrDefault(Predicate<? super E> filter, E defalutValue);

	/**
	 * 符合条件的实例集合
	 * 
	 * @return 元素实例集合
	 */
	IArrayList<E> where(Predicate<? super E> filter);
}
