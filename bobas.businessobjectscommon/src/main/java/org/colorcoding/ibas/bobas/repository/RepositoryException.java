package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.exception.BasException;

public class RepositoryException extends BasException {

	private static final long serialVersionUID = -7114791616597264059L;

	public RepositoryException() {
		super();
	}

	public RepositoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RepositoryException(String message, Throwable cause) {
		super(message, cause);
	}

	public RepositoryException(String message) {
		super(message);
	}

	public RepositoryException(Throwable cause) {
		super(cause);
	}

}
