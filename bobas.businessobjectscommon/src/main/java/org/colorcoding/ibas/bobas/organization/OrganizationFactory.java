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
						throw new RuntimeException(i18n.prop("msg_bobas_create_approval_process_manager_falid"), e);
					}
					if (defaultManager == null) {
						// 初始化后仍然无效
						throw new RuntimeException(i18n.prop("msg_bobas_create_approval_process_manager_falid"));
					}
				}
			}
		}
		return defaultManager;

	}

	private static IOrganizationManager newManager(String type)
			throws BOFactoryException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> managerClass = getInstance(OrganizationFactory.class, type, "OrganizationManager");
		if (managerClass == null) {
			throw new ClassNotFoundException("msg_bobas_not_found_organization_manager");
		}
		IOrganizationManager manager = (IOrganizationManager) managerClass.newInstance();// 审批流程管理员
		if (manager == null) {
			throw new NullPointerException("msg_bobas_not_found_organization_manager");
		}
		RuntimeLog.log(RuntimeLog.MSG_APPROVAL_PROCESS_MANAGER_CREATED, managerClass.getName());
		return manager;
	}
}
