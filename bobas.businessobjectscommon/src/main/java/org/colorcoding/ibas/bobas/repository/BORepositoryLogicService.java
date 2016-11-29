package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.approval.ApprovalException;
import org.colorcoding.ibas.bobas.approval.ApprovalFactory;
import org.colorcoding.ibas.bobas.approval.IApprovalData;
import org.colorcoding.ibas.bobas.approval.IApprovalProcess;
import org.colorcoding.ibas.bobas.approval.IApprovalProcessManager;
import org.colorcoding.ibas.bobas.bo.IBOReferenced;
import org.colorcoding.ibas.bobas.bo.IBOTagCanceled;
import org.colorcoding.ibas.bobas.bo.IBOTagDeleted;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.core.SaveActionsException;
import org.colorcoding.ibas.bobas.core.SaveActionsType;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.logics.BusinessLogicsFactory;
import org.colorcoding.ibas.bobas.logics.IBusinessLogicsChain;
import org.colorcoding.ibas.bobas.logics.IBusinessLogicsManager;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;
import org.colorcoding.ibas.bobas.organization.InvalidAuthorizationException;
import org.colorcoding.ibas.bobas.rules.BusinessRuleException;
import org.colorcoding.ibas.bobas.rules.BusinessRulesFactory;
import org.colorcoding.ibas.bobas.rules.IBusinessRules;
import org.colorcoding.ibas.bobas.rules.ICheckRules;

/**
 * 业务仓库服务，带业务逻辑处理
 * 
 * 
 * @author niuren.zhu
 *
 */
public class BORepositoryLogicService extends BORepositoryService {

