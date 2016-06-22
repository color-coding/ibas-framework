package org.colorcoding.ibas.bobas.data;

/**
 * 数据表 用于接受数据库查询数据
 */
public interface IDataTable {
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
	 * 获取-列集合
	 * 
	 * @return
	 */
	IDataTableColumns getColumns();

	/**
	 * 设置-列集合
	 * 
	 * @param columns
	 */
	void setColumns(IDataTableColumns columns);

	/**
	 * 获取-行集合
	 * 
	 * @return
	 */
	IDataTableRows getRows();

	/**
	 * 设置-行集合
	 * 
	 * @param rows
	 */
	void setRows(IDataTableRows rows);
}
