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
	 * 获取-用户归属
	 * 
	 * @return
	 */
	String getBelong();

	/**
	 * 获取-授权码
	 */
	String getToken();

	/**
	 * 检查授权
	 * 
	 * @param authorizationCode 授权码
	 * @throws InvalidAuthorizationException 不通过报错
	 */
	default void checkAuthorization(String authorizationCode) throws InvalidAuthorizationException {
		if (authorizationCode != null) {
			if (authorizationCode.equals(this.getToken())) {
				return;
			}
		}
		throw new InvalidAuthorizationException();
	}

}
