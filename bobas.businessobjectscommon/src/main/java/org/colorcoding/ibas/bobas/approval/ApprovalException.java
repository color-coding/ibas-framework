package org.colorcoding.ibas.bobas.approval;

/**
 * 审批流程异常
 * @author Niuren.Zhu
 *
 */
public class ApprovalException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2929830967934745503L;

	public ApprovalException() {
		super();
	}

	public ApprovalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
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
