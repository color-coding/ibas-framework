package org.colorcoding.ibas.bobas.logics;

public class NotFoundBusinessLogicsException extends BusinessLogicsException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3828583628302548133L;

	public NotFoundBusinessLogicsException() {
	}

	public NotFoundBusinessLogicsException(String message) {
		super(message);
	}

	public NotFoundBusinessLogicsException(Throwable cause) {
		super(cause);
	}

	public NotFoundBusinessLogicsException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotFoundBusinessLogicsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
