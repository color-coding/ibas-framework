package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.approval.ApprovalFactory;
import org.colorcoding.ibas.bobas.approval.ApprovalProcessException;
import org.colorcoding.ibas.bobas.approval.IApprovalData;
import org.colorcoding.ibas.bobas.approval.IApprovalProcess;
import org.colorcoding.ibas.bobas.approval.IApprovalProcessManager;
import org.colorcoding.ibas.bobas.bo.BusinessObject;
import org.colorcoding.ibas.bobas.bo.IBOTagCanceled;
import org.colorcoding.ibas.bobas.bo.IBOTagDeleted;
import org.colorcoding.ibas.bobas.bo.IBOTagReferenced;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.core.SaveActionType;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.logic.BusinessLogicsFactory;
import org.colorcoding.ibas.bobas.logic.IBusinessLogicChain;
import org.colorcoding.ibas.bobas.logic.IBusinessLogicsManager;
import org.colorcoding.ibas.bobas.organization.InvalidAuthorizationException;
import org.colorcoding.ibas.bobas.rule.BusinessRuleException;
import org.colorcoding.ibas.bobas.rule.ICheckRules;

/**
 * 业务仓库服务，带业务逻辑处理
 * 
 * 
 * @author niuren.zhu
 *
 */
public class BORepositoryLogicService extends BORepositoryService {

