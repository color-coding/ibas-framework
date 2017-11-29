package org.colorcoding.ibas.bobas.db;

import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.ISqlQuery;
import org.colorcoding.ibas.bobas.common.SortType;
import org.colorcoding.ibas.bobas.data.KeyValue;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;

/**
 * SQL脚本库
 */
public interface ISqlScripts {
	/**
	 * 获取数据库对象标记( "%s" )
	 */
	String getDbObjectSign();

	/**
	 * 获取数据库语句分割符
	 */
	String getScriptBreakSign();

	/**
	 * 获取条件与
	 */
	String getAndSign();

	/**
	 * 获取字段分割符
	 */
	String getFieldBreakSign();

	/**
	 * 获取空值字符
	 */
	String getNullSign();

	/**
	 * 获取服务器时间脚本
	 * 
	 * @return 脚本
	 */
	ISqlQuery getServerTimeQuery();

	/**
	 * 字段值转为条件字段类型( CAST(%s AS NVARCHAR) )
	 */
	String getCastTypeString(DbFieldType dbFieldType);

	/**
	 * 获取数据库绑定数据类型
	 * 
	 * @param dbType
	 *            数据库数据类型
	 * @return 数据库绑定数据类型
	 */
	DbFieldType toDbFieldType(String dbType);

	/**
	 * 获取值的语句
	 * 
	 * @param type
	 *            类型
	 * @param value
	 *            字符串
	 * @return 语句值 （N'%s'）
	 * @throws SqlScriptException
	 */
	String getSqlString(DbFieldType type, String value);

	/**
	 * 获取条件操作符的语句
	 * 
	 * @param value
	 *            操作符
	 * @param opValue
	 *            操作的值
	 * @return 语句值（ = %s）
	 * @throws SqlScriptException
	 */
	String getSqlString(ConditionOperation value, String opValue);

	/**
	 * 获取条件操作符的语句
	 * 
	 * @param value
	 *            操作符
	 * @return 语句值（ = %s）
	 * @throws SqlScriptException
	 */
	String getSqlString(ConditionOperation value);

	/**
	 * 获取条件关系的语句
	 * 
	 * @param value
	 *            关系符
	 * @return 语句值 （and）
	 * @throws SqlScriptException
	 */
	String getSqlString(ConditionRelationship value);

	/**
	 * 获取排序的语句
	 * 
	 * @param value
	 *            排序
	 * @return 语句值 （DESC;ASC）
	 * @throws SqlScriptException
	 */
	String getSqlString(SortType value);

	/**
	 * 获取业务对象主键查询
	 * 
	 * @param boCode
	 *            业务对象
	 * @return 主键查询语句，例:SELECT "AutoKey" FROM "CC_SYS_ONNM" WHERE "ObjectCode" =
	 *         '%s'
	 * @throws SqlScriptException
	 */
	String getPrimaryKeyQuery(String boCode);

	/**
	 * 获取业务对象系列
	 * 
	 * @param boCode
	 *            业务对象
	 * @return 主键查询语句，例:SELECT "NextNum", "Template" FROM "CC_SYS_NNM1" WHERE
	 *         "ObjectCode" = '%s' AND "Series" = %s AND "Locked" = 'N'
	 * @throws SqlScriptException
	 */
	String getSeriesKeyQuery(String boCode, int series);

	/**
	 * 获取更新业务对象主键查询
	 * 
	 * @param boCode
	 *            业务对象
	 * @param addValue
	 *            增加值
	 * @return 主键查询语句，例:UPDATE "CC_SYS_NNM1" SET "NextNum" = "NextNum" + {addValue}
	 *         WHERE "ObjectCode" = '%s' AND "Series" = %s AND "Locked" = 'N'
	 * @throws SqlScriptException
	 */
	String getUpdateSeriesKeyScript(String boCode, int series, int addValue);

	/**
	 * 获取更新业务对象主键查询
	 * 
	 * @param boCode
	 *            业务对象
	 * @param addValue
	 *            增加值
	 * @return 主键查询语句，例:UPDATE "CC_SYS_ONNM" SET "AutoKey" = "AutoKey" + {addValue}
	 *         WHERE "ObjectCode" = '%s'
	 * @throws SqlScriptException
	 */
	String getUpdatePrimaryKeyScript(String boCode, int addValue);

	/**
	 * 获取业务对象事务通知语句
	 * 
	 * @param boCode
	 *            业务对象编码
	 * @param type
	 *            事务类型
	 * @param keyCount
	 *            对象主键数量
	 * @param keyNames
	 *            对象主键名称
	 * @param keyValues
	 *            对象主键值
	 * @return
	 * @throws SqlScriptException
	 */
	String getTransactionNotificationQuery(String boCode, String type, int keyCount, String keyNames, String keyValues);

	/**
	 * 组合select查询
	 * 
	 * @param partSelect
	 *            select部分（*）
	 * @param table
	 *            表名（"OUSR"）
	 * @param partWhere
	 *            条件（空；"Supper" = 'Y' AND "Name" = 'admin'）
	 * @param partOrder
	 *            排序（空； "Code" DESC, "Name" ASC）
	 * @param result
	 *            结果数量（-1，不限制；100）
	 * @return 组合后的查询
	 * @throws SqlScriptException
	 */
	String groupSelectQuery(String partSelect, String table, String partWhere, String partOrder, int result);

	/**
	 * 组合删除查询
	 * 
	 * @param table
	 *            表
	 * @param partWhere
	 *            条件
	 * @return
	 * @throws SqlScriptException
	 */
	String groupDeleteScript(String table, String partWhere);

	/**
	 * 组合更新查询
	 * 
	 * @param table
	 *            表
	 * @param partFieldValues
	 *            字段及值
	 * @param partWhere
	 *            条件
	 * @return
	 * @throws SqlScriptException
	 */
	String groupUpdateScript(String table, String partFieldValues, String partWhere);

	/**
	 * 组合插入查询
	 * 
	 * @param table
	 *            表
	 * @param partFields
	 *            字段
	 * @param partValues
	 *            值
	 * @return
	 * @throws SqlScriptException
	 */
	String groupInsertScript(String table, String partFields, String partValues);

	/**
	 * 组合获取最大值查询
	 * 
	 * @param field
	 *            最大值字段
	 * @param table
	 *            表名
	 * @param partWhere
	 *            条件
	 * @return
	 * @throws SqlScriptException
	 */
	String groupMaxValueQuery(String field, String table, String partWhere);

	/**
	 * 组合存储过程语句
	 * 
	 * @param spName
	 *            存储过程名称
	 * @param selects
	 *            选择的列名称，可能为空
	 * @param parameters
	 *            参数
	 * @return
	 * @throws SqlScriptException
	 */
	String groupStoredProcedure(String spName, KeyValue[] parameters);
}
