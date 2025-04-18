package org.colorcoding.ibas.bobas.data;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 集合列表
 * 
 * @author Niuren.Zhu
 *
 * @param <E>
 */
public interface List<E> extends java.util.List<E> {

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
	List<E> where(Predicate<? super E> filter);

	/**
	 * 判断是否包含符合条件的数据
	 * 
	 * @param filter 过滤条件
	 * @return
	 */
	boolean contains(Predicate<? super E> filter);

	/**
	 * 添加数组元素
	 * 
	 * @param c
	 * @return
	 */
	boolean addAll(E[] c);

	/**
	 * 添加数组元素
	 * 
	 * @param c
	 * @return
	 */
	boolean addAll(Iterable<? extends E> c);

	/**
	 * 集合求和
	 * 
	 * @param <T>      求和数据类型
	 * @param function 求值内容
	 * @return
	 */
	<T extends Number> T sum(Function<E, T> function);

	/**
	 * 集合最大值
	 * 
	 * @param <T>      最大值数据类型
	 * @param function 求值内容
	 * @return
	 */
	<T extends Comparable<T>> T max(Function<E, T> function);

	/**
	 * 集合最小值
	 * 
	 * @param <T>      最小值数据类型
	 * @param function 求值内容
	 * @return
	 */
	<T extends Comparable<T>> T min(Function<E, T> function);
}
