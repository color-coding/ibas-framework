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

	transient private PropertyChangeSupport propertyChangeisteners = new PropertyChangeSupport(this);

	@Override
	public final void addPropertyChangeListener(PropertyChangeListener listener) {
		this.propertyChangeisteners.addPropertyChangeListener(listener);
	}

	@Override
	public final void removePropertyChangeListener(PropertyChangeListener listener) {
		this.propertyChangeisteners.removePropertyChangeListener(listener);
	}

	protected final void firePropertyChange(String name, Object oldValue, Object newValue) {
		this.propertyChangeisteners.firePropertyChange(name, oldValue, newValue);
	}

}
