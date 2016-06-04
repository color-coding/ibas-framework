package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.core.IBORepositoryReadonly;

/**
 * 业务对象的文件仓库（只读）
 * 
 * @author Niuren.Zhu
 *
 */
public interface IBORepository4FileReadonly extends IBORepositoryReadonly {
	/**
	 * 获取-文件仓库路径
	 * 
	 * @return
	 */
	String getRepositoryFolder();

	/**
	 * 设置-文件仓库路径
	 * 
	 * @param folder
	 */
	void setRepositoryFolder(String folder);
}
