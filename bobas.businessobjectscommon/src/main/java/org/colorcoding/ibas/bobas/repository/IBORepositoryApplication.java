package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.organization.InvalidAuthorizationException;

/**
 * 业务仓库应用接口，提供用户认证
 *
 * @author Niuren.Zhu
 *
 */
public interface IBORepositoryApplication {

	/** 获取用户口令 */
	String getUserToken();

	/**
	 * 设置用户口令
	 *
	 * @param token 用户口令
	 * @throws InvalidAuthorizationException 口令无效时抛出
	 */
	void setUserToken(String token) throws InvalidAuthorizationException;

}