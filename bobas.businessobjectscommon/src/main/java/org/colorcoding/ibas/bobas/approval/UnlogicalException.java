package org.colorcoding.ibas.bobas.approval;

/**
 * 不符合逻辑的异常
 * 
 * @author Niuren.Zhu
 *
 */
public class UnlogicalException extends ApprovalProcessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8552353432956770890L;

	public UnlogicalException() {
		super();
	}

	public UnlogicalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UnlogicalException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnlogicalException(String message) {
		super(message);
	}

	public UnlogicalException(Throwable cause) {
		super(cause);
	}

}
