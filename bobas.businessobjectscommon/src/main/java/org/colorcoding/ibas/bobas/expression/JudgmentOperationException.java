package org.colorcoding.ibas.bobas.expression;

import org.colorcoding.ibas.bobas.exception.BasRuntimeException;

public class JudgmentOperationException extends BasRuntimeException {

	private static final long serialVersionUID = -793232791382567764L;

	public JudgmentOperationException() {
		super();
	}

	public JudgmentOperationException(String message) {
		super(message);
	}

	public JudgmentOperationException(Throwable cause) {
		super(cause);
	}

	public JudgmentOperationException(String message, Throwable cause) {
		super(message, cause);
	}

}
