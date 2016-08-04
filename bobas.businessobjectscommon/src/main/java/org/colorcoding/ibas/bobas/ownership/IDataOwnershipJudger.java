package org.colorcoding.ibas.bobas.ownership;

import org.colorcoding.ibas.bobas.organization.IUser;

/**
 * 数据权限判员
 */
public interface IDataOwnershipJudger {
	/**
	 * 判断用户是否有权限读取数据
	 * 
	 * @param bo
	 *            数据
	 * 
	 * @param user
	 *            用户
	 * 
	 * @return true，可读；false，不可读
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
	 * @param throwError
	 *            是否抛出不可读异常
	 * 
	 * @return true，可读；false，不可读
	 * 
	 * @throws UnauthorizedException
	 *             未授权异常
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
	 * @return true，可写；false，不可写
	 */
	boolean canSave(IDataOwnership bo, IUser user);

	/**
	 * 判断用户是否有权限读取数据
	 * 
	 * @param bo
	 *            数据
	 * 
	 * @param user
	 *            用户
	 * @param throwError
	 *            是否抛出不可写异常
	 * @return true，可写；false，不可写
	 *
	 * @throws UnauthorizedException
	 *             未授权异常
	 */
	boolean canSave(IDataOwnership bo, IUser user, boolean throwError) throws UnauthorizedException;
}
