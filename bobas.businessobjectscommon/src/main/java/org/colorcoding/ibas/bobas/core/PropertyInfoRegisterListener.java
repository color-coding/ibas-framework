package org.colorcoding.ibas.bobas.core;

/**
 * 属性变化监听
 */
public interface PropertyInfoRegisterListener {
	/**
	 * 对象信息注册
	 * 
	 * @param clazz 对象
	 */
	void onRegistered(Class<?> clazz);

}
