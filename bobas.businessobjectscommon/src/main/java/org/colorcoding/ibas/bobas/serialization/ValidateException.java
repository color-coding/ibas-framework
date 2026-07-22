package org.colorcoding.ibas.bobas.serialization;

import org.colorcoding.ibas.bobas.exception.BasException;

public class ValidateException extends BasException {

	private static final long serialVersionUID = 3301816157795784826L;

	public ValidateException() {
		super();
	}

	public ValidateException(String message) {
		super(message);
	}

	public ValidateException(Throwable cause) {
		super(cause);
	}

	public ValidateException(String message, Throwable cause) {
		super(message, cause);
	}

	public ValidateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
