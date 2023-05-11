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
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.logic.BusinessLogicsFactory;
import org.colorcoding.ibas.bobas.logic.IBusinessLogicChain;
import org.colorcoding.ibas.bobas.logic.IBusinessLogicsManager;
import org.colorcoding.ibas.bobas.organization.InvalidAuthorizationException;
import org.colorcoding.ibas.bobas.period.IPeriodData;
import org.colorcoding.ibas.bobas.period.IPeriodsManager;
import org.colorcoding.ibas.bobas.period.PeriodException;
import org.colorcoding.ibas.bobas.period.PeriodsFactory;
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
		this.setCheckPeriods(
				!MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_BO_DISABLED_BUSINESS_PERIOD, false));
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

	private boolean checkPeriods;

	protected final boolean isCheckPeriods() {
		return checkPeriods;
	}

	protected final void setCheckPeriods(boolean value) {
		this.checkPeriods = value;
	}

	/**
	 * 触发事务
	 * 
	 * @param type    类型
	 * @param trigger 触发对象
	 * @throws TransactionException
	 */
	@Override
	protected void fireTransaction(TransactionType type, IBusinessObject trigger) throws TransactionException {
		if (type == TransactionType.BEFORE_ADD) {
			// 运行基类方法
			super.fireTransaction(type, trigger);
		}
		// 新增逻辑
		if (type == TransactionType.BEFORE_ADD || type == TransactionType.BEFORE_UPDATE
				|| type == TransactionType.BEFORE_DELETE) {
			// 期间检查
			if (this.isCheckPeriods()) {
				try {
					this.checkPeriods(trigger);
				} catch (PeriodException e) {
					throw new TransactionException(e);
				}
			}
			// 业务规则检查
			if (this.isCheckRules()) {
				try {
					this.checkRules(trigger);
				} catch (BusinessRuleException e) {
					throw new TransactionException(e);
				}
			}
		}
		// 审批流程相关，先执行审批逻辑，可能对bo的状态有影响
		if (this.isCheckApprovalProcess()) {
			// 触发审批流程
			try {
				this.checkApprovals(type, trigger);
			} catch (ApprovalProcessException e) {
				throw new TransactionException(e);
			}
		}
		// 业务逻辑相关，最后执行业务逻辑，因为要求状态可用
		if (this.isCheckLogics()) {
			// 执行业务逻辑
			this.runLogics(type, trigger);
		}
		if (type != TransactionType.BEFORE_ADD) {
			// 运行基类方法
			super.fireTransaction(type, trigger);
		}
	}

	/**
	 * 检查期间
	 * 
	 * @param bo
	 * @throws PeriodException
	 */
	private void checkPeriods(IBusinessObject bo) throws PeriodException {
		if (!(bo instanceof IPeriodData)) {
			// 业务对象不是期间数据，退出处理
			return;
		}
		if (!bo.isSavable()) {
			// 业务对象不是可保存对象，退出处理
			return;
		}
		IPeriodsManager manager = PeriodsFactory.create().createManager();
		IPeriodData data = (IPeriodData) bo;
		manager.applyPeriod(data);
		manager.checkPeriod(data);
	}

	/**
	 * 业务规则检查
	 * 
	 * @param bo 对象
	 * @throws BusinessRuleException
	 */
	private void checkRules(IBusinessObject bo) throws BusinessRuleException {
		// 运行对象业务规则
		if (bo instanceof BusinessObject<?>) {
			((BusinessObject<?>) bo).executeRules();
		}
		// 检查业务规则
		if (bo instanceof ICheckRules) {
			((ICheckRules) bo).check();
		}
	}

	private IApprovalProcessManager approvalManager;

	/**
	 * 触发审批流程
	 * 
	 * @param type 操作类型
	 * @param bo   业务数据
	 * @throws ApprovalException
	 * @throws InvalidAuthorizationException
	 */
	private void checkApprovals(TransactionType type, IBusinessObject bo) throws ApprovalProcessException {
		if (!(bo instanceof IApprovalData)) {
			// 业务对象不是需要审批的数据，退出处理
			return;
		}
		if (!bo.isSavable()) {
			// 业务对象不是可保存对象，退出处理
			return;
		}
		if (this.approvalManager == null) {
			this.approvalManager = ApprovalFactory.create().createManager();
			this.approvalManager.useRepository(this.getRepository());
		}
		IApprovalProcess approvalProcess = this.approvalManager.checkProcess((IApprovalData) bo);
		if (approvalProcess == null) {
			return;
		}
		// 存在流程实例
		if (type == TransactionType.BEFORE_UPDATE || type == TransactionType.BEFORE_DELETE) {
			// 检查是否有权限修改数据
			approvalProcess.checkToSave(this.getCurrentUser());
			// 数据无效时，清理流程
			if (bo.isDeleted()) {
				// 删除数据，取消流程
				approvalProcess.cancel(this.getCurrentUser().getToken(),
						I18N.prop("msg_bobas_user_deleted_approval_data"));
			} else if (bo instanceof IBOTagDeleted) {
				// 删除，取消流程
				IBOTagDeleted tagDeleted = (IBOTagDeleted) bo;
				if (tagDeleted.getDeleted() == emYesNo.YES) {
					approvalProcess.cancel(this.getCurrentUser().getToken(),
							I18N.prop("msg_bobas_user_deleted_approval_data"));
				}
			} else if (bo instanceof IBOTagCanceled) {
				// 取消，取消流程
				IBOTagCanceled tagCanceled = (IBOTagCanceled) bo;
				if (tagCanceled.getCanceled() == emYesNo.YES) {
					approvalProcess.cancel(this.getCurrentUser().getToken(),
							I18N.prop("msg_bobas_user_deleted_approval_data"));
				}
			}
		} else if (type == TransactionType.ADD || type == TransactionType.UPDATE || type == TransactionType.DELETE) {
			// 保存完成（添加，更新，删除），审批流程保存
			approvalProcess.save();
		}
	}

	private IBusinessLogicsManager logicsManager;

	/**
	 * 执行业务逻辑
	 * 
	 * 根BO进入，
	 * 
	 * @param type 操作类型
	 * @param bo   业务数据
	 */
	private void runLogics(TransactionType type, IBusinessObject bo) {
		// 事务链标记，结束事务时关闭
		if (this.logicsManager == null) {
			this.logicsManager = BusinessLogicsFactory.create().createManager();
		}
		IBusinessLogicChain logicChain = this.logicsManager.getChain(bo);
		if (logicChain == null) {
			// 没有已存在的，创建并注册
			logicChain = this.logicsManager.createChain();
			// 设置事务标记
			logicChain.setGroup(this.getRepository().getTransactionId());
			// 设置使用仓库
			logicChain.useRepository(this.getRepository());
			// 设置触发者
			logicChain.setTrigger(bo);
		}
		// 执行逻辑
		if (type == TransactionType.ADD) {
			// 新建数据，正向逻辑
			logicChain.forwardLogics();
			logicChain.commit();
		} else if (type == TransactionType.BEFORE_DELETE) {
			// 删除数据前，反向逻辑
			logicChain.reverseLogics();
			logicChain.commit();
		} else if (type == TransactionType.BEFORE_UPDATE) {
			// 更新数据前，反向逻辑
			logicChain.reverseLogics();
			// 等待更新完成提交
		} else if (type == TransactionType.UPDATE) {
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
		// 清理审批
		if (this.approvalManager != null) {
			this.approvalManager = null;
		}
		super.rollbackTransaction();
	}

	@Override
	public void commitTransaction() throws RepositoryException {
		// 关闭业务链
		if (this.logicsManager != null) {
			this.logicsManager.closeChains(this.getRepository().getTransactionId());
		}
		// 清理审批
		if (this.approvalManager != null) {
			this.approvalManager = null;
		}
		super.commitTransaction();
	}
}
