package org.colorcoding.ibas.bobas.ownership;

import org.colorcoding.ibas.bobas.bo.BusinessObjectUnit;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.organization.IUser;

/**
 * 数据权限判员
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
	 * @param throwError 是否抛出不可读异常
	 * @return true，可读；false，不可读
	 * @throws UnauthorizedException 未授权异常
	 */
	public abstract boolean canRead(IDataOwnership data, IUser user, boolean throwError) throws UnauthorizedException;

	/**
	 * 判断用户是否有权限写入数据
	 * 
	 * @param data 数据
	 * @param user 用户
	 * @return true，可写；false，不可写
	 */
	public abstract boolean canSave(IDataOwnership data, IUser user);

	/**
	 * 判断用户是否有权限读取数据
	 * 
	 * @param data       数据
	 * @param user       用户
	 * @param throwError 是否抛出不可写异常
	 * @return true，可写；false，不可写
	 * @throws UnauthorizedException 未授权异常
	 */
	public abstract boolean canSave(IDataOwnership data, IUser user, boolean throwError) throws UnauthorizedException;

	/**
	 * 获取过滤查询
	 * 
	 * @param boUnit 对象单元
	 * @param user   用户
	 * @return
	 */
	public abstract ICriteria filterCriteria(BusinessObjectUnit boUnit, IUser user);
}
