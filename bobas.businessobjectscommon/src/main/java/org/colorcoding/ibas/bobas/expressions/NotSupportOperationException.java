package org.colorcoding.ibas.bobas.expressions;

public class NotSupportOperationException extends JudmentOperationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 407074477877621849L;

	/**
	 * 
	 */
	public NotSupportOperationException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NotSupportOperationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public NotSupportOperationException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public NotSupportOperationException(Throwable cause) {
		super(cause);
	}

}
