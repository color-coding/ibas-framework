package org.colorcoding.ibas.bobas.core;

import org.colorcoding.ibas.bobas.exception.BasRuntimeException;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class Bindable extends Serializable implements IBindable, ICloneable {

	private static final long serialVersionUID = 1L;

	private transient PropertyChangeSupport listeners;

	public final void registerListener(PropertyChangeListener listener) {
		if (this.listeners == null) {
			this.listeners = new PropertyChangeSupport(this);
		}
		this.listeners.addPropertyChangeListener(listener);
	}

	public final void removeListener(PropertyChangeListener listener) {
		if (this.listeners == null) {
			// 从未注册过，无需创建空容器
			return;
		}
		this.listeners.removePropertyChangeListener(listener);
	}

	protected void firePropertyChange(String name, Object oldValue, Object newValue) {
		if (this.listeners == null) {
			return;
		}
		this.listeners.firePropertyChange(name, oldValue, newValue);
	}

	@Override
	public Object clone() {
		try {
			Bindable nData = (Bindable) super.clone();
			nData.listeners = null;
			return nData;
		} catch (Exception e) {
			throw new BasRuntimeException(e.getMessage(), e);
		}
	}
}
