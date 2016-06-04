package org.colorcoding.ibas.bobas.db;


public class BOParseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 53554104536888660L;

	public BOParseException() {
	}

	public BOParseException(String message, Throwable exception) {
		super(message, exception);
	}

	public BOParseException(String message) {
		super(message);
	}

	public BOParseException(Throwable exception) {
		super(exception);
	}
}
