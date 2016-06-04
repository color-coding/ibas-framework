package org.colorcoding.ibas.bobas.db;

/**
 * 数据库适配器
 */
public interface IDbAdapter {
	/**
	 * 创建数据库链接
	 * 
	 * @throws DbException
	 */
	IDbConnection createDbConnection() throws DbException;

	/**
	 * 创建数据库链接
	 * 
	 * @param sign
	 *            组标记
	 * @return
	 * @throws DbException
	 */

	IDbConnection createDbConnection(String sign) throws DbException;

	/**
	 * 创建数据库链接
	 * 
	 * @param dbType
	 *            数据库类型
	 * @param dbServer
	 *            数据库服务地址
	 * @param dbName
	 *            数据库名称
	 * @param dbUser
	 *            用户名
	 * @param dbPassword
	 *            用户密码
	 * @return 打开的数据连接
	 * @throws DbException
	 */

	IDbConnection createDbConnection(String dbServer, String dbName, String dbUser, String dbPassword)
			throws DbException;

	/**
	 * 创建业务对象适配器
	 */
	IBOAdapter4Db createBOAdapter();
}
