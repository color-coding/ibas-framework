package org.colorcoding.ibas.bobas.approval;

/**
 * 没有授权异常
 * @author Niuren.Zhu
 *
 */
public class UnauthorizedException extends ApprovalProcessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7689315325176956037L;

	public UnauthorizedException() {
		super();
	}

	public UnauthorizedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
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
