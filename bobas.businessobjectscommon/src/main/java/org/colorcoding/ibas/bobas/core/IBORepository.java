package org.colorcoding.ibas.bobas.core;

import org.colorcoding.ibas.bobas.common.IOperationResult;

/**
 * 业务仓库
 */
public interface IBORepository extends IBORepositoryReadonly {

	/**
	 * 保存对象
	 * 
	 * @param bo
	 *            被保存的对象
	 * 
	 * @return 操作结果及新对象实例
	 */
	<T extends IBusinessObjectBase> IOperationResult<T> save(T bo);

	/**
	 * 保存对象包括子项
	 * 
	 * @param bo
	 *            被保存的对象
	 * 
	 * @return 操作结果及新对象实例
	 */
	<T extends IBusinessObjectBase> IOperationResult<T> saveEx(T bo);

	/**
	 * 开启事务
	 * 
	 * @return 创建是否成功（已存在返回false）
	 * @throws RepositoryException
	 */
	boolean beginTransaction() throws RepositoryException;

	/**
	 * 回滚事务
	 * 
	 * @throws RepositoryException
	 */
	void rollbackTransaction() throws RepositoryException;

	/**
	 * 提交事务
	 * 
	 * @throws RepositoryException
	 */
	void commitTransaction() throws RepositoryException;

	/**
	 * 是否处于事务中
	 * 
	 */
	boolean inTransaction();

	/**
	 * 添加事务监听
	 * 
	 * @param listener
	 */
	void addSaveActionsListener(SaveActionsListener listener);

	/**
	 * 移出事务监听
	 * 
	 * @param listener
	 */
	void removeSaveActionsListener(SaveActionsListener listener);
}
