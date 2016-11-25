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

	protected <T extends IBusinessObjectBase> IOperationResult<T> newInvalidOperationResult() {
		OperationResult<T> operationResult = new OperationResult<>();
		operationResult.setError(new InvalidRepositoryException());
		return operationResult;
	}

	@Override
	public <T extends IBusinessObjectBase> IOperationResult<T> save(T bo) {
		return this.newInvalidOperationResult();
	}

	@Override
	public <T extends IBusinessObjectBase> IOperationResult<T> saveEx(T bo) {
		return this.newInvalidOperationResult();
	}

	@Override
	public <T extends IBusinessObjectBase> IOperationResult<T> fetch(ICriteria criteria, Class<T> boType) {
		return this.newInvalidOperationResult();
	}

	@Override
	public <T extends IBusinessObjectBase> IOperationResult<T> fetchEx(ICriteria criteria, Class<T> boType) {
		return this.newInvalidOperationResult();
	}

	@Override
	public <T extends IBusinessObjectBase> IOperationResult<T> fetchCopy(T bo) {
		return this.newInvalidOperationResult();
	}

	@Override
	public <T extends IBusinessObjectBase> IOperationResult<T> fetchCopyEx(T bo) {
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
