package org.colorcoding.ibas.bobas.logic;

public class BusinessLogicException extends RuntimeException {

	private static final long serialVersionUID = 6654310086635040829L;

	public BusinessLogicException() {
		super();
	}

	public BusinessLogicException(String message) {
		super(message);
	}

	public BusinessLogicException(Throwable cause) {
		super(cause);
	}

	public BusinessLogicException(String message, Throwable cause) {
		super(message, cause);
	}

	public BusinessLogicException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
