package org.colorcoding.ibas.bobas.data;

import java.util.List;

/**
 * 数据表行集合
 *
 * @author Niuren.Zhu
 *
 */
public interface IDataTableRows extends List<IDataTableRow> {
	/**
	 * 创建新的行
	 *
	 * @return 新创建的行实例
	 */
	IDataTableRow create();
}
