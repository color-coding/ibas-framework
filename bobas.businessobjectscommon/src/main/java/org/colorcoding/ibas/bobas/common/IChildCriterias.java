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
	 * @return 新建的子项查询
	 */
	IChildCriteria create();

	/**
	 * 获取子对象查询
	 *
	 * @param index 索引
	 * @return 子项查询
	 */
	IChildCriteria get(int index);

	/**
	 * 获取子项查询
	 *
	 * @param propertyPath 属性路径
	 * @return 匹配的子项查询；未找到返回null
	 */
	IChildCriteria getCriteria(String propertyPath);
}