package org.colorcoding.ibas.bobas.db;


public class SqlScriptsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -92016001204817675L;

	public SqlScriptsException() {
	}

	public SqlScriptsException(String message) {
		super(message);
	}

	public SqlScriptsException(String message, Throwable exception) {
		super(message, exception);
	}

	public SqlScriptsException(Throwable exception) {
		super(exception);
	}
}
