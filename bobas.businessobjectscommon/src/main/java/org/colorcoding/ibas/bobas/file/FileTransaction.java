package org.colorcoding.ibas.bobas.file;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.List;
import org.colorcoding.ibas.bobas.expression.FileJudgmentLink;
import org.colorcoding.ibas.bobas.expression.JudgmentOperationException;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.repository.RepositoryException;
import org.colorcoding.ibas.bobas.repository.Transaction;

public abstract class FileTransaction extends Transaction {

	/**
	 * 检索条件项目：包含子文件夹。如： emYesNo.Yes，条件仅可等于，其他忽略。
	 */
	public static final String CONDITION_ALIAS_INCLUDE_SUBFOLDER = "IncludeSubfolder";
	/**
	 * 检索条件项目：文件所属文件夹（比较对象是文件夹时是自身）
	 */
	public static final String CONDITION_ALIAS_FILE_FOLDER = FileJudgmentLink.CRITERIA_CONDITION_ALIAS_FILE_FOLDER;
	/**
	 * 检索条件项目：文件名称
	 */
	public static final String CONDITION_ALIAS_FILE_NAME = FileJudgmentLink.CRITERIA_CONDITION_ALIAS_FILE_NAME;
	/**
	 * 检索条件项目：最后修改时间（文件时间）
	 */
	public static final String CONDITION_ALIAS_MODIFIED_TIME = FileJudgmentLink.CRITERIA_CONDITION_ALIAS_MODIFIED_TIME;
	/**
	 * 检索条件项目：文件路径（文件夹+名称）
	 */
	public static final String CONDITION_ALIAS_FILE_PATH = FileJudgmentLink.CRITERIA_CONDITION_ALIAS_FILE_PATH;
	/**
	 * 索条件项目：文件夹(Folder)或文件(File)
	 */
	public static final String CONDITION_ALIAS_FILE_TYPE = FileJudgmentLink.CRITERIA_CONDITION_ALIAS_FILE_TYPE;
	/**
	 * 查询条件值-文件
	 */
	public static final String CONDITION_VALUE_FILE = FileJudgmentLink.CRITERIA_CONDITION_VALUE_FILE;
	/**
	 * 查询条件值-文件夹
	 */
	public static final String CONDITION_VALUE_FOLDER = FileJudgmentLink.CRITERIA_CONDITION_VALUE_FOLDER;

	private String repositoryFolder;

	/**
	 * 仓库工作目录
	 * 
	 * @return
	 */
	public String getRepositoryFolder() {
		return this.repositoryFolder;
	}

	public void setRepositoryFolder(String folder) {
		this.repositoryFolder = folder;
	}

	private boolean groupingFiles;

	/**
	 * 文件分组存储
	 * 
	 * @return
	 */
	public final boolean isGroupingFiles() {
		return groupingFiles;
	}

	public final void setGroupingFiles(boolean groupingFiles) {
		this.groupingFiles = groupingFiles;
	}

	@Override
	public boolean inTransaction() throws RepositoryException {
		return false;
	}

	@Override
	public boolean beginTransaction() throws RepositoryException {
		return false;
	}

	@Override
	public void rollback() throws RepositoryException {
	}

