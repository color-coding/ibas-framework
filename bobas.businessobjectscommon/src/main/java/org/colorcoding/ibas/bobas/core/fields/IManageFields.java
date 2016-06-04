package org.colorcoding.ibas.bobas.core.fields;

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
	 * 获取主键字段
	 * 
	 * @return 主键字段
	 */
	IFieldData[] getKeyFields();

	/**
	 * 获取字段
	 * 
	 * @return 字段
	 */
	IFieldData getField(String name);
}
