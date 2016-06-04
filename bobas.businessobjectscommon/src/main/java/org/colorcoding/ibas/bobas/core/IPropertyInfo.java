package org.colorcoding.ibas.bobas.core;

/**
 * 属性信息 
 */
public interface IPropertyInfo<P> {

	/**
	 * 属性名称
	 */
	String getName();

	/**
	 * 索引
	 */
	int getIndex();

	/**
	 * 值的类型
	 */
	java.lang.Class<?> getValueType();

	/**
	 * 默认值
	 */
	P getDefaultValue();
}
