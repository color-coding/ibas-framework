package org.colorcoding.ibas.bobas.logic.common;

import org.colorcoding.ibas.bobas.logic.IBusinessLogicContract;

/**
 * 业务对象主键契约
 */
public interface IBOPrimaryKeyContract extends IBusinessLogicContract {
	/**
	 * 对象编码
	 * 
	 * @return
	 */
	String getObjectCode();

	/**
	 * 获取-服务系列
	 * 
	 * @return 值
	 */
	Integer getSeries();

	/**
	 * 设置主键
	 * 
	 * @param keys
	 * @return
	 */
	boolean setPrimaryKey(Object... keys);

	/**
	 * 设置系列键
	 * 
	 * @param key
	 * @return
	 */
	boolean setSeriesKey(String key);
}
