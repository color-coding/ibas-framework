package org.colorcoding.ibas.bobas.repository;

import java.util.function.Supplier;

import org.colorcoding.ibas.bobas.organization.IUser;

/**
 * 用户获取器
 *
 */
public interface IUserGeter {
	/**
	 * 用户提供者
	 * 
	 * @return
	 */
	Supplier<IUser> supplier();

	/**
	 * 获取用户
	 * 
	 * @return
	 */
	default IUser getUser() {
		if (this.supplier() != null) {
			return this.supplier().get();
		}
		throw new RuntimeException("not support.");
	}
}
