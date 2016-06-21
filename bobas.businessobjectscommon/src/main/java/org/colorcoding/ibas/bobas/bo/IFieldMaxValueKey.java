package org.colorcoding.ibas.bobas.bo;

import org.colorcoding.ibas.bobas.common.IConditions;
import org.colorcoding.ibas.bobas.core.fields.IFieldDataDb;
/**
 * 自定义主键
 * 
 * 从当前BO的主表获取某个字段的最大值
 * 
 * @author Niuren.Zhu
 *
 */
public interface IFieldMaxValueKey extends ICustomPrimaryKeys {
	/**
	 * 获取最大值的字段名称
	 * 
	 * @return
	 */
	IFieldDataDb getMaxValueField();

	/**
	 * 获取主键的条件
	 * 
	 * 例：'ItemCode' = 'A00001' AND 'BatchNum' = 'B000000001'
	 * 
	 * @return 条件
	 */
	IConditions getMaxValueConditions();
}
