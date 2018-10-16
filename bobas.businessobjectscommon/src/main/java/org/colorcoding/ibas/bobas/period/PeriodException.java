package org.colorcoding.ibas.bobas.period;

public class PeriodException extends Exception {

	private static final long serialVersionUID = 4578596692789709867L;

	public PeriodException() {
		super();
	}

	public PeriodException(String message) {
		super(message);
	}

	public PeriodException(String message, Throwable exception) {
		super(message, exception);
	}

	public PeriodException(Throwable exception) {
		super(exception);
	}
}
