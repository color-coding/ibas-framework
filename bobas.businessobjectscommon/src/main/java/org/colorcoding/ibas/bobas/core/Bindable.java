package org.colorcoding.ibas.bobas.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class Bindable extends Serializable implements IBindable {

	private static final long serialVersionUID = 1L;

	transient private PropertyChangeSupport listeners;


	public final void registerListener(PropertyChangeListener listener) {
		if (this.listeners == null) {
			this.listeners = new PropertyChangeSupport(this);
		}
		this.listeners.addPropertyChangeListener(listener);
	}


	public final void removeListener(PropertyChangeListener listener) {
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
