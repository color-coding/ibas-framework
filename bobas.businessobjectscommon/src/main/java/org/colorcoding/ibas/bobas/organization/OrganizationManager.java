package org.colorcoding.ibas.bobas.organization;

/**
 * 组织管理员
 * 
 * @author Niuren.Zhu
 *
 */
public abstract class OrganizationManager {

	public static IUser UNKNOWN_USER = OrganizationFactory.UNKNOWN_USER;

	public static IUser SYSTEM_USER = OrganizationFactory.SYSTEM_USER;

	/**
	 * 初始化
	 */
	public abstract void initialize();

	/**
	 * 获取用户
	 * 
	 * @param token 用户的口令
	 * @return 未找到应返回null
	 */
	public abstract IUser getUser(String token);

	/**
	 * 获取用户
	 * 
	 * @param id 用户ID
	 * @return 未找到返回UnknownUser
	 */
	public abstract IUser getUser(int id);

	/**
	 * 注册用户
	 * 
	 * @param user
	 * @return 返回注册用户
	 */
	public abstract IUser register(IUser user);

}
