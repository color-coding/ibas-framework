package org.colorcoding.ibas.bobas.db;

public class SqlScriptException extends Exception {

	private static final long serialVersionUID = -92016001204817675L;

	public SqlScriptException() {
	}

	public SqlScriptException(String message) {
		super(message);
	}

	public SqlScriptException(String message, Throwable exception) {
		super(message, exception);
	}

	public SqlScriptException(Throwable exception) {
		super(exception);
	}
}
