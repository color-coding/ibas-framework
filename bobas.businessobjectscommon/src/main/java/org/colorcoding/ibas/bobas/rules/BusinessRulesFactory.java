package org.colorcoding.ibas.bobas.rules;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.approval.ApprovalException;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;
import org.colorcoding.ibas.bobas.core.BOFactoryException;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;

/**
 * 业务规则工厂
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessRulesFactory extends ConfigurableFactory {

    private volatile static IBusinessRulesManager defaultManager = null;

    /**
     * 创建流程管理员实例
     * 
     * @return
     * @throws ApprovalException
     */
    public synchronized static IBusinessRulesManager createManager() throws BusinessRuleException {
        if (defaultManager == null) {
            synchronized (BusinessRulesFactory.class) {
                if (defaultManager == null) {
                    // 尝试初始化
                    try {
                        defaultManager = newManager(MyConfiguration
                                .getConfigValue(MyConfiguration.CONFIG_ITEM_BUSINESS_RULES_WAY, "").toLowerCase());
                    } catch (Exception e) {
                        throw new BusinessRuleException(i18n.prop("msg_bobas_create_business_rules_manager_falid"), e);
                    }
                    if (defaultManager == null) {
                        // 初始化后仍然无效
                        throw new BusinessRuleException(i18n.prop("msg_bobas_create_business_rules_manager_falid"));
                    }
                }
            }
        }
        return defaultManager;

    }

    private static IBusinessRulesManager newManager(String type)
            throws BOFactoryException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (type == null || type.isEmpty()) {
            return new BusinessRulesManager();
        }
        // 创建实现的实例
        Class<?> managerClass = getInstance(BusinessRulesFactory.class, type, "BusinessRulesManager");
        if (managerClass == null) {
            throw new ClassNotFoundException("msg_bobas_not_found_business_rules_manager");
        }
        IBusinessRulesManager manager = (IBusinessRulesManager) managerClass.newInstance();// 审批流程管理员
        if (manager == null) {
            throw new NullPointerException("msg_bobas_not_found_business_rules_manager");
        }
        RuntimeLog.log(RuntimeLog.MSG_BUSINESS_RULES_MANAGER_CREATED, managerClass.getName());
        return manager;
    }
}
