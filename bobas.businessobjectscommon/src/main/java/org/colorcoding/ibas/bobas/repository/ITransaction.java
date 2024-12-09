package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.ICriteria;

public interface ITransaction {

	/**
	 * 回滚事务
	 */
	void rollback();

	/**
	 * 提交事务
	 */
	void commit();

	/**
	 * 查询对象
	 * 
	 * @param criteria 查询条件
	 * 
	 * @param boType   查询的对象类型
	 * 
	 * @return 操作结果及新对象实例
	 */
	<T extends IBusinessObject> T[] fetch(ICriteria criteria, Class<T> boType);

	/**
	 * 保存对象
	 * 
	 * @param bo 被保存的对象
	 * 
	 * @return 操作结果及新对象实例
	 */
	<T extends IBusinessObject> T save(T bo);
}
