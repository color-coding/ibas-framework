package org.colorcoding.ibas.bobas.common;

/**
 * 查询语句
 * 
 * @author Niuren.Zhu
 *
 */
public interface ISqlQuery {
	/**
	 * 是否允许读取
	 * 
	 * @return
	 */
	boolean isAllowRead();

	/**
	 * 设置-允许读取
	 * 
	 * @param value
	 */
	void setAllowRead(boolean value);

	/**
	 * 是否允许写入
	 * 
	 * @return
	 */
	boolean isAllowWrite();

	/**
	 * 设置-允许写入
	 * 
	 * @param value
	 */
	void setAllowWrite(boolean value);

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
