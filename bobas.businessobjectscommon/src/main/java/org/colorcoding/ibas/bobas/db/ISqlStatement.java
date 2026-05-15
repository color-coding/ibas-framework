package org.colorcoding.ibas.bobas.db;

/**
 * SQL语句接口
 *
 * @author Niuren.Zhu
 *
 */
public interface ISqlStatement {

	/**
	 * 获取SQL语句内容
	 *
	 * @return SQL语句字符串
	 */
	String getContent();

	/**
	 * 设置SQL语句内容
	 *
	 * @param value SQL语句字符串
	 */
	void setContent(String value);

	/**
	 * 清理所有参数
	 */
	void clearParameters();

	/**
	 * 设置参数值
	 *
	 * @param parameterIndex 参数索引（从1开始）
	 * @param x              参数值
	 * @param targetType     目标数据库字段类型
	 */
	void setObject(int parameterIndex, Object x, DbFieldType targetType);

	/**
	 * 设置参数值（类型默认UNKNOWN）
	 *
	 * @param parameterIndex 参数索引（从1开始）
	 * @param x              参数值
	 */
	void setObject(int parameterIndex, Object x);
}
