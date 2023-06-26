package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emApprovalStepStatus;

/**
 * 审批流程步骤
 */
public abstract class ApprovalProcessStep implements IApprovalProcessStep {

	protected abstract void setId(int value);

	protected abstract void setStatus(emApprovalStepStatus value);

	protected abstract void setStartedTime(DateTime value);

	protected abstract void setFinishedTime(DateTime value);

	protected abstract void setJudgment(String value);

	@Override
	public String toString() {
		return String.format("{approval process step: %s %s}", this.getId(), this.getStatus());
	}

	/**
	 * 恢复为初始状态
	 */
	void restore() {
		this.setStatus(emApprovalStepStatus.PENDING);
		this.setStartedTime(DateTime.MAX_VALUE);
		this.setFinishedTime(DateTime.MAX_VALUE);
		this.setJudgment("");
		if (this instanceof IApprovalProcessStepMultiOwner) {
			IApprovalProcessStepMultiOwner multiStep = (IApprovalProcessStepMultiOwner) this;
			if (multiStep.getItems() != null) {
				ApprovalProcessStep itemStep = null;
				for (IApprovalProcessStepItem item : multiStep.getItems()) {
					if (item instanceof ApprovalProcessStep) {
						itemStep = (ApprovalProcessStep) item;
						itemStep.setStatus(this.getStatus());
						itemStep.setStartedTime(this.getStartedTime());
						itemStep.setFinishedTime(this.getFinishedTime());
						itemStep.setJudgment(this.getJudgment());
					}
				}
			}
		}
	}

	/**
	 * 开始进入审批
	 * 
	 * @throws UnlogicalException
	 */
	void start() throws UnlogicalException {
		if (this.getStatus() != emApprovalStepStatus.PENDING) {
			throw new UnlogicalException();
		}
		this.setStartedTime(DateTime.getNow());
		this.setStatus(emApprovalStepStatus.PROCESSING);
		if (this instanceof IApprovalProcessStepMultiOwner) {
			IApprovalProcessStepMultiOwner multiStep = (IApprovalProcessStepMultiOwner) this;
			if (multiStep.getItems() != null) {
				ApprovalProcessStep itemStep = null;
				for (IApprovalProcessStepItem item : multiStep.getItems()) {
					if (item instanceof ApprovalProcessStep) {
						itemStep = (ApprovalProcessStep) item;
						itemStep.setStartedTime(this.getStartedTime());
						itemStep.setStatus(this.getStatus());
					}
				}
			}
		}
	}

	/**
	 * 批准
	 * 
	 * @param judgment 意见
	 * @throws UnlogicalException
	 */
	public void approve(String judgment) throws UnlogicalException {
		if (this.getStatus() != emApprovalStepStatus.PROCESSING) {
			throw new UnlogicalException();
		}
		this.setFinishedTime(DateTime.getNow());
		this.setStatus(emApprovalStepStatus.APPROVED);
		this.setJudgment(judgment);
	}

	/**
	 * 拒绝
	 * 
	 * @param judgment 意见
	 * @throws UnlogicalException
	 */
	public void reject(String judgment) throws UnlogicalException {
		if (this.getStatus() != emApprovalStepStatus.PROCESSING) {
			throw new UnlogicalException();
		}
		this.setFinishedTime(DateTime.getNow());
		this.setStatus(emApprovalStepStatus.REJECTED);
		this.setJudgment(judgment);
	}

	/**
	 * 退回
	 * 
	 * @param judgment 意见
	 * @throws UnlogicalException
	 */
	public void retreat(String judgment) throws UnlogicalException {
		if (this.getStatus() != emApprovalStepStatus.PROCESSING) {
			throw new UnlogicalException();
		}
		this.setFinishedTime(DateTime.getNow());
		this.setStatus(emApprovalStepStatus.RETURNED);
		this.setJudgment(judgment);
	}

	/**
	 * 重置为进行中
	 * 
	 * @throws UnlogicalException
	 */
	public void reset() throws UnlogicalException {
		if (this.getStatus() != emApprovalStepStatus.APPROVED && this.getStatus() != emApprovalStepStatus.REJECTED) {
			throw new UnlogicalException();
		}
		this.setFinishedTime(DateTime.MAX_VALUE);
		this.setStatus(emApprovalStepStatus.PROCESSING);
		this.setJudgment("");
	}

	/**
	 * 跳过
	 * 
	 * @throws UnlogicalException
	 */
	public void skip() throws UnlogicalException {
		if (this.getStatus() != emApprovalStepStatus.PENDING) {
			throw new UnlogicalException();
		}
		this.setStartedTime(DateTime.getNow());
		this.setFinishedTime(DateTime.getNow());
		this.setStatus(emApprovalStepStatus.SKIPPED);
		this.setJudgment("");
		if (this instanceof IApprovalProcessStepMultiOwner) {
			IApprovalProcessStepMultiOwner multiStep = (IApprovalProcessStepMultiOwner) this;
			if (multiStep.getItems() != null) {
				ApprovalProcessStep itemStep = null;
				for (IApprovalProcessStepItem item : multiStep.getItems()) {
					if (item instanceof ApprovalProcessStep) {
						itemStep = (ApprovalProcessStep) item;
						itemStep.setStartedTime(this.getStartedTime());
						itemStep.setFinishedTime(this.getFinishedTime());
						itemStep.setStatus(this.getStatus());
						itemStep.setJudgment("");
					}
				}
			}
		}
	}

}
