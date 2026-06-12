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

	/**
	 * 预分配行容量，减少扩容次数
	 *
	 * @param capacity 最小容量
	 */
	void ensureCapacity(int capacity);
}
