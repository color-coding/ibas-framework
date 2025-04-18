package org.colorcoding.ibas.bobas.approval;

import java.util.List;

import org.colorcoding.ibas.bobas.bo.BOFactory;
import org.colorcoding.ibas.bobas.bo.IBOTagCanceled;
import org.colorcoding.ibas.bobas.bo.IBOTagDeleted;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.Numbers;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emApprovalResult;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.data.emApprovalStepStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.expression.JudmentOperationException;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.logging.Logger;
import org.colorcoding.ibas.bobas.organization.IUser;
import org.colorcoding.ibas.bobas.organization.InvalidAuthorizationException;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
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
		return String.format("{approval process: %s %s}", this.getName(), this.getStatus());
	}

	private IApprovalData approvalData;

	/**
	 * 审批数据
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

	/**
	 * 审批所有者
	 * 
	 * @return
	 */
	public IUser getOwner() {
		return OrganizationFactory.createManager().getUser(this.getProcessData().getApprovalData().getDataOwner());
	}

	/**
	 * 恢复初始状态
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
	 * 获取步骤
	 * 
	 * @param id 步骤编号
	 * @return
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
	 * 获取前一个步骤
	 * 
	 * @return
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
					return preStep;
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
	 * 获取当前步骤
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

	public final boolean start(IApprovalData data) {
		if (data == null) {
			return false;
		}
		this.restore();// 重置初始状态
		for (ApprovalProcessStep<?> item : this.getProcessSteps()) {
			ApprovalProcessStep<?> stepItem = (ApprovalProcessStep<?>) item;
			try {
				ApprovalDataJudgmentLink judgmentLinks = new ApprovalDataJudgmentLink(this.getTransaction());
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
	private ApprovalProcessStep<?> nextStep() throws JudmentOperationException, ApprovalException {
		for (ApprovalProcessStep<?> item : this.getProcessSteps()) {
			if (item.getStatus() != emApprovalStepStatus.PENDING) {
				// 只考虑挂起的步骤
				continue;
			}
			ApprovalProcessStep<?> stepItem = (ApprovalProcessStep<?>) item;
			ApprovalDataJudgmentLink judgmentLinks = new ApprovalDataJudgmentLink(this.getTransaction());
			judgmentLinks.parsingConditions(stepItem.getConditions());
			boolean done = true;
			// 有条件，则加载实际数据进行比较
			if (judgmentLinks.getJudgmentItems() != null && judgmentLinks.getJudgmentItems().length > 0) {
				// 审批的数据可能存在是代理数据情况
				if (this.approvalData == null) {
					this.approvalData = this.fetchApprovalData();
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
			throw new ApprovalException(I18N.prop("msg_bobas_invaild_user_authorization"), e);
		}
		// 下个步骤，如果还是当前用户，则自动批准
		if (this.getStatus() == emApprovalStatus.PROCESSING && apResult == emApprovalResult.APPROVED) {
			// 当前步骤
			ApprovalProcessStep<?> step = this.currentStep();
			// 不是当前步骤，且为单用户审批步骤
			if (!(step == apStep || step instanceof IApprovalProcessStepItem
					|| step instanceof IApprovalProcessStepMultiOwner)) {
				if (step.getOwner() == apStep.getOwner() || Numbers.equals(apStep.getOwner(), step.getId())) {
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
					ApprovalProcessStep<?> nextStep = this.nextStep();
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
				this.approval((ApprovalProcessStep<?>) parent, apResult, judgment);
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

	private void onStatusChanged() {
		Logger.log("approval process: [%s]'s status change to [%s].", this.getName(), this.getStatus());
		if (this.getFinishedTime() != DateTimes.VALUE_MAX) {
			if (this.getStatus() != emApprovalStatus.PROCESSING) {
				if (this.approvalData == null) {
					try {
						this.approvalData = this.fetchApprovalData();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		this.changeApprovalDataStatus(this.getStatus());
	}

	/**
	 * 判断用户是否有权限修改数据，可重载。
	 * 
	 */
	public void checkToSave(IUser user) throws ApprovalException {
		// 没有审批步骤，无效的审批流，可修改数据
		if (this.getProcessSteps() == null || this.getProcessSteps().length == 0) {
			return;
		}
		// 所有者修改数据
		if (Integer.compare(this.getApprovalData().getDataOwner(), user.getId()) == 0) {
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

	/**
	 * 获取审批数据
	 * 
	 * @return
	 * @throws ApprovalException
	 * @throws RepositoryException
	 */
	protected IApprovalData fetchApprovalData() throws ApprovalException {
		if (this.getTransaction() == null) {
			throw new ApprovalException(I18N.prop("msg_bobas_invaild_bo_repository"));
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
			throw new ApprovalException(e);
		}
	}

	/**
	 * 保存当前审批流程及数据 保存审批数据时，若不是实际数据，则自动查询实际数据，此时需要保证Class已被加载。
	 */
	public void save() throws ApprovalException {
		ITransaction transaction = this.getTransaction();
		if (transaction == null) {
			throw new ApprovalException(I18N.prop("msg_bobas_invaild_bo_repository"));
		}
		boolean myTrans = false;
		try {
			// 开启事务
			myTrans = transaction.beginTransaction();
			// 调用保存审批数据
			List<IBusinessObject> beSaved = new ArrayList<>(2);
			// 保存审批数据（先加载真正实例）
			if (this.approvalData instanceof IBusinessObject && this.approvalData.isDirty()) {
				beSaved.add((IBusinessObject) this.getApprovalData());
			}
			// 调用保存审批流程
			if (this.processData instanceof IBusinessObject && this.processData.isDirty()) {
				beSaved.add(this.getProcessData());
			}
			transaction.save(beSaved.toArray(new IBusinessObject[] {}));
			// 提交事务
			if (myTrans) {
				this.getTransaction().commit();
			}
		} catch (Exception e) {
			// 回滚事务
			if (myTrans) {
				try {
					this.getTransaction().rollback();
				} catch (RepositoryException e1) {
					throw new ApprovalException(e);
				}
			}
			throw new ApprovalException(e);
		}
	}

}
