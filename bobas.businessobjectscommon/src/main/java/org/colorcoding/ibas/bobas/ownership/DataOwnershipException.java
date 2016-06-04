package org.colorcoding.ibas.bobas.ownership;

public class DataOwnershipException extends OwnershipException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5199974197993814339L;

	public DataOwnershipException() {
		super();
	}

	public DataOwnershipException(String message, Throwable exception) {
		super(message, exception);
	}

	public DataOwnershipException(String message) {
		super(message);
	}

	public DataOwnershipException(Throwable exception) {
		super(exception);
	}

}
