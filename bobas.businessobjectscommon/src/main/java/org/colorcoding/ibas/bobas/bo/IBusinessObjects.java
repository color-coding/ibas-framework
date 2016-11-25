package org.colorcoding.ibas.bobas.bo;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.core.IBusinessObjectListBase;

public interface IBusinessObjects<E extends IBusinessObject, P extends IBusinessObject>
		extends IBusinessObjectListBase<E> {
	/**
	 * 子项查询
	 * 
	 * @return 子项的查询条件
	 */
	ICriteria getElementCriteria();

	/**
	 * 获取子项元素类型
	 * 
	 * @return 元素类型
	 */
	Class<?> getElementType();

	/**
	 * 添加元素
	 */
	boolean add(E item);

	/**
	 * 删除元素（仅新对象移出集合）
	 * 
	 * @param item
	 *            删除的对象
	 */
	void delete(E item);

	/**
	 * 删除元素（仅新对象移出集合）
	 * 
	 * @param index
	 *            删除的对象的索引
	 */
	void delete(int index);

	/**
	 * 删除全部元素（仅新对象移出集合）
	 */
	void deleteAll();
}
