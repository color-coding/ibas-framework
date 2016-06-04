package org.colorcoding.ibas.bobas.db;

/**
 * 解决连接Hana时connection.getMetaData().getURL()获取不到需要检验的数据库名称
 * 
 * @author Eric Peng
 *
 */
public interface ConnectionUrl {
	/**
	 * 获取hana连接字符串
	 * 
	 * @return
	 * @throws DbException
	 */
	String getUrl() throws DbException;
}
