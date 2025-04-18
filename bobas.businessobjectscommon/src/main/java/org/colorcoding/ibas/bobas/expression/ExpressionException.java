package org.colorcoding.ibas.bobas.expression;

public class ExpressionException extends RuntimeException {

	private static final long serialVersionUID = -3098369422805629747L;

	public ExpressionException() {
		super();
	}

	public ExpressionException(String message) {
		super(message);
	}

	public ExpressionException(Throwable cause) {
		super(cause);
	}

	public ExpressionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExpressionException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
