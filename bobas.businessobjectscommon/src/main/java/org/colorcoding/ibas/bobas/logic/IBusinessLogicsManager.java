package org.colorcoding.ibas.bobas.logic;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;

/**
 * 业务逻辑管理员
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBusinessLogicsManager extends Iterable<IBusinessLogicChain> {
	/**
	 * 获取已存在的逻辑链
	 * 
	 * @param host 业务逻辑宿主
	 * @return
	 */
	IBusinessLogicChain getChain(IBusinessObject host);

	/**
	 * 创建新的逻辑链
	 * 
	 * @return
	 */
	IBusinessLogicChain createChain();

	/**
	 * 关闭逻辑链
	 * 
	 * @param transId 事务标记
	 * @return 成功移出，true；不成功或不存在，false
	 */
	void closeChains(String transId);

	/**
	 * 创建业务逻辑实例
	 * 
	 * @param contract 契约
	 * @return
	 * @throws NotFoundBusinessLogicException
	 */
	IBusinessLogic<?> createLogic(Class<?> contract) throws NotFoundBusinessLogicException;
}
