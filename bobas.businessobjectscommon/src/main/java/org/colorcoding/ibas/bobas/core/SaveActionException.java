package org.colorcoding.ibas.bobas.core;

public class SaveActionException extends Exception {

	private static final long serialVersionUID = 6944430098732451483L;

	public SaveActionException() {
	}

	public SaveActionException(String message) {
		super(message);
	}

	public SaveActionException(Throwable cause) {
		super(cause);
	}

	public SaveActionException(String message, Throwable cause) {
		super(message, cause);
	}

	public SaveActionException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
