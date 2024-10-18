package org.colorcoding.ibas.bobas.common;

import org.colorcoding.ibas.bobas.data.IArrayList;

/**
 * 查询条件集合
 * 
 * @author Niuren.Zhu
 *
 */
public interface IConditions extends IArrayList<ICondition> {
	/**
	 * 新建条件
	 *
	 * @return 条件
	 */
	ICondition create();

	/**
	 * 获取条件
	 * 
	 * @param index 索引
	 * @return
	 */
	ICondition get(int index);
}