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
	 * @return
	 */
	IDataTableColumn create();

	/**
	 * 创建新的列
	 * 
	 * @param name
	 *            名称
	 * @param type
	 *            类型
	 * @return
	 */
	IDataTableColumn create(String name, Class<?> type);

	/**
	 * 获取列
	 * 
	 * @param name
	 *            名称
	 * @return
	 */
	IDataTableColumn get(String name);
}
