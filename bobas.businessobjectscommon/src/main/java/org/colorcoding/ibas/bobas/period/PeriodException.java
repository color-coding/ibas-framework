package org.colorcoding.ibas.bobas.period;

import org.colorcoding.ibas.bobas.exception.BasException;

public class PeriodException extends BasException {

	private static final long serialVersionUID = 4578596692789709867L;

	public PeriodException() {
		super();
	}

	public PeriodException(String message) {
		super(message);
	}

	public PeriodException(String message, Throwable cause) {
		super(message, cause);
	}

	public PeriodException(Throwable cause) {
		super(cause);
	}
}
