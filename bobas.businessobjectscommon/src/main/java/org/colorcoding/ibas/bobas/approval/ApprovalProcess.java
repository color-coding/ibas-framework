package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.bo.IBODocument;
import org.colorcoding.ibas.bobas.bo.IBODocumentLine;
import org.colorcoding.ibas.bobas.bo.IBOTagCanceled;
import org.colorcoding.ibas.bobas.bo.IBOTagDeleted;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.core.IBORepository;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emApprovalResult;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emApprovalStepStatus;
import org.colorcoding.ibas.bobas.data.emBOStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.expressions.JudmentOperationException;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;
import org.colorcoding.ibas.bobas.organization.IOrganizationManager;
import org.colorcoding.ibas.bobas.organization.IUser;
import org.colorcoding.ibas.bobas.organization.InvalidAuthorizationException;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.bobas.repository.IBORepository4DbReadonly;

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
	 * @throws Exception
	 *             异常
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
		}
		return null;
	}

	/**
	 * 恢复初始状态
	 */
	protected final void restore() {
		this.setApprovalData(null);
		this.setStartedTime(DateTime.getMaxValue());
		this.setFinishedTime(DateTime.getMaxValue());
		this.setStatus(emApprovalStatus.UNAFFECTED);
		for (IApprovalProcessStep item : this.getProcessSteps()) {
			ApprovalProcessStep stepItem = (ApprovalProcessStep) item;
			stepItem.restore();// 重置初始状态
		}
	}

	@Override
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

	@Override
	public final boolean start(IApprovalData data) {
		if (data == null) {
			return false;
		}
		this.restore();// 重置初始状态
		for (IApprovalProcessStep item : this.getProcessSteps()) {
			ApprovalProcessStep stepItem = (ApprovalProcessStep) item;
			try {
				ApprovalDataJudgmentLinks judgmentLinks = null;
				if (this.getRepository() instanceof IBORepository4DbReadonly) {
					judgmentLinks = new ApprovalDataJudgmentLinksEx();
					((ApprovalDataJudgmentLinksEx) judgmentLinks)
							.setRepository((IBORepository4DbReadonly) this.getRepository());
				} else {
					judgmentLinks = new ApprovalDataJudgmentLinks();
				}
				judgmentLinks.parsingConditions(stepItem.getConditions());
				boolean done = judgmentLinks.judge((IBusinessObjectBase) data);
				if (done) {
					// 满足条件，开启此步骤
					stepItem.start();
					this.setApprovalData(data);
					this.setStartedTime(DateTime.getNow());
					this.setStatus(emApprovalStatus.PROCESSING);
					this.onStatusChanged();
					return true;
				} else {
					// 跳过此步骤
					stepItem.skip();
				}
			} catch (JudmentOperationException | ApprovalProcessException e) {
				RuntimeLog.log(e);
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
	private ApprovalProcessStep nextStep() throws JudmentOperationException, ApprovalException, RepositoryException {
		for (IApprovalProcessStep item : this.getProcessSteps()) {
			if (item.getStatus() != emApprovalStepStatus.PENDING) {
				// 只考虑挂起的步骤
				continue;
			}
			ApprovalProcessStep stepItem = (ApprovalProcessStep) item;
			ApprovalDataJudgmentLinks judgmentLinks = null;
			if (this.getRepository() instanceof IBORepository4DbReadonly) {
				judgmentLinks = new ApprovalDataJudgmentLinksEx();
				((ApprovalDataJudgmentLinksEx) judgmentLinks)
						.setRepository((IBORepository4DbReadonly) this.getRepository());
			} else {
				judgmentLinks = new ApprovalDataJudgmentLinks();
			}
			judgmentLinks.parsingConditions(stepItem.getConditions());
			boolean done = true;
			if (judgmentLinks.getJudgmentItems() != null && judgmentLinks.getJudgmentItems().length > 0) {
				// 有条件，则加载实际数据进行比较
				// 审批的数据可能存在是代理数据情况
				if (this.getApprovalData(true) instanceof IBusinessObjectBase) {
					// 数据为业务对象时进行属性的条件判断
					IBusinessObjectBase bo = (IBusinessObjectBase) this.getApprovalData();
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
			throws InvalidAuthorizationException, ApprovalException, RepositoryException {
		ApprovalProcessStep apStep = this.getProcessStep(stepId);
		if (apStep == null) {
			throw new ApprovalProcessException(i18n.prop("msg_bobas_not_found_approval_process_step", stepId));
		}
		apStep.getOwner().checkAuthorization(authorizationCode);
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
				this.setFinishedTime(DateTime.getMaxValue());
				this.setStatus(emApprovalStatus.PROCESSING);
				this.onStatusChanged();
			} else {
				// 操作的步骤不是正在进行的步骤
				throw new ApprovalProcessException(
						i18n.prop("msg_bobas_next_approval_process_step_was_stated", stepId));
			}
		} else {
			// 当前步骤操作
			if (apStep != this.currentStep()) {
				// 操作的步骤不是正在进行的步骤
				throw new ApprovalProcessException(i18n.prop("msg_bobas_not_processing_approval_process_step", stepId));
			}
			if (apResult == emApprovalResult.APPROVED) {
				// 批准
				apStep.approve(judgment);
				// 激活下一个符合条件的步骤，不存在则审批完成
				try {
					ApprovalProcessStep nextStep = this.nextStep();
					if (nextStep == null) {
						// 没有下一个步骤，流程完成
						this.setFinishedTime(DateTime.getNow());
						this.setStatus(emApprovalStatus.APPROVED);
						this.onStatusChanged();
					} else {
						// 进行下一步骤
						this.setStatus(emApprovalStatus.PROCESSING);
					}
				} catch (JudmentOperationException e) {
					throw new ApprovalProcessException(e);
				}
			} else if (apResult == emApprovalResult.REJECTED) {
				// 拒绝
				apStep.reject(judgment);
				// 任意步骤拒绝，流程拒绝
				this.setFinishedTime(DateTime.getNow());
				this.setStatus(emApprovalStatus.REJECTED);
				this.onStatusChanged();
			}
		}
	}

	public final boolean cancel(String authorizationCode, String remarks)
			throws ApprovalProcessException, InvalidAuthorizationException {
		if (this.getStatus() == emApprovalStatus.PROCESSING) {
			// 仅审批中的可以取消
			this.getOwner().checkAuthorization(authorizationCode);
			this.setFinishedTime(DateTime.getNow());
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
		RuntimeLog.log(RuntimeLog.MSG_APPROVAL_PROCESS_STATUS_CHANGED, this.getName(), this.getStatus());
		this.changeApprovalDataStatus(this.getStatus());
	}

	/**
	 * 判断用户是否有权限修改数据，可重载。
	 * 
	 * 默认流程未开始可以修改； 数据所有者，可以取消，删除数据，其他人不可。
	 */
	@Override
	public void checkToSave(IUser user) throws UnauthorizedException {
		// 流程未开始，所有者可以修改数据
		if (this.getProcessSteps() == null || this.getProcessSteps().length == 0) {
			return;
		}
		if (Integer.compare(this.getApprovalData().getDataOwner(), user.getId()) == 0) {
			// 所有者修改数据
			if (this.getApprovalData().isDeleted()) {
				// 可删除数据
				return;
			}
			if (this.getApprovalData() instanceof IBOTagDeleted) {
				IBOTagDeleted referenced = (IBOTagDeleted) this.getApprovalData();
				if (referenced.getDeleted() == emYesNo.YES) {
					// 可标记删除数据
					return;
				}
			}
			if (this.getApprovalData() instanceof IBOTagCanceled) {
				IBOTagCanceled referenced = (IBOTagCanceled) this.getApprovalData();
				if (referenced.getCanceled() == emYesNo.YES) {
					// 可标记取消数据
					return;
				}
			}
			if (this.getApprovalData() instanceof IBODocument) {
				// 单据类型
				IBODocument document = (IBODocument) this.getApprovalData();
				if (document.getStatus() == emBOStatus.CLOSED
						&& this.getApprovalData().getApprovalStatus() == emApprovalStatus.APPROVED) {
					// 可关闭数据，仅审批通过后
					return;
				}
			}
			if (this.getApprovalData() instanceof IBODocumentLine) {
				// 单据行类型
				IBODocumentLine documentLine = (IBODocumentLine) this.getApprovalData();
				if (documentLine.getStatus() == emBOStatus.CLOSED
						&& this.getApprovalData().getApprovalStatus() == emApprovalStatus.APPROVED) {
					// 可关闭数据，仅审批通过后
					return;
				}
			}
		}
		// 不允许修改数据
		throw new UnauthorizedException(i18n.prop("msg_bobas_data_in_approval_process_not_allow_to_update",
				this.getApprovalData().toString(), this.getName(), user.toString()));
	}

	/**
	 * 状态发生变化时调用
	 * 
	 * @param value
	 *            当前状态
	 */
	protected void changeApprovalDataStatus(emApprovalStatus status) {
		if (this.getApprovalData().getApprovalStatus() != status) {
			// 当审批数据状态与变化状态不一样时
			this.getApprovalData().setApprovalStatus(status);
		}
	}

	private IBORepository repository;

	protected final IBORepository getRepository() throws ApprovalProcessException {
		if (this.repository == null) {
			throw new ApprovalProcessException(i18n.prop("msg_bobas_invaild_bo_repository"));
		}
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
	protected IApprovalData getApprovalData(boolean real) throws ApprovalException, RepositoryException {
		if (!real) {
			return this.getApprovalData();
		}
		if (!this.getApprovalData().isSavable()) {
			// 审批数据不是业务对象，则查询实际业务对象
			ICriteria criteria = this.getApprovalData().getCriteria();
			if (criteria == null || criteria.getConditions().size() == 0) {
				throw new ApprovalException(i18n.prop("msg_bobas_approval_data_identifiers_unrecognizable",
						this.getApprovalData().getIdentifiers()));
			}
			ApprovalProcessRepository apRepository = new ApprovalProcessRepository();
			apRepository.setRepository(this.getRepository());
			boolean myOpend = apRepository.openRepository();
			IOperationResult<IBusinessObject> opRsltFetch = apRepository.fetch(criteria);
			if (myOpend) {
				apRepository.closeRepository();
			}
			if (opRsltFetch.getError() != null) {
				throw new ApprovalException(opRsltFetch.getError());
			}
			if (opRsltFetch.getResultCode() != 0) {
				throw new ApprovalException(opRsltFetch.getMessage());
			}
			Object tmpBO = opRsltFetch.getResultObjects().firstOrDefault();
			if (!(tmpBO instanceof IApprovalData)) {
				throw new ApprovalException(
						i18n.prop("msg_bobas_approval_data_not_exist", this.getApprovalData().getIdentifiers()));
			}
			IApprovalData data = (IApprovalData) tmpBO;
			data.setApprovalStatus(this.getApprovalData().getApprovalStatus());
			this.setApprovalData(data);
			this.freshData = true;
		}
		return this.getApprovalData();
	}

	/**
	 * 保存当前审批流程及数据 保存审批数据时，若不是实际数据，则自动查询实际数据，此时需要保证Class已被加载。
	 */
	@Override
	public void save() throws ApprovalProcessException {
		boolean myTrans = false;
		boolean myOpend = false;
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
					IOperationResult<?> opRsltSave = apRepository.save((IBusinessObject) approvalData);
					if (opRsltSave.getError() != null) {
						throw new ApprovalException(opRsltSave.getError());
					}
					if (opRsltSave.getResultCode() != 0) {
						throw new ApprovalException(opRsltSave.getMessage());
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
				throw new ApprovalProcessException(e1);
			}
			throw new ApprovalProcessException(e);
		} finally {
			if (myOpend && apRepository != null) {
				try {
					apRepository.closeRepository();
				} catch (RepositoryException e) {
					throw new ApprovalProcessException(e);
				}
			}
		}
	}

}
