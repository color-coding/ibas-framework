package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.data.FileData;

/**
 * 文件仓库
 * 
 * @author Niuren.Zhu
 *
 */
public interface IFileRepository extends IFileRepositoryReadonly {

	/**
	 * 写文件
	 * 
	 * @param fileData
	 *            文件数据
	 * @return 操作结果
	 */
	IOperationResult<FileData> write(FileData fileData);
}
