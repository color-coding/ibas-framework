package org.colorcoding.ibas.bobas.core;

/**
 * 反序列化监听
 * 
 * @author Niuren.Zhu
 *
 */
public class UnmarshallerListener extends javax.xml.bind.Unmarshaller.Listener {

	@Override
	public void beforeUnmarshal(Object target, Object parent) {
		super.beforeUnmarshal(target, parent);
		if (target instanceof SerializationListener) {
			SerializationListener listener = (SerializationListener) target;
			listener.beforeUnmarshal(parent);
		}
	}

	@Override
	public void afterUnmarshal(Object target, Object parent) {
		super.afterUnmarshal(target, parent);
		if (target instanceof SerializationListener) {
			SerializationListener listener = (SerializationListener) target;
			listener.afterUnmarshal(parent);
		}
	}

}
