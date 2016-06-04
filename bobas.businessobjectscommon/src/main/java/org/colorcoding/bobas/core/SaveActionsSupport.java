package org.colorcoding.bobas.core;

public class SaveActionsSupport {

	public SaveActionsSupport(Object source) {
		if (source == null) {
			throw new NullPointerException();
		}
		this.source = source;
	}

	private static final int array_extension_step = 2;
	private Object source = null;
	private SaveActionsListener[] listeners;

	public void addListener(SaveActionsListener listener) {
		if (listener == null) {
			return;
		}
		if (this.listeners == null) {
			this.listeners = new SaveActionsListener[array_extension_step];
		}
		boolean done = false;
		for (int i = 0; i < this.listeners.length; i++) {
			if (this.listeners[i] == null) {
				this.listeners[i] = listener;
				done = true;
			}
		}
		if (!done) {
			// 数组不够
			SaveActionsListener[] tmps = new SaveActionsListener[this.listeners.length + array_extension_step];
			int i = 0;
			for (; i < this.listeners.length; i++) {
				tmps[i] = this.listeners[i];
			}
			tmps[i] = listener;
		}
	}

	public void removeListener(SaveActionsListener listener) {
		if (listener == null) {
			return;
		}
		if (this.listeners == null) {
			return;
		}
		for (int i = 0; i < this.listeners.length; i++) {
			if (this.listeners[i] == listener) {
				this.listeners[i] = null;
			}
		}
	}

	public boolean fireActions(SaveActionsType type, IBusinessObjectBase bo) {
		if (this.listeners == null) {
			return true;
		}
		for (SaveActionsListener item : this.listeners) {
			boolean value = item.actionsNotification(new SaveActionsEvent(this.source, type, bo));
			if (!value) {
				// 返回为false，直接退出
				return value;
			}
		}
		return true;
	}
}
