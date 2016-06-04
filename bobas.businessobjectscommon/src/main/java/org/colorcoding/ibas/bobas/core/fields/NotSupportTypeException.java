package org.colorcoding.ibas.bobas.core.fields;


public class NotSupportTypeException extends RuntimeException {
	/**
		 * 
		 */
	private static final long serialVersionUID = -8613509235684988507L;

	public NotSupportTypeException() {
		super();
	}

	public NotSupportTypeException(String message) {
		super(message);
	}

	public NotSupportTypeException(String message, Throwable exception) {
		super(message, exception);
	}

	public NotSupportTypeException(Throwable exception) {
		super(exception);
	}

}
