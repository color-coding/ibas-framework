package org.colorcoding.ibas.bobas.exception;

/**
 * 业务异常（Unchecked）根类。
 */
public class BasRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BasRuntimeException() {
		super();
	}

	public BasRuntimeException(String message) {
		super(message);
	}

	public BasRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public BasRuntimeException(Throwable cause) {
		super(cause);
	}

	public BasRuntimeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
