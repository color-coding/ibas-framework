package org.colorcoding.ibas.bobas.core.fields;

import java.util.function.Predicate;

/**
 * 管理数据字段
 * 
 * @author Niuren.Zhu
 *
 */
public interface IManageFields {
	/**
	 * 获取字段
	 * 
	 * @return 字段
	 */
	IFieldData[] getFields();

	/**
	 * 获取字段
	 * 
	 * @return 字段
	 */
	IFieldData[] getFields(Predicate<? super IFieldData> filter);

	/**
	 * 获取字段
	 * 
	 * @return 字段
	 */
	IFieldData getField(String name);
}
