package org.colorcoding.ibas.bobas.logics;


public class BusinessLogicsException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6654310086635040829L;

	public BusinessLogicsException() {
	}

	public BusinessLogicsException(String message) {
		super(message);
	}

	public BusinessLogicsException(Throwable cause) {
		super(cause);
	}

	public BusinessLogicsException(String message, Throwable cause) {
		super(message, cause);
	}

	public BusinessLogicsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
