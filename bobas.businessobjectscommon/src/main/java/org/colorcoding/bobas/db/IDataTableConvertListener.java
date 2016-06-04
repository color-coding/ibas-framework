package org.colorcoding.bobas.db;

/**
 * 转过程监听者
 * 
 * 可用于多线程处理转换后的数据
 * 
 * @author Niuren.Zhu
 *
 */
public interface IDataTableConvertListener {
	/**
	 * 获取使用的线程数
	 * 
	 * @return -1 or 0，表示自动多线程；1，表示单线程；
	 */
	int getUseThreadCount();
	
	/**
	 * 获取监听触发的行数
	 * 
	 * @return -1 or 0，表示不监听；
	 */
	int getListenRowCount();

	/**
	 * 获取监听触发的行列乘积数
	 * 
	 * @return -1 or 0，表示不监听；
	 */
	int getListenRanksCount();

	/**
	 * 转换后调用方法
	 * 
	 * @param rows
	 *            此次转换的数据行
	 * @param batchCount
	 *            第几次转换
	 * @param last
	 *            最后一组
	 */
	void afterConverted(IDataTableRow[] rows, int count, boolean last);
}
