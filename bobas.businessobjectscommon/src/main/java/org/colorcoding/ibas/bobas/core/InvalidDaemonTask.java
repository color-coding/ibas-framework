package org.colorcoding.ibas.bobas.core;

/**
 * 无效的后台任务
 * 
 * @author Niuren.Zhu
 *
 */
public class InvalidDaemonTask extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 398846462991172932L;

	public InvalidDaemonTask() {
		super();
	}

	public InvalidDaemonTask(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidDaemonTask(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidDaemonTask(String message) {
		super(message);
	}

	public InvalidDaemonTask(Throwable cause) {
		super(cause);
	}

}
