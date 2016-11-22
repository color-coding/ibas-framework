package org.colorcoding.ibas.bobas.core;

/**
 * 序列化监听
 * 
 * @author Niuren.Zhu
 *
 */
public class MarshallerListener extends javax.xml.bind.Marshaller.Listener {

	@Override
	public void beforeMarshal(Object source) {
		super.beforeMarshal(source);
		if (source instanceof SerializationListener) {
			SerializationListener listener = (SerializationListener) source;
			listener.beforeMarshal();
		}
	}

	@Override
	public void afterMarshal(Object source) {
		super.afterMarshal(source);
		if (source instanceof SerializationListener) {
			SerializationListener listener = (SerializationListener) source;
			listener.afterMarshal();
		}
	}

}
