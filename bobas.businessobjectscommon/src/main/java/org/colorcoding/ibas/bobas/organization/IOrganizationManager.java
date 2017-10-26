package org.colorcoding.ibas.bobas.organization;

/**
 * 组织管理员
 * 
 * @author Niuren.Zhu
 *
 */
public interface IOrganizationManager {

	/**
	 * 初始化
	 */
	void initialize();

	/**
	 * 获取用户
	 * 
	 * @param token
	 *            用户的口令
	 */
	IUser getUser(String token);

	/**
	 * 获取用户
	 * 
	 * @param id
	 *            用户ID
	 * @return
	 */
	IUser getUser(int id);

	/**
	 * 注册用户
	 * 
	 * @param user
	 */
	void register(IUser user);

	/**
	 * 获取用户角色
	 * 
	 * @param user
	 *            查询的用户
	 * @return
	 */
	String[] getRoles(IUser user);
}
