package org.colorcoding.bobas.mapping.db;

import org.colorcoding.bobas.mapping.MappingException;

public class DbMappingException extends MappingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 757281980479307074L;

	public DbMappingException() {
		super();
	}

	public DbMappingException(String message, Throwable exception) {
		super(message, exception);
	}

	public DbMappingException(String message) {
		super(message);
	}

	public DbMappingException(Throwable exception) {
		super(exception);
	}
}
