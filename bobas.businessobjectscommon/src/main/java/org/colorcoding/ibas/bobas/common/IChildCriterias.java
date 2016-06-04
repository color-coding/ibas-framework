package org.colorcoding.ibas.bobas.common;

import java.util.Collection;

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
	 * 添加子项查询
	 * 
	 * @param collection
	 */
	// void addRange(Iterable<IChildCriteria> collection);

	/**
	 * 获取子项查询
	 * 
	 * @param propertyPath
	 *            查询的子项
	 * @return
	 */
	IChildCriteria getCriteria(String propertyPath);
}