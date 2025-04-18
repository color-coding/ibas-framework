package org.colorcoding.ibas.bobas.approval;

/**
 * 审批步骤（子项）
 * 
 * @author Niuren.Zhu
 *
 */
public interface IApprovalProcessStepItem extends IApprovalProcessStep {
	/**
	 * 获取-父项
	 * 
	 * @return
	 */
	IApprovalProcessStepMultiOwner getParent();
}
