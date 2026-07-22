package org.colorcoding.ibas.bobas.expression;

import org.colorcoding.ibas.bobas.exception.BasRuntimeException;

public class ExpressionException extends BasRuntimeException {

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
