package org.colorcoding.bobas.repository;

import org.colorcoding.bobas.core.RepositoryException;

public class InvalidRepositoryException extends RepositoryException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2860909011993101779L;

	public InvalidRepositoryException() {
	}

	public InvalidRepositoryException(String message, Throwable exception) {
		super(message, exception);
	}

	public InvalidRepositoryException(String message) {
		super(message);
	}

	public InvalidRepositoryException(Throwable exception) {
		super(exception);
	}
}
