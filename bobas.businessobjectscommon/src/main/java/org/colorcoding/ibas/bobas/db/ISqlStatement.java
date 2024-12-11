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
}
