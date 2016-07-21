package org.colorcoding.ibas.bobas.organization;

/**
 * 系统用户
 */
public interface IUser {

	/**
	 * 获取-用户ID
	 * 
	 * @return
	 */
	int getId();

	/**
	 * 检查授权
	 * 
	 * @param authorizationCode
	 *            授权码
	 * @throws InvalidAuthorizationException
	 *             不通过报错
	 */
	void checkAuthorization(String authorizationCode) throws InvalidAuthorizationException;
}
