package org.colorcoding.ibas.bobas.logics;

import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;

/**
 * 业务逻辑
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBusinessLogic<B extends IBusinessObjectBase> {
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
	 * 被影响的对象
	 * 
	 * @return
	 */
	B getBeAffected();

	/**
	 * 是否完成
	 * 
	 * @return
	 */
	boolean isDone();

	/**
	 * 提交逻辑
	 */
	void commit();
}
