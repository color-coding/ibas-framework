package org.colorcoding.ibas.bobas.expression;

public class JudgmentLinkException extends RuntimeException {

	private static final long serialVersionUID = -3098369422805629747L;

	public JudgmentLinkException() {
	}

	public JudgmentLinkException(String message) {
		super(message);
	}

	public JudgmentLinkException(Throwable cause) {
		super(cause);
	}

	public JudgmentLinkException(String message, Throwable cause) {
		super(message, cause);
	}

	public JudgmentLinkException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
