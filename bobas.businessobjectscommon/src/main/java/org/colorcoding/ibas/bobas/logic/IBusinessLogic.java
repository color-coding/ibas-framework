package org.colorcoding.ibas.bobas.logic;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;

/**
 * 业务逻辑
 *
 * @author Niuren.Zhu
 *
 */
public interface IBusinessLogic<T extends IBusinessObject> {
	/**
	 * 被影响的对象
	 *
	 * @return 被影响的业务对象；未加载时为null
	 */
	T getBeAffected();

	/**
	 * 正向执行逻辑
	 */
	void forward();

	/**
	 * 反向执行逻辑（撤销）
	 */
	void reverse();
}