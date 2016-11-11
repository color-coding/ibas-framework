package org.colorcoding.ibas.bobas.test.approval;

import org.colorcoding.ibas.bobas.approval.IApprovalData;
import org.colorcoding.ibas.bobas.approval.IApprovalProcessStep;
import org.colorcoding.ibas.bobas.approval.UnauthorizedException;
import org.colorcoding.ibas.bobas.core.IBORepository;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.organization.IUser;

public class ApprovalProcess extends org.colorcoding.ibas.bobas.approval.ApprovalProcess {

	private String name;

	@Override
	public String getName() {
		return this.name;
	}

	public void setName(String value) {
		this.name = value;
	}

	private emApprovalStatus status;

	@Override
	public emApprovalStatus getStatus() {
		return this.status;
	}

	@Override
	protected void setStatus(emApprovalStatus value) {
		this.status = value;
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

	private IApprovalData approvalData;

	@Override
	public IApprovalData getApprovalData() {
		return this.approvalData;
	}

	@Override
	public void setApprovalData(IApprovalData value) {
		this.approvalData = value;
	}

	private IUser owner;

	@Override
	public IUser getOwner() {
		return this.owner;
	}

	public void setOwner(IUser value) {
		this.owner = value;
	}

	private IApprovalProcessStep[] processSteps;

	@Override
	public IApprovalProcessStep[] getProcessSteps() {
		return this.processSteps;
	}

	public void setProcessSteps(IApprovalProcessStep[] value) {
		this.processSteps = value;
	}

	@Override
	public void checkToSave(IUser user) throws UnauthorizedException {

	}

	@Override
	protected void saveProcess(IBORepository boRepository) throws Exception {

	}

	@Override
	public boolean isNew() {
		return false;
	}

}
