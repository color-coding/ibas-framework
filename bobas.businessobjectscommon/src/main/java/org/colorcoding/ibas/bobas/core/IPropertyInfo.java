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
	 * @return
	 */
	boolean isPrimaryKey();

	/**
	 * 是否为唯一键
	 * 
	 * @return
	 */
	boolean isUniqueKey();

	/**
	 * 获取属性属性
	 * 
	 * @param <A>
	 * @param type
	 * @return
	 */
	<A extends Annotation> A getAnnotation(Class<A> type);
}
