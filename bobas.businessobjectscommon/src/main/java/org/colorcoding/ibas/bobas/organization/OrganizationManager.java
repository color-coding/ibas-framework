package org.colorcoding.ibas.bobas.organization;

import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;

public abstract class OrganizationManager extends ConfigurableFactory {

	public OrganizationManager() {

	}

	public static IOrganization instance = null;

	/**
	 * 创建组织 线程安全
	 */
	public synchronized static IOrganization createOrganization() throws InvalidOrganizationException {
		if (instance == null) {
			try {
				// 获取配置的组织
				instance = new UnknownOrganization();// 设置为默认组织
			} catch (Exception e) {
				// 获取组织过程失败
				e.printStackTrace();// 打印错误
				throw new InvalidOrganizationException(e.getMessage(), e);
			}
		}
		return instance;
	}
}
