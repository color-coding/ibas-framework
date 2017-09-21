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

	public boolean fireAction(SaveActionType type, IBusinessObjectBase bo) throws RepositoryException {
		if (this.listeners == null) {
			return true;
		}
		for (SaveActionListener item : this.listeners) {
			if (item == null) {
				continue;
			}
			boolean value = item.onActionEvent(new SaveActionEvent(this.source, type, bo));
			if (!value) {
				// 返回为false，直接退出
				return value;
			}
		}
		return true;
	}

}
