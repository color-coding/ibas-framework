package org.colorcoding.ibas.bobas.repository;

import java.util.Objects;

import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.logging.Logger;
import org.colorcoding.ibas.bobas.logic.BusinessLogicsManager;
import org.colorcoding.ibas.bobas.logic.IBusinessLogicChain;

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

	public synchronized boolean inTransaction() throws RepositoryException {
		if (this.transaction == null) {
			return false;
		}
		return this.transaction.inTransaction();
	}

	public synchronized boolean beginTransaction() throws RepositoryException {
		if (this.inTransaction()) {
			return false;
		}
		try {
			this.transaction = this.startTransaction();
			return this.transaction.beginTransaction();
		} catch (Exception e) {
			throw new RepositoryException(e);
		}
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

	protected <T extends IBusinessObject> IOperationResult<T> fetch(Class<?> boType, ICriteria criteria) {
		try {
			Objects.requireNonNull(boType);
			boolean mine = this.beginTransaction();
			try {
				OperationResult<T> operationResult = new OperationResult<T>();
				for (IBusinessObject item : this.getTransaction().fetch(boType, criteria)) {
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
			Logger.log(e);
			return new OperationResult<>(e);
		}
	}

	protected <T extends IBusinessObject> IOperationResult<T> save(T bo) {
		try {
			Objects.requireNonNull(bo);
			boolean mine = this.beginTransaction();
			try {
				T boCopy = null;
				// 更新数据时，检查版本是否新于数据库副本
				if (bo.isSavable() && !bo.isNew()) {
					ICriteria criteria = bo.getCriteria();
					if (criteria == null || criteria.getConditions().isEmpty()) {
						throw new RepositoryException(I18N.prop("msg_bobas_invaild_criteria"));
					}
					criteria.setResultCount(1);
					IOperationResult<T> opRsltFetch = this.fetch(bo.getClass(), criteria);
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
					// 跳过业务逻辑
					this.getTransaction().save(new IBusinessObject[] { bo });
				} else {
					// 执行业务逻辑
					try (IBusinessLogicChain logicChain = BusinessLogicsManager.create()
							.createChain(this.getTransaction())) {
						logicChain.setTrigger(bo);
						logicChain.setTriggerCopy(boCopy);
						logicChain.commit();
					}
				}
				// 非删除，返回对象
				if (bo.isDeleted() == false) {
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
			Logger.log(e);
			return new OperationResult<>(e);
		}
	}

	protected abstract ITransaction startTransaction() throws RepositoryException;
}
