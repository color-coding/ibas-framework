package org.colorcoding.ibas.bobas.logic.common;

import org.colorcoding.ibas.bobas.approval.IApprovalData;
import org.colorcoding.ibas.bobas.logic.IBusinessLogicContract;

/**
 * 业务对象审批契约
 */
public interface IBOApprovalContract extends IBusinessLogicContract {

	/**
	 * 业务对象实例
	 * 
	 * @return
	 */
	IApprovalData getHost();
}
