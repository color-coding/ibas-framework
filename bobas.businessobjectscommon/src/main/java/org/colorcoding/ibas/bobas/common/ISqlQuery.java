package org.colorcoding.ibas.bobas.common;

/**
 * 查询语句
 * 
 * @author Niuren.Zhu
 *
 */
public interface ISqlQuery {
	/**
	 * 获取-查询语句
	 * 
	 * @return
	 */
	String getQueryString();

	/**
	 * 设置-查询语句
	 * 
	 * @param value
	 */
	void setQueryString(String value);
}
