package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.ISqlQuery;
import org.colorcoding.ibas.bobas.common.ISqlStoredProcedure;
import org.colorcoding.ibas.bobas.core.IBORepositoryReadonly;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.data.IDataTable;
import org.colorcoding.ibas.bobas.data.SingleValue;
import org.colorcoding.ibas.bobas.db.DbException;
import org.colorcoding.ibas.bobas.db.IDbAdapter;
import org.colorcoding.ibas.bobas.db.IDbConnection;

/**
 * 业务对象的数据库仓库（只读）
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBORepository4DbReadonly extends IBORepositoryReadonly {
	/**
	 * 获取数据库链接
	 */
	IDbConnection getDbConnection() throws DbException;

	/**
	 * 设置数据库链接
	 */
	void setDbConnection(IDbConnection connection);

	/**
	 * 创建数据库适配器
	 * 
	 * @return
	 * @throws DbException
	 */
	IDbAdapter createDbAdapter() throws DbException;

	/**
	 * 连接数据库
	 * 
	 * @param dbType
	 *            数据库类型
	 * @param dbServer
	 *            服务地址
	 * @param dbName
	 *            数据库名称
	 * @param dbUser
	 *            用户
	 * @param dbPassword
	 *            密码
	 * @throws DbException
	 */
	void connectDb(String dbType, String dbServer, String dbName, String dbUser, String dbPassword) throws DbException;

	/**
	 * 连接数据库（默认数据库类型）
	 * 
	 * @param dbServer
	 *            服务地址
	 * @param dbName
	 *            数据库名称
	 * @param dbUser
	 *            用户
	 * @param dbPassword
	 *            密码
	 * @throws DbException
	 */
	void connectDb(String dbServer, String dbName, String dbUser, String dbPassword) throws DbException;

	/**
	 * 打开数据库连接
	 * 
	 * @return 是否第一次打开
	 * @throws DbException
	 */
	boolean openDbConnection() throws DbException;

	/**
	 * 关闭数据库连接
	 * 
	 * @throws DbException
	 */
	void closeDbConnection() throws DbException;

	/**
	 * 查询对象
	 * 
	 * @param sqlQuery
	 *            查询语句
	 * 
	 * @param boType
	 *            对象类型
	 * 
	 * @return 操作结果及新对象实例
	 */
	<T extends IBusinessObjectBase> IOperationResult<T> fetch(ISqlQuery sqlQuery, Class<T> boType);

	/**
	 * 查询对象包括子项
	 * 
	 * @param sqlQuery
	 *            查询语句
	 * 
	 * @param boType
	 *            对象类型
	 * 
	 * @return 操作结果及新对象实例
	 */
	<T extends IBusinessObjectBase> IOperationResult<T> fetchEx(ISqlQuery sqlQuery, Class<T> boType);

	/**
	 * 运行查询语句
	 * 
	 * @param sqlQuery
	 *            查询语句
	 * @return 操作结果及查询第一行第一列
	 */
	IOperationResult<SingleValue> fetch(ISqlQuery sqlQuery);

	/**
	 * 运行查询语句
	 * 
	 * @param sqlQuery
	 *            查询语句
	 * @return 操作结果及查询结果
	 */
	IOperationResult<IDataTable> query(ISqlQuery sqlQuery);

	/**
	 * 查询对象
	 * 
	 * @param sp
	 *            调用的存储过程
	 * 
	 * @param boType
	 *            对象类型
	 * 
	 * @return 操作结果及新对象实例
	 */
	<T extends IBusinessObjectBase> IOperationResult<T> fetch(ISqlStoredProcedure sp, Class<T> boType);

	/**
	 * 查询对象包括子项
	 * 
	 * @param sp
	 *            调用的存储过程
	 * 
	 * @param boType
	 *            对象类型
	 * 
	 * @return 操作结果及新对象实例
	 */
	<T extends IBusinessObjectBase> IOperationResult<T> fetchEx(ISqlStoredProcedure sp, Class<T> boType);

	/**
	 * 运行存储过程，并返回值
	 * 
	 * @param sp
	 *            查询语句
	 * @return 操作结果及查询第一行第一列
	 */
	IOperationResult<SingleValue> fetch(ISqlStoredProcedure sp);
}
