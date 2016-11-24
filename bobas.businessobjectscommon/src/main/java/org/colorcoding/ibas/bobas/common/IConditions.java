package org.colorcoding.ibas.bobas.common;

import org.colorcoding.ibas.bobas.util.Collection;

/**
 * 查询条件集合
 * 
 * @author Niuren.Zhu
 *
 */
public interface IConditions extends Collection<ICondition> {
	/**
	 * 新建条件
	 *
	 * @return 条件
	 */
	ICondition create();

	/**
	 * 获取sql字符串（仅示意）
	 *
	 * @return sql字符串
	 */
	String getSqlString();

	/**
	 * 获取条件
	 * 
	 * @param index
	 *            索引
	 * @return
	 */
	ICondition get(int index);

}