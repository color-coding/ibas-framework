package org.colorcoding.ibas.bobas.db;

/**
 * SQL语句
 * 
 * @author Niuren.Zhu
 *
 */
public interface ISqlStatement {

	/**
	 * 获取-语句
	 * 
	 * @return
	 */
	String getContent();

	/**
	 * 设置-语句
	 * 
	 * @param value
	 */
	void setContent(String value);

	/**
	 * 清理参数
	 */
	void clearParameters();

	/**
	 * 设置参数值
	 * 
	 * @param parameterIndex 索引（0）
	 * @param x              值
	 * @param targetType     目标类型
	 */
	void setObject(int parameterIndex, Object x, DbFieldType targetType);

	/**
	 * 设置参数值
	 * 
	 * @param parameterIndex 索引（0）
	 * @param x              值
	 */
	void setObject(int parameterIndex, Object x);
}
