package org.colorcoding.ibas.bobas.core;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.db.DbException;
import org.colorcoding.ibas.bobas.organization.IUser;

public interface IBORepositoryReadonly {
	/**
	 * 获取业务对象工厂
	 */
	IBOFactory getBOFactory();

	/**
	 * 设置业务对象工厂
	 */
	void setBOFactory(IBOFactory factory);

	/**
	 * 获取当前用户
	 * 
	 * @return 当前用户
	 */
	IUser getCurrentUser();

	/**
	 * 设置当前用户
	 * 
	 * @param user
	 *            用户
	 */
	void setCurrentUser(IUser user);

	/**
	 * 释放资源
	 * 
	 * @throws RepositoryException
	 */
	void dispose() throws RepositoryException;

	/**
	 * 获取服务器时间
	 * 
	 * @return 服务器当前时间
	 * @throws DbException
	 */
	DateTime getServerTime();

	/**
	 * 获取事务ID
	 * 
	 * @return
	 */
	String getTransactionId();

	/**
	 * 查询对象
	 * 
	 * @param criteria
	 *            查询条件
	 * 
	 * @param boType
	 *            查询的对象类型
	 * 
	 * @return 操作结果及新对象实例
	 */
	<T extends IBusinessObjectBase> IOperationResult<T> fetch(ICriteria criteria, Class<T> boType);

	/**
	 * 查询对象包括子项
	 * 
	 * @param criteria
	 *            查询条件
	 * 
	 * @param boType
	 *            查询的对象类型
	 * 
	 * @return 操作结果及新对象实例
	 */
	<T extends IBusinessObjectBase> IOperationResult<T> fetchEx(ICriteria criteria, Class<T> boType);

	/**
	 * 查询对象副本
	 * 
	 * @param bo
	 *            对象
	 * 
	 * @return
	 */
	<T extends IBusinessObjectBase> IOperationResult<T> fetchCopy(T bo);

	/**
	 * 查询对象副本包括子项
	 * 
	 * @param bo
	 *            对象
	 * 
	 * @return 操作结果及新对象实例
	 */
	<T extends IBusinessObjectBase> IOperationResult<T> fetchCopyEx(T bo);

}
