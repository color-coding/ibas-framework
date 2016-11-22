package org.colorcoding.ibas.bobas.core;

public class SerializationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 639883472487495216L;

	public SerializationException() {
	}

	public SerializationException(String message) {
		super(message);
	}

	public SerializationException(Throwable cause) {
		super(cause);
	}

	public SerializationException(String message, Throwable cause) {
		super(message, cause);
	}

	public SerializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
