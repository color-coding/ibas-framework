package org.colorcoding.ibas.bobas.approval;

/**
 * 多人审批步骤
 * 
 * @author Niuren.Zhu
 *
 */
public abstract class ApprovalProcessStepMultiOwner<T extends IProcessStepData> extends ApprovalProcessStep<T> {

	public ApprovalProcessStepMultiOwner(T stepData) {
		super(stepData);
	}

	/**
	 * 所需批准者
	 * 
	 * @return
	 */
	public abstract int getApproversRequired();

	/**
	 * 步骤项目
	 * 
	 * @return
	 */
	public abstract ApprovalProcessStepItem<T>[] getItems();

}
