package org.colorcoding.ibas.bobas.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.UUID;
import java.util.function.Consumer;

import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.Enums;
import org.colorcoding.ibas.bobas.common.Files;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.ISort;
import org.colorcoding.ibas.bobas.common.SortType;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.List;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.expression.FileJudgmentLink;
import org.colorcoding.ibas.bobas.expression.JudgmentOperationException;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;

public class LocalFileTransaction extends FileTransaction {
	@Override
	public void setRepositoryFolder(String folder) {
		// 统一路径格式
		super.setRepositoryFolder(Files.valueOf(folder).getPath());
	}

	/**
	 * 判断文件路径是否在仓库范围内。
	 * <p>
	 * 采用"逻辑路径优先、真实路径兜底"策略：
	 * <ul>
	 * <li>逻辑路径：{@link Path#toAbsolutePath()} + {@link Path#normalize()}，处理 {@code ..} 穿越，但<b>不解析符号链接</b>，
	 * 从而支持仓库内部存在软连接（如子目录软连接到外部存储）的合法场景；</li>
	 * <li>真实路径：仅当逻辑路径不在仓库范围内时，再用 {@link Path#toRealPath()} 解析符号链接做兜底校验，
	 * 防止通过 {@code ../} 或外部软连接逃逸出仓库边界。</li>
	 * </ul>
	 * 仓库路径始终用逻辑路径比较，避免仓库本身是软连接时与目标路径解析方式不一致导致误判。
	 *
	 * @param file 待校验文件或目录
	 * @return 在仓库范围内返回true
	 * @throws IOException 路径解析失败
	 */
	private boolean isInRepositoryScope(File file) throws IOException {
		// 仓库逻辑路径：规范化但不解析软连接，兼容仓库本身为软连接的情况
		Path logicalRepo = Files.valueOf(this.getRepositoryFolder()).toPath().toAbsolutePath().normalize();
		// 目标逻辑路径：同样规范化但不解析软连接，支持仓库内子目录为软连接
		Path logicalTarget = file.toPath().toAbsolutePath().normalize();
		if (logicalTarget.startsWith(logicalRepo)) {
			// 逻辑路径在仓库范围内，允许访问（包含仓库内软连接的合法场景）
			return true;
		}
		// 逻辑路径不在仓库范围内，再用真实路径兜底，防止通过 ../ 或外部软连接逃逸
		Path repositoryPath = logicalRepo.toRealPath();
		Path targetPath;
		if (file.exists()) {
			targetPath = file.toPath().toRealPath();
		} else {
			File parent = file.getParentFile();
			if (parent != null && parent.exists()) {
				targetPath = parent.toPath().toRealPath().resolve(file.getName()).normalize();
			} else {
				targetPath = logicalTarget;
			}
		}
		return targetPath.startsWith(repositoryPath);
	}

	private void checkRepositoryScope(File file) throws IOException {
		if (!this.isInRepositoryScope(file)) {
			throw new IllegalStateException(Strings.format("path [%s] is out of repository scope.", file.getPath()));
		}
	}

	protected String groupingOf(String name) {
		StringBuilder builder = new StringBuilder();
		char[] items = new char[4];
		for (int i = 0; i < 4 && i < name.length(); i++) {
			items[i] = name.charAt(i);
		}
		char item;
		for (int i = 0; i < items.length; i++) {
			item = items[i];
			if (i == 2) {
				builder.append(File.separator);
			}
			if (item == 0 || item < 32 || item > 127) {
				item = 95;
			}
			builder.append(item);
		}
		return builder.toString();
	}

