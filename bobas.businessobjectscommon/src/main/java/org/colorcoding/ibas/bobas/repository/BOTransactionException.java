package org.colorcoding.ibas.bobas.repository;


public class BOTransactionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8566095122136934954L;

	public BOTransactionException() {
	}

	public BOTransactionException(String message) {
		super(message);
	}

	public BOTransactionException(Throwable cause) {
		super(cause);
	}

	public BOTransactionException(String message, Throwable cause) {
		super(message, cause);
	}

	public BOTransactionException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
