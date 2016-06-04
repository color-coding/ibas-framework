package org.colorcoding.bobas.bo;

import org.colorcoding.bobas.common.ICondition;
import org.colorcoding.bobas.data.KeyValue;

/**
 * 自定义主键
 * 
 * 从当前BO的主表获取某个字段的最大值
 * 
 * @author Niuren.Zhu
 *
 */
public interface ICustomPrimaryKeys {
	/**
	 * 获取最大值的字段名称
	 * 
	 * @return
	 */
	String getMaxValueField();

	/**
	 * 设置主键
	 * 
	 * @param keys
	 *            当前键的值
	 * @return 使用的字段值
	 */
	KeyValue[] setPrimaryKeys(KeyValue[] keys);

	/**
	 * 获取主键的条件
	 * 
	 * 例：'ItemCode' = 'A00001' AND 'BatchNum' = 'B000000001'
	 * 
	 * @return 条件
	 */
	ICondition[] getPrimaryKeyConditions();
}
