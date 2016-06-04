package org.colorcoding.ibas.bobas.mapping;


public class MappingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6029089645663638232L;

	public MappingException() {
		super();
	}

	public MappingException(String message, Throwable exception) {
		super(message, exception);
	}

	public MappingException(String message) {
		super(message);
	}

	public MappingException(Throwable exception) {
		super(exception);
	}

}
