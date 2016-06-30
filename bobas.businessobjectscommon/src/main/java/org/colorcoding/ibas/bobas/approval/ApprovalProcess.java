package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emApprovalResult;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emApprovalStepStatus;
import org.colorcoding.ibas.bobas.expressions.JudmentOperationException;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;
import org.colorcoding.ibas.bobas.organization.IOrganizationManager;
import org.colorcoding.ibas.bobas.organization.IUser;
import org.colorcoding.ibas.bobas.organization.InvalidAuthorizationException;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;

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

	private IUser owner;

	@Override
	public IUser getOwner() {
		if (this.owner == null || this.owner.getId() != this.getApprovalData().getDataOwner()) {
			// 通过审批数据的所有者去组织结构里找用户
			IOrganizationManager orgManger = OrganizationFactory.createManager();
			this.owner = orgManger.getUser((int) this.getApprovalData().getDataOwner());
		}
		return this.owner;
	}

	@Override
	public String toString() {
		return String.format("ap [%s] [%s]", this.getName(), this.getStatus());
	}

	/**
	 * 获取步骤
	 * 
	 * @param id
	 *            步骤编号
	 * @return
	 */
	private ApprovalProcessStep getProcessStep(int id) {
		if (this.getProcessSteps() == null) {
			return null;
		}
		for (IApprovalProcessStep item : this.getProcessSteps()) {
			if (item.getId() == id) {
				return (ApprovalProcessStep) item;
			}
		}
		return null;
	}

	/**
	 * 获取前一个步骤
	 * 
	 * @param step
	 *            基准步骤
	 * @return
	 */
	private IApprovalProcessStep getPreviousProcessStep() {
		if (this.getProcessSteps() == null) {
			return null;
		}
		if (this.getStatus() == emApprovalStatus.Processing) {
			IApprovalProcessStep preStep = null;
			IApprovalProcessStep step = this.currentStep();
			for (IApprovalProcessStep item : this.getProcessSteps()) {
				if (step == item) {
					return preStep;
				}
				preStep = item;
			}
		} else if (this.getStatus() == emApprovalStatus.Approved) {
			for (IApprovalProcessStep item : this.getProcessSteps()) {
				if (item.getStatus() == emApprovalStepStatus.Approved) {
					return item;
				}
			}
		} else if (this.getStatus() == emApprovalStatus.Rejected) {
			for (IApprovalProcessStep item : this.getProcessSteps()) {
				if (item.getStatus() == emApprovalStepStatus.Rejected) {
					return item;
				}
			}
		}
		return null;
	}

	/**
	 * 恢复初始状态
	 */
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

	/**
	 * 激活下一个步骤
	 * 
	 * @throws UnlogicalException
	 * @throws JudmentOperationException
	 */
	private ApprovalProcessStep nextStep() throws UnlogicalException, JudmentOperationException {
		for (IApprovalProcessStep item : this.getProcessSteps()) {
			if (item.getStatus() != emApprovalStepStatus.Pending) {
				// 只考虑挂起的步骤
				continue;
			}
			ApprovalProcessStep stepItem = (ApprovalProcessStep) item;
			ApprovalDataJudgmentLinks judgmentLinks = new ApprovalDataJudgmentLinks();
			judgmentLinks.parsingConditions(stepItem.getConditions());
			boolean done = judgmentLinks.judge((IBusinessObjectBase) this.getApprovalData());
			if (done) {
				// 满足条件，开启此步骤
				stepItem.start();
				return stepItem;
			} else {
				// 跳过此步骤
				stepItem.skip();
			}
		}
		return null;
	}

	public synchronized void approval(int stepId, emApprovalResult apResult, String authorizationCode, String judgment)
			throws ApprovalProcessException, InvalidAuthorizationException {
		ApprovalProcessStep apStep = this.getProcessStep(stepId);
		if (apStep == null) {
			throw new ApprovalProcessException(i18n.prop("msg_bobas_not_found_approval_process_step", stepId));
		}
		apStep.getOwner().checkAuthorization(authorizationCode);
		if (apResult == emApprovalResult.Processing) {
			// 重置步骤，上一个步骤操作
			ApprovalProcessStep curStep = (ApprovalProcessStep) this.currentStep();
			IApprovalProcessStep preStep = this.getPreviousProcessStep();
			if (preStep == apStep) {
				// 上一步骤与操作步骤相同，或操作步骤为第一步骤
				if (curStep != null && curStep != apStep)
					curStep.restore();// 恢复当前步骤
				apStep.reset();// 操作步骤进行中
				// 流程状态设置
				this.setFinishedTime(DateTime.getMaxValue());
				this.setStatus(emApprovalStatus.Processing);
				this.onStatusChanged();
			}
		} else {
			// 当前步骤操作
			if (apStep != this.currentStep()) {
				// 操作的步骤不是正在进行的步骤
				throw new ApprovalProcessException(i18n.prop("msg_bobas_not_processing_approval_process_step", stepId));
			}
			if (apResult == emApprovalResult.Approved) {
				// 批准
				apStep.approve(judgment);
				// 激活下一个符合条件的步骤，不存在则审批完成
				try {
					ApprovalProcessStep nextStep = this.nextStep();
					if (nextStep == null) {
						// 没有下一个步骤，流程完成
						this.setFinishedTime(DateTime.getNow());
						this.setStatus(emApprovalStatus.Approved);
						this.onStatusChanged();
					} else {
						// 进行下一步骤
						this.setStatus(emApprovalStatus.Processing);
					}
				} catch (JudmentOperationException e) {
					throw new ApprovalProcessException(e);
				}
			} else if (apResult == emApprovalResult.Rejected) {
				// 拒绝
				apStep.reject(judgment);
				// 任意步骤拒绝，流程拒绝
				this.setFinishedTime(DateTime.getNow());
				this.setStatus(emApprovalStatus.Rejected);
				this.onStatusChanged();
			}
		}
	}

	public void cancel(String authorizationCode, String remarks)
			throws ApprovalProcessException, InvalidAuthorizationException {
		this.getOwner().checkAuthorization(authorizationCode);
		this.setFinishedTime(DateTime.getNow());
		this.setStatus(emApprovalStatus.Cancelled);
		this.onStatusChanged();
	}

	/**
	 * 流程状态发生变化
	 */
	protected void onStatusChanged() {

	}

	@Override
	public void checkToSave(IUser user) throws UnauthorizedException {
		// 流程未开始，所有者可以修改数据
		if (this.getProcessSteps() == null || this.getProcessSteps().length == 0) {
			return;
		}

	}

}
