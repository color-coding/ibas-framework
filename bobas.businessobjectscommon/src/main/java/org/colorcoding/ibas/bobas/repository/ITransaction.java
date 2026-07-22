package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.common.ICriteria;

/**
 * 事务接口，提供数据查询、保存和事务控制
 */
public interface ITransaction extends AutoCloseable, Cloneable {

	/**
	 * 获取事务标识
	 */
	String getId();

	/**
	 * 是否处于事务中
	 *
	 * @return true处于事务中
	 * @throws RepositoryException 判断事务状态时发生错误
	 */
	boolean inTransaction() throws RepositoryException;

	/**
	 * 开启事务
	 *
	 * @return true成功开启，false已在事务中
	 * @throws RepositoryException 开启事务失败
	 */
	boolean beginTransaction() throws RepositoryException;

	/**
	 * 回滚事务
	 *
	 * @throws RepositoryException 回滚失败
	 */
	void rollback() throws RepositoryException;

	/**
	 * 提交事务
	 *
	 * @throws RepositoryException 提交失败
	 */
	void commit() throws RepositoryException;

	/**
	 * 查询业务对象
	 *
	 * @param boType   查询的对象类型
	 * @param criteria 查询条件
	 * @return 查询结果数组，可能为空
	 * @throws RepositoryException 查询失败
	 */
	<T> T[] fetch(Class<?> boType, ICriteria criteria) throws RepositoryException;

	/**
	 * 保存业务对象
	 *
	 * @param bos 被保存的对象数组
	 * @return 保存后的对象数组
	 * @throws RepositoryException 保存失败
	 */
	<T> T[] save(T[] bos) throws RepositoryException;
}