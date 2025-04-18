package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.organization.InvalidAuthorizationException;

/**
 * 业务仓库应用
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBORepositoryApplication {

	/**
	 * 获取-用户口令
	 */
	String getUserToken();

	/**
	 * 设置-用户口令
	 * 
	 * @throws InvalidTokenException
	 */
	void setUserToken(String token) throws InvalidAuthorizationException;

}
