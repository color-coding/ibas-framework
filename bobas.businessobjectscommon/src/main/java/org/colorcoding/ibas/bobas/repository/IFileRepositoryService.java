package org.colorcoding.ibas.bobas.repository;

/**
 * 文件业务仓库服务，对外发布使用
 * 
 * @author Niuren.Zhu
 *
 */
public interface IFileRepositoryService {
	/**
	 * 获取-文件仓库
	 */
	IFileRepository getRepository();

	/**
	 * 设置-文件仓库
	 */
	void setRepository(IFileRepository repository);
}
