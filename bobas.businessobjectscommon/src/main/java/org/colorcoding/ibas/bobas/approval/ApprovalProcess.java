package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emApprovalStepStatus;
import org.colorcoding.ibas.bobas.expressions.JudmentOperationException;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;

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

	protected void restore() {
		this.setApprovalData(null);
		this.setStartedTime(DateTime.getMaxValue());
		this.setFinishedTime(DateTime.getMaxValue());
		this.setStatus(emApprovalStatus.Unaffected);
		for (IApprovalProcessStep item : this.getProcessSteps()) {
			ApprovalProcessStep stepItem = (ApprovalProcessStep) item;
			stepItem.restore();// 重置初始状态
		}
	}

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
		this.restore();// 重置初始状态
		for (IApprovalProcessStep item : this.getProcessSteps()) {
			ApprovalProcessStep stepItem = (ApprovalProcessStep) item;
			ApprovalDataJudgmentLinks judgmentLinks = new ApprovalDataJudgmentLinks();
			judgmentLinks.parsingConditions(stepItem.getConditions());
			try {
				boolean done = judgmentLinks.judge((IBusinessObjectBase) data);
				if (done) {
					// 满足条件，开启此步骤
					stepItem.start();
					this.setApprovalData(data);
					this.setStartedTime(DateTime.getNow());
					this.setStatus(emApprovalStatus.Processing);
					return true;
				} else {
					// 跳过此步骤
					stepItem.skip();
				}
			} catch (JudmentOperationException | UnlogicalException e) {
				RuntimeLog.log(e);
				return false;
			}
		}
		return false;
	}

	@Override
	public void nextStep() {

	}
}