	/**
	 * 查询文件
	 * 
	 * @param criteria 条件
	 * @return 符合条件的文件数组
	 */
	@Override
	protected List<FileItem> searchFiles(ICriteria criteria) throws Exception {
		if (criteria == null) {
			criteria = new Criteria();
		}
		List<ICondition> conditions;
		String workFolder = this.getRepositoryFolder();
		// 修正查询条件
		for (ICondition condition : criteria.getConditions()) {
			// 修改路径符号
			if (Strings.equalsIgnoreCase(CONDITION_ALIAS_FILE_PATH, condition.getAlias())
					|| Strings.equalsIgnoreCase(CONDITION_ALIAS_FILE_NAME, condition.getAlias())
					|| Strings.equalsIgnoreCase(CONDITION_ALIAS_FILE_FOLDER, condition.getAlias())) {
				if (!Strings.isNullOrEmpty(condition.getValue())) {
					condition.setValue(condition.getValue().replace(Strings.VALUE_BACKSLASH, File.separator));
					condition.setValue(condition.getValue().replace(Strings.VALUE_SLASH, File.separator));
					condition.setValue(Files.pathOf(condition.getValue()));
					if (Strings.equalsIgnoreCase(CONDITION_ALIAS_FILE_FOLDER, condition.getAlias())) {
						if (!Strings.endsWith(condition.getValue(), File.separator)) {
							condition.setValue(condition.getValue() + File.separator);
						}
					}
				}
			}
		}
		// 是否包含子文件夹
		boolean include = false;
		conditions = criteria.getConditions().where(c -> CONDITION_ALIAS_INCLUDE_SUBFOLDER.equals(c.getAlias()));
		for (ICondition condition : conditions) {
			emYesNo value = emYesNo.NO;
			if (condition.getValue().length() > 1)
				value = emYesNo.valueOf(condition.getValue());
			else {
				value = (emYesNo) Enums.valueOf(emYesNo.class, condition.getValue());
			}
			include = value == emYesNo.YES ? true : false;
			condition.setOperation(ConditionOperation.NONE);
		}
		// 是否指定工作目录
		conditions = criteria.getConditions().where(c -> CONDITION_ALIAS_FILE_FOLDER.equals(c.getAlias()));
		if (conditions.size() == 1) {
			for (ICondition condition : conditions) {
				if (condition.getOperation() == ConditionOperation.EQUAL) {
					// 指定查询文件夹，则更改工作目录
					workFolder = Files.valueOf(workFolder, condition.getValue()).getPath();
					condition.setOperation(ConditionOperation.NONE);
				}
			}
		} else if (conditions.size() > 0) {
			include = true;
			for (ICondition condition : conditions) {
				// 包含子目录的等于改为开始于
				if (condition.getOperation() == ConditionOperation.EQUAL) {
					condition.setOperation(ConditionOperation.START);
				}
			}
		}
		// 是否包含文件路径
		conditions = criteria.getConditions().where(c -> CONDITION_ALIAS_FILE_PATH.equals(c.getAlias()));
		if (conditions.size() > 0) {
			include = true;
			for (ICondition condition : conditions) {
				if (condition.getOperation() == ConditionOperation.EQUAL) {
					if (Strings.indexOf(condition.getValue(), File.separator) > 0) {
						condition.setOperation(ConditionOperation.END);
					} else {
						condition.setOperation(ConditionOperation.CONTAIN);
					}
				}
			}
		}
		// 是否指定的文件含目录
		conditions = criteria.getConditions().where(c -> CONDITION_ALIAS_FILE_NAME.equals(c.getAlias()));
		if (conditions.size() == 1) {
			// 文件名查询，条件仅一个时
			for (ICondition condition : conditions) {
				if (condition.getOperation() == ConditionOperation.EQUAL) {
					// 指定文件路径，则更改工作目录
					if (Strings.indexOf(condition.getValue(), File.separator) > 0) {
						workFolder = Files.valueOf(workFolder, condition.getValue()).getParentFile().getPath();
						condition.setValue(
								condition.getValue().substring(condition.getValue().lastIndexOf(File.separator) + 1));
					} else if (this.isGroupingFiles()) {
						workFolder = Files.valueOf(workFolder, this.groupingOf(condition.getValue())).getPath();
					}
				}
			}
		} else {
			for (ICondition condition : conditions) {
				if (condition.getOperation() == ConditionOperation.EQUAL) {
					if (Strings.indexOf(condition.getValue(), File.separator) > 0) {
						include = true;
						// 有路径，则改成路径比较
						condition.setAlias(CONDITION_ALIAS_FILE_PATH);
						condition.setOperation(ConditionOperation.END);
					}
				}
			}
		}
		// 检查文件夹内文件是否符合条件
		File folder = Files.valueOf(workFolder);
		// 访问路径不在允许范围
		this.checkRepositoryScope(folder);
		if (!folder.isDirectory() || !folder.exists()) {
			Logger.log(MessageLevel.WARN, "repository: not found folder [%s].", workFolder);
			return new ArrayList<>();
		}
		// 重建查询
		Criteria nCriteria = new Criteria();
		nCriteria.setResultCount(criteria.getResultCount());
		for (int i = 0; i < criteria.getConditions().size(); i++) {
			ICondition condition = criteria.getConditions().get(i);
			if (condition.getOperation() == ConditionOperation.NONE) {
				if (i > 0 && condition.getBracketClose() > 0) {
					// 闭括号后移
					criteria.getConditions().get(i - 1).setBracketClose(condition.getBracketClose());
				}
				if (i < criteria.getConditions().size() - 1 && condition.getBracketOpen() > 0) {
					// 开括号前移
					criteria.getConditions().get(i + 1).setBracketOpen(condition.getBracketOpen());
				}
				continue;
			}
			nCriteria.getConditions().add(condition);
		}
		for (ISort sort : criteria.getSorts()) {
			nCriteria.getSorts().add(sort);
		}
		// 查询符合条件的文件
		ArrayList<FileItem> fileItems = new ArrayList<>();
		for (File file : this.searchFiles(folder, include, nCriteria)) {
			fileItems.add(new FileItem(file));
			if (criteria.getResultCount() > 0 && fileItems.size() >= criteria.getResultCount()) {
				break;
			}
		}
		return fileItems;
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
					if (folderFiles == null) {
						return;
					}
					// 文件排序
					if (!criteria.getSorts().isEmpty()) {
						for (ISort sort : criteria.getSorts()) {
							if (CONDITION_ALIAS_FILE_NAME.equalsIgnoreCase(sort.getAlias())) {
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
							} else if (CONDITION_ALIAS_MODIFIED_TIME.equalsIgnoreCase(sort.getAlias())) {
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
					} catch (JudgmentOperationException e) {
						Logger.log(e);
					}
				}
			}
		};
		if (folder.isDirectory()) {
			File[] folderFiles = folder.listFiles();
			if (folderFiles != null) {
				for (File item : folderFiles) {
					searcher.accept(item);
				}
			}
		} else {
			searcher.accept(folder);
		}
		return files;
	}

	/**
	 * 保存文件
	 * 
	 * @param fileData 文件数据
	 * @return
	 * @throws Exception
	 */
	@Override
	protected FileItem save(FileData fileData) throws Exception {
		if (fileData == null || fileData.getStream() == null) {
			return null;
		}
		// 形成新文件名，保留扩展名
		if (Strings.isNullOrEmpty(fileData.getName())) {
			String tmpValue = Files.extensionOf(fileData.getOriginalName());
			fileData.setName(Strings.isNullOrEmpty(tmpValue) ? UUID.randomUUID().toString()
					: Strings.concat(UUID.randomUUID().toString(), Strings.VALUE_DOT, tmpValue.toLowerCase()));
		}
		StringBuilder builder = new StringBuilder();
		builder.append(this.getRepositoryFolder());
		builder.append(File.separator);
		if (this.isGroupingFiles()) {
			builder.append(this.groupingOf(fileData.getName()));
			builder.append(File.separator);
		}
		builder.append(fileData.getName());
		fileData.setLocation(builder.toString());
		builder = null;
		File file = Files.valueOf(fileData.getLocation());
		// 访问路径不在允许范围
		this.checkRepositoryScope(file);
		Files.writeTo(fileData.getStream(), file);
		return new FileItem(file);
	}

	@Override
	protected boolean delete(FileItem data) throws Exception {
		File file = Files.valueOf(data.getPath());
		// 访问路径不在允许范围
		this.checkRepositoryScope(file);
		// 不允许删除文件夹
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				return true;
			}
		}
		return false;
	}
}
