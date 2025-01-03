package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.ICriteria;

public interface ITransaction extends AutoCloseable {
	/**
	 * 获取-标识
	 * 
	 * @return
	 */
	String getId();

	/**
	 * 处于事务中
	 * 
	 * @return
	 * @throws RepositoryException
	 */
	boolean inTransaction() throws RepositoryException;

	/**
	 * 开启事务
	 * 
	 * @return
	 * @throws RepositoryException
	 */
	boolean beginTransaction() throws RepositoryException;

	/**
	 * 回滚事务
	 */
	void rollback() throws RepositoryException;

	/**
	 * 提交事务
	 */
	void commit() throws RepositoryException;

	/**
	 * 查询对象
	 * 
	 * @param boType   查询的对象类型
	 * 
	 * @param criteria 查询条件
	 * 
	 * @return
	 */
	<T extends IBusinessObject> T[] fetch(Class<?> boType, ICriteria criteria) throws RepositoryException;

	/**
	 * 保存对象
	 * 
	 * @param bos 被保存的对象
	 * 
	 * @return
	 */
	<T extends IBusinessObject> T[] save(T[] bos) throws RepositoryException;
}
