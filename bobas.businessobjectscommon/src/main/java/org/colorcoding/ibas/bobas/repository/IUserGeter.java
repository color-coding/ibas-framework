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
	Supplier<IUser> userSupplier();

	/**
	 * 获取用户
	 * 
	 * @return
	 */
	default IUser getUser() {
		if (this.userSupplier() != null) {
			return this.userSupplier().get();
		}
		throw new RuntimeException("not support.");
	}
}
