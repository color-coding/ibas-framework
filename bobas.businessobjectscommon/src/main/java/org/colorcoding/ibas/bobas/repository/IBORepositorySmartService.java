package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.core.IBORepositoryReadonly;
import org.colorcoding.ibas.bobas.core.RepositoryException;

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
	 * @throws RepositoryException
	 */
	void connectReadonlyRepository(String type, String server, String name, String user, String password)
			throws RepositoryException;

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
	 * @throws RepositoryException
	 */
	void connectReadonlyRepository(String server, String name, String user, String password) throws RepositoryException;

}
