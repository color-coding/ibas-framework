package org.colorcoding.ibas.bobas.db;

public class ParsingException extends Exception {

	private static final long serialVersionUID = 53554104536888660L;

	public ParsingException() {
		super();
	}

	public ParsingException(String message, Throwable exception) {
		super(message, exception);
	}

	public ParsingException(String message) {
		super(message);
	}

	public ParsingException(Throwable exception) {
		super(exception);
	}
}
