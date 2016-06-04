package org.colorcoding.bobas.organization;


public class InvalidOrganizationException extends OrganizationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 16469047991674711L;

	public InvalidOrganizationException() {

	}

	public InvalidOrganizationException(String message) {
		super(message);
		
	}

	public InvalidOrganizationException(String message, Throwable exception) {
		super(message, exception);
	
	}

}
