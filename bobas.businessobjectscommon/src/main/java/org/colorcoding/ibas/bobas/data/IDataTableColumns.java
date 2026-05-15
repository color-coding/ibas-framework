package org.colorcoding.ibas.bobas.data;

import java.util.List;

/**
 * 数据表列集合
 *
 * @author Niuren.Zhu
 *
 */
public interface IDataTableColumns extends List<IDataTableColumn> {
	/**
	 * 创建新的列
	 *
	 * @return 新创建的列实例
	 */
	IDataTableColumn create();

	/**
	 * 创建新的列
	 *
	 * @param name 列名称
	 * @param type 数据类型
	 * @return 新创建的列实例
	 */
	IDataTableColumn create(String name, Class<?> type);

	/**
	 * 按名称获取列
	 *
	 * @param name 列名称
	 * @return 列实例（未找到返回null）
	 */
	IDataTableColumn get(String name);
}
