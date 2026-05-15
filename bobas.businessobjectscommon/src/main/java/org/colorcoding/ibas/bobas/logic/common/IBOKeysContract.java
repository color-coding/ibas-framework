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
	 * @return 业务对象
	 */
	IBusinessObject getHost();

	/**
	 * 对象编码
	 *
	 * @return 业务对象编码
	 */
	String getObjectCode();

	/**
	 * 设置主键
	 *
	 * @param keys 主键值数组
	 * @return 设置成功返回true；对象类型不支持返回false
	 */
	boolean setPrimaryKey(Object... keys);

	/**
	 * 获取-服务系列
	 *
	 * @return 系列编号；未设置时返回0
	 */
	Integer getSeries();

	/**
	 * 设置系列键
	 *
	 * @param key 系列键值
	 * @return 设置成功返回true；对象不支持系列键返回false
	 */
	boolean setSeriesKey(String key);

	/**
	 * 获取-最大编号键值
	 *
	 * @return 最大编号的条件拼接键
	 */
	String getMaxValueKey();

	/**
	 * 获取-最大值字段
	 *
	 * @return 最大值属性信息；不支持时返回null
	 */
	IPropertyInfo<?> getMaxValueField();

	/**
	 * 获取-最大值条件属性
	 *
	 * @return 条件属性数组；不支持时返回空数组
	 */
	IPropertyInfo<?>[] getMaxValueConditions();

	/**
	 * 设置最大值
	 *
	 * @param value 最大编号值
	 * @return 设置成功返回true；不支持返回false
	 */
	boolean setMaxValue(Integer value);

	/**
	 * 获取-步长
	 *
	 * @return 编号递增步长；默认1
	 */
	int getMaxValueStep();
}