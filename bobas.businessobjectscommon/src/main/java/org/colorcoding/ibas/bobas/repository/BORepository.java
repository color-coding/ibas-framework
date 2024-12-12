package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;

public abstract class BORepository implements AutoCloseable {

	private volatile ITransaction transaction;

	public synchronized final ITransaction getTransaction() {
		return transaction;
	}

	public synchronized final void setTransaction(ITransaction transaction) {
		this.transaction = transaction;
	}

	public synchronized boolean inTransaction() {
		if (this.transaction == null) {
			return false;
		}
		return true;
	}

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

	@Override
	public synchronized void close() throws Exception {
		if (this.transaction != null) {
			this.transaction.close();
			this.transaction = null;
		}
	}

	protected <T extends IBusinessObject> IOperationResult<T> fetch(ICriteria criteria, Class<T> boType) {
		try {
			boolean mine = this.beginTransaction();
			try {
				OperationResult<T> operationResult = new OperationResult<T>();
				for (T item : this.transaction.fetch(criteria, boType)) {
					operationResult.addResultObjects(item);
				}
				if (mine == true) {
					synchronized (this) {
						this.commitTransaction();
						this.close();
					}
				}
				return operationResult;
			} catch (Exception e) {
				if (mine == true) {
					synchronized (this) {
						this.rollbackTransaction();
						this.close();
					}
				}
				throw e;
			}
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
	}

	protected <T extends IBusinessObject> IOperationResult<T> save(T bo) {
		try {
			boolean mine = this.beginTransaction();
			try {
				OperationResult<T> operationResult = new OperationResult<T>();
				operationResult.addResultObjects(this.transaction.save(bo));
				if (mine == true) {
					synchronized (this) {
						this.commitTransaction();
						this.close();
					}
				}
				return operationResult;
			} catch (Exception e) {
				if (mine == true) {
					synchronized (this) {
						this.rollbackTransaction();
						this.close();
					}
				}
				throw e;
			}
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
	}

	protected abstract ITransaction startTransaction() throws RepositoryException;
}