	@Override
	public void commit() throws RepositoryException {
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T[] fetch(Class<?> dataType, ICriteria criteria) throws RepositoryException {
		if (!FileItem.class.isAssignableFrom(dataType)) {
			throw new RepositoryException(I18N.prop("msg_bobas_data_type_not_support", dataType));
		}
		try {
			List<FileItem> results = this.searchFiles(criteria);
			for (FileItem fileItem : results) {
				fileItem.setMaskFolder(this.getRepositoryFolder());
			}
			return results.toArray((T[]) Array.newInstance(dataType, results.size()));
		} catch (RepositoryException e) {
			throw e;
		} catch (Exception e) {
			throw new RepositoryException(e.getMessage(), e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T[] save(T[] datas) throws RepositoryException {
		for (T data : datas) {
			if (data instanceof FileData) {
				continue;
			}
			throw new RepositoryException(I18N.prop("msg_bobas_data_type_not_support", data.getClass()));
		}
		try {
			FileItem fileItem;
			FileData fileData;
			List<FileItem> results = new ArrayList<>();
			for (T data : datas) {
				fileData = (FileData) data;
				fileItem = this.save(fileData);
				if (fileItem == null) {
					continue;
				}
				fileItem.setMaskFolder(this.getRepositoryFolder());
				results.add(fileItem);
			}
			return results.toArray((T[]) new FileItem[] {});
		} catch (RepositoryException e) {
			throw e;
		} catch (Exception e) {
			throw new RepositoryException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T[] delete(T[] datas) throws RepositoryException {
		for (T data : datas) {
			if (data instanceof FileItem) {
				continue;
			}
			throw new RepositoryException(I18N.prop("msg_bobas_data_type_not_support", data.getClass()));
		}
		try {
			FileItem fileItem;
			List<FileItem> results = new ArrayList<>();
			for (T data : datas) {
				fileItem = (FileItem) data;
				if (!this.delete(fileItem)) {
					continue;
				}
				results.add(fileItem);
			}
			return results.toArray((T[]) new FileItem[] {});
		} catch (RepositoryException e) {
			throw e;
		} catch (Exception e) {
			throw new RepositoryException(e.getMessage(), e);
		}
	}

	private volatile HashSet<Object> cacheDatas = null;

	/**
	 * 缓存数据
	 * 
	 * @param data 待缓存数据
	 * @return true:缓存成功（新）; false:已缓存
	 */
	@Override
	public synchronized boolean cache(Object data) {
		Objects.requireNonNull(data);
		if (this.cacheDatas == null) {
			this.cacheDatas = new HashSet<>();
		}
		return this.cacheDatas.add(data);
	}

	/**
	 * 缓存中查询数据
	 * 
	 * @param <T>      数据类型
	 * @param dataType 类型
	 * @param criteria 查询条件
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public synchronized <T> T[] fetchInCache(Class<?> dataType, ICriteria criteria) throws RepositoryException {
		try {
			Objects.requireNonNull(criteria);
			Objects.requireNonNull(dataType);
			// 缓存数据迭代器（不一次取出）
			Iterator<?> iterator = new Iterator<Object>() {
				// 数据迭代器
				Iterator<?> current = FileTransaction.this.cacheDatas != null
						? FileTransaction.this.cacheDatas.iterator()
						: null;

				@Override
				public boolean hasNext() {
					if (this.current != null && this.current.hasNext()) {
						return true;
					}
					return false;
				}

				@Override
				public Object next() {
					if (this.current != null && this.current.hasNext()) {
						Object data = this.current.next();
						if (dataType.isInstance(data)) {
							return data;
						} else {
							return this.next();
						}
					}
					return null;
				}
			};
			FileJudgmentLink judgmentLinks = new FileJudgmentLink();
			judgmentLinks.setMaskFolder(this.getRepositoryFolder());
			judgmentLinks.parsingConditions(criteria.getConditions());
			Object data;
			List<Object> datas = new ArrayList<>();
			while (iterator.hasNext()) {
				data = iterator.next();
				if (!dataType.isInstance(data)) {
					continue;
				}
				if (judgmentLinks.judge(data)) {
					datas.add(data);
				}
			}
			return datas.toArray((T[]) Array.newInstance(dataType, datas.size()));
		} catch (JudgmentOperationException e) {
			throw new RepositoryException(e.getMessage(), e);
		}
	}

	@Override
	public void close() throws Exception {
		this.cacheDatas = null;
	}

	protected abstract List<FileItem> searchFiles(ICriteria criteria) throws Exception;

	protected abstract FileItem save(FileData data) throws Exception;

	protected abstract boolean delete(FileItem data) throws Exception;
}
