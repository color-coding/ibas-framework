package org.colorcoding.ibas.bobas.repository;

import java.util.Objects;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;

public abstract class BORepository {

	public synchronized boolean beginTransaction() throws RepositoryException {
		if (this.inTransaction()) {
			return false;
		}
		try {
			this.transaction = this.startTransaction();
		} catch (Exception e) {
			throw new RepositoryException(e);
		}
		return true;
	}

	public synchronized void rollbackTransaction() throws RepositoryException {
		if (this.inTransaction()) {
			try {
				this.transaction.rollback();
				this.transaction = null;
			} catch (Exception e) {
				throw new RepositoryException(e);
			}
		}
	}

	public synchronized void commitTransaction() throws RepositoryException {
		if (this.inTransaction()) {
			try {
				this.transaction.commit();
				this.transaction = null;
			} catch (Exception e) {
				throw new RepositoryException(e);
			}
		}
	}

	private volatile ITransaction transaction;

	public synchronized boolean inTransaction() {
		if (this.transaction == null) {
			return false;
		}
		return true;
	}

	protected abstract ITransaction startTransaction();

	protected <T extends IBusinessObject> IOperationResult<T> fetch(ICriteria criteria, Class<T> boType) {
		try {
			ITransaction transaction = this.inTransaction() ? this.transaction : this.startTransaction();
			Objects.requireNonNull(transaction);
			return new OperationResult<T>().addResultObjects(transaction.fetch(criteria, boType));
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
	}

	protected <T extends IBusinessObject> IOperationResult<T> save(T bo) {
		try {
			ITransaction transaction = this.inTransaction() ? this.transaction : this.startTransaction();
			Objects.requireNonNull(transaction);
			return new OperationResult<T>().addResultObjects(transaction.save(bo));
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
	}
}
