package org.colorcoding.ibas.bobas.core;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;

@XmlType(name = "RepositoryException", namespace = MyConsts.NAMESPACE_BOBAS_CORE)
public class RepositoryException extends Exception {

	private static final long serialVersionUID = 8757920226663320025L;

	public RepositoryException() {
		super();
	}

	public RepositoryException(String message, Throwable exception) {
		super(message, exception);
	}

	public RepositoryException(String message) {
		super(message);
	}

	public RepositoryException(Throwable exception) {
		super(exception);
	}

}
