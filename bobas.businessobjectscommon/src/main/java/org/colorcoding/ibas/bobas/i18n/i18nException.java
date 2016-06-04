package org.colorcoding.ibas.bobas.i18n;


public class i18nException extends Exception {
	/**
		 * 
		 */
	private static final long serialVersionUID = 4120009314759594522L;

	public i18nException() {
		super();
	}

	public i18nException(String message, Throwable exception) {
		super(message, exception);
	}

	public i18nException(String message) {
		super(message);
	}

	public i18nException(Throwable exception) {
		super(exception);
	}
}
