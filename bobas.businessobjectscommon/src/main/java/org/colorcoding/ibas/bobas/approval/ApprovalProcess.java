package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.bo.IBOTagCanceled;
import org.colorcoding.ibas.bobas.bo.IBOTagDeleted;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.core.IBORepository;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emApprovalResult;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emApprovalStepStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.expression.JudmentOperationException;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.logging.Logger;
import org.colorcoding.ibas.bobas.organization.IOrganizationManager;
import org.colorcoding.ibas.bobas.organization.IUser;
import org.colorcoding.ibas.bobas.organization.InvalidAuthorizationException;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.bobas.repository.IBORepository4DbReadonly;
import org.colorcoding.ibas.bobas.repository.RepositoryException;

/**
 * 审批流程
 * 
 * @author Niuren.Zhu
 *
 */
public abstract class ApprovalProcess implements IApprovalProcess {

	public abstract void setApprovalData(IApprovalData value);

	protected abstract void setStatus(emApprovalStatus value);

	protected abstract void setStartedTime(DateTime value);

	protected abstract void setFinishedTime(DateTime value);

	/**
	 * 保存审批流程数据
	 * 
	 * @throws Exception 异常
	 */
	protected abstract void saveProcess() throws Exception;

	private IUser owner;

	@Override
	public IUser getOwner() {
		if (this.owner == null || Integer.compare(this.owner.getId(), this.getApprovalData().getDataOwner()) != 0) {
			// 通过审批数据的所有者去组织结构里找用户
			IOrganizationManager orgManger = OrganizationFactory.create().createManager();
			this.owner = orgManger.getUser((int) this.getApprovalData().getDataOwner());
		}
		return this.owner;
	}

	@Override
	public String toString() {
		return String.format("{approval process: %s %s}", this.getName(), this.getStatus());
	}

	/**
	 * 获取步骤
	 * 
	 * @param id 步骤编号
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
			if (item instanceof IApprovalProcessStepMultiOwner) {
				IApprovalProcessStepMultiOwner mtlStep = (IApprovalProcessStepMultiOwner) item;
				for (IApprovalProcessStepItem sItem : mtlStep.getItems()) {
					if (sItem.getId() == id) {
						return (ApprovalProcessStep) sItem;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 获取前一个步骤
	 * 
	 * @param step 基准步骤
	 * @return
	 */
	private IApprovalProcessStep getPreviousProcessStep() {
		if (this.getProcessSteps() == null) {
			return null;
		}
		if (this.getStatus() == emApprovalStatus.PROCESSING) {
			IApprovalProcessStep preStep = null;
			IApprovalProcessStep step = this.currentStep();
			for (IApprovalProcessStep item : this.getProcessSteps()) {
				if (step == item) {
					return preStep;
				}
				preStep = item;
			}
		} else if (this.getStatus() == emApprovalStatus.APPROVED) {
			for (int i = this.getProcessSteps().length - 1; i >= 0; i--) {
				IApprovalProcessStep item = this.getProcessSteps()[i];
				if (item.getStatus() == emApprovalStepStatus.APPROVED) {
					return item;
				}
			}
		} else if (this.getStatus() == emApprovalStatus.REJECTED) {
			for (int i = this.getProcessSteps().length - 1; i >= 0; i--) {
				IApprovalProcessStep item = this.getProcessSteps()[i];
				if (item.getStatus() == emApprovalStepStatus.REJECTED) {
					return item;
				}
			}
		} else if (this.getStatus() == emApprovalStatus.RETURNED) {
			for (int i = this.getProcessSteps().length - 1; i >= 0; i--) {
				IApprovalProcessStep item = this.getProcessSteps()[i];
				if (item.getStatus() == emApprovalStepStatus.RETURNED) {
					return item;
				}
			}
		}
		return null;
	}

