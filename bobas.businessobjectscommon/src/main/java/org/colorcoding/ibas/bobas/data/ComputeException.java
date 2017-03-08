package org.colorcoding.ibas.bobas.data;

public class ComputeException extends Exception {

	private static final long serialVersionUID = -3222542562501337569L;

	public ComputeException() {
	}

	public ComputeException(String message) {
		super(message);
	}

	public ComputeException(String message, Throwable exception) {
		super(message, exception);
	}

	public ComputeException(Throwable exception) {
		super(exception);
	}
}
