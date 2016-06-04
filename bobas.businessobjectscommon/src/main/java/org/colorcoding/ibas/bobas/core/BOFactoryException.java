package org.colorcoding.ibas.bobas.core;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;

@XmlType(name = "BOFactoryException", namespace = MyConsts.NAMESPACE_BOBAS_CORE)
public class BOFactoryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2779136620364469447L;

	public BOFactoryException() {
	}

	public BOFactoryException(String message) {
		super(message);
	}

	public BOFactoryException(String message, Throwable exception) {
		super(message, exception);
	}

	public BOFactoryException(Throwable exception) {
		super(exception);
	}
}
