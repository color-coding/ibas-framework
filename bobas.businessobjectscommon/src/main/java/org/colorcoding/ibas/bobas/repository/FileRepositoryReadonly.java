package org.colorcoding.ibas.bobas.repository;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.ISort;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.common.SortType;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.FileData;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.db.DataConvert;
import org.colorcoding.ibas.bobas.expression.FileJudgmentLink;
import org.colorcoding.ibas.bobas.expression.JudmentOperationException;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;

/**
 * 文件仓库只读
 * 
 * @author Niuren.Zhu
 *
 */
public class FileRepositoryReadonly implements IFileRepositoryReadonly {

	/**
	 * 检索条件项目：文件夹。如：documents，条件仅可等于，其他忽略。
	 */
	public static final String CRITERIA_CONDITION_ALIAS_FOLDER = "FileFolder";
	/**
	 * 检索条件项目：包含子文件夹。如： emYesNo.Yes，条件仅可等于，其他忽略。
	 */
	public static final String CRITERIA_CONDITION_ALIAS_INCLUDE_SUBFOLDER = "IncludeSubfolder";

	/**
	 * 检索条件项目：文件名称。如：ibas.*.jar，条件仅可等于，其他忽略。
	 */
	public static final String CRITERIA_CONDITION_ALIAS_FILE_NAME = FileJudgmentLink.CRITERIA_CONDITION_ALIAS_FILE_NAME;

	/**
	 * 检索条件项目：最后修改时间（文件时间）。如：1479965348，条件可等于，大小等于。
	 */
	public static final String CRITERIA_CONDITION_ALIAS_MODIFIED_TIME = FileJudgmentLink.CRITERIA_CONDITION_ALIAS_MODIFIED_TIME;

	private String repositoryFolder;

