package org.colorcoding.ibas.bobas.core;

import java.util.ArrayList;

public class SaveActionSupport {

	public SaveActionSupport(Object source) {
		if (source == null) {
			throw new NullPointerException();
		}
		this.source = source;
	}

	private Object source = null;
	private ArrayList<SaveActionListener> listeners;

	public void registerListener(SaveActionListener listener) {
		if (listener == null) {
			return;
		}
		if (this.listeners == null) {
			this.listeners = new ArrayList<>(2);
		}
		// 检查是否已监听
		for (int i = 0; i < this.listeners.size(); i++) {
			if (this.listeners.get(i) == listener) {
				return;
			}
		}
		this.listeners.add(listener);
	}

	public void removeListener(SaveActionListener listener) {
		if (listener == null) {
			return;
		}
		if (this.listeners == null) {
			return;
		}
		this.listeners.remove(listener);
	}

	public void fireAction(SaveActionType type, IBusinessObjectBase bo) throws RepositoryException {
		if (this.listeners == null) {
			return;
		}
		// 复制监听数组，防止循环过程监听发生变化
		SaveActionListener[] tmpListeners = this.listeners.toArray(new SaveActionListener[] {});
		for (SaveActionListener item : tmpListeners) {
			if (item == null) {
				continue;
			}
			item.onActionEvent(new SaveActionEvent(this.source, type, bo));
		}
	}

}
