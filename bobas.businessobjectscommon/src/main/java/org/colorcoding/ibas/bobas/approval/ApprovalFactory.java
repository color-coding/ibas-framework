package org.colorcoding.ibas.bobas.approval;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;
import org.colorcoding.ibas.bobas.core.BOFactoryException;
import org.colorcoding.ibas.bobas.data.emApprovalStatus;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;

/**
 * 审批工厂
 * 
 * @author Niuren.Zhu
 *
 */
public class ApprovalFactory extends ConfigurableFactory {

    private volatile static IApprovalProcessManager defaultManager = null;

    /**
     * 创建流程管理员实例
     * 
     * @return
     * @throws ApprovalException
     */
    public synchronized static IApprovalProcessManager createManager() throws ApprovalException {
        if (defaultManager == null) {
            synchronized (ApprovalFactory.class) {
                if (defaultManager == null) {
                    // 尝试初始化
                    try {
                        defaultManager = newManager(MyConfiguration
                                .getConfigValue(MyConfiguration.CONFIG_ITEM_APPROVAL_WAY, "").toLowerCase());
                    } catch (Exception e) {
                        throw new ApprovalException(i18n.prop("msg_bobas_create_approval_process_manager_falid"), e);
                    }
                    if (defaultManager == null) {
                        // 初始化后仍然无效
                        throw new ApprovalException(i18n.prop("msg_bobas_create_approval_process_manager_falid"));
                    }
                }
            }
        }
        return defaultManager;

    }

    private static IApprovalProcessManager newManager(String type)
            throws BOFactoryException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (type == null || type.isEmpty()) {
            // 没有指定实现方式，则使用代理
            return new IApprovalProcessManager() {
                @Override
                public IApprovalProcess checkProcess(IApprovalData data) {
                    if (data.getApprovalStatus() != emApprovalStatus.Unaffected) {
                        // 重置数据状态
                        data.setApprovalStatus(emApprovalStatus.Unaffected);
                    }
                    return null;
                }
            };
        }
        // 创建实现的实例
        Class<?> managerClass = getInstance(ApprovalFactory.class, type, "ApprovalProcessManager");
        if (managerClass == null) {
            throw new ClassNotFoundException("msg_bobas_not_found_approval_process_manager");
        }
        IApprovalProcessManager manager = (IApprovalProcessManager) managerClass.newInstance();// 审批流程管理员
        if (manager == null) {
            throw new NullPointerException("msg_bobas_not_found_approval_process_manager");
        }
        RuntimeLog.log(RuntimeLog.MSG_APPROVAL_PROCESS_MANAGER_CREATED, managerClass.getName());
        return manager;
    }
}
