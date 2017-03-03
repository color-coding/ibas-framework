package org.colorcoding.ibas.bobas.db;


public class BOParsingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 53554104536888660L;

	public BOParsingException() {
	}

	public BOParsingException(String message, Throwable exception) {
		super(message, exception);
	}

	public BOParsingException(String message) {
		super(message);
	}

	public BOParsingException(Throwable exception) {
		super(exception);
	}
}
