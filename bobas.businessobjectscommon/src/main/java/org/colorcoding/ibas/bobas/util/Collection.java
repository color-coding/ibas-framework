package org.colorcoding.ibas.bobas.util;

import java.util.function.Predicate;

public interface Collection<E> extends java.util.Collection<E> {

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
}
