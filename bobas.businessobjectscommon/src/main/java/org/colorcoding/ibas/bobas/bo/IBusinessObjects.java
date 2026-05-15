package org.colorcoding.ibas.bobas.bo;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.data.List;

public interface IBusinessObjects<E extends IBusinessObject, P extends IBusinessObject> extends List<E> {
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
	 * 创建新的元素实例
	 *
	 * @return 新的元素实例
	 */
	E create();

	/**
	 * 添加元素
	 *
	 * @param item 添加的对象
	 * @return 是否成功添加
	 */
	boolean add(E item);

	/**
	 * 添加集合
	 *
	 * @param items 添加的集合
	 * @return 是否成功添加
	 */
	boolean addAll(Iterable<? extends E> items);

	/**
	 * 删除元素（新建对象移出集合，非新建对象标记删除）
	 *
	 * @param item 删除的对象
	 */
	void delete(E item);

	/**
	 * 删除元素（新建对象移出集合，非新建对象标记删除）
	 *
	 * @param index 删除的对象的索引
	 */
	void delete(int index);

	/**
	 * 删除全部元素（新建对象移出集合，非新建对象标记删除）
	 */
	void deleteAll();
}