package org.colorcoding.bobas.core;


public class ClonerException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 639883472487495216L;

	public ClonerException() {
	}

	public ClonerException(String message) {
		super(message);
	}

	public ClonerException(Throwable cause) {
		super(cause);
	}

	public ClonerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ClonerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
