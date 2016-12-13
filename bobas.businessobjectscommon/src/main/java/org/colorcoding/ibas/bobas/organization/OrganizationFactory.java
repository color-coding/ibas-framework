package org.colorcoding.ibas.bobas.organization;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.approval.ApprovalException;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;
import org.colorcoding.ibas.bobas.core.BOFactoryException;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;

/**
 * 审批工厂
 * 
 * @author Niuren.Zhu
 *
 */
public class OrganizationFactory extends ConfigurableFactory {

    private volatile static IOrganizationManager defaultManager = null;

    /**
     * 创建流程管理员实例
     * 
     * @return
     * @throws ApprovalException
     */
    public synchronized static IOrganizationManager createManager() throws RuntimeException {
        if (defaultManager == null) {
            synchronized (OrganizationFactory.class) {
                if (defaultManager == null) {
                    // 尝试初始化
                    try {
                        defaultManager = newManager(MyConfiguration
                                .getConfigValue(MyConfiguration.CONFIG_ITEM_ORGANIZATION_WAY, "").toLowerCase());
                    } catch (Exception e) {
                        throw new RuntimeException(i18n.prop("msg_bobas_create_organization_manager_falid"), e);
                    }
                    if (defaultManager == null) {
                        // 初始化后仍然无效
                        throw new RuntimeException(i18n.prop("msg_bobas_create_organization_manager_falid"));
                    } else {
                        // 有效数据，进行初始化
                        defaultManager.initialize();
                    }
                }
            }
        }
        return defaultManager;

    }

    private static IOrganizationManager newManager(String type)
            throws BOFactoryException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (type == null || type.isEmpty()) {
            // 没有指定实现方式，则使用代理
            return new IOrganizationManager() {
                @Override
                public IUser getUser(String token) {
                    return UNKNOWN_USER;
                }

                @Override
                public IUser getUser(int id) {
                    return UNKNOWN_USER;
                }

                @Override
                public void initialize() {
                }
            };
        }
        Class<?> managerClass = getInstance(OrganizationFactory.class, type, "OrganizationManager");
        if (managerClass == null) {
            throw new ClassNotFoundException(i18n.prop("msg_bobas_not_found_organization_manager"));
        }
        IOrganizationManager manager = (IOrganizationManager) managerClass.newInstance();
        if (manager == null) {
            throw new NullPointerException(i18n.prop("msg_bobas_not_found_organization_manager"));
        }
        RuntimeLog.log(RuntimeLog.MSG_APPROVAL_PROCESS_MANAGER_CREATED, managerClass.getName());
        return manager;
    }

    public final static IUser UNKNOWN_USER = new IUser() {

        @Override
        public int getId() {
            return IOrganizationManager.UNKNOWN_USER_SIGN;
        }

        @Override
        public String getBelong() {
            return null;
        }

        @Override
        public String getToken() {
            return null;
        }

        @Override
        public void checkAuthorization(String token) throws InvalidAuthorizationException {
        }

        @Override
        public String toString() {
            return String.format("{user: %s}", this.getId());
        }

    };

}
