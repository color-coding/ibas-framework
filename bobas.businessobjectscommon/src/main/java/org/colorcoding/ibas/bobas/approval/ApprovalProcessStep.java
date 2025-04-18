package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emApprovalStepStatus;
import org.colorcoding.ibas.bobas.organization.IUser;

/**
 * 审批流程步骤
 */
public abstract class ApprovalProcessStep<T extends IProcessStepData> {

	public ApprovalProcessStep(T stepData) {
		this.setStepData(stepData);
	}

	private T stepData;

	protected final T getStepData() {
		return stepData;
	}

	private final void setStepData(T stepData) {
		this.stepData = stepData;
	}

	public final int getId() {
		return this.getStepData().getId();
	}

	public final DateTime getStartedTime() {
		return this.getStepData().getStartedTime();
	}

	protected final void setStartedTime(DateTime value) {
		this.getStepData().setStartedTime(value);
	}

	public final DateTime getFinishedTime() {
		return this.getStepData().getFinishedTime();
	}

	protected final void setFinishedTime(DateTime value) {
		this.getStepData().setFinishedTime(value);
	}

	public final String getJudgment() {
		return this.getStepData().getJudgment();
	}

	protected final void setJudgment(String value) {
		this.getStepData().setJudgment(value);
	}

	public final emApprovalStepStatus getStatus() {
		return this.getStepData().getStatus();
	}

	protected final void setStatus(emApprovalStepStatus value) {
		this.getStepData().setStatus(value);
	}

	public abstract IApprovalProcessStepCondition[] getConditions();

	@Override
	public String toString() {
		return String.format("{approval process step: %s %s}", this.getId(), this.getStatus());
	}

	/**
	 * 步骤所有者
	 * 
	 * @return
	 */
	public abstract IUser getOwner();

	/**
	 * 恢复为初始状态
	 */
	void restore() {
		this.setStatus(emApprovalStepStatus.PENDING);
		this.setStartedTime(DateTimes.VALUE_MAX);
		this.setFinishedTime(DateTimes.VALUE_MIN);
		this.setJudgment("");
		if (this instanceof ApprovalProcessStepMultiOwner) {
			ApprovalProcessStepMultiOwner<T> multiStep = (ApprovalProcessStepMultiOwner<T>) this;
			if (multiStep.getItems() != null) {
				ApprovalProcessStep<?> itemStep = null;
				for (ApprovalProcessStep<T> item : multiStep.getItems()) {
					if (item instanceof ApprovalProcessStep) {
						itemStep = (ApprovalProcessStep<?>) item;
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
	 * @throws ApprovalException
	 */
	void start() throws ApprovalException {
		if (this.getStatus() != emApprovalStepStatus.PENDING) {
			throw new ApprovalException();
		}
		this.setStartedTime(DateTimes.now());
		this.setStatus(emApprovalStepStatus.PROCESSING);
		if (this instanceof ApprovalProcessStepMultiOwner) {
			ApprovalProcessStepMultiOwner<T> multiStep = (ApprovalProcessStepMultiOwner<T>) this;
			if (multiStep.getItems() != null) {
				ApprovalProcessStep<?> itemStep = null;
				for (ApprovalProcessStep<T> item : multiStep.getItems()) {
					if (item instanceof ApprovalProcessStep) {
						itemStep = (ApprovalProcessStep<?>) item;
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
	 * @throws ApprovalException
	 */
	public void approve(String judgment) throws ApprovalException {
		if (this.getStatus() != emApprovalStepStatus.PROCESSING) {
			throw new ApprovalException();
		}
		this.setFinishedTime(DateTimes.now());
		this.setStatus(emApprovalStepStatus.APPROVED);
		this.setJudgment(judgment);
	}

	/**
	 * 拒绝
	 * 
	 * @param judgment 意见
	 * @throws ApprovalException
	 */
	public void reject(String judgment) throws ApprovalException {
		if (this.getStatus() != emApprovalStepStatus.PROCESSING) {
			throw new ApprovalException();
		}
		this.setFinishedTime(DateTimes.now());
		this.setStatus(emApprovalStepStatus.REJECTED);
		this.setJudgment(judgment);
	}

	/**
	 * 退回
	 * 
	 * @param judgment 意见
	 * @throws ApprovalException
	 */
	public void retreat(String judgment) throws ApprovalException {
		if (this.getStatus() != emApprovalStepStatus.PROCESSING) {
			throw new ApprovalException();
		}
		this.setFinishedTime(DateTimes.now());
		this.setStatus(emApprovalStepStatus.RETURNED);
		this.setJudgment(judgment);
	}

	/**
	 * 重置为进行中
	 * 
	 * @throws ApprovalException
	 */
	public void reset() throws ApprovalException {
		if (this.getStatus() != emApprovalStepStatus.APPROVED && this.getStatus() != emApprovalStepStatus.REJECTED) {
			throw new ApprovalException();
		}
		this.setFinishedTime(DateTimes.VALUE_MAX);
		this.setStatus(emApprovalStepStatus.PROCESSING);
		this.setJudgment("");
	}

	/**
	 * 跳过
	 * 
	 * @throws ApprovalException
	 */
	public void skip() throws ApprovalException {
		if (this.getStatus() != emApprovalStepStatus.PENDING) {
			throw new ApprovalException();
		}
		this.setStartedTime(DateTimes.now());
		this.setFinishedTime(DateTimes.now());
		this.setStatus(emApprovalStepStatus.SKIPPED);
		this.setJudgment("");
		if (this instanceof ApprovalProcessStepMultiOwner) {
			ApprovalProcessStepMultiOwner<T> multiStep = (ApprovalProcessStepMultiOwner<T>) this;
			if (multiStep.getItems() != null) {
				ApprovalProcessStep<?> itemStep = null;
				for (ApprovalProcessStep<T> item : multiStep.getItems()) {
					if (item instanceof ApprovalProcessStep) {
						itemStep = (ApprovalProcessStep<?>) item;
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
