package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.approval.ApprovalException;
import org.colorcoding.ibas.bobas.approval.ApprovalFactory;
import org.colorcoding.ibas.bobas.approval.IApprovalData;
import org.colorcoding.ibas.bobas.approval.IApprovalProcess;
import org.colorcoding.ibas.bobas.approval.IApprovalProcessManager;
import org.colorcoding.ibas.bobas.bo.IBOReferenced;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.core.SaveActionsEvent;
import org.colorcoding.ibas.bobas.core.SaveActionsException;
import org.colorcoding.ibas.bobas.core.SaveActionsType;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.logics.BusinessLogicsFactory;
import org.colorcoding.ibas.bobas.logics.IBusinessLogicsChain;
import org.colorcoding.ibas.bobas.logics.IBusinessLogicsManager;
import org.colorcoding.ibas.bobas.rules.BusinessRuleException;
import org.colorcoding.ibas.bobas.rules.ICheckRules;

/**
 * 业务仓库服务，带业务逻辑处理
 * 
 * 
 * @author niuren.zhu
 *
 */
public class BORepositoryLogicService extends BORepositoryService {

	@Override
	public boolean onActionsEvent(SaveActionsEvent event) {
		try {
			if (event.getType() == SaveActionsType.before_deleting) {
				// 删除前检查
				if (event.getBO() instanceof IBOReferenced) {
					IBOReferenced bo = (IBOReferenced) event.getBO();
					if (bo.getReferenced() == emYesNo.Yes) {
						// 被引用的数据，不允许删除，可以标记删除
						throw new Exception(i18n.prop("msg_bobas_not_allow_delete_referenced_bo", event.getBO()));
					}
				}
			}
			if (event.getType() == SaveActionsType.before_adding || event.getType() == SaveActionsType.before_deleting
					|| event.getType() == SaveActionsType.before_updating) {
				// 业务规则检查
				if (!MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_BO_DISABLED_BUSINESS_LOGICS, false)) {
					// 检查规则
					this.checkRules(event.getType(), event.getBO());
				}
				// 审批流程相关，先执行审批逻辑，可能对bo的状态有影响
				if (!MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_BO_DISABLED_BUSINESS_APPROVAL, false)) {
					// 触发审批流程
					this.triggerApprovals(event.getBO());
				}
			}
			if (event.getType() != SaveActionsType.before_adding && event.getType() != SaveActionsType.deleted) {
				// 业务逻辑相关，最后执行业务逻辑，因为要求状态可用
				if (!MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_BO_DISABLED_BUSINESS_LOGICS, false)) {
					// 执行业务逻辑
					this.runLogics(event.getType(), event.getBO());
				}

			}
			// 运行基类方法
			return super.onActionsEvent(event);
		} catch (Exception e) {
			throw new SaveActionsException(e);
		}
	}

	/**
	 * 业务规则检查
	 * 
	 * @param bo
	 *            对象
	 * @throws BusinessRuleException
	 */
	private void checkRules(SaveActionsType type, IBusinessObjectBase bo) throws BusinessRuleException {
		if (!(bo instanceof ICheckRules)) {
			// 业务对象不需要逻辑检查
			return;
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
	 */
	private void triggerApprovals(IBusinessObjectBase bo) throws ApprovalException {
		if (!(bo instanceof IApprovalData)) {
			// 业务对象不是需要审批的数据，退出处理
			return;
		}
		IApprovalProcessManager apManager = ApprovalFactory.createManager();
		IApprovalProcess approvalProcess = apManager.checkProcess((IApprovalData) bo);
		if (approvalProcess != null) {
			// 创建了流程实例
			// 保存流程实例，使用当前仓库以保证事务完整
			if (!bo.isNew()) {
				// 非新建时，检查用户是否有权限保存修改
				approvalProcess.checkToSave(this.getCurrentUser());
			}
			approvalProcess.save(this.getRepository());
		}
	}

	/**
	 * 执行业务逻辑
	 * 
	 * @param type
	 *            操作类型
	 * @param bo
	 *            业务数据
	 */
	private void runLogics(SaveActionsType type, IBusinessObjectBase bo) {
		String transId = this.getRepository().getTransactionId();// 事务链标记，结束事务时关闭
		IBusinessLogicsManager logicsManager = BusinessLogicsFactory.createManager();
		IBusinessLogicsChain logicsChain = logicsManager.getChain(transId);
		if (logicsChain == null) {
			// 没有已存在的，创建并注册
			logicsChain = logicsManager.registerChain(transId);
			// 传递仓库
			logicsChain.useRepository(this.getRepository());
		}
		// 执行逻辑
		if (type == SaveActionsType.added) {
			// 新建数据，正向逻辑
			logicsChain.forwardLogics(bo);
			logicsChain.commit(bo);
		} else if (type == SaveActionsType.before_deleting) {
			// 删除数据前，反向逻辑
			logicsChain.reverseLogics(bo);
			logicsChain.commit(bo);
		} else if (type == SaveActionsType.before_updating) {
			// 更新数据前，反向逻辑
			logicsChain.reverseLogics(bo);
			// 等待更新完成提交
		} else if (type == SaveActionsType.updated) {
			// 更新数据后，正向逻辑
			logicsChain.forwardLogics(bo);
			logicsChain.commit(bo);
		}
	}

	@Override
	protected void rollbackTransaction() throws RepositoryException {
		// 关闭业务链
		BusinessLogicsFactory.createManager().closeChain(this.getRepository().getTransactionId());
		super.rollbackTransaction();
	}

	@Override
	protected void commitTransaction() throws RepositoryException {
		// 关闭业务链
		BusinessLogicsFactory.createManager().closeChain(this.getRepository().getTransactionId());
		super.commitTransaction();
	}
}
