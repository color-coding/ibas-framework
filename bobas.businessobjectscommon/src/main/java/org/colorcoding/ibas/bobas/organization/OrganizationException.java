package org.colorcoding.ibas.bobas.organization;


public class OrganizationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8920927864236097565L;

	public OrganizationException() {

	}

	public OrganizationException(String message) {
		super(message);

	}

	public OrganizationException(String message, Throwable exception) {
		super(message, exception);

	}

	public OrganizationException(Throwable exception) {
		super(exception);

	}
}
