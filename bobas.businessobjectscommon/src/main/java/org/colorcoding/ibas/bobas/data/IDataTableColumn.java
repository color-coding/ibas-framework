package org.colorcoding.ibas.bobas.data;

/**
 * 数据表列
 *
 * @author Niuren.Zhu
 *
 */
public interface IDataTableColumn {
	/**
	 * 获取列名称
	 *
	 * @return 列名称
	 */
	String getName();

	/**
	 * 设置列名称
	 *
	 * @param name 列名称
	 */
	void setName(String name);

	/**
	 * 获取数据类型
	 *
	 * @return 数据类型的Class
	 */
	Class<?> getDataType();

	/**
	 * 设置数据类型
	 *
	 * @param type 数据类型
	 */
	void setDataType(Class<?> type);

	/**
	 * 获取描述
	 *
	 * @return 描述
	 */
	String getDescription();

	/**
	 * 设置描述
	 *
	 * @param description 描述
	 */
	void setDescription(String description);
}
