package org.colorcoding.ibas.bobas.logics;

import org.colorcoding.ibas.bobas.core.IBORepository;

/**
 * 业务逻辑
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBusinessLogic {

	/**
	 * 设置-契约
	 * 
	 * @param contract
	 */
	void setContract(IBusinessLogicContract contract);

	/**
	 * 设置-仓库
	 * 
	 * @param boRepository
	 */
	void setRepository(IBORepository repository);

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
}
