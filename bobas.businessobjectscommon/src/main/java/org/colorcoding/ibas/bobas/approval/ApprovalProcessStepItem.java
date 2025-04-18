package org.colorcoding.ibas.bobas.approval;

/**
 * 审批步骤子项
 * 
 * @author Niuren.Zhu
 *
 */
public abstract class ApprovalProcessStepItem<T extends IProcessStepData> extends ApprovalProcessStep<T> {

	public ApprovalProcessStepItem(T stepData, ApprovalProcessStepMultiOwner<T> parent) {
		super(stepData);
		this.parent = parent;
	}

	private ApprovalProcessStepMultiOwner<T> parent;

	public ApprovalProcessStepMultiOwner<T> getParent() {
		return this.parent;
	}

}
