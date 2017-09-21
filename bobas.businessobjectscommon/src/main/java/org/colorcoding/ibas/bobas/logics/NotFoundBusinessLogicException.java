package org.colorcoding.ibas.bobas.logics;

public class NotFoundBusinessLogicException extends BusinessLogicException {

	private static final long serialVersionUID = -3828583628302548133L;

	public NotFoundBusinessLogicException() {
	}

	public NotFoundBusinessLogicException(String message) {
		super(message);
	}

	public NotFoundBusinessLogicException(Throwable cause) {
		super(cause);
	}

	public NotFoundBusinessLogicException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotFoundBusinessLogicException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
