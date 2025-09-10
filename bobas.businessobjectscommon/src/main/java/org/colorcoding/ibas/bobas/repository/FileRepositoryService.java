package org.colorcoding.ibas.bobas.repository;

import java.io.File;
import java.nio.file.Paths;

import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.data.FileData;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.organization.IOrganizationManager;
import org.colorcoding.ibas.bobas.organization.IUser;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;

/**
 * 文件仓库服务
 * 
 * 
 * @author niuren.zhu
 *
 */
public class FileRepositoryService implements IFileRepositoryService {

	protected static final String MSG_REPOSITORY_CHANGED_USER = "repository: changed user [%s].";

	public FileRepositoryService() {

	}

	private IFileRepository repository;

	@Override
	public final IFileRepository getRepository() {
		if (this.repository == null) {
			this.setRepository(new FileRepository());
		}
		return this.repository;
	}

	@Override
	public final void setRepository(IFileRepository repository) {
		this.repository = repository;
	}

	private IUser currentUser = null;

	/**
	 * 当前用户
	 * 
	 * @return
	 */
	public IUser getCurrentUser() {
		if (this.currentUser == null) {
			// 未设置用户则为未知用户
			this.currentUser = OrganizationFactory.UNKNOWN_USER;
		}
		return this.currentUser;
	}

	/**
	 * 设置当前用户
	 * 
	 * @param token 用户口令
	 * @throws InvalidTokenException
	 */
	protected void setCurrentUser(String token) throws InvalidTokenException {
		if (this.currentUser != null && this.currentUser.getToken() != null
				&& this.currentUser.getToken().equals(token)) {
			// 与当前的口令相同，不做处理
			return;
		}
		IOrganizationManager orgManager = OrganizationFactory.create().createManager();
		IUser user = orgManager.getUser(token);
		if (user == null) {
			// 没有用户匹配次口令
			throw new InvalidTokenException(I18N.prop("msg_bobas_no_user_match_the_token"));
		}
		this.setCurrentUser(user);
	}

	void setCurrentUser(IUser user) {
		this.currentUser = user;
		Logger.log(MSG_REPOSITORY_CHANGED_USER, this.getCurrentUser());
	}

	/**
	 * 查询文件数据
	 * 
	 * @param criteria 查询
	 * @param token    用户口令
	 * @return
	 */
	protected IOperationResult<FileData> fetch(ICriteria criteria, String token) {
		try {
			this.setCurrentUser(token);
			// 如果只是按文件名查询，则快速处理
			if (criteria != null && !criteria.getConditions().isEmpty()) {
				if (criteria.getConditions().size() == 1) {
					// 仅一个条件，条件是文件名
					ICondition condition = criteria.getConditions().firstOrDefault();
					if (FileRepositoryReadonly.CRITERIA_CONDITION_ALIAS_FILE_NAME.equals(condition.getAlias())
							&& condition.getOperation() == ConditionOperation.EQUAL) {
						FileData fileData = this.fetchFileData(condition.getValue());
						OperationResult<FileData> opRslt = new OperationResult<>();
						if (fileData != null) {
							opRslt.addResultObjects(fileData);
						}
						return opRslt;
					}
				} else {
					// 多个条件，全部条件为文件名且是或关系
					OperationResult<FileData> opRslt = new OperationResult<>();
					for (int i = 0; i < criteria.getConditions().size(); i++) {
						ICondition condition = criteria.getConditions().get(i);
						if (!FileRepositoryReadonly.CRITERIA_CONDITION_ALIAS_FILE_NAME.equals(condition.getAlias())) {
							opRslt = null;
						}
						if (condition.getOperation() != ConditionOperation.EQUAL) {
							opRslt = null;
						}
						if (i > 0 && condition.getRelationship() == ConditionRelationship.AND) {
							opRslt = null;
						}
						if (opRslt == null) {
							break;
						}
						FileData fileData = this.fetchFileData(condition.getValue());
						if (fileData != null) {
							opRslt.addResultObjects(fileData);
						}
					}
					if (opRslt != null) {
						return opRslt;
					}
				}
			}
			return this.getRepository().fetch(criteria);
		} catch (Exception e) {
			Logger.log(e);
			return new OperationResult<>(e);
		}
	}

	protected FileData fetchFileData(String fileName) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(this.getRepository().getRepositoryFolder());
		stringBuilder.append(File.separator);
		stringBuilder.append(fileName);
		File file = Paths.get(stringBuilder.toString()).normalize().toFile();
		if (file.exists() && file.isFile()) {
			FileData fileData = new FileData();
			fileData.setFileName(file.getName());
			fileData.setLocation(file.getPath());
			return fileData;
		}
		return null;
	}

	/**
	 * 保存文件数据
	 * 
	 * @param fileData 文件数据
	 * @param token    用户口令
	 * @return
	 */
	protected IOperationResult<FileData> save(FileData fileData, String token) {
		try {
			this.setCurrentUser(token);
			return this.getRepository().save(fileData);
		} catch (Exception e) {
			Logger.log(e);
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
	protected IOperationResult<FileData> delete(ICriteria criteria, String token) {
		try {
			this.setCurrentUser(token);
			return this.getRepository().delete(criteria);
		} catch (Exception e) {
			Logger.log(e);
			return new OperationResult<>(e);
		}
	}
}
