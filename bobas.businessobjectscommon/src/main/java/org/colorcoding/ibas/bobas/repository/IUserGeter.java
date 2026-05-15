package org.colorcoding.ibas.bobas.repository;

import java.util.function.Supplier;

import org.colorcoding.ibas.bobas.organization.IUser;

/**
 * 用户获取器接口，通过供应者获取当前用户
 */
public interface IUserGeter {
	/**
	 * 用户供应者
	 *
	 * @return 用户供应者，可为null
	 */
	Supplier<IUser> userSupplier();

	/**
	 * 获取当前用户
	 *
	 * @return 通过供应者获取的用户
	 * @throws RuntimeException 供应者为null时不支持
	 */
	default IUser getUser() {
		if (this.userSupplier() != null) {
			return this.userSupplier().get();
		}
		throw new RuntimeException("not support.");
	}
}