package org.colorcoding.ibas.bobas.approval;

public class ApprovalProcessException extends ApprovalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5565402598264677846L;

	public ApprovalProcessException() {
		super();
	}

	public ApprovalProcessException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ApprovalProcessException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApprovalProcessException(String message) {
		super(message);
	}

	public ApprovalProcessException(Throwable cause) {
		super(cause);
	}

}
