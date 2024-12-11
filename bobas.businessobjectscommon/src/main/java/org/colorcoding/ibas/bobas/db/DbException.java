package org.colorcoding.ibas.bobas.db;

public class DbException extends Exception {

	private static final long serialVersionUID = 4454068030298562133L;

	public DbException() {
		super();
	}

	public DbException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DbException(String message, Throwable cause) {
		super(message, cause);
	}

	public DbException(String message) {
		super(message);
	}

	public DbException(Throwable cause) {
		super(cause);
	}

}
