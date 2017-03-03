package org.colorcoding.ibas.bobas.db;

import org.colorcoding.ibas.bobas.common.IConditions;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.ISqlQuery;
import org.colorcoding.ibas.bobas.common.ISqlStoredProcedure;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.IBusinessObjectListBase;
import org.colorcoding.ibas.bobas.repository.TransactionType;

/**
 * 业务对象的数据库适配器
 */
public interface IBOAdapter4Db extends IBONumberingManager4Db {

	/**
	 * 解析查询
	 * 
	 * @param criteria
	 *            查询
	 * @return 查询语句
	 * @throws BOParseException
	 */
	ISqlQuery parseSqlQuery(ICriteria criteria) throws BOParseException;

	/**
	 * 解析查询条件
	 * 
	 * @param conditions
	 *            查询条件
	 * @return 查询语句
	 * @throws BOParseException
	 */
	ISqlQuery parseSqlQuery(IConditions conditions) throws BOParseException;

	/**
	 * 解析查询
	 * 
	 * @param criteria
	 *            查询
	 * @param boType
	 *            对象类型
	 * @return 查询脚本
	 */
	ISqlQuery parseSqlQuery(ICriteria criteria, Class<?> boType) throws BOParseException;

	/**
	 * 获取服务器时间脚本
	 * 
	 * @return 脚本
	 * @throws BOParseException
	 */
	ISqlQuery getServerTimeScript() throws BOParseException;

	/**
	 * 获取插入语句
	 * 
	 * @param bo
	 *            业务对象
	 * @return 插入语句
	 * @throws BOParseException
	 */
	ISqlQuery parseSqlInsert(IBusinessObjectBase bo) throws BOParseException;

	/**
	 * 获取删除语句
	 * 
	 * @param bo
	 *            业务对象
	 * @return 删除语句
	 * @throws BOParseException
	 */
	ISqlQuery parseSqlDelete(IBusinessObjectBase bo) throws BOParseException;

	/**
	 * 获取更新语句
	 * 
	 * @param bo
	 *            业务对象
	 * @return 更新语句
	 * @throws BOParseException
	 */
	ISqlQuery parseSqlUpdate(IBusinessObjectBase bo) throws BOParseException;

	/**
	 * 解析业务对象
	 * 
	 * @param reader
	 *            数据
	 * @param boType
	 *            业务对象
	 * 
	 * @return 解析的对象数组
	 * @throws BOParseException
	 */
	IBusinessObjectBase[] parseBOs(IDbDataReader reader, Class<?> boType) throws BOParseException;

	/**
	 * 解析业务对象
	 * 
	 * @param reader
	 *            数据
	 * @param bo
	 *            业务对象实例
	 * 
	 * @return 解析的对象
	 * @throws BOParseException
	 */
	IBusinessObjectBase[] parseBOs(IDbDataReader reader, IBusinessObjectListBase<?> bos) throws BOParseException;

	/**
	 * 获取事务通知语句
	 * 
	 * @param bo
	 *            业务对象
	 * @return 通知语句
	 * @throws BOParseException
	 */
	ISqlQuery parseBOTransactionNotification(TransactionType type, IBusinessObjectBase bo) throws BOParseException;

	/**
	 * 解析查询
	 * 
	 * @param sp
	 *            调用的存储过程信息
	 * @return 查询对象
	 * @throws SqlScriptsException
	 */
	ISqlQuery parseSqlQuery(ISqlStoredProcedure sp) throws BOParseException;

}
