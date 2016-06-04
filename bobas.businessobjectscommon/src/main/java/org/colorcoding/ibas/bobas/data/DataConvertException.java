package org.colorcoding.ibas.bobas.data;


public class DataConvertException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6363663547488861425L;

	public DataConvertException() {
	}

	public DataConvertException(String message) {
		super(message);
	}

	public DataConvertException(Throwable cause) {
		super(cause);
	}

	public DataConvertException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataConvertException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
