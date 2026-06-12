package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.file.FileData;
import org.colorcoding.ibas.bobas.file.FileItem;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;

public class FileRepositoryService extends FileRepository {

	public FileRepositoryService() {
		this(Strings.VALUE_EMPTY);
	}

	public FileRepositoryService(String fileSign) {
		super(fileSign);
	}

	/**
	 * 查询文件数据
	 * 
	 * @param criteria 查询
	 * @param token    用户口令
	 * @return
	 */
	public OperationResult<FileItem> fetch(ICriteria criteria, String token) {
		try {
			this.setUserToken(token);
			return this.fetch(criteria);
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
	}

	/**
	 * 查询文件数据
	 * 
	 * @param criteria 查询
	 * @return
	 */
	@Override
	public OperationResult<FileItem> fetch(ICriteria criteria) {
		if (this.getCurrentUser() == OrganizationFactory.UNKNOWN_USER) {
			return new OperationResult<>(new RepositoryException(I18N.prop("msg_bobas_invalid_user")));
		}
		return super.fetch(criteria);
	}

	/**
	 * 保存文件数据
	 * 
	 * @param fileData 文件数据
	 * @param token    用户口令
	 * @return
	 */
	public OperationResult<FileItem> save(FileData fileData, String token) {
		try {
			this.setUserToken(token);
			return this.save(fileData);
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
	}

	/**
	 * 保存文件数据
	 * 
	 * @param fileData 文件数据
	 * @return
	 */
	@Override
	public OperationResult<FileItem> save(FileData fileData) {
		if (this.getCurrentUser() == OrganizationFactory.UNKNOWN_USER) {
			return new OperationResult<>(new RepositoryException(I18N.prop("msg_bobas_invalid_user")));
		}
		return super.save(fileData);
	}

	/**
	 * 删除文件数据
	 * 
	 * @param criteria 查询
	 * @param token    用户口令
	 * @return
	 */
	public OperationResult<FileItem> delete(ICriteria criteria, String token) {
		try {
			this.setUserToken(token);
			return this.delete(criteria);
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
	}

	/**
	 * 删除文件数据
	 * 
	 * @param criteria 查询
	 * @return
	 */
	@Override
	public OperationResult<FileItem> delete(ICriteria criteria) {
		if (this.getCurrentUser() == OrganizationFactory.UNKNOWN_USER) {
			return new OperationResult<>(new RepositoryException(I18N.prop("msg_bobas_invalid_user")));
		}
		if (criteria == null || criteria.getConditions().isEmpty()) {
			return new OperationResult<>(new RepositoryException(I18N.prop("msg_bobas_invalid_criteria")));
		}
		return super.delete(criteria);
	}
}
