package org.colorcoding.ibas.bobas.logic;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.repository.ITransaction;

/**
 * 业务逻辑链
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBusinessLogicChain {
	/**
	 * 获取-触发对象
	 * 
	 * @return
	 */
	<T extends IBusinessObject> T getTrigger();

	/**
	 * 设置-触发对象
	 * 
	 * @param bo
	 */
	<T extends IBusinessObject> void setTrigger(T trigger);

	/**
	 * 使用触发对象副本
	 * 
	 * @param bo
	 */
	<T extends IBusinessObject> void setTriggerCopy(T trigger);

	/**
	 * 使用事务
	 * 
	 * @param transaction 事务
	 */
	void setTransaction(ITransaction transaction);

	/**
	 * 正向执行逻辑
	 * 
	 */
	void forwardLogics();

	/**
	 * 反向执行逻辑
	 * 
	 */
	void reverseLogics();

	/**
	 * 提交逻辑
	 */
	void commit();
}
