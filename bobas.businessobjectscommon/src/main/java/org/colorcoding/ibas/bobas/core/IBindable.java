package org.colorcoding.ibas.bobas.core;

import java.beans.PropertyChangeListener;

/**
 * 支持绑定的对象
 */
public interface IBindable {
	/**
	 * 添加属性变化监听
	 *
	 * @param listener 监听器
	 */
	void registerListener(PropertyChangeListener listener);

	/**
	 * 移除属性变化监听
	 *
	 * @param listener 监听器
	 */
	void removeListener(PropertyChangeListener listener);
}
