package org.colorcoding.ibas.bobas.core;

public class SaveActionsException extends RuntimeException {

	private static final long serialVersionUID = 6944430098732451483L;

	public SaveActionsException() {
	}

	public SaveActionsException(String message) {
		super(message);
	}

	public SaveActionsException(Throwable cause) {
		super(cause);
	}

	public SaveActionsException(String message, Throwable cause) {
		super(message, cause);
	}

	public SaveActionsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
