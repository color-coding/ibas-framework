package org.colorcoding.ibas.bobas.logics;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;

/**
 * 业务逻辑
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBusinessLogic<B extends IBusinessObject> {
	/**
	 * 被影响的对象
	 * 
	 * @return
	 */
	B getBeAffected();

	/**
	 * 正向执行逻辑
	 * 
	 */
	void forward();

	/**
	 * 反向执行逻辑
	 * 
	 */
	void reverse();

	/**
	 * 提交逻辑
	 */
	void commit();
}
