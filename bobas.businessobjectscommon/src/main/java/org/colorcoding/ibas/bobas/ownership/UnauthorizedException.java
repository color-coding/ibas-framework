package org.colorcoding.ibas.bobas.ownership;

/**
 * 未被授权异常
 */
public class UnauthorizedException extends OwnershipException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6197956381582100910L;

	public UnauthorizedException() {
	}

	public UnauthorizedException(String message, Throwable exception) {
		super(message, exception);
	}

	public UnauthorizedException(String message) {
		super(message);
	}

	public UnauthorizedException(Throwable exception) {
		super(exception);
	}
}
