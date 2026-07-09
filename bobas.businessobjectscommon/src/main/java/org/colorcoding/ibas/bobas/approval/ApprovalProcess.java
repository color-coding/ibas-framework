package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.bo.BOFactory;
import org.colorcoding.ibas.bobas.bo.IBOTagCanceled;
import org.colorcoding.ibas.bobas.bo.IBOTagDeleted;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Enums;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.Numbers;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emApprovalResult;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emApprovalStepStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.expression.JudgmentOperationException;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.logic.common.IBOApprovalContract;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.organization.IUser;
import org.colorcoding.ibas.bobas.organization.InvalidAuthorizationException;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.bobas.repository.BORepositoryService;
import org.colorcoding.ibas.bobas.repository.ITransaction;
import org.colorcoding.ibas.bobas.repository.RepositoryException;

/**
 * 审批流程
 *
 * @author Niuren.Zhu
 *
 */
public abstract class ApprovalProcess<T extends IProcessData> {

	public ApprovalProcess(T processData) {
		this.setProcessData(processData);
	}

	private ITransaction transaction;

	protected final ITransaction getTransaction() {
		return this.transaction;
	}

	final void setTransaction(ITransaction transaction) {
		this.transaction = transaction;
	}

	private T processData;

	public final T getProcessData() {
		return processData;
	}

	final void setProcessData(T processData) {
		this.processData = processData;
	}

	public final String getName() {
		return this.getProcessData().getName();
	}

	public final emApprovalStatus getStatus() {
		return this.getProcessData().getStatus();
	}

	protected void setStatus(emApprovalStatus value) {
		this.getProcessData().setStatus(value);
	}

	public final DateTime getStartedTime() {
		return this.getProcessData().getStartedTime();
	}

	protected void setStartedTime(DateTime value) {
		this.getProcessData().setStartedTime(value);
	}

	public final DateTime getFinishedTime() {
		return this.getProcessData().getFinishedTime();
	}

	protected void setFinishedTime(DateTime value) {
		this.getProcessData().setFinishedTime(value);
	}

	@Override
	public String toString() {
		return String.format("{approvalProcess: %s %s}", this.getName(), this.getStatus());
	}

	private IApprovalData approvalData;

	/**
	 * 获取审批数据，优先使用显式设置的数据，否则从流程数据获取
	 *
	 * @return 审批数据，可能为null
	 */
	public final IApprovalData getApprovalData() {
		if (this.approvalData == null) {
			return this.getProcessData().getApprovalData();
		}
		return this.approvalData;
	}

	protected void setApprovalData(IApprovalData approvalData) {
		if (this.processData != null) {
			this.processData.setApprovalData(approvalData);
		}
		this.approvalData = approvalData;
	}

	private IUser owner = null;

	/**
	 * 获取审批所有者，根据流程数据中的用户ID从组织工厂加载
	 *
	 * @return 审批所有者
	 */
	public IUser getOwner() {
		IProcessData pData = this.getProcessData();
		if (pData == null || pData.getOwner() == null) {
			return null;
		}
		if (this.owner == null || Integer.compare(this.owner.getId(), pData.getOwner().getId()) != 0) {
			this.owner = OrganizationFactory.createManager().getUser(pData.getOwner().getId());
		}
		return this.owner;
	}

	/**
	 * 恢复流程和所有步骤为初始状态
	 */
	protected final void restore() {
		this.setStartedTime(DateTimes.VALUE_MIN);
		this.setFinishedTime(DateTimes.VALUE_MAX);
		this.setStatus(emApprovalStatus.UNAFFECTED);
		for (ApprovalProcessStep<?> item : this.getProcessSteps()) {
			// 重置初始状态
			item.restore();
		}
	}

	public abstract ApprovalProcessStep<?>[] getProcessSteps();

