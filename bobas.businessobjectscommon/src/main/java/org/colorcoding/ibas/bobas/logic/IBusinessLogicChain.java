package org.colorcoding.ibas.bobas.logic;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;

/**
 * 业务逻辑链
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBusinessLogicChain extends AutoCloseable {
	/**
	 * 获取-触发对象
	 * 
	 * @return
	 */
	<T extends IBusinessObject> T getTrigger();

	/**
	 * 设置-触发对象
	 * 
	 * @param trigger
	 */
	<T extends IBusinessObject> void setTrigger(T trigger);

	/**
	 * 设置-触发对象副本
	 * 
	 * @param trigger
	 */
	<T extends IBusinessObject> void setTriggerCopy(T trigger);

	/**
	 * 执行逻辑
	 */
	void execute();

	/**
	 * 添加跳过的逻辑
	 * 
	 * @param contractType 逻辑对应的契约类型
	 */
	void addSkipLogics(Class<?> contractType);
}
