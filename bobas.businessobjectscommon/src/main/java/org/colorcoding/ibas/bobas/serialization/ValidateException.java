package org.colorcoding.ibas.bobas.serialization;

public class ValidateException extends Exception {

	private static final long serialVersionUID = 639883472487495216L;

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