    public BORepositoryLogicService() {
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

    @Override
    protected boolean onSaveActionsEvent(SaveActionsType action, IBusinessObjectBase bo) {
        try {
            // 响应事件
            if (action == SaveActionsType.before_deleting) {
                // 删除前检查
                if (bo instanceof IBOReferenced) {
                    IBOReferenced refBO = (IBOReferenced) bo;
                    if (refBO.getReferenced() == emYesNo.Yes) {
                        // 被引用的数据，不允许删除，可以标记删除
                        throw new Exception(i18n.prop("msg_bobas_not_allow_delete_referenced_bo", bo.toString()));
                    }
                }
            }
            if (action == SaveActionsType.before_adding || action == SaveActionsType.before_deleting
                    || action == SaveActionsType.before_updating) {
                // 业务规则检查
                if (this.isCheckRules()) {
                    // 检查规则
                    this.checkRules(action, bo);
                }
                // 审批流程相关，先执行审批逻辑，可能对bo的状态有影响
                if (this.isCheckApprovalProcess()) {
                    // 触发审批流程
                    this.triggerApprovals(bo);
                }
            }
            if (action != SaveActionsType.before_adding) {
                // 业务逻辑相关，最后执行业务逻辑，因为要求状态可用
                if (this.isCheckLogics()) {
                    // 执行业务逻辑
                    this.runLogics(action, bo);
                }
            }
            // 运行基类方法
            return super.onSaveActionsEvent(action, bo);
        } catch (Exception e) {
            throw new SaveActionsException(e);
        }
    }

    /**
     * 对象的子项保存事件
     * 
     * @param action
     *            事件
     * @param bo
     *            发生事件对象
     * @param parent
     *            所属的父项
     * @throws Exception
     */
    @Override
    protected boolean onSaveActionsEvent(SaveActionsType action, IBusinessObjectBase bo, IBusinessObjectBase root)
            throws Exception {
        if (action == SaveActionsType.before_deleting) {
            // 删除前检查
            if (bo instanceof IBOReferenced) {
                IBOReferenced refBO = (IBOReferenced) bo;
                if (refBO.getReferenced() == emYesNo.Yes) {
                    // 被引用的数据，不允许删除，可以标记删除
                    throw new Exception(i18n.prop("msg_bobas_not_allow_delete_referenced_bo", bo.toString()));
                }
            }
        }
        if (action == SaveActionsType.before_adding || action == SaveActionsType.before_deleting
                || action == SaveActionsType.before_updating) {
            // 业务规则检查
            if (this.isCheckRules()) {
                // 检查规则
                this.checkRules(action, bo);
            }
        }
        return true;
    }

    /**
     * 业务规则检查
     * 
     * @param bo
     *            对象
     * @throws BusinessRuleException
     * @throws BusinessRuleExecuteException
     */
    private void checkRules(SaveActionsType type, IBusinessObjectBase bo) throws BusinessRuleException {
        // 运行对象业务规则
        IBusinessRules rules = BusinessRulesFactory.createManager().getRules(bo.getClass());
        if (rules != null)
            rules.execute(bo);
        if (bo instanceof ICheckRules) {
            // 检查业务规则
            ICheckRules checkRules = (ICheckRules) bo;
            checkRules.check();
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
    private void triggerApprovals(IBusinessObjectBase bo) throws ApprovalException, InvalidAuthorizationException {
        if (!(bo instanceof IApprovalData)) {
            // 业务对象不是需要审批的数据，退出处理
            return;
        }
        IApprovalProcessManager apManager = ApprovalFactory.createManager();
        IApprovalProcess approvalProcess = apManager.checkProcess((IApprovalData) bo);
        if (approvalProcess != null) {
            // 创建了流程实例
            // 保存流程实例，使用当前仓库以保证事务完整
            if (!bo.isNew() && !approvalProcess.isNew()) {
                // 非新建时，检查用户是否有权限保存修改
                approvalProcess.checkToSave(this.getCurrentUser());
                if (bo.isDeleted()) {
                    // 删除数据，取消流程
                    approvalProcess.cancel(this.getCurrentUser().getToken(),
                            i18n.prop("msg_bobas_user_deleted_approval_data"));
                } else if (bo instanceof IBOTagDeleted) {
                    // 删除，取消流程
                    IBOTagDeleted referenced = (IBOTagDeleted) bo;
                    if (referenced.getDeleted() == emYesNo.Yes) {
                        approvalProcess.cancel(this.getCurrentUser().getToken(),
                                i18n.prop("msg_bobas_user_deleted_approval_data"));
                    }
                } else if (bo instanceof IBOTagCanceled) {
                    // 取消，取消流程
                    IBOTagCanceled referenced = (IBOTagCanceled) bo;
                    if (referenced.getCanceled() == emYesNo.Yes) {
                        approvalProcess.cancel(this.getCurrentUser().getToken(),
                                i18n.prop("msg_bobas_user_deleted_approval_data"));
                    }
                }
            }
            approvalProcess.save(this.getRepository());
        }
    }

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
    private void runLogics(SaveActionsType type, IBusinessObjectBase bo) {
        String transId = this.getRepository().getTransactionId();// 事务链标记，结束事务时关闭
        IBusinessLogicsManager logicsManager = BusinessLogicsFactory.createManager();
        IBusinessLogicsChain logicsChain = logicsManager.getChain(transId);
        if (logicsChain == null) {
            // 没有已存在的，创建并注册
            logicsChain = logicsManager.registerChain(transId);
            // 传递仓库
            logicsChain.useRepository(this.getRepository());
            // 记录触发者
            logicsChain.setTrigger(bo);
            RuntimeLog.log(RuntimeLog.MSG_LOGICS_CHAIN_CREATED, transId, bo.toString());
        }
        try {
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
        } catch (Exception e) {
            // 出现错误关闭逻辑链，释放资源
            logicsManager.closeChain(logicsChain.getId());
            RuntimeLog.log(RuntimeLog.MSG_LOGICS_CHAIN_REMOVED, transId, e.getMessage());
            throw e;
        }
        // 触发的BO完成操作，释放资源
        if (type == SaveActionsType.added || type == SaveActionsType.updated || type == SaveActionsType.deleted) {
            if (logicsChain != null && logicsChain.getTrigger() == bo) {
                // 释放业务链
                logicsManager.closeChain(logicsChain.getId());
                RuntimeLog.log(RuntimeLog.MSG_LOGICS_CHAIN_REMOVED, transId, "done");
            }
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
