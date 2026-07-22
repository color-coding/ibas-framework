package org.colorcoding.ibas.bobas.task;

import org.colorcoding.ibas.bobas.exception.BasRuntimeException;

/**
 * 无效的后台任务
 *
 * @author Niuren.Zhu
 *
 */
public class InvalidDaemonTaskException extends BasRuntimeException {

	private static final long serialVersionUID = 398846462991172932L;

	public InvalidDaemonTaskException() {
		super();
	}

	public InvalidDaemonTaskException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidDaemonTaskException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidDaemonTaskException(String message) {
		super(message);
	}

	public InvalidDaemonTaskException(Throwable cause) {
		super(cause);
	}

}
