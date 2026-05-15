package org.colorcoding.ibas.bobas.organization;

/**
 * 组织管理员基类，提供用户查询与注册
 *
 * @author Niuren.Zhu
 *
 */
public abstract class OrganizationManager {

	public static IUser UNKNOWN_USER = OrganizationFactory.UNKNOWN_USER;

	public static IUser SYSTEM_USER = OrganizationFactory.SYSTEM_USER;

	/** 初始化组织数据 */
	public abstract void initialize();

	/**
	 * 根据口令获取用户
	 *
	 * @param token 用户口令
	 * @return 匹配的用户，未找到返回null
	 */
	public abstract IUser getUser(String token);

	/**
	 * 根据ID获取用户
	 *
	 * @param id 用户ID
	 * @return 匹配的用户，未找到返回UNKNOWN_USER
	 */
	public abstract IUser getUser(int id);

	/**
	 * 注册用户
	 *
	 * @param user 待注册用户
	 * @return 注册后的用户
	 */
	public abstract IUser register(IUser user);

	/**
	 * 取消注册用户
	 *
	 * @param user 待取消注册的用户
	 * @return 被移除的用户，未找到时返回UNKNOWN_USER
	 */
	public abstract IUser unregister(IUser user);
}