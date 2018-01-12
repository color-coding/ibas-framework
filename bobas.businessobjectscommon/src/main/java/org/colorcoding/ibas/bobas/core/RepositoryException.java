package org.colorcoding.ibas.bobas.core;

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
