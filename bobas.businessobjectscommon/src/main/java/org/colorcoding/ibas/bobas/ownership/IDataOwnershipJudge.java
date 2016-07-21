package org.colorcoding.ibas.bobas.ownership;

import org.colorcoding.ibas.bobas.organization.IUser;

/**
 * 数据权限判官
 */
public interface IDataOwnershipJudge {
	/**
	 * 判断用户是否有权限读取数据
	 * 
	 * @param bo
	 *            数据
	 * 
	 * @param user
	 *            用户
	 * 
	 * @return 是否具有权限（布尔值）
	 */
	boolean canRead(IDataOwnership bo, IUser user);

	/**
	 * 判断用户是否有权限读取数据
	 * 
	 * @param bo
	 *            数据
	 * 
	 * @param user
	 *            用户
	 * 
	 * @return 是否具有权限（布尔值）
	 */
	boolean canRead(IDataOwnership bo, IUser user, boolean throwError) throws UnauthorizedException;

	/**
	 * 判断用户是否有权限写入数据
	 * 
	 * @param bo
	 *            数据
	 * 
	 * @param user
	 *            用户
	 * 
	 * @return 是否具有权限（布尔值）
	 */
	boolean cansave(IDataOwnership bo, IUser user);

	/**
	 * 判断用户是否有权限读取数据
	 * 
	 * @param bo
	 *            数据
	 * 
	 * @param user
	 *            用户
	 * 
	 * @return 是否具有权限（布尔值） 及权限错误
	 */
	boolean cansave(IDataOwnership bo, IUser user, boolean throwError) throws UnauthorizedException;
}
