package org.colorcoding.ibas.bobas.db;

/**
 * 数据库连接池
 * 
 * @author Niuren.Zhu
 *
 */
public interface IDbConnectionPool {
	/**
	 * 获取-连接池大小
	 * 
	 * @return
	 */
	int getSize();

	/**
	 * 获取-设置接池大小
	 * 
	 * @param value
	 */
	void setSize(int value);

	/**
	 * 获取连接
	 * 
	 * @param sign
	 *            标记
	 * @return 可用连接
	 */
	IDbConnection obtain(String sign);

	/**
	 * 回收连接
	 * 
	 * @param connection
	 *            数据库连接
	 * @return true，回收成功；false，回收失败
	 */
	boolean recycling(IDbConnection connection);

	/**
	 * 获取-连接持有时间
	 * 
	 * @return
	 */
	long getHoldingTime();

	/**
	 * 设置-连接持有时间
	 * 
	 * @param holdingTime
	 */
	void setHoldingTime(long holdingTime);
}
