package org.colorcoding.ibas.bobas.logics;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;
import org.colorcoding.ibas.bobas.core.BOFactoryException;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;

/**
 * 业务逻辑工厂
 * 
 * @author Niuren.Zhu
 *
 */
public class BusinessLogicsFactory extends ConfigurableFactory {
	private volatile static IBusinessLogicsManager defaultManager = null;

	/**
	 * 创建业务逻辑管理员实例
	 * 
	 * @return
	 * @throws BusinessLogicsException
	 */
	public synchronized static IBusinessLogicsManager createManager() throws BusinessLogicsException {
		if (defaultManager == null) {
			synchronized (BusinessLogicsFactory.class) {
				if (defaultManager == null) {
					// 尝试初始化
					try {
						defaultManager = newManager(MyConfiguration
								.getConfigValue(MyConfiguration.CONFIG_ITEM_BUSINESS_LOGICS_WAY, "").toLowerCase());
					} catch (Exception e) {
						throw new BusinessLogicsException(i18n.prop("msg_bobas_create_approval_process_manager_falid"),
								e);
					}
					if (defaultManager == null) {
						// 初始化后仍然无效
						throw new BusinessLogicsException(i18n.prop("msg_bobas_create_approval_process_manager_falid"));
					}
				}
			}
		}
		return defaultManager;

	}

	private static IBusinessLogicsManager newManager(String type)
			throws BOFactoryException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> managerClass = getInstance(BusinessLogicsFactory.class, type, "BusinessLogicsManager");
		if (managerClass == null) {
			throw new ClassNotFoundException(i18n.prop("msg_bobas_not_found_business_logics_manager"));
		}
		IBusinessLogicsManager manager = (IBusinessLogicsManager) managerClass.newInstance();
		if (manager == null) {
			throw new NullPointerException(i18n.prop("msg_bobas_not_found_business_logics_manager"));
		}
		RuntimeLog.log(RuntimeLog.MSG_BUSINESS_LOGICS_MANAGER_CREATED, managerClass.getName());
		return manager;
	}
}
