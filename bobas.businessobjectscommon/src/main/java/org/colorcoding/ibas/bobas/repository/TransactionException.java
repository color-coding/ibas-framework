package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.core.RepositoryException;

public class TransactionException extends RepositoryException {

	private static final long serialVersionUID = 8780220025540362087L;

	public TransactionException() {
		super();
	}

	public TransactionException(String message, Throwable exception) {
		super(message, exception);

	}

	public TransactionException(String message) {
		super(message);

	}

	public TransactionException(Throwable exception) {
		super(exception);
	}
}
