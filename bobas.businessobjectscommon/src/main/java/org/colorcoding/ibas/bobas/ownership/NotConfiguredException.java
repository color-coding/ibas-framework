package org.colorcoding.ibas.bobas.ownership;

public class NotConfiguredException extends OwnershipException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5938782209368437091L;

	public NotConfiguredException() {
	}

	public NotConfiguredException(String message) {
		super(message);
	}

	public NotConfiguredException(String message, Throwable exception) {
		super(message, exception);
	}

	public NotConfiguredException(Throwable exception) {
		super(exception);
	}

}