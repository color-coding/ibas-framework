package org.colorcoding.ibas.bobas.test.approval;

import org.colorcoding.ibas.bobas.approval.IApprovalProcessStepCondition;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emApprovalStepStatus;
import org.colorcoding.ibas.bobas.organization.IUser;

public class ApprovalProcessStep extends org.colorcoding.ibas.bobas.approval.ApprovalProcessStep {

	private int id;

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	protected void setId(int value) {
		this.id = value;
	}

	private IApprovalProcessStepCondition[] conditions;

	@Override
	public IApprovalProcessStepCondition[] getConditions() {
		return this.conditions;
	}

	protected void setConditions(IApprovalProcessStepCondition[] value) {
		this.conditions = value;
	}

	private DateTime startedTime;

	@Override
	public DateTime getStartedTime() {
		return this.startedTime;
	}

	@Override
	protected void setStartedTime(DateTime value) {
		this.startedTime = value;
	}

	private DateTime finishedTime;

	@Override
	public DateTime getFinishedTime() {
		return this.finishedTime;
	}

	@Override
	protected void setFinishedTime(DateTime value) {
		this.finishedTime = value;
	}

	private IUser owner;

	@Override
	public IUser getOwner() {
		return this.owner;
	}

	protected void setOwner(IUser value) {
		this.owner = value;
	}

	private String judgment;

	@Override
	public String getJudgment() {
		return this.judgment;
	}

	@Override
	protected void setJudgment(String value) {
		this.judgment = value;
	}

	private emApprovalStepStatus status;

	@Override
	public emApprovalStepStatus getStatus() {
		return this.status;
	}

	@Override
	protected void setStatus(emApprovalStepStatus value) {
		this.status = value;
	}

}
