package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.data.FileData;

/**
 * 文件仓库只读
 * 
 * @author Niuren.Zhu
 *
 */
public interface IFileRepositoryReadonly {
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

	/**
	 * 读取文件
	 * 
	 * @param criteria
	 *            读取文件的条件
	 * @return 操作结果
	 */
	IOperationResult<FileData> read(ICriteria criteria);
}
