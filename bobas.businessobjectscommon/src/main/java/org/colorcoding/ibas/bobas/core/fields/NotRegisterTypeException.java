package org.colorcoding.ibas.bobas.core.fields;


public class NotRegisterTypeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4500063563394473065L;

	public NotRegisterTypeException() {
		super();
	}

	public NotRegisterTypeException(String message) {
		super(message);
	}

	public NotRegisterTypeException(String message, Throwable exception) {
		super(message, exception);
	}

	public NotRegisterTypeException(Throwable exception) {
		super(exception);
	}

}
