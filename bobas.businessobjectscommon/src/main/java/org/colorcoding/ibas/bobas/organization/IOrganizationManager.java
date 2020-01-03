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
	 * @param token 用户的口令
	 * @return 未找到应返回null
	 */
	IUser getUser(String token);

	/**
	 * 获取用户
	 * 
	 * @param id 用户ID
	 * @return 未找到返回UnknownUser
	 */
	IUser getUser(int id);

	/**
	 * 注册用户
	 * 
	 * @param user
	 * @return 返回注册用户
	 */
	IUser register(IUser user);

}
