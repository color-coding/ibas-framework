package org.colorcoding.ibas.bobas.repository;

import java.util.Objects;

import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.logging.Logger;
import org.colorcoding.ibas.bobas.logic.BusinessLogicsManager;
import org.colorcoding.ibas.bobas.logic.IBusinessLogicChain;

/**
 * 业务对象仓库（查询、保存）
 * 
 * @author Niuren.Zhu
 *
 */
public abstract class BORepository extends Repository {

	private boolean skipLogics;

	protected final boolean isSkipLogics() {
		return skipLogics;
	}

	protected final void setSkipLogics(boolean skipLogics) {
		this.skipLogics = skipLogics;
	}

	private volatile ITransaction transaction;

	public synchronized final ITransaction getTransaction() throws RepositoryException {
		return transaction;
	}

	public synchronized void setTransaction(ITransaction transaction) {
		this.transaction = transaction;
	}

	public synchronized final boolean inTransaction() throws RepositoryException {
		if (this.transaction == null) {
			return false;
		}
		return this.transaction.inTransaction();
	}

	public synchronized final boolean beginTransaction() throws RepositoryException {
		if (this.transaction == null) {
			this.initTransaction();
		}
		if (this.inTransaction()) {
			return false;
		}
		return this.transaction.beginTransaction();
	}

	public synchronized final void rollbackTransaction() throws RepositoryException {
		if (this.inTransaction()) {
			this.transaction.rollback();
		}
	}

	public synchronized final void commitTransaction() throws RepositoryException {
		if (this.inTransaction()) {
			this.transaction.commit();
		}
	}

	protected <T extends IBusinessObject> OperationResult<T> fetch(Class<?> boType, ICriteria criteria) {
		try {
			Objects.requireNonNull(boType);
			// 未打开事务，则创建
			this.initTransaction();
			IBusinessObject[] results = this.getTransaction().fetch(boType, criteria);
			OperationResult<T> operationResult = new OperationResult<T>(results.length);
			for (IBusinessObject item : results) {
				operationResult.addResultObjects(item);
			}
			return operationResult;
		} catch (Exception e) {
			Logger.log(e);
			return new OperationResult<>(e);
		}
	}

	protected <T extends IBusinessObject> OperationResult<T> save(T bo) {
		try {
			Objects.requireNonNull(bo);
			this.initTransaction();
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
						// 不存在副本
						throw new RepositoryException(I18N.prop("msg_bobas_not_found_bo_copy", bo.toString()));
					}
					// 存在副本
					if (BOUtilities.isNewer(boCopy, bo)) {
						throw new RepositoryException(I18N.prop("msg_bobas_bo_copy_is_more_newer", bo.toString()));
					}
					// 如果是删除数据，则使用数据库副本
					if (bo.isDeleted()) {
						bo = BOUtilities.clone(boCopy);
						bo.delete();
					}
				}
				// 返回结果
				OperationResult<T> operationResult = new OperationResult<T>(1);
				if (this.isSkipLogics()) {
					// 跳过业务逻辑
					this.getTransaction().save(new IBusinessObject[] { bo });
				} else {
					// 执行业务逻辑
					try (IBusinessLogicChain logicChain = BusinessLogicsManager.create()
							.createChain(this.getTransaction(), this.getCurrentUser())) {
						logicChain.setTrigger(bo);
						logicChain.setTriggerCopy(boCopy);
						logicChain.execute();
					}
				}
				// 非删除，返回对象
				if (bo.isDeleted() == false) {
					operationResult.addResultObjects(bo);
				}
				if (mine == true) {
					this.commitTransaction();
					mine = false;
				}
				return operationResult;
			} catch (Exception e) {
				if (mine == true) {
					this.rollbackTransaction();
				}
				throw e;
			}
		} catch (Exception e) {
			Logger.log(e);
			return new OperationResult<>(e);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
	}

	@Override
	public void close() throws Exception {
		this.transaction = null;
	}

	/**
	 * 初始化事务
	 * 
	 * @return true，自建
	 * @throws RepositoryException
	 */
	protected abstract boolean initTransaction() throws RepositoryException;
}
