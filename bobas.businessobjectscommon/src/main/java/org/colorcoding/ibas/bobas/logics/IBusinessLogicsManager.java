package org.colorcoding.ibas.bobas.logics;

/**
 * 业务逻辑管理员
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBusinessLogicsManager {
	/**
	 * 获取已存在的逻辑链
	 * 
	 * @param transId
	 *            事务标记
	 * @return
	 */
	IBusinessLogicsChain getChain(String transId);

	/**
	 * 创建新的逻辑链
	 * 
	 * @param transId
	 *            事务标记
	 * @return
	 */
	IBusinessLogicsChain createChain(String transId);

	/**
	 * 创建业务逻辑实例
	 * 
	 * @param contract
	 *            契约
	 * @return
	 * @throws NotFoundBusinessLogicsException
	 */
	IBusinessLogic createLogic(Class<?> contract) throws NotFoundBusinessLogicsException;
}
