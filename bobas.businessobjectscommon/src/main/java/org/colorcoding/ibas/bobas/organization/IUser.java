package org.colorcoding.ibas.bobas.organization;

/**
 * 系统用户接口
 */
public interface IUser {

	/** 获取用户ID，未知用户为-1，系统用户为-9 */
	int getId();

	/**
	 * 获取用户归属（组织）
	 *
	 * @return 组织标识，未知用户返回空字符串
	 */
	String getBelong();

	/**
	 * 获取用户授权码（token）
	 *
	 * @return 授权码，未知用户返回空字符串
	 */
	String getToken();

	/**
	 * 检查授权码是否匹配当前用户的token
	 *
	 * @param authorizationCode 待验证的授权码，为null或不匹配时抛出异常
	 * @throws InvalidAuthorizationException 授权码无效
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