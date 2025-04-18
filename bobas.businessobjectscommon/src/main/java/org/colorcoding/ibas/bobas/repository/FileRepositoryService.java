package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.data.FileData;
import org.colorcoding.ibas.bobas.data.FileItem;

public class FileRepositoryService extends FileRepository {

	/**
	 * 查询文件数据
	 * 
	 * @param criteria 查询
	 * @param token    用户口令
	 * @return
	 */
	protected OperationResult<FileItem> fetch(ICriteria criteria, String token) {
		try {
			this.setUserToken(token);
			return super.fetch(criteria);
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
	}

	/**
	 * 保存文件数据
	 * 
	 * @param fileData 文件数据
	 * @param token    用户口令
	 * @return
	 */
	protected OperationResult<FileItem> save(FileData fileData, String token) {
		try {
			this.setUserToken(token);
			return super.save(fileData);
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
	}

	/**
	 * 删除文件数据
	 * 
	 * @param criteria 查询
	 * @param token    用户口令
	 * @return
	 */
	protected OperationResult<FileItem> delete(ICriteria criteria, String token) {
		try {
			this.setUserToken(token);
			return super.delete(criteria);
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
	}
}
