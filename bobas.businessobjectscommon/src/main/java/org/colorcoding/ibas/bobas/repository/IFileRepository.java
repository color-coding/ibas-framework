package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.common.ICriteria;
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
	 * 保存文件数据
	 * 
	 * @param fileData
	 *            文件数据
	 * @return 操作结果
	 */
	IOperationResult<FileData> save(FileData fileData);

	/**
	 * 删除文件数据
	 * 
	 * @param criteria
	 *            删除条件
	 * @return 操作结果
	 */
	IOperationResult<FileData> delete(ICriteria criteria);
}
