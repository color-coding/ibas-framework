package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.core.IBORepositoryReadonly;

/**
 * 业务仓库服务（智能），对外发布使用
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBORepositorySmartService extends IBORepositoryService {

	/**
	 * 获取-业务对象仓库（只读）
	 */
	IBORepositoryReadonly getReadonlyRepository();

	/**
	 * 设置-业务对象仓库（只读）
	 */
	void setReadonlyRepository(IBORepositoryReadonly repository);

	/**
	 * 连接业务仓库
	 * 
	 * @param type
	 *            类型
	 * @param server
	 *            服务
	 * @param name
	 *            名称
	 * @param user
	 *            用户
	 * @param password
	 *            密码
	 * @throws InvalidRepositoryException
	 *             连接不成功或无效
	 */
	void connectReadonlyRepository(String type, String server, String name, String user, String password)
			throws InvalidRepositoryException;

	/**
	 * 连接业务仓库（默认类型）
	 * 
	 * @param server
	 *            服务
	 * @param name
	 *            名称
	 * @param user
	 *            用户
	 * @param password
	 *            密码
	 * @throws InvalidRepositoryException
	 *             连接不成功或无效
	 */
	void connectReadonlyRepository(String server, String name, String user, String password)
			throws InvalidRepositoryException;

}
