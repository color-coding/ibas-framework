package org.colorcoding.ibas.bobas.core;

/**
 * 属性变化监听
 */
public interface PropertyInfoRegisterListener {
	/**
	 * 属性信息注册
	 * 
	 * @param clazz        对象
	 * @param propertyInfo 属性
	 */
	void onRegistered(Class<?> clazz, IPropertyInfo<?> propertyInfo);

}
