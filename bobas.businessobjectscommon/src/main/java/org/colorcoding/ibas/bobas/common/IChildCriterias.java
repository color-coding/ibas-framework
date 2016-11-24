package org.colorcoding.ibas.bobas.common;

import org.colorcoding.ibas.bobas.util.Collection;

/**
 * 子对象查询
 * 
 * @author Niuren.Zhu
 *
 */
public interface IChildCriterias extends Collection<IChildCriteria> {
	/**
	 * 创建子项查询
	 * 
	 * @return
	 */
	IChildCriteria create();

	/**
	 * 获取子对象查询
	 * 
	 * @param index
	 *            索引
	 * @return
	 */
	IChildCriteria get(int index);

	/**
	 * 获取子项查询
	 * 
	 * @param propertyPath
	 *            查询的子项
	 * @return
	 */
	IChildCriteria getCriteria(String propertyPath);
}