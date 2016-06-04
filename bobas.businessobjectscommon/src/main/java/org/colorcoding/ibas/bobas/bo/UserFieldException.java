package org.colorcoding.ibas.bobas.bo;


public class UserFieldException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6653049461272212539L;

	public UserFieldException() {
	}

	public UserFieldException(String message) {
		super(message);
	}

	public UserFieldException(Throwable cause) {
		super(cause);
	}

	public UserFieldException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserFieldException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
