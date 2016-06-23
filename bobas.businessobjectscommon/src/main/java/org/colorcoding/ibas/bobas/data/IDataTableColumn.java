package org.colorcoding.ibas.bobas.data;

/**
 * 数据表列
 * 
 * @author Niuren.Zhu
 *
 */
public interface IDataTableColumn {
	/**
	 * 获取-名称
	 * 
	 * @return
	 */
	String getName();

	/**
	 * 设置-名称
	 * 
	 * @param name
	 */
	void setName(String name);

	/**
	 * 获取-数据类型
	 * 
	 * @return
	 */
	Class<?> getDataType();

	/**
	 * 设置-数据类型
	 * 
	 * @param type
	 */
	void setDataType(Class<?> type);

	/**
	 * 获取-描述
	 * 
	 * @return
	 */
	String getDescription();

	/**
	 * 设置-描述
	 * 
	 * @param description
	 */
	void setDescription(String description);
}
