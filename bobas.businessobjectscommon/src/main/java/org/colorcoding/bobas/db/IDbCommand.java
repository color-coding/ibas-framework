package org.colorcoding.bobas.db;

import org.colorcoding.bobas.common.ISqlQuery;

/**
 * 数据库命令
 */
public interface IDbCommand {
	/**
	 * 查询sql语句
	 * 
	 * @param sql
	 *            语句
	 * 
	 * @return 结果集
	 */
	IDbDataReader executeReader(String sql) throws DbException;

	/**
	 * 查询sql语句
	 * 
	 * @param sql
	 *            语句
	 * 
	 * @return 结果集
	 */
	IDbDataReader executeReader(ISqlQuery sql) throws DbException;

	/**
	 * 执行sql语句
	 * 
	 * @param sql
	 *            更新语句
	 * @return 受影响的行数
	 * @throws DbException
	 */
	int executeUpdate(ISqlQuery sql) throws DbException;

	/**
	 * 执行sql语句
	 * 
	 * @param sql
	 *            更新语句
	 * @return 受影响的行数
	 * @throws DbException
	 */
	int executeUpdate(String sql) throws DbException;

	/**
	 * 关闭数据库
	 * 
	 * @throws DbException
	 */
	void close() throws DbException;
}
