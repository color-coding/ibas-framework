package org.colorcoding.bobas.organization;

public interface IOrganization {
	/**
	 * 获取用户
	 * 
	 * @param token 用户的口令
	 */
	IUser getUser(String token);
}
