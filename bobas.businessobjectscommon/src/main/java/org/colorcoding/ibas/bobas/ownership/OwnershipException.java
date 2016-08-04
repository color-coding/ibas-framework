package org.colorcoding.ibas.bobas.ownership;

/**
 * 所有权异常
 * 
 * @author Niuren.Zhu
 *
 */
public class OwnershipException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1052466059828817472L;

	public OwnershipException() {
	}

	public OwnershipException(String message) {
		super(message);
	}

	public OwnershipException(String message, Throwable exception) {
		super(message, exception);
	}

	public OwnershipException(Throwable exception) {
		super(exception);
	}

}
