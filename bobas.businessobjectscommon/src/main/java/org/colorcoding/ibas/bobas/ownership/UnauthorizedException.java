package org.colorcoding.ibas.bobas.ownership;

import org.colorcoding.ibas.bobas.exception.BasException;

public class UnauthorizedException extends BasException {

	private static final long serialVersionUID = -6197956381582100910L;

	public UnauthorizedException() {
		super();
	}

	public UnauthorizedException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnauthorizedException(String message) {
		super(message);
	}

	public UnauthorizedException(Throwable cause) {
		super(cause);
	}
}
