package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emApprovalStepStatus;

/**
 * 审批流程
 * 
 * @author Niuren.Zhu
 *
 */
public abstract class ApprovalProcess implements IApprovalProcess {
	
	protected abstract void setApprovalData(IApprovalData value);

	protected abstract void setStatus(emApprovalStatus value);

	protected abstract void setStartedTime(DateTime value);

	protected abstract void setFinishedTime(DateTime value);

	@Override
	public IApprovalProcessStep currentStep() {
		if (this.getProcessSteps() == null) {
			return null;
		}
		for (int i = 0; i < this.getProcessSteps().length; i++) {
			IApprovalProcessStep step = this.getProcessSteps()[i];
			if (step == null) {
				continue;
			}
			if (step.getStatus() == emApprovalStepStatus.Processing) {
				// 当前进行的步骤
				return step;
			}
		}
		return null;
	}

	@Override
	public boolean start(IApprovalData data) {
		if (data == null) {
			return false;
		}

		return false;
	}
	
	@Override
	public void nextStep() {

	}
}
