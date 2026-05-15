package org.colorcoding.ibas.bobas.core;

import java.lang.annotation.Annotation;

/**
 * 属性信息
 */
public interface IPropertyInfo<P> {

	/**
	 * 属性名称
	 */
	String getName();

	/**
	 * 值的类型
	 */
	java.lang.Class<?> getValueType();

	/**
	 * 默认值
	 */
	P getDefaultValue();

	/**
	 * 索引
	 */
	int getIndex();

	/**
	 * 是否为主键
	 *
	 * @return true表示主键
	 */
	boolean isPrimaryKey();

	/**
	 * 是否为唯一键
	 *
	 * @return true表示唯一键
	 */
	boolean isUniqueKey();

	/**
	 * 获取属性上的注解
	 *
	 * @param <A>  注解类型
	 * @param type 注解类
	 * @return 注解实例，未找到返回null
	 */
	<A extends Annotation> A getAnnotation(Class<A> type);
}
