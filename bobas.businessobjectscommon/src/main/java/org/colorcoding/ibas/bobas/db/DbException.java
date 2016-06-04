package org.colorcoding.ibas.bobas.db;


public class DbException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8116151432717068905L;

	public DbException() {
		super();
	}

	public DbException(String message, Throwable exception) {
		super(message, exception);
	}

	public DbException(String message) {
		super(message);
	}

	public DbException(Throwable exception) {
		super(exception);
	}

}
