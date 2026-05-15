package org.colorcoding.ibas.bobas.ownership;

import org.colorcoding.ibas.bobas.bo.BusinessObjectUnit;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.organization.IUser;

/**
 * 数据权限判断器
 */
public abstract class OwnershipJudger {
	/**
	 * 判断用户是否有权限读取数据
	 *
	 * @param data 数据
	 * @param user 用户
	 * @return true，可读；false，不可读
	 */
	public abstract boolean canRead(IDataOwnership data, IUser user);

	/**
	 * 判断用户是否有权限读取数据
	 *
	 * @param data       数据
	 * @param user       用户
	 * @param throwError 无权限时是否抛出异常
	 * @return true，可读；false，不可读
	 * @throws UnauthorizedException throwError为true且无权限时抛出
	 */
	public abstract boolean canRead(IDataOwnership data, IUser user, boolean throwError) throws UnauthorizedException;

	/**
	 * 判断用户是否有权限保存数据
	 *
	 * @param data 数据
	 * @param user 用户
	 * @return true，可保存；false，不可保存
	 */
	public abstract boolean canSave(IDataOwnership data, IUser user);

	/**
	 * 判断用户是否有权限保存数据
	 *
	 * @param data       数据
	 * @param user       用户
	 * @param throwError 无权限时是否抛出异常
	 * @return true，可保存；false，不可保存
	 * @throws UnauthorizedException throwError为true且无权限时抛出
	 */
	public abstract boolean canSave(IDataOwnership data, IUser user, boolean throwError) throws UnauthorizedException;

	/**
	 * 获取数据权限过滤查询条件
	 *
	 * @param boUnit 业务对象单元标注
	 * @param user   用户
	 * @return 过滤查询条件，无限制时返回null
	 */
	public abstract ICriteria filterCriteria(BusinessObjectUnit boUnit, IUser user);
}