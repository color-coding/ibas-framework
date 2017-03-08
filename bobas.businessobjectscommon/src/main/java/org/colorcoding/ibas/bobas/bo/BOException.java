package org.colorcoding.ibas.bobas.bo;

public class BOException extends Exception {

	private static final long serialVersionUID = 5170381467555264194L;

	public BOException() {
		super();
	}

	public BOException(String message, Throwable exception) {
		super(message, exception);
	}

	public BOException(String message) {
		super(message);
	}

	public BOException(Throwable exception) {
		super(exception);
	}
}
