package org.colorcoding.ibas.bobas.rule;

public class BusinessRuleException extends RuntimeException {

	private static final long serialVersionUID = 3107704185880165493L;

	public BusinessRuleException() {
		super();
	}

	public BusinessRuleException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BusinessRuleException(String message, Throwable cause) {
		super(message, cause);
	}

	public BusinessRuleException(String message) {
		super(message);
	}

	public BusinessRuleException(Throwable cause) {
		super(cause);
	}

}
