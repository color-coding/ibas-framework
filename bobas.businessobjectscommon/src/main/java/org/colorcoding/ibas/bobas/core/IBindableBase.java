package org.colorcoding.ibas.bobas.core;

import java.beans.PropertyChangeListener;

/**
 * 支持绑定的对象
 */
public interface IBindableBase {
	/**
	 * 添加属性变化监听
	 */
	void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * 移出属性变化监听
	 */
	void removePropertyChangeListener(PropertyChangeListener listener);
}
