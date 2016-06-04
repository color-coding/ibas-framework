package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.core.BORepositoryBase;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.core.SaveActionsListener;
import org.colorcoding.ibas.bobas.data.DateTime;

/**
 * 无效的业务仓库（避免空引用时使用）
 * 
 * @author niuren.zhu
 *
 */
public class BORepository4Invalid extends BORepositoryBase implements IBORepository4Invalid {

	public BORepository4Invalid() {
	}

	@Override
	public void dispose() throws RepositoryException {

	}

	protected IOperationResult<?> newInvalidOperationResult() {
		OperationResult<?> operationResult = new OperationResult<Object>();
		operationResult.setError(new InvalidRepositoryException());
		return operationResult;
	}

	@Override
	public IOperationResult<?> save(IBusinessObjectBase bo) {
		return this.newInvalidOperationResult();
	}

	@Override
	public IOperationResult<?> saveEx(IBusinessObjectBase bo) {
		return this.newInvalidOperationResult();
	}

	@Override
	public IOperationResult<?> fetch(ICriteria criteria, Class<? extends IBusinessObjectBase> boType) {
		return this.newInvalidOperationResult();
	}

	@Override
	public IOperationResult<?> fetchEx(ICriteria criteria, Class<? extends IBusinessObjectBase> boType) {
		return this.newInvalidOperationResult();
	}

	@Override
	public IOperationResult<?> fetchCopy(IBusinessObjectBase bo) {
		return this.newInvalidOperationResult();
	}

	@Override
	public IOperationResult<?> fetchCopyEx(IBusinessObjectBase bo) {
		return this.newInvalidOperationResult();
	}

	@Override
	public DateTime getServerTime() {
		return DateTime.getNow();
	}

	@Override
	public boolean beginTransaction() throws RepositoryException {
		throw new InvalidRepositoryException();
	}

	@Override
	public void rollbackTransaction() throws RepositoryException {
		throw new InvalidRepositoryException();
	}

	@Override
	public void commitTransaction() throws RepositoryException {
		throw new InvalidRepositoryException();
	}

	@Override
	public boolean inTransaction() {
		return false;
	}

	@Override
	public void addSaveActionsListener(SaveActionsListener listener) {

	}

	@Override
	public void removeSaveActionsListener(SaveActionsListener listener) {

	}

}