	/**
	 * 恢复初始状态
	 */
	protected final void restore() {
		this.setApprovalData(null);
		this.setStartedTime(DateTimes.VALUE_MIN);
		this.setFinishedTime(DateTimes.VALUE_MAX);
		this.setStatus(emApprovalStatus.UNAFFECTED);
		for (IApprovalProcessStep item : this.getProcessSteps()) {
			ApprovalProcessStep stepItem = (ApprovalProcessStep) item;
			stepItem.restore();// 重置初始状态
		}
	}

	public final IApprovalProcessStep currentStep() {
		if (this.getProcessSteps() == null) {
			return null;
		}
		for (int i = 0; i < this.getProcessSteps().length; i++) {
			IApprovalProcessStep step = this.getProcessSteps()[i];
			if (step == null) {
				continue;
			}
			if (step.getStatus() == emApprovalStepStatus.PROCESSING) {
				// 当前进行的步骤
				return step;
			}
		}
		return null;
	}

	public final boolean start(IApprovalData data) {
		if (data == null) {
			return false;
		}
		this.restore();// 重置初始状态
		for (IApprovalProcessStep item : this.getProcessSteps()) {
			ApprovalProcessStep stepItem = (ApprovalProcessStep) item;
			try {
				ApprovalDataJudgmentLink judgmentLinks = new ApprovalDataJudgmentLink();
				if (this.getRepository() instanceof IBORepository4DbReadonly) {
					judgmentLinks.setRepository((IBORepository4DbReadonly) this.getRepository());
				}
				judgmentLinks.parsingConditions(stepItem.getConditions());
				boolean done = judgmentLinks.judge((IBusinessObject) data);
				if (done) {
					// 满足条件，开启此步骤
					stepItem.start();
					this.setApprovalData(data);
					this.setStartedTime(DateTimes.now());
					this.setStatus(emApprovalStatus.PROCESSING);
					this.onStatusChanged();
					return true;
				} else {
					// 跳过此步骤
					stepItem.skip();
				}
			} catch (JudmentOperationException | ApprovalException e) {
				Logger.log(e);
				return false;
			}
		}
		return false;
	}