	public BORepositoryLogicService() {
		super();
		this.setCheckRules(
				!MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_BO_DISABLED_BUSINESS_RULES, false));
		this.setCheckLogics(
				!MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_BO_DISABLED_BUSINESS_LOGICS, false));
		this.setCheckApprovalProcess(
				!MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_BO_DISABLED_BUSINESS_APPROVAL, false));
	}

	private boolean checkRules;

	protected final boolean isCheckRules() {
		return checkRules;
	}

	protected final void setCheckRules(boolean value) {
		this.checkRules = value;
	}

	private boolean checkLogics;

	protected final boolean isCheckLogics() {
		return checkLogics;
	}

	protected final void setCheckLogics(boolean value) {
		this.checkLogics = value;
	}

	private boolean checkApprovalProcess;

	protected final boolean isCheckApprovalProcess() {
		return checkApprovalProcess;
	}

	protected final void setCheckApprovalProcess(boolean value) {
		this.checkApprovalProcess = value;
	}

	/**
	 * 对象的子项保存事件
	 * 
	 * @param action
	 *            事件
	 * @param trigger
	 *            发生事件对象
	 * @param parent
	 *            所属的父项
	 * @throws SaveActionException
	 */
	@Override
	protected void onSaveActionEvent(SaveActionType action, IBusinessObject trigger) throws RepositoryException {
		if (action == SaveActionType.BEFORE_DELETING) {
			// 删除前检查
			if (trigger instanceof IBOTagReferenced) {
				IBOTagReferenced refBO = (IBOTagReferenced) trigger;
				if (refBO.getReferenced() == emYesNo.YES) {
					// 被引用的数据，不允许删除，可以标记删除
					throw new RepositoryException(
							I18N.prop("msg_bobas_not_allow_delete_referenced_bo", trigger.toString()));
				}
			}
		}
		if (action == SaveActionType.BEFORE_ADDING || action == SaveActionType.BEFORE_DELETING
				|| action == SaveActionType.BEFORE_UPDATING) {
			// 业务规则检查
			if (this.isCheckRules()) {
				try {
					this.checkRules(action, trigger);
				} catch (BusinessRuleException e) {
					throw new RepositoryException(e);
				}
			}
			// 审批流程相关，先执行审批逻辑，可能对bo的状态有影响
			if (this.isCheckApprovalProcess()) {
				// 触发审批流程
				try {
					this.triggerApprovals(trigger);
				} catch (InvalidAuthorizationException | ApprovalProcessException e) {
					throw new RepositoryException(e);
				}
			}
		}
		// 业务逻辑相关，最后执行业务逻辑，因为要求状态可用
		if (this.isCheckLogics()) {
			// 执行业务逻辑
			this.runLogics(action, trigger);
		}
		// 运行基类方法
		super.onSaveActionEvent(action, trigger);
	}

	/**
	 * 业务规则检查
	 * 
	 * @param bo
	 *            对象
	 * @throws BusinessRuleException
	 */
	private void checkRules(SaveActionType type, IBusinessObject bo) throws BusinessRuleException {
		// 运行对象业务规则
		if (bo instanceof BusinessObject<?>) {
			((BusinessObject<?>) bo).executeRules();
		} else if (bo instanceof ICheckRules) {
			((ICheckRules) bo).check();
		}
	}

	/**
	 * 触发审批流程
	 * 
	 * @param type
	 *            操作类型
	 * @param bo
	 *            业务数据
	 * @throws ApprovalException
	 * @throws InvalidAuthorizationException
	 */
	private void triggerApprovals(IBusinessObject bo) throws ApprovalProcessException, InvalidAuthorizationException {
		if (!(bo instanceof IApprovalData)) {
			// 业务对象不是需要审批的数据，退出处理
			return;
		}
		IApprovalProcessManager apManager = ApprovalFactory.create().createManager();
		IApprovalProcess approvalProcess = apManager.checkProcess((IApprovalData) bo, this.getRepository());
		if (approvalProcess != null) {
			// 创建了流程实例
			// 保存流程实例，使用当前仓库以保证事务完整
			if (!bo.isNew() && !approvalProcess.isNew()) {
				// 非新建时，检查用户是否有权限保存修改
				approvalProcess.checkToSave(this.getCurrentUser());
				if (bo.isDeleted()) {
					// 删除数据，取消流程
					approvalProcess.cancel(this.getCurrentUser().getToken(),
							I18N.prop("msg_bobas_user_deleted_approval_data"));
				} else if (bo instanceof IBOTagDeleted) {
					// 删除，取消流程
					IBOTagDeleted referenced = (IBOTagDeleted) bo;
					if (referenced.getDeleted() == emYesNo.YES) {
						approvalProcess.cancel(this.getCurrentUser().getToken(),
								I18N.prop("msg_bobas_user_deleted_approval_data"));
					}
				} else if (bo instanceof IBOTagCanceled) {
					// 取消，取消流程
					IBOTagCanceled referenced = (IBOTagCanceled) bo;
					if (referenced.getCanceled() == emYesNo.YES) {
						approvalProcess.cancel(this.getCurrentUser().getToken(),
								I18N.prop("msg_bobas_user_deleted_approval_data"));
					}
				}
			}
			approvalProcess.setRepository(this.getRepository());
			approvalProcess.save();
		}
	}

	private IBusinessLogicsManager logicsManager;

	/**
	 * 执行业务逻辑
	 * 
	 * 根BO进入，
	 * 
	 * @param type
	 *            操作类型
	 * @param bo
	 *            业务数据
	 */
	private void runLogics(SaveActionType type, IBusinessObject bo) {
		String transId = this.getRepository().getTransactionId();// 事务链标记，结束事务时关闭
		if (this.logicsManager == null) {
			this.logicsManager = BusinessLogicsFactory.create().createManager();
		}
		IBusinessLogicsManager logicsManager = this.logicsManager;
		IBusinessLogicChain logicChain = logicsManager.getChain(bo);
		if (logicChain == null) {
			// 没有已存在的，创建并注册
			logicChain = logicsManager.createChain();
			// 设置事务标记
			logicChain.setGroup(transId);
			// 设置使用仓库
			logicChain.useRepository(this.getRepository());
			// 设置触发者
			logicChain.setTrigger(bo);
		}
		// 执行逻辑
		if (type == SaveActionType.ADDED) {
			// 新建数据，正向逻辑
			logicChain.forwardLogics();
			logicChain.commit();
		} else if (type == SaveActionType.BEFORE_DELETING) {
			// 删除数据前，反向逻辑
			logicChain.reverseLogics();
			logicChain.commit();
		} else if (type == SaveActionType.BEFORE_UPDATING) {
			// 更新数据前，反向逻辑
			logicChain.reverseLogics();
			// 等待更新完成提交
		} else if (type == SaveActionType.UPDATED) {
			// 更新数据后，正向逻辑
			logicChain.forwardLogics();
			logicChain.commit();
		}
	}

	@Override
	public void rollbackTransaction() throws RepositoryException {
		// 关闭业务链
		if (this.logicsManager != null) {
			this.logicsManager.closeChains(this.getRepository().getTransactionId());
		}
		super.rollbackTransaction();
	}

	@Override
	public void commitTransaction() throws RepositoryException {
		// 关闭业务链
		if (this.logicsManager != null) {
			this.logicsManager.closeChains(this.getRepository().getTransactionId());
		}
		super.commitTransaction();
	}
}
