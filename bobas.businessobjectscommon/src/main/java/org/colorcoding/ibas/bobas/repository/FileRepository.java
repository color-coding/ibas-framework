package org.colorcoding.ibas.bobas.repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.Conditions;
import org.colorcoding.ibas.bobas.common.Enums;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.FileData;
import org.colorcoding.ibas.bobas.data.FileItem;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.expression.FileJudgmentLink;
import org.colorcoding.ibas.bobas.expression.JudmentOperationException;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.logging.Logger;

public class FileRepository extends Repository {

	static final String MSG_REPOSITORY_WRITE_FILE = "repository: writed file [%s].";

	static final String MSG_REPOSITORY_DELETE_FILE = "repository: deleted file [%s].";

	/**
	 * 检索条件项目：文件夹。如：documents，条件仅可等于，其他忽略。
	 */
	public static final String CONDITION_ALIAS_FOLDER = "FileFolder";
	/**
	 * 检索条件项目：包含子文件夹。如： emYesNo.Yes，条件仅可等于，其他忽略。
	 */
	public static final String CONDITION_ALIAS_INCLUDE_SUBFOLDER = "IncludeSubfolder";
	/**
	 * 检索条件项目：文件名称。如：ibas.*.jar，条件仅可等于，其他忽略。
	 */
	public static final String CONDITION_ALIAS_FILE_NAME = FileJudgmentLink.CRITERIA_CONDITION_ALIAS_FILE_NAME;
	/**
	 * 检索条件项目：最后修改时间（文件时间）。如：1479965348，条件可等于，大小等于。
	 */
	public static final String CONDITION_ALIAS_MODIFIED_TIME = FileJudgmentLink.CRITERIA_CONDITION_ALIAS_MODIFIED_TIME;
	/**
	 * 查询条件字段-文件路径
	 */
	public static final String CONDITION_ALIAS_FILE_PATH = "FilePath";
	/**
	 * 查询条件字段-类型:文件夹(Folder);文件(File)
	 */
	public static final String CONDITION_ALIAS_FILE_TYPE = "FileType";
	/**
	 * 查询条件值-文件
	 */
	public static final String CONDITION_VALUE_FILE = "File";
	/**
	 * 查询条件值-文件夹
	 */
	public static final String CONDITION_VALUE_FOLDER = "Folder";

	private String repositoryFolder;