	@Override
	public String getRepositoryFolder() {
		if (this.repositoryFolder == null || this.repositoryFolder.isEmpty()) {
			String workFolder = MyConfiguration.getDataFolder() + File.separator + "filerepository";
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
	public IOperationResult<FileData> fetch(ICriteria criteria) {
		try {
			return new OperationResult<FileData>().addResultObjects(this.searchFiles(criteria));
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
	}

	private List<FileData> searchFiles(ICriteria criteria) throws Exception {
		if (criteria == null || criteria.getConditions().isEmpty()) {
			throw new Exception(I18N.prop("msg_bobas_invaild_criteria"));
		}
		List<ICondition> conditions;
		String workFolder = this.getRepositoryFolder();

		// 包含子文件夹
		boolean include = false;
		conditions = criteria.getConditions()
				.where(c -> CRITERIA_CONDITION_ALIAS_INCLUDE_SUBFOLDER.equals(c.getAlias()));
		for (ICondition condition : conditions) {
			emYesNo value = emYesNo.NO;
			if (condition.getValue().length() > 1)
				value = emYesNo.valueOf(condition.getValue());
			else {
				value = (emYesNo) DataConvert.toEnumValue(emYesNo.class, condition.getValue());
			}
			include = value == emYesNo.YES ? true : false;
			condition.setOperation(ConditionOperation.NONE);
		}
		// 是否指定工作目录
		conditions = criteria.getConditions().where(c -> CRITERIA_CONDITION_ALIAS_FOLDER.equals(c.getAlias()));
		for (ICondition condition : conditions) {
			if (condition.getValue() != null) {
				// 修正路径符
				condition.setValue(condition.getValue().replace("\\", File.separator));
				if (!condition.getValue().endsWith(File.separator)) {
					condition.setValue(condition.getValue() + File.separator);
				}
			}
		}
		if (conditions.size() == 1) {
			for (ICondition condition : conditions) {
				if (condition.getOperation() == ConditionOperation.EQUAL) {
					// 指定查询文件夹，则更改工作目录
					workFolder = Paths.get(workFolder, condition.getValue()).normalize().toFile().getPath();
					condition.setOperation(ConditionOperation.NONE);
				}
			}
		} else {
			for (ICondition condition : conditions) {
				if (include) {
					if (condition.getOperation() == ConditionOperation.EQUAL) {
						condition.setOperation(ConditionOperation.START);
					}
				}
			}
		}

		Criteria nCriteria = new Criteria();
		for (ICondition condition : criteria.getConditions()) {
			if (condition.getOperation() == ConditionOperation.NONE) {
				continue;
			}
			nCriteria.getConditions().add(condition);
		}
		for (ISort sort : criteria.getSorts()) {
			nCriteria.getSorts().add(sort);
		}
		// 检查文件夹内文件是否符合条件
		File folder = new File(workFolder);
		if (!folder.isDirectory() || !folder.exists()) {
			throw new Exception(
					I18N.prop("msg_bobas_not_found_folder", workFolder.replace(this.getRepositoryFolder(), ".")));
		}
		// 查询符合条件的文件
		List<File> files = this.searchFiles(folder, include, nCriteria);
		// 输出文件数据
		ArrayList<FileData> nFileDatas = new ArrayList<>();
		for (File file : files) {
			FileData fileData = new FileData();
			fileData.setFileName(file.getName());
			fileData.setLocation(file.getPath());
			nFileDatas.add(fileData);
			if (criteria.getResultCount() > 0 && nFileDatas.size() >= criteria.getResultCount()) {
				break;
			}
		}
		return nFileDatas;
	}

	/**
	 * 查询文件
	 * 
	 * @param folder   目录
	 * @param include  是否包含子目录
	 * @param criteria 条件
	 * @return 符合条件的文件数组
	 */
	private List<File> searchFiles(File folder, boolean include, ICriteria criteria) {
		FileJudgmentLink judgmentLinks = new FileJudgmentLink();
		judgmentLinks.setMaskFolder(this.getRepositoryFolder());
		judgmentLinks.parsingConditions(criteria.getConditions());

		ArrayList<File> files = new ArrayList<>();
		Consumer<File> searcher = new Consumer<File>() {
			@Override
			public void accept(File file) {
				if (file.isDirectory() && include) {
					File[] folderFiles = file.listFiles();
					// 文件排序
					if (!criteria.getSorts().isEmpty()) {
						for (ISort sort : criteria.getSorts()) {
							if (CRITERIA_CONDITION_ALIAS_FILE_NAME.equalsIgnoreCase(sort.getAlias())) {
								if (sort.getSortType() == SortType.ASCENDING) {
									Arrays.sort(folderFiles, new Comparator<File>() {
										@Override
										public int compare(File o1, File o2) {
											return o1.getName().compareTo(o2.getName());
										}
									});
								} else if (sort.getSortType() == SortType.DESCENDING) {
									Arrays.sort(folderFiles, new Comparator<File>() {
										@Override
										public int compare(File o1, File o2) {
											return o2.getName().compareTo(o1.getName());
										}
									});
								}
							} else if (CRITERIA_CONDITION_ALIAS_MODIFIED_TIME.equalsIgnoreCase(sort.getAlias())) {
								if (sort.getSortType() == SortType.ASCENDING) {
									Arrays.sort(folderFiles, new Comparator<File>() {
										@Override
										public int compare(File o1, File o2) {
											return Long.compare(o1.lastModified(), o2.lastModified());
										}
									});
								} else if (sort.getSortType() == SortType.DESCENDING) {
									Arrays.sort(folderFiles, new Comparator<File>() {
										@Override
										public int compare(File o1, File o2) {
											return Long.compare(o2.lastModified(), o1.lastModified());
										}
									});
								}
							}
						}
					}
					for (File item : folderFiles) {
						this.accept(item);
					}
				} else if (file.isFile()) {
					try {
						if (judgmentLinks.judge(file)) {
							files.add(file);
						}
						if (criteria.getResultCount() > 0 && files.size() >= criteria.getResultCount()) {
							return;
						}
					} catch (JudmentOperationException e) {
						Logger.log(e);
					}
				}
			}
		};
		if (folder.isDirectory()) {
			for (File item : folder.listFiles()) {
				searcher.accept(item);
			}
		} else {
			searcher.accept(folder);
		}
		return files;
	}

}
