package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.organization.IUser;

/**
 * 审批步骤
 * 
 * @author Niuren.Zhu
 *
 */
public interface IApprovalProcessStepSingleOwner extends IApprovalProcessStep {

	/**
	 * 获取步骤所有者
	 * 
	 * @return
	 */
	IUser getOwner();

}
