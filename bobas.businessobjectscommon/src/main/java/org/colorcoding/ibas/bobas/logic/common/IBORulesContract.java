package org.colorcoding.ibas.bobas.logic.common;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.logic.IBusinessLogicContract;

/**
 * 业务对象逻辑契约
 */
public interface IBORulesContract extends IBusinessLogicContract {

	/**
	 * 业务对象实例
	 * 
	 * @return
	 */
	IBusinessObject getHost();
}
