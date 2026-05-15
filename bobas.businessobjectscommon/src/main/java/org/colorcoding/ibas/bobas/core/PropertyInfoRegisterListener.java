package org.colorcoding.ibas.bobas.core;

/**
 * 属性变化监听
 */
public interface PropertyInfoRegisterListener {
	/**
	 * 属性注册通知
	 *
	 * @param clazz 已注册属性的类型
	 */
	void onRegistered(Class<?> clazz);

}
