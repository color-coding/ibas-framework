package org.colorcoding.ibas.bobas.data;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 集合列表
 *
 * @author Niuren.Zhu
 *
 * @param <E> 元素类型
 */
public interface List<E> extends java.util.List<E> {

	/***
	 * 返回第一个元素，列表为空则返回null
	 *
	 * @return 第一个元素或null
	 */
	E firstOrDefault();

	/**
	 * 返回最后一个元素，列表为空则返回null
	 *
	 * @return 最后一个元素或null
	 */
	E lastOrDefault();

	/***
	 * 返回第一个元素，列表为空则返回默认值
	 *
	 * @param defalutValue 默认值
	 * @return 第一个元素或默认值
	 */
	E firstOrDefault(E defalutValue);

	/**
	 * 返回最后一个元素，列表为空则返回默认值
	 *
	 * @param defalutValue 默认值
	 * @return 最后一个元素或默认值
	 */
	E lastOrDefault(E defalutValue);

	/**
	 * 返回第一个符合条件的元素，不存在则返回null
	 *
	 * @param filter 过滤条件
	 * @return 符合条件的第一个元素或null
	 */
	E firstOrDefault(Predicate<? super E> filter);

	/**
	 * 返回最后一个符合条件的元素，不存在则返回null
	 *
	 * @param filter 过滤条件
	 * @return 符合条件的最后一个元素或null
	 */
	E lastOrDefault(Predicate<? super E> filter);

	/**
	 * 返回第一个符合条件的元素，不存在则返回默认值
	 *
	 * @param filter       过滤条件
	 * @param defalutValue 默认值
	 * @return 符合条件的第一个元素或默认值
	 */
	E firstOrDefault(Predicate<? super E> filter, E defalutValue);

	/**
	 * 返回最后一个符合条件的元素，不存在则返回默认值
	 *
	 * @param filter       过滤条件
	 * @param defalutValue 默认值
	 * @return 符合条件的最后一个元素或默认值
	 */
	E lastOrDefault(Predicate<? super E> filter, E defalutValue);

	/**
	 * 符合条件的实例集合
	 *
	 * @param filter 过滤条件
	 * @return 符合条件的元素列表
	 */
	List<E> where(Predicate<? super E> filter);

	/**
	 * 判断是否包含符合条件的数据
	 *
	 * @param filter 过滤条件
	 * @return 是否包含
	 */
	boolean contains(Predicate<? super E> filter);

	/**
	 * 添加数组元素
	 *
	 * @param c 数组
	 * @return 是否添加成功（c为null返回false）
	 */
	boolean addAll(E[] c);

	/**
	 * 添加可迭代元素
	 *
	 * @param c 可迭代集合
	 * @return 是否添加成功（c为null返回false）
	 */
	boolean addAll(Iterable<? extends E> c);

	/**
	 * 集合求和（null值跳过）
	 *
	 * @param <T>      求和数据类型
	 * @param function 求值内容
	 * @return 求和结果
	 */
	<T extends Number> T sum(Function<E, T> function);

	/**
	 * 集合最大值（null值跳过，全为null则返回null）
	 *
	 * @param <T>      最大值数据类型
	 * @param function 求值内容
	 * @return 最大值或null
	 */
	<T extends Comparable<T>> T max(Function<E, T> function);

	/**
	 * 集合最小值（null值跳过，全为null则返回null）
	 *
	 * @param <T>      最小值数据类型
	 * @param function 求值内容
	 * @return 最小值或null
	 */
	<T extends Comparable<T>> T min(Function<E, T> function);
}