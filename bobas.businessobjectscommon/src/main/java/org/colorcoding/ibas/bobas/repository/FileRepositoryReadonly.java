package org.colorcoding.ibas.bobas.repository;

import java.io.File;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.configuration.Configuration;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.data.FileData;
import org.colorcoding.ibas.bobas.i18n.i18n;

/**
 * 文件仓库只读
 * 
 * @author Niuren.Zhu
 *
 */
public class FileRepositoryReadonly implements IFileRepositoryReadonly {

	/**
	 * 检索条件项目：文件夹
	 */
	public static final String CRITERIA_CONDITION_ALIAS_FOLDER = "FileFolder";

	/**
	 * 检索条件项目：文件名称
	 */
	public static final String CRITERIA_CONDITION_ALIAS_FILE_NAME = "FileName";

	/**
	 * 检索条件项目：扩展名
	 */
	public static final String CRITERIA_CONDITION_ALIAS_EXTENSION_NAME = "Extension";

	/**
	 * 检索条件项目：最后修改时间
	 */
	public static final String CRITERIA_CONDITION_ALIAS_UPDATE_DATE = "LastModifiedTime";

	private String repositoryFolder;

	@Override
	public String getRepositoryFolder() {
		if (this.repositoryFolder == null || this.repositoryFolder.isEmpty()) {
			String workFolder = Configuration.getWorkFolder() + File.separator + "filerepository";
			workFolder = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_FILE_REPOSITORY_FOLDER, workFolder);
			File file = new File(workFolder);
			if (!file.exists() && !file.isDirectory()) {
				file.mkdirs();
			}
			this.repositoryFolder = file.getPath();
		}
		return this.repositoryFolder;
	}

	@Override
	public void setRepositoryFolder(String folder) {
		this.repositoryFolder = folder;
	}

	@Override
	public IOperationResult<FileData> read(ICriteria criteria) {
		OperationResult<FileData> operationResult = new OperationResult<>();
		try {
			operationResult.addResultObjects(this.readFile(criteria));
			return operationResult;
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
	}

	private FileData[] readFile(ICriteria criteria) throws Exception {
		if (criteria == null || criteria.getConditions().size() == 0) {
			throw new RepositoryException(i18n.prop("msg_bobas_invaild_criteria"));
		}
		String workFolder = this.getRepositoryFolder();
		FileData[] nFileDatas = new FileData[] {};
		// 检查文件夹权限
		ICondition condition = criteria.getConditions()
				.firstOrDefault(item -> CRITERIA_CONDITION_ALIAS_FOLDER.equals(item.getAlias()));
		if (condition != null && condition.getCondVal() != null && !condition.getCondVal().isEmpty()) {
			workFolder = workFolder + File.separator + condition.getCondVal();
		}
		// 检查文件夹内文件是否符合条件

		return nFileDatas;
	}
}
