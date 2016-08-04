package org.colorcoding.ibas.bobas.ownership;

import org.colorcoding.ibas.bobas.organization.IUser;

/**
 * 方法权限判断员
 * 
 * @author Niuren.Zhu
 *
 */
public interface IPermissionsJudger {
	/**
	 * 
	 * 是否有权限调用
	 * 
	 * @param className
	 *            所属类
	 * @param methodName
	 *            方法名称
	 * @param user
	 *            用户
	 * @return true，可用；false，不可用
	 * @throws NotConfiguredException
	 *             未配置此项权限异常
	 * @throws UnauthorizedException
	 *             未授权异常
	 */
	boolean canCall(String className, String methodName, IUser user)
			throws NotConfiguredException, UnauthorizedException;
}
