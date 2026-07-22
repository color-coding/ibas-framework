package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.exception.BasException;

public class ApprovalException extends BasException {

	private static final long serialVersionUID = 5565402598264677846L;

	public ApprovalException() {
		super();
	}

	public ApprovalException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ApprovalException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApprovalException(String message) {
		super(message);
	}

	public ApprovalException(Throwable cause) {
		super(cause);
	}

}
