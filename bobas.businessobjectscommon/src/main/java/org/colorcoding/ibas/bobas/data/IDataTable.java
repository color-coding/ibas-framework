package org.colorcoding.ibas.bobas.data;

/**
 * 数据表，用于接收数据库查询数据
 */
public interface IDataTable {
	/**
	 * 获取表名称
	 *
	 * @return 表名称
	 */
	String getName();

	/**
	 * 设置表名称
	 *
	 * @param name 表名称
	 */
	void setName(String name);

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

	/**
	 * 获取列集合
	 *
	 * @return 列集合
	 */
	IDataTableColumns getColumns();

	/**
	 * 获取行集合
	 *
	 * @return 行集合
	 */
	IDataTableRows getRows();

	/**
	 * 预分配行容量，减少扩容次数
	 *
	 * @param rowCount 预期行数
	 */
	void ensureCapacity(int rowCount);
}
