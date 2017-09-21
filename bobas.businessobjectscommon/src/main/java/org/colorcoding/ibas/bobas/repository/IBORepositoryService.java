package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.core.IBORepository;
import org.colorcoding.ibas.bobas.core.RepositoryException;

/**
 * 业务仓库服务，对外发布使用
 */
public interface IBORepositoryService {

	/**
	 * 获取-业务对象仓库
	 */
	IBORepository getRepository();

	/**
	 * 设置-业务对象仓库
	 */
	void setRepository(IBORepository repository);

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
	void connectRepository(String type, String server, String name, String user, String password)
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
	void connectRepository(String server, String name, String user, String password) throws RepositoryException;

}
