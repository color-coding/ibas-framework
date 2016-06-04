package org.colorcoding.bobas.common;

import java.util.Collection;

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
	 * 添加条件集合
	 *
	 * @param collection
	 *            条件集合
	 */
	// void addRange(Iterable<ICondition> collection);

	/**
	 * 获取sql字符串（仅示意）
	 *
	 * @return sql字符串
	 */
	String getSqlString();
}