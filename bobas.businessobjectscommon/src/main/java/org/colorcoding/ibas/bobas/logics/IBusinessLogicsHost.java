package org.colorcoding.ibas.bobas.logics;

/**
 * 业务逻辑宿主
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBusinessLogicsHost {

	/**
	 * 获取逻辑契约
	 * 
	 * @return
	 */
	IBusinessLogicContract[] getContracts();
}
