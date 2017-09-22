package org.colorcoding.ibas.bobas.logics;

import org.colorcoding.ibas.bobas.core.IBORepository;

/**
 * 业务逻辑链
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBusinessLogicChain {
	/**
	 * 设置-组标记
	 * 
	 * @param value
	 */
	void setGroup(String value);

	/**
	 * 获取-组标记
	 * 
	 * @param value
	 */
	String getGroup();

	/**
	 * 获取-触发逻辑链对象
	 * 
	 * @return
	 */
	IBusinessLogicsHost getTrigger();

	/**
	 * 设置-触发逻辑链对象
	 * 
	 * @param bo
	 */
	void setTrigger(IBusinessLogicsHost trigger);

	/**
	 * 改变业务对象仓库
	 * 
	 * @param boRepository
	 *            仓库
	 * @return 成功，true；失败，false
	 */
	void useRepository(IBORepository boRepository);

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
