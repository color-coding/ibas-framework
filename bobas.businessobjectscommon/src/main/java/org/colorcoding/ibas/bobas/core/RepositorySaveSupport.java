package org.colorcoding.ibas.bobas.core;

import java.util.ArrayList;

public class RepositorySaveSupport {

	public RepositorySaveSupport(Object source) {
		if (source == null) {
			throw new NullPointerException();
		}
		this.source = source;
	}

	private Object source = null;

	private ArrayList<RepositorySaveListener> listeners;

	public void registerListener(RepositorySaveListener listener) {
		if (listener == null) {
			return;
		}
		if (this.listeners == null) {
			this.listeners = new ArrayList<>(3);
		}
		synchronized (this.listeners) {
			// 检查是否已监听
			for (int i = 0; i < this.listeners.size(); i++) {
				if (this.listeners.get(i) == listener) {
					return;
				}
			}
			this.listeners.add(listener);
		}
	}

	public void removeListener(RepositorySaveListener listener) {
		if (listener == null) {
			return;
		}
		if (this.listeners == null) {
			return;
		}
		synchronized (this.listeners) {
			this.listeners.remove(listener);
		}
	}

	public void fireRepositorySave(RepositorySaveEventType type, IBusinessObjectBase bo) throws RepositoryException {
		if (this.listeners == null) {
			return;
		}
		// 复制监听数组，防止循环过程监听发生变化
		synchronized (this.listeners) {
			for (int i = 0; i < this.listeners.size(); i++) {
				RepositorySaveListener item = this.listeners.get(i);
				if (item == null) {
					continue;
				}
				item.repositorySave(new RepositorySaveEvent(this.source, type, bo));
			}
		}
	}

}
