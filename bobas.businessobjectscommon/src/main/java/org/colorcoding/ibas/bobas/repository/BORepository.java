package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.logic.BusinessLogicChain;

public abstract class BORepository implements AutoCloseable {

	private boolean skipLogics;

	public final boolean isSkipLogics() {
		return skipLogics;
	}

	public final void setSkipLogics(boolean skipLogics) {
		this.skipLogics = skipLogics;
	}

	private volatile ITransaction transaction;

	public final synchronized ITransaction getTransaction() {
		return transaction;
	}

	public final synchronized void setTransaction(ITransaction transaction) {
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

	protected <T extends IBusinessObject> IOperationResult<T> fetch(ICriteria criteria, Class<?> boType) {
		try {
			boolean mine = this.beginTransaction();
			try {
				OperationResult<T> operationResult = new OperationResult<T>();
				for (IBusinessObject item : this.transaction.fetch(criteria, boType)) {
					operationResult.addResultObjects(item);
				}
				if (mine == true) {
					synchronized (this) {
						this.commitTransaction();
						this.close();
						mine = false;
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
				T boCopy = null;
				// 更新数据时，检查版本是否新于数据库副本
				if (bo.isSavable() && !bo.isNew()) {
					IOperationResult<T> opRsltFetch = this.fetch(bo.getCriteria(), bo.getClass());
					if (opRsltFetch.getError() != null) {
						throw opRsltFetch.getError();
					}
					boCopy = opRsltFetch.getResultObjects().firstOrDefault();
					if (boCopy == null) {
						throw new RepositoryException(Strings.format("not found %s in database.", bo.toString()));
					}
					if (BOUtilities.isNewer(boCopy, bo)) {
						throw new RepositoryException(Strings.format("%s db copy is more newer.", bo.toString()));
					}
					// 如果是删除数据，则使用数据库副本
					if (bo.isDeleted()) {
						bo = BOUtilities.clone(boCopy);
						bo.delete();
					}
				}
				// 返回结果
				OperationResult<T> operationResult = new OperationResult<T>();
				if (this.isSkipLogics()) {
					this.transaction.save(new IBusinessObject[] { bo });
					operationResult.addResultObjects(bo);
				} else {
					// 执行业务逻辑
					BusinessLogicChain logicChain = new BusinessLogicChain();
					logicChain.setTransaction(this.transaction);
					logicChain.setTrigger(bo);
					logicChain.setTriggerCopy(boCopy);
					logicChain.commit();
					operationResult.addResultObjects(bo);
				}
				if (mine == true) {
					synchronized (this) {
						this.commitTransaction();
						this.close();
						mine = false;
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
