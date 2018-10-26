package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.core.RepositoryException;

public class InvalidTokenException extends RepositoryException {

	private static final long serialVersionUID = 1420946876180404469L;

	public InvalidTokenException() {
		super();
	}

	public InvalidTokenException(String message, Throwable exception) {
		super(message, exception);

	}

	public InvalidTokenException(String message) {
		super(message);

	}

	public InvalidTokenException(Throwable exception) {
		super(exception);
	}
}
