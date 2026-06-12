package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.organization.IUser;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;

/**
 * 用户感知接口，支持获取和设置当前用户
 */
public interface IUserAware {

	/**
	 * 获取当前用户
	 *
	 * @return 当前用户，未设置时返回UNKNOWN_USER，永不为null
	 */
	default IUser getUser() {
		return OrganizationFactory.UNKNOWN_USER;
	}

	/**
	 * 设置当前用户
	 *
	 * @param user 用户对象，可为null（等同于未知用户）
	 */
	default void setUser(IUser user) {
	}
}