package org.colorcoding.ibas.bobas.common;

import org.colorcoding.ibas.bobas.data.List;

/**
 * 子对象查询
 * 
 * @author Niuren.Zhu
 *
 */
public interface IChildCriterias extends List<IChildCriteria> {
	/**
	 * 创建子项查询
	 * 
	 * @return
	 */
	IChildCriteria create();

	/**
	 * 获取子对象查询
	 * 
	 * @param index 索引
	 * @return
	 */
	IChildCriteria get(int index);

	/**
	 * 获取子项查询
	 * 
	 * @param propertyPath 查询的子项
	 * @return
	 */
	IChildCriteria getCriteria(String propertyPath);
}