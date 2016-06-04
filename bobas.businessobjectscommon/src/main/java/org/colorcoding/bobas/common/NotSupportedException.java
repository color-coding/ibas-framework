package org.colorcoding.bobas.common;

/**
 * 不支持异常
 * 
 * @author Niuren.Zhu
 *
 */
public class NotSupportedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6517456444051289763L;

	public NotSupportedException() {
	}

	public NotSupportedException(String message) {
		super(message);
	}

	public NotSupportedException(Throwable cause) {
		super(cause);
	}

	public NotSupportedException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotSupportedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
