package org.colorcoding.ibas.bobas.exception;

/**
 * 业务异常（Checked）根类。
 */
public class BasException extends Exception {

	private static final long serialVersionUID = 1L;

	public BasException() {
		super();
	}

	public BasException(String message) {
		super(message);
	}

	public BasException(String message, Throwable cause) {
		super(message, cause);
	}

	public BasException(Throwable cause) {
		super(cause);
	}

	public BasException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
