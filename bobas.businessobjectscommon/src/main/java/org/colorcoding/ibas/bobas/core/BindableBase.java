package org.colorcoding.ibas.bobas.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "BindableBase", namespace = MyConsts.NAMESPACE_BOBAS_CORE)
public abstract class BindableBase implements IBindableBase {

	transient private PropertyChangeSupport listeners;

	@Override
	public final void addPropertyChangeListener(PropertyChangeListener listener) {
		if (this.listeners == null) {
			this.listeners = new PropertyChangeSupport(this);
		}
		this.listeners.addPropertyChangeListener(listener);
	}

	@Override
	public final void removePropertyChangeListener(PropertyChangeListener listener) {
		if (this.listeners == null) {
			this.listeners = new PropertyChangeSupport(this);
		}
		this.listeners.removePropertyChangeListener(listener);
	}

	protected void firePropertyChange(String name, Object oldValue, Object newValue) {
		if (this.listeners == null) {
			return;
		}
		this.listeners.firePropertyChange(name, oldValue, newValue);
	}

}
