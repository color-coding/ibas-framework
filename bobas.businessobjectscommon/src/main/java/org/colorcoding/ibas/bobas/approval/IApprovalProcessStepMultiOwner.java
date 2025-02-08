package org.colorcoding.ibas.bobas.approval;

/**
 * 审批步骤
 * 
 * @author Niuren.Zhu
 *
 */
public interface IApprovalProcessStepMultiOwner extends IApprovalProcessStep {

	/**
	 * 所需批准者
	 * 
	 * @return
	 */
	int getApproversRequired();

	/**
	 * 步骤项目
	 * 
	 * @return
	 */
	IApprovalProcessStepItem[] getItems();

}