	/**
	 * 激活下一个步骤
	 * 
	 * @throws JudmentOperationException
	 * @throws ApprovalException
	 * @throws RepositoryException
	 */
	private ApprovalProcessStep nextStep() throws JudmentOperationException, ApprovalException {
		for (IApprovalProcessStep item : this.getProcessSteps()) {
			if (item.getStatus() != emApprovalStepStatus.PENDING) {
				// 只考虑挂起的步骤
				continue;
			}
			ApprovalProcessStep stepItem = (ApprovalProcessStep) item;
			ApprovalDataJudgmentLink judgmentLinks = new ApprovalDataJudgmentLink();
			if (this.getRepository() instanceof IBORepository4DbReadonly) {
				judgmentLinks.setRepository((IBORepository4DbReadonly) this.getRepository());
			}
			judgmentLinks.parsingConditions(stepItem.getConditions());
			boolean done = true;
			if (judgmentLinks.getJudgmentItems() != null && judgmentLinks.getJudgmentItems().length > 0) {
				// 有条件，则加载实际数据进行比较
				// 审批的数据可能存在是代理数据情况
				if (this.getApprovalData(true) instanceof IBusinessObject) {
					// 数据为业务对象时进行属性的条件判断
					IBusinessObject bo = (IBusinessObject) this.getApprovalData();
					done = judgmentLinks.judge(bo);
				}
			}
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

	public final void approval(int stepId, emApprovalResult apResult, String authorizationCode, String judgment)
			throws ApprovalException {
		ApprovalProcessStep apStep = this.getProcessStep(stepId);
		if (apStep == null) {
			throw new ApprovalException(I18N.prop("msg_bobas_not_found_approval_process_step", stepId));
		}
		try {
			if (apStep instanceof IApprovalProcessStepSingleOwner) {
				IApprovalProcessStepSingleOwner ownerApStep = (IApprovalProcessStepSingleOwner) apStep;
				ownerApStep.getOwner().checkAuthorization(authorizationCode);
				this.approval(ownerApStep, apResult, judgment);
			} else if (apStep instanceof IApprovalProcessStepItem) {
				IApprovalProcessStepItem apStepItem = (IApprovalProcessStepItem) apStep;
				apStepItem.getOwner().checkAuthorization(authorizationCode);
				this.approval(apStepItem, apResult, judgment);
			} else {
				throw new ApprovalException(
						I18N.prop("msg_bobas_commands_invalid_argument", apStep.getClass().getName()));
			}
		} catch (InvalidAuthorizationException e) {
			throw new ApprovalException(I18N.prop("msg_bobas_invaild_user_authorization"), e);
		}
	}

	private void approval(IApprovalProcessStep apStep, emApprovalResult apResult, String judgment)
			throws ApprovalException {
		if (apResult == emApprovalResult.PROCESSING) {
			// 重置步骤，上一个步骤操作
			ApprovalProcessStep curStep = (ApprovalProcessStep) this.currentStep();
			IApprovalProcessStep preStep = this.getPreviousProcessStep();
			if (preStep == apStep) {
				// 上一步骤与操作步骤相同，或操作步骤为第一步骤
				if (curStep != null && curStep != apStep)
					curStep.restore();// 恢复当前步骤
				apStep.reset();// 操作步骤进行中
				// 流程状态设置
				this.setFinishedTime(DateTimes.VALUE_MAX);
				this.setStatus(emApprovalStatus.PROCESSING);
				this.onStatusChanged();
			} else {
				// 操作的步骤不是正在进行的步骤
				throw new ApprovalException(
						I18N.prop("msg_bobas_next_approval_process_step_was_stated", apStep.getId()));
			}
		} else {
			// 当前步骤操作
			if (apStep != this.currentStep()) {
				// 操作的步骤不是正在进行的步骤
				throw new ApprovalException(
						I18N.prop("msg_bobas_not_processing_approval_process_step", apStep.getId()));
			}
			if (apResult == emApprovalResult.APPROVED) {
				// 批准
				apStep.approve(judgment);
				// 激活下一个符合条件的步骤，不存在则审批完成
				try {
					ApprovalProcessStep nextStep = this.nextStep();
					if (nextStep == null) {
						// 没有下一个步骤，流程完成
						this.setFinishedTime(DateTimes.now());
						this.setStatus(emApprovalStatus.APPROVED);
						this.onStatusChanged();
					} else {
						// 进行下一步骤
						this.setStatus(emApprovalStatus.PROCESSING);
					}
				} catch (JudmentOperationException e) {
					throw new ApprovalException(e);
				}
			} else if (apResult == emApprovalResult.REJECTED) {
				// 拒绝
				apStep.reject(judgment);
				// 任意步骤拒绝，流程拒绝
				this.setFinishedTime(DateTimes.now());
				this.setStatus(emApprovalStatus.REJECTED);
				this.onStatusChanged();
			} else if (apResult == emApprovalResult.RETURNED) {
				// 退回
				apStep.retreat(judgment);
				// 任意步骤退回，流程退回
				this.setFinishedTime(DateTimes.now());
				this.setStatus(emApprovalStatus.RETURNED);
				this.onStatusChanged();
			}
		}
	}

	private void approval(IApprovalProcessStepItem apStep, emApprovalResult apResult, String judgment)
			throws ApprovalException {
		IApprovalProcessStepMultiOwner parent = apStep.getParent();
		if (apResult == emApprovalResult.PROCESSING) {
			apStep.reset();
			boolean done = true;
			for (IApprovalProcessStepItem apItem : parent.getItems()) {
				if (apItem.getStatus() != emApprovalStepStatus.PROCESSING) {
					done = false;
					break;
				}
			}
			if (done) {
				// 子项都重置了，则父项重置
				this.approval(parent, apResult, judgment);
			}
		} else {
			if (apResult == emApprovalResult.APPROVED) {
				// 批准
				apStep.approve(judgment);
				int count = 0;
				for (IApprovalProcessStepItem apItem : parent.getItems()) {
					if (apItem.getStatus() == emApprovalStepStatus.APPROVED) {
						count++;
					}
				}
				if (parent.getApproversRequired() > 0) {
					// 设置了审批人数
					if (count >= parent.getApproversRequired()) {
						this.approval(parent, apResult, judgment);
					}
				} else {
					// 没设置审批人数，则需要全部通过
					if (count == parent.getItems().length) {
						this.approval(parent, apResult, judgment);
					}
				}
			} else if (apResult == emApprovalResult.REJECTED) {
				// 拒绝
				apStep.reject(judgment);
				this.approval(parent, apResult, judgment);
			} else if (apResult == emApprovalResult.RETURNED) {
				// 退回
				apStep.retreat(judgment);
				this.approval(parent, apResult, judgment);
			}
		}
	}

	public final boolean cancel(String authorizationCode, String remarks) throws ApprovalException {
		if (this.getStatus() == emApprovalStatus.PROCESSING) {
			// 仅审批中的可以取消
			try {
				this.getOwner().checkAuthorization(authorizationCode);
			} catch (InvalidAuthorizationException e) {
				throw new ApprovalException(I18N.prop("msg_bobas_invaild_user_authorization"), e);
			}
			this.setFinishedTime(DateTimes.now());
			this.setStatus(emApprovalStatus.CANCELLED);
			this.onStatusChanged();
			return true;
		}
		return false;
	}

	/**
	 * 流程状态发生变化
	 */
	private void onStatusChanged() {
		Logger.log("approval process: [%s]'s status change to [%s].", this.getName(), this.getStatus());
		this.changeApprovalDataStatus(this.getStatus());
	}

	/**
	 * 判断用户是否有权限修改数据，可重载。
	 * 
	 * 默认流程未开始可以修改； 数据所有者，可以取消，删除数据，其他人不可。
	 */
	public void checkToSave(IUser user) throws ApprovalException {
		// 没有审批步骤，无效的审批流，可修改数据
		if (this.getProcessSteps() == null || this.getProcessSteps().length == 0) {
			return;
		}
		// 所有者修改数据
		if (Integer.compare(this.getApprovalData().getDataOwner(), user.getId()) == 0) {
			// 审批新建状态，可修改数据
			if (this.isNew()) {
				return;
			}
			// 可删除数据
			if (this.getApprovalData().isDeleted()) {
				return;
			}
			// 可标记删除数据
			if (this.getApprovalData() instanceof IBOTagDeleted) {
				IBOTagDeleted referenced = (IBOTagDeleted) this.getApprovalData();
				if (referenced.getDeleted() == emYesNo.YES) {
					return;
				}
			}
			// 可标记取消数据
			if (this.getApprovalData() instanceof IBOTagCanceled) {
				IBOTagCanceled referenced = (IBOTagCanceled) this.getApprovalData();
				if (referenced.getCanceled() == emYesNo.YES) {
					return;
				}
			}
			// 审批尚未开始，可修改数据
			if (this.getProcessSteps() != null) {
				boolean not_start = true;
				for (int i = 0; i < this.getProcessSteps().length; i++) {
					if (this.getProcessSteps()[i].getStatus() == emApprovalStepStatus.APPROVED
							|| this.getProcessSteps()[i].getStatus() == emApprovalStepStatus.REJECTED) {
						not_start = false;
						break;
					}
				}
				if (not_start) {
					return;
				}
			}
		}
		// 已批准
		if (this.getStatus() == emApprovalStatus.APPROVED) {
			throw new ApprovalException(
					I18N.prop("msg_bobas_data_was_approved_not_allow_to_update", this.getApprovalData().toString()));
		}
		// 不允许修改数据
		throw new ApprovalException(I18N.prop("msg_bobas_data_in_approval_process_not_allow_to_update",
				this.getApprovalData().toString(), this.getName(), user.toString()));
	}

	/**
	 * 状态发生变化时调用
	 * 
	 * @param value 当前状态
	 */
	protected void changeApprovalDataStatus(emApprovalStatus status) {
		if (this.getApprovalData().getApprovalStatus() != status) {
			// 当审批数据状态与变化状态不一样时
			this.getApprovalData().setApprovalStatus(status);
		}
	}

	private IBORepository repository;

	protected final IBORepository getRepository() throws ApprovalException {
		return repository;
	}

	@Override
	public final void setRepository(IBORepository repository) {
		this.repository = repository;
	}

	private boolean freshData;

	/**
	 * 获取审批数据
	 * 
	 * @return
	 * @throws ApprovalException
	 * @throws RepositoryException
	 */
	protected IApprovalData getApprovalData(boolean real) throws ApprovalException {
		if (!real) {
			return this.getApprovalData();
		}
		if (!this.getApprovalData().isSavable()) {
			// 审批数据不是业务对象，则查询实际业务对象
			ICriteria criteria = this.getApprovalData().getCriteria();
			if (criteria == null || criteria.getConditions().isEmpty()) {
				throw new ApprovalException(I18N.prop("msg_bobas_approval_data_identifiers_unrecognizable",
						this.getApprovalData().getIdentifiers()));
			}
			if (this.getRepository() == null) {
				throw new ApprovalException(I18N.prop("msg_bobas_invaild_bo_repository"));
			}
			try {
				ApprovalProcessRepository apRepository = new ApprovalProcessRepository();
				apRepository.setRepository(this.getRepository());
				boolean myOpend = apRepository.openRepository();
				IOperationResult<IBusinessObject> opRsltFetch = apRepository.fetchData(criteria);
				if (myOpend) {
					apRepository.closeRepository();
				}
				if (opRsltFetch.getError() != null) {
					throw opRsltFetch.getError();
				}
				Object tmpBO = opRsltFetch.getResultObjects().firstOrDefault();
				if (!(tmpBO instanceof IApprovalData)) {
					throw new Exception(
							I18N.prop("msg_bobas_approval_data_not_exist", this.getApprovalData().getIdentifiers()));
				}
				IApprovalData data = (IApprovalData) tmpBO;
				data.setApprovalStatus(this.getApprovalData().getApprovalStatus());
				this.setApprovalData(data);
				this.freshData = true;
			} catch (Exception e) {
				throw new ApprovalException(e);
			}
		}
		return this.getApprovalData();
	}

	/**
	 * 保存当前审批流程及数据 保存审批数据时，若不是实际数据，则自动查询实际数据，此时需要保证Class已被加载。
	 */
	public void save() throws ApprovalException {
		boolean myTrans = false;
		boolean myOpend = false;
		if (this.getRepository() == null) {
			throw new ApprovalException(I18N.prop("msg_bobas_invaild_bo_repository"));
		}
		ApprovalProcessRepository apRepository = null;
		try {
			apRepository = new ApprovalProcessRepository();
			apRepository.setRepository(this.getRepository());
			myOpend = apRepository.openRepository();
			myTrans = apRepository.beginTransaction();// 开启事务
			// 调用保存审批数据
			if (this.getApprovalData().isDirty()) {
				IApprovalData approvalData = this.getApprovalData(true);
				if (this.freshData) {
					// 是业务对象实例时，业务对象实例已在保存队列中
					// 保存审批数据
					IOperationResult<?> opRsltSave = apRepository.saveData((IBusinessObject) approvalData);
					if (opRsltSave.getError() != null) {
						throw new ApprovalException(opRsltSave.getError());
					}
				}
			}
			// 调用保存审批流程
			this.saveProcess();
			// 提交事务
			if (myTrans)
				apRepository.commitTransaction();
		} catch (Exception e) {
			try {
				// 回滚事务
				if (myTrans)
					apRepository.rollbackTransaction();
			} catch (RepositoryException e1) {
				throw new ApprovalException(e1);
			}
			if (e instanceof ApprovalException) {
				throw (ApprovalException) e;
			}
			throw new ApprovalException(e);
		} finally {
			if (myOpend && apRepository != null) {
				try {
					apRepository.closeRepository();
				} catch (RepositoryException e) {
					throw new ApprovalException(e);
				}
			}
		}
	}

}