	/**
	 * 获取仓库工作目录
	 * 
	 * @return
	 */
	public String getRepositoryFolder() {
		if (Strings.isNullOrEmpty(this.repositoryFolder)) {
			String workFolder = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_FILE_REPOSITORY_FOLDER);
			if (Strings.isNullOrEmpty(workFolder)) {
				workFolder = MyConfiguration.getDataFolder() + File.separator + "files";
			}
			File file = new File(workFolder);
			if (!file.exists()) {
				file.mkdirs();
			}
			this.repositoryFolder = file.getPath();
		}
		return this.repositoryFolder;
	}

	public void setRepositoryFolder(String folder) {
		this.repositoryFolder = folder;
	}

	/**
	 * 查询文件
	 * 
	 * @param criteria 查询条件
	 * @return
	 */
	public OperationResult<FileItem> fetch(ICriteria criteria) {
		try {
			if (criteria == null || criteria.getConditions().isEmpty()) {
				throw new Exception(I18N.prop("msg_bobas_invaild_criteria"));
			}
			boolean include = false;
			String workFolder = this.getRepositoryFolder();
			Conditions conditions = new Conditions();
			for (ICondition condition : criteria.getConditions()) {
				if (CONDITION_ALIAS_FOLDER.equalsIgnoreCase(condition.getAlias())) {
					// 文件夹条件
					if (!Strings.isNullOrEmpty(condition.getValue())) {
						workFolder = workFolder + File.separator + condition.getValue();
					}
				} else if (CONDITION_ALIAS_INCLUDE_SUBFOLDER.equalsIgnoreCase(condition.getAlias())) {
					// 包含子文件夹
					if (!Strings.isNullOrEmpty(condition.getValue())) {
						if (Enums.valueOf(emYesNo.class, condition.getValue()) == emYesNo.YES) {
							include = true;
						}
					}
				} else {
					conditions.add(condition);
				}
			}
			// 检查文件夹内文件是否符合条件
			File folder = new File(workFolder);
			if (!folder.isDirectory() || !folder.exists()) {
				throw new Exception(
						I18N.prop("msg_bobas_not_found_folder", workFolder.replace(this.getRepositoryFolder(), ".")));
			}
			// 检索文件
			OperationResult<FileItem> operationResult = new OperationResult<>();
			for (File file : this.searchFiles(folder, include, conditions)) {
				operationResult.addResultObjects(new FileItem(file));
				// 超出返回数量，则退出
				if (criteria.getResultCount() > 0) {
					if (operationResult.getResultObjects().size() >= criteria.getResultCount()) {
						break;
					}
				}
			}
			return operationResult;
		} catch (Exception e) {
			Logger.log(e);
			return new OperationResult<>(e);
		}
	}

	/**
	 * 查询文件
	 * 
	 * @param folder     目录
	 * @param include    是否包含子目录
	 * @param conditions 条件
	 * @return 符合条件的文件数组
	 */
	private ArrayList<File> searchFiles(File folder, boolean include, Conditions conditions) {
		ArrayList<File> files = new ArrayList<>();
		// 根据条件提速
		if (!conditions.isEmpty() && conditions
				.where(c -> c.getOperation() == ConditionOperation.EQUAL
						&& c.getRelationship() != ConditionRelationship.AND
						&& Strings.equalsIgnoreCase(CONDITION_ALIAS_FILE_NAME, c.getAlias()))
				.size() == conditions.size()) {
			// 简单查询条件，都是按文件名称查询
			File file;
			for (ICondition condition : conditions) {
				file = new File(folder, condition.getValue());
				if (file.isFile() && file.exists()) {
					files.add(file);
				}
			}
		} else {
			// 复杂查询条件
			FileJudgmentLink judgmentLinks = new FileJudgmentLink();
			File[] folderFiles = folder.listFiles();
			if (folderFiles != null) {
				for (File file : folderFiles) {
					if (file.isDirectory() && include) {
						files.addAll(this.searchFiles(file, include, conditions));
					} else if (file.isFile()) {
						try {
							judgmentLinks.parsingConditions(conditions);
							if (judgmentLinks.judge(file)) {
								files.add(file);
							}
						} catch (JudmentOperationException e) {
							Logger.log(e);
						}
					}
				}
			}
		}
		return files;
	}

	/**
	 * 保存文件
	 * 
	 * @param fileData 文件数据
	 * @return
	 */
	public OperationResult<FileItem> save(FileData fileData) {
		try {
			if (fileData == null || fileData.getStream() == null) {
				throw new RepositoryException(I18N.prop("msg_bobas_invalid_data"));
			}
			File workFolder = new File(this.getRepositoryFolder());
			if (!workFolder.exists()) {
				workFolder.mkdirs();
			}
			FileItem fileItem = new FileItem();
			fileItem.setName(fileData.getFileName());
			// 形成新文件名，保留扩展名
			if (Strings.isNullOrEmpty(fileItem.getName())) {
				String exName = Strings.VALUE_EMPTY;
				int index = fileData.getOriginalName().lastIndexOf(".");
				if (index > 0 && index < fileData.getOriginalName().length()) {
					exName = fileData.getOriginalName().substring(index);
				}
				fileItem.setName(Strings.isNullOrEmpty(exName) ? UUID.randomUUID().toString()
						: UUID.randomUUID().toString() + exName.toLowerCase());
			}
			fileItem.setPath(workFolder.getPath() + File.separator + fileItem.getName());
			// 创建工作目录
			File ouFile = new File(fileItem.getPath());
			if (ouFile.getParentFile() != null && !ouFile.getParentFile().exists()) {
				ouFile.getParentFile().mkdirs();
			}
			int bytesRead = 0;
			byte[] buffer = new byte[512];
			try (OutputStream outputStream = new FileOutputStream(ouFile)) {
				while ((bytesRead = fileData.getStream().read(buffer, 0, buffer.length)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
			}
			Logger.log(MSG_REPOSITORY_WRITE_FILE, Strings.isNullOrEmpty(fileData.getOriginalName()) ? fileItem.getPath()
					: String.format("%s|%s", fileData.getOriginalName(), fileItem.getPath()));
			return new OperationResult<FileItem>().addResultObjects(fileItem);
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param criteria 条件
	 * @return
	 */
	public OperationResult<FileItem> delete(ICriteria criteria) {
		OperationResult<FileItem> operationResult = new OperationResult<>();
		try {
			if (criteria == null || criteria.getConditions().isEmpty()) {
				// 没有条件，不允许删除
				return operationResult;
			}
			OperationResult<FileItem> opRsltFetch = this.fetch(criteria);
			if (opRsltFetch.getError() != null) {
				throw opRsltFetch.getError();
			}
			for (FileItem item : opRsltFetch.getResultObjects()) {
				File file = new File(item.getPath());
				// 不允许删除文件夹
				if (file.exists() && file.isDirectory()) {
					if (file.delete()) {
						operationResult.addResultObjects(item);
						Logger.log(MSG_REPOSITORY_DELETE_FILE, file.getPath());
					}
				}
			}
		} catch (Exception e) {
			Logger.log(e);
			operationResult.setError(e);
		}
		return operationResult;
	}
}
