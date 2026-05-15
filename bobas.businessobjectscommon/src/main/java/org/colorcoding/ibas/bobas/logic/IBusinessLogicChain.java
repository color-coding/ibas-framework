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
	 * @return 触发逻辑链的业务对象
	 */
	<T extends IBusinessObject> T getTrigger();

	/**
	 * 设置-触发对象
	 *
	 * @param trigger 触发逻辑链的业务对象
	 */
	<T extends IBusinessObject> void setTrigger(T trigger);

	/**
	 * 设置-触发对象副本（用于反向逻辑比较）
	 *
	 * @param trigger 触发对象的数据库副本
	 */
	<T extends IBusinessObject> void setTriggerCopy(T trigger);

	/**
	 * 执行逻辑链（先反向再正向）
	 */
	void execute();

	/**
	 * 添加跳过的逻辑
	 *
	 * @param contractType 逻辑对应的契约类型，该契约的逻辑将被跳过
	 */
	void addSkipLogics(Class<?> contractType);
}