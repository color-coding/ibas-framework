package org.colorcoding.ibas.bobas.db;

/**
 * 数据库连接
 */
public interface IDbConnection {

	/**
	 * 获取连接地址
	 * 
	 * @return
	 * @throws DbException
	 */
	String getURL() throws DbException;

	/**
	 * 获取用户名称
	 * 
	 * @return
	 * @throws DbException
	 */
	String getUserName() throws DbException;

	/**
	 * 打开数据库连接
	 * 
	 * @return 成功，true；已打开，false
	 * @throws DbException
	 *             不能打开
	 */
	boolean open() throws DbException;

	/**
	 * 关闭数据库连接
	 * 
	 * @throws DbException
	 */
	void close() throws DbException;

	/**
	 * 关闭数据库连接
	 * 
	 * @param force
	 *            强制关闭
	 * @throws DbException
	 */
	void close(boolean force) throws DbException;

	/**
	 * 是否关闭数据库连接
	 * 
	 * @return
	 * @throws DbException
	 */
	boolean isClosed() throws DbException;

	/**
	 * 创建数据库命令
	 * 
	 * @throws DbException
	 * @return 数据库命令
	 */
	IDbCommand createCommand() throws DbException;

	/**
	 * 开启数据库事务
	 * 
	 * @return 成功创建，true；已存在，false；
	 * @throws DbException
	 */
	boolean beginTransaction() throws DbException;

	/**
	 * 回滚数据库事务
	 * 
	 * @throws DbException
	 */
	void rollbackTransaction() throws DbException;

	/**
	 * 提交数据库事务
	 * 
	 * @throws DbException
	 */
	void commitTransaction() throws DbException;

	/**
	 * 是否处于事务中
	 */
	boolean inTransaction();

	/**
	 * 释放引用的资源
	 */
	void dispose();
}
