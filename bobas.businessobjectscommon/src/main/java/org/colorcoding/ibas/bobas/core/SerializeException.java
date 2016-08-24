package org.colorcoding.ibas.bobas.core;

public class SerializeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 639883472487495216L;

	public SerializeException() {
	}

	public SerializeException(String message) {
		super(message);
	}

	public SerializeException(Throwable cause) {
		super(cause);
	}

	public SerializeException(String message, Throwable cause) {
		super(message, cause);
	}

	public SerializeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
