package org.colorcoding.ibas.bobas.organization;

import org.colorcoding.ibas.bobas.exception.BasException;

public class InvalidAuthorizationException extends BasException {

	private static final long serialVersionUID = -2013118631823755958L;

	public InvalidAuthorizationException() {
		super();
	}

	public InvalidAuthorizationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidAuthorizationException(String message) {
		super(message);
	}

	public InvalidAuthorizationException(Throwable cause) {
		super(cause);
	}

}
