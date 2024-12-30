package org.colorcoding.ibas.bobas.logic.common;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.logic.IBusinessLogicContract;

/**
 * 业务对象主键契约
 */
public interface IBOKeysContract extends IBusinessLogicContract {

	/**
	 * 业务对象实例
	 * 
	 * @return
	 */
	IBusinessObject getHost();

	/**
	 * 对象编码
	 * 
	 * @return
	 */
	String getObjectCode();

	/**
	 * 设置主键
	 * 
	 * @param keys
	 * @return
	 */
	boolean setPrimaryKey(Object... keys);

	/**
	 * 获取-服务系列
	 * 
	 * @return 值
	 */
	Integer getSeries();

	/**
	 * 设置系列键
	 * 
	 * @param key
	 * @return
	 */
	boolean setSeriesKey(String key);

	/**
	 * 获取-最大编号键值
	 * 
	 * @return
	 */
	String getMaxValueKey();

	/**
	 * 获取-最大字段
	 * 
	 * @return
	 */
	IPropertyInfo<?> getMaxValueField();

	/**
	 * 获取-最大值条件
	 * 
	 * @return
	 */
	IPropertyInfo<?>[] getMaxValueConditions();

	/**
	 * 设置最大值
	 * 
	 * @param value
	 * @return
	 */
	boolean setMaxValue(Integer value);

	/**
	 * 获取-步长
	 * 
	 * @return
	 */
	int getMaxValueStep();
}