	/**
	 * 根据编号获取步骤，包括多人审批步骤的子项
	 *
	 * @param id 步骤编号
	 * @return 步骤，未找到返回null
	 */
	protected ApprovalProcessStep<?> getProcessStep(int id) {
		if (this.getProcessSteps() == null) {
			return null;
		}
		for (ApprovalProcessStep<?> item : this.getProcessSteps()) {
			if (Numbers.equals(item.getId(), id)) {
				return (ApprovalProcessStep<?>) item;
			}
			if (item instanceof IApprovalProcessStepMultiOwner) {
				IApprovalProcessStepMultiOwner mtlStep = (IApprovalProcessStepMultiOwner) item;
				for (IApprovalProcessStepItem sItem : mtlStep.getItems()) {
					if (Numbers.equals(sItem.getId(), id)) {
						return (ApprovalProcessStep<?>) sItem;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 获取当前步骤或前一个已完成步骤
	 *
	 * @return 步骤，可能为null
	 */
	protected final ApprovalProcessStep<?> getPreviousProcessStep() {
		if (this.getProcessSteps() == null) {
			return null;
		}
		if (this.getStatus() == emApprovalStatus.PROCESSING) {
			ApprovalProcessStep<?> preStep = null;
			ApprovalProcessStep<?> step = this.currentStep();
			for (ApprovalProcessStep<?> item : this.getProcessSteps()) {
				if (step == item) {
					return preStep == null ? step : preStep;
				}
				preStep = item;
			}
		} else if (this.getStatus() == emApprovalStatus.APPROVED) {
			for (int i = this.getProcessSteps().length - 1; i >= 0; i--) {
				ApprovalProcessStep<?> item = this.getProcessSteps()[i];
				if (item.getStatus() == emApprovalStepStatus.APPROVED) {
					return item;
				}
			}
		} else if (this.getStatus() == emApprovalStatus.REJECTED) {
			for (int i = this.getProcessSteps().length - 1; i >= 0; i--) {
				ApprovalProcessStep<?> item = this.getProcessSteps()[i];
				if (item.getStatus() == emApprovalStepStatus.REJECTED) {
					return item;
				}
			}
		} else if (this.getStatus() == emApprovalStatus.RETURNED) {
			for (int i = this.getProcessSteps().length - 1; i >= 0; i--) {
				ApprovalProcessStep<?> item = this.getProcessSteps()[i];
				if (item.getStatus() == emApprovalStepStatus.RETURNED) {
					return item;
				}
			}
		}
		return null;
	}

	/**
	 * 获取当前正在审批的步骤
	 *
	 * @return 当前步骤，无进行中步骤时返回null
	 */
	public final ApprovalProcessStep<?> currentStep() {
		if (this.getProcessSteps() == null) {
			return null;
		}
		for (int i = 0; i < this.getProcessSteps().length; i++) {
			ApprovalProcessStep<?> step = this.getProcessSteps()[i];
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

	/**
	 * 根据审批数据启动流程，依次判断步骤条件并激活首个满足条件的步骤
	 *
	 * @param data 审批数据，为null时返回false
	 * @return true流程启动成功，false无步骤满足条件或出错
	 */
	public final boolean start(IApprovalData data) {
		if (data == null) {
			return false;
		}
		this.restore();// 重置初始状态
		ApprovalDataJudgmentLink judgmentLinks;
		for (ApprovalProcessStep<?> stepItem : this.getProcessSteps()) {
			try {
				judgmentLinks = new ApprovalDataJudgmentLink(this.getTransaction());
				judgmentLinks.parsingConditions(stepItem.getConditions());
				if (judgmentLinks.judge((IBusinessObject) data)) {
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
			} catch (JudgmentOperationException | ApprovalException e) {
				Logger.log(e);
				return false;
			}
		}
		return false;
	}

	/**
	 * 激活下一个满足条件的挂起步骤
	 *
	 * @return 下一个步骤，无满足条件的步骤时返回null
	 * @throws JudgmentOperationException 条件判断异常
	 * @throws ApprovalException          审批异常
	 */
	private ApprovalProcessStep<?> nextStep() throws JudgmentOperationException, ApprovalException {
		ApprovalDataJudgmentLink judgmentLinks;
		for (ApprovalProcessStep<?> stepItem : this.getProcessSteps()) {
			if (stepItem.getStatus() != emApprovalStepStatus.PENDING) {
				// 只考虑挂起的步骤
				continue;
			}
			judgmentLinks = new ApprovalDataJudgmentLink(this.getTransaction());
			judgmentLinks.parsingConditions(stepItem.getConditions());
			boolean done = true;
			// 有条件，则加载实际数据进行比较
			if (judgmentLinks.getJudgmentItems() != null && judgmentLinks.getJudgmentItems().length > 0) {
				// 审批的数据可能存在是代理数据情况
				if (this.approvalData == null) {
					this.setApprovalData(this.fetchApprovalData());
				}
				if (this.getApprovalData() instanceof IBusinessObject) {
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

	/**
	 * 完善流程数据（流程被确认是调用）
	 */
	public void perfecting() throws ApprovalException {
	}

	/**
	 * 审批指定步骤，需验证用户授权码。若当前步骤完成后下一步骤所有者与当前相同，则自动审批。
	 *
	 * @param stepId            步骤编号
	 * @param apResult          审批结果
	 * @param authorizationCode 用户授权码
	 * @param judgment          审批意见
	 * @throws ApprovalException 步骤不存在、授权码无效或审批状态异常
	 */
	public final void approval(int stepId, emApprovalResult apResult, String authorizationCode, String judgment)
			throws ApprovalException {
		ApprovalProcessStep<?> apStep = this.getProcessStep(stepId);
		if (apStep == null) {
			throw new ApprovalException(I18N.prop("msg_bobas_not_found_approval_process_step", stepId));
		}
		try {
			apStep.getOwner().checkAuthorization(authorizationCode);
			if (apStep instanceof IApprovalProcessStepItem) {
				this.approval((IApprovalProcessStepItem) apStep, apResult, judgment);
			} else {
				this.approval(apStep, apResult, judgment);
			}
		} catch (InvalidAuthorizationException e) {
			throw new ApprovalException(I18N.prop("msg_bobas_invalid_user_authorization"), e);
		}
		// 下个步骤，如果还是当前用户，则自动批准
		if (this.getStatus() == emApprovalStatus.PROCESSING && apResult == emApprovalResult.APPROVED) {
			// 当前步骤
			ApprovalProcessStep<?> step = this.currentStep();
			// 不是当前步骤，且为单用户审批步骤
			if (!(step == apStep || step instanceof IApprovalProcessStepItem
					|| step instanceof IApprovalProcessStepMultiOwner)) {
				if (step.getOwner() != null && apStep.getOwner() != null
						&& Integer.compare(step.getOwner().getId(), apStep.getOwner().getId()) == 0) {
					this.approval(step.getId(), apResult, authorizationCode, judgment);
				}
			}
		}
	}

	private void approval(ApprovalProcessStep<?> apStep, emApprovalResult apResult, String judgment)
			throws ApprovalException {
		if (apResult == emApprovalResult.PROCESSING) {
			// 重置步骤，上一个步骤操作
			ApprovalProcessStep<?> curStep = this.currentStep();
			ApprovalProcessStep<?> preStep = this.getPreviousProcessStep();
			if (preStep == apStep) {
				// 上一步骤与操作步骤相同，或操作步骤为第一步骤
				if (curStep != null && curStep != apStep)
					curStep.restore();// 恢复当前步骤
				apStep.reset();// 操作步骤进行中
				// 流程状态设置
				this.setFinishedTime(DateTimes.VALUE_MAX);
				if (!Enums.equals(this.getStatus(), emApprovalStatus.PROCESSING)) {
					this.setStatus(emApprovalStatus.PROCESSING);
					this.onStatusChanged();
				}
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
					ApprovalProcessStep<?> nextStep = this.nextStep();
					if (nextStep == null) {
						// 没有下一个步骤，流程完成
						this.setFinishedTime(DateTimes.now());
						if (!Enums.equals(this.getStatus(), emApprovalStatus.APPROVED)) {
							this.setStatus(emApprovalStatus.APPROVED);
							this.onStatusChanged();
						}
					} else {
						// 进行下一步骤
						this.setStatus(emApprovalStatus.PROCESSING);
					}
				} catch (JudgmentOperationException e) {
					throw new ApprovalException(e.getMessage(), e);
				}
			} else if (apResult == emApprovalResult.REJECTED) {
				// 拒绝
				apStep.reject(judgment);
				// 任意步骤拒绝，流程拒绝
				this.setFinishedTime(DateTimes.now());
				if (!Enums.equals(this.getStatus(), emApprovalStatus.REJECTED)) {
					this.setStatus(emApprovalStatus.REJECTED);
					this.onStatusChanged();
				}
			} else if (apResult == emApprovalResult.RETURNED) {
				// 退回
				apStep.retreat(judgment);
				// 任意步骤退回，流程退回
				this.setFinishedTime(DateTimes.now());
				if (!Enums.equals(this.getStatus(), emApprovalStatus.RETURNED)) {
					this.setStatus(emApprovalStatus.RETURNED);
					this.onStatusChanged();
				}
			}
		}
	}

	private void approval(IApprovalProcessStepItem apStep, emApprovalResult apResult, String judgment)
			throws ApprovalException {
		IApprovalProcessStepMultiOwner parent = apStep.getParent();
		if (apResult == emApprovalResult.PROCESSING) {
			apStep.reset();
			// 审批中的
			if (parent.getStatus() == emApprovalStepStatus.PROCESSING) {
				return;
			}
			// 已批准的，批准数量满足，则不撤销状态
			int count = 0;
			for (IApprovalProcessStepItem apItem : parent.getItems()) {
				if (apItem.getStatus() == emApprovalStepStatus.APPROVED) {
					count++;
				}
			}
			if (parent.getStatus() == emApprovalStepStatus.APPROVED) {
				if (parent.getApproversRequired() > 0) {
					if (count >= parent.getApproversRequired()) {
						return;
					}
				}
			}
			// 撤销
			this.approval((ApprovalProcessStep<?>) parent, apResult, judgment);
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
						this.approval((ApprovalProcessStep<?>) parent, apResult, judgment);
					}
				} else {
					// 没设置审批人数，则需要全部通过
					if (count == parent.getItems().length) {
						this.approval((ApprovalProcessStep<?>) parent, apResult, judgment);
					}
				}
			} else if (apResult == emApprovalResult.REJECTED) {
				// 拒绝
				apStep.reject(judgment);
				this.approval((ApprovalProcessStep<?>) parent, apResult, judgment);
			} else if (apResult == emApprovalResult.RETURNED) {
				// 退回
				apStep.retreat(judgment);
				this.approval((ApprovalProcessStep<?>) parent, apResult, judgment);
			}
		}
	}

	/**
	 * 取消审批流程，仅审批中的状态可取消，需验证所有者授权码
	 *
	 * @param authorizationCode 所有者授权码
	 * @param remarks           取消备注
	 * @return true取消成功，false流程非审批中状态无法取消
	 * @throws ApprovalException 授权码无效
	 */
	public final boolean cancel(String authorizationCode, String remarks) throws ApprovalException {
		if (this.getStatus() == emApprovalStatus.PROCESSING) {
			// 仅审批中的可以取消
			try {
				this.getOwner().checkAuthorization(authorizationCode);
			} catch (InvalidAuthorizationException e) {
				throw new ApprovalException(I18N.prop("msg_bobas_invalid_user_authorization"), e);
			}
			// 重置当前进行中的步骤
			ApprovalProcessStep<?> currentStep = this.currentStep();
			if (currentStep != null) {
				currentStep.restore();
			}
			this.setFinishedTime(DateTimes.now());
			if (!Enums.equals(this.getStatus(), emApprovalStatus.CANCELLED)) {
				this.setStatus(emApprovalStatus.CANCELLED);
				this.onStatusChanged();
			}
			return true;
		}
		return false;
	}

	private void onStatusChanged() throws ApprovalException {
		Logger.log("approval process: [%s]'s status change to [%s].", this.getName(), this.getStatus());
		if (this.approvalData == null) {
			this.setApprovalData(this.fetchApprovalData());
		}
		this.changeApprovalDataStatus(this.getStatus());
	}

	/**
	 * 判断用户是否有权限修改数据，可重载
	 *
	 * @param user 操作用户
	 * @throws ApprovalException 无权限时抛出
	 */
	public void checkToSave(IUser user) throws ApprovalException {
		// 没有审批步骤，无效的审批流，可修改数据
		if (this.getProcessSteps() == null || this.getProcessSteps().length == 0) {
			return;
		}
		// 所有者修改数据
		if (Integer.compare(this.getOwner().getId(), user.getId()) == 0) {
			// 审批新建状态，可修改数据
			if (this.getProcessData().isNew()) {
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
	 * 流程状态变化时同步更新审批数据状态
	 *
	 * @param status 当前流程状态
	 * @throws ApprovalException 审批异常
	 */
	protected void changeApprovalDataStatus(emApprovalStatus status) throws ApprovalException {
		if (this.getApprovalData().getApprovalStatus() != status) {
			// 当审批数据状态与变化状态不一样时
			this.getApprovalData().setApprovalStatus(status);
		}
	}

	/**
	 * 从数据库查询实际审批数据，需事务已设置且审批数据有有效查询条件
	 *
	 * @return 审批数据
	 * @throws ApprovalException 事务未设置、查询条件无效或数据不存在
	 */
	protected IApprovalData fetchApprovalData() throws ApprovalException {
		if (this.getTransaction() == null) {
			throw new ApprovalException(I18N.prop("msg_bobas_invalid_bo_repository"));
		}
		// 审批数据不是业务对象，则查询实际业务对象
		ICriteria criteria = this.getApprovalData().getCriteria();
		if (criteria == null || criteria.getConditions().isEmpty()) {
			throw new ApprovalException(I18N.prop("msg_bobas_approval_data_identifiers_unrecognizable",
					this.getApprovalData().getIdentifiers()));
		}
		try {
			criteria.setResultCount(0);
			IBusinessObject[] results = this.getTransaction().fetch(BOFactory.classOf(criteria.getBusinessObject()),
					criteria);
			if (results == null || results.length == 0 || !(results[0] instanceof IApprovalData)) {
				throw new ApprovalException(
						I18N.prop("msg_bobas_approval_data_not_exist", this.getApprovalData().getIdentifiers()));
			}
			IApprovalData data = (IApprovalData) results[0];
			data.setApprovalStatus(this.getStatus());
			return data;
		} catch (ApprovalException e) {
			throw e;
		} catch (Exception e) {
			throw new ApprovalException(e.getMessage(), e);
		}
	}

	/**
	 * 保存审批流程及审批数据。非实际数据时自动查询实际数据，需保证Class已被加载。 自建事务时自动提交或回滚。
	 *
	 * @throws ApprovalException 事务未设置或保存失败
	 */
	public void save() throws ApprovalException {
		ITransaction transaction = this.getTransaction();
		if (transaction == null) {
			throw new ApprovalException(I18N.prop("msg_bobas_invalid_bo_repository"));
		}
		boolean myTrans = false;
		try (BORepository4Approval boRepository = new BORepository4Approval()) {
			// 开启事务
			myTrans = transaction.beginTransaction();
			// 设置仓库事务
			boRepository.setTransaction(transaction);

			// 保存审批数据
			if (this.approvalData != null && this.approvalData.isSavable()) {
				if (this.approvalData.isDirty()) {
					boRepository.toSave(this.approvalData);
				}
			}
			// 保存审批流程
			if (this.processData != null && this.processData.isSavable()) {
				if (this.processData.isDirty()) {
					boRepository.toSave(this.processData);
				}
			}

			// 提交事务
			if (myTrans) {
				transaction.commit();
			}
		} catch (Exception e) {
			// 回滚事务
			if (myTrans) {
				try {
					transaction.rollback();
				} catch (RepositoryException e1) {
					throw new ApprovalException(e1.getMessage(), e1);
				}
			}
			throw new ApprovalException(e.getMessage(), e);
		}
	}

	private class BORepository4Approval extends BORepositoryService {

		public BORepository4Approval() {
			// 跳过审批契约
			this.addSkipLogics(IBOApprovalContract.class);
		}

		public void toSave(IApprovalData data) throws Exception {
			if (data instanceof IBusinessObject) {
				IOperationResult<?> operationResult = super.save((IBusinessObject) data);
				if (operationResult.getError() != null) {
					throw operationResult.getError();
				}
			}
		}

		public void toSave(IProcessData data) throws Exception {
			if (data instanceof IBusinessObject) {
				IOperationResult<?> operationResult = super.save((IBusinessObject) data);
				if (operationResult.getError() != null) {
					throw operationResult.getError();
				}
			}
		}
	}

}