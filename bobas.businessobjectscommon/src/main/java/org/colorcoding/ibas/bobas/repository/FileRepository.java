package org.colorcoding.ibas.bobas.repository;

import java.util.Objects;

import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.file.FileData;
import org.colorcoding.ibas.bobas.file.FileFactory;
import org.colorcoding.ibas.bobas.file.FileItem;
import org.colorcoding.ibas.bobas.file.FileTransaction;
import org.colorcoding.ibas.bobas.message.Logger;

/**
 * 文件仓库（读、写）
 * 
 * @author Niuren.Zhu
 *
 */
public class FileRepository extends Repository {
	/**
	 * 检索条件项目：包含子文件夹。如： emYesNo.Yes，条件仅可等于，其他忽略。
	 */
	public static final String CONDITION_ALIAS_INCLUDE_SUBFOLDER = FileTransaction.CONDITION_ALIAS_INCLUDE_SUBFOLDER;
	/**
	 * 检索条件项目：文件所属文件夹（比较对象是文件夹时是自身）
	 */
	public static final String CONDITION_ALIAS_FILE_FOLDER = FileTransaction.CONDITION_ALIAS_FILE_FOLDER;
	/**
	 * 检索条件项目：文件名称
	 */
	public static final String CONDITION_ALIAS_FILE_NAME = FileTransaction.CONDITION_ALIAS_FILE_NAME;
	/**
	 * 检索条件项目：最后修改时间（文件时间）
	 */
	public static final String CONDITION_ALIAS_MODIFIED_TIME = FileTransaction.CONDITION_ALIAS_MODIFIED_TIME;
	/**
	 * 检索条件项目：文件路径（文件夹+名称）
	 */
	public static final String CONDITION_ALIAS_FILE_PATH = FileTransaction.CONDITION_ALIAS_FILE_PATH;

	/**
	 * 索条件项目：文件夹(Folder)或文件(File)
	 */
	public static final String CONDITION_ALIAS_FILE_TYPE = FileTransaction.CONDITION_ALIAS_FILE_TYPE;
	/**
	 * 查询条件值-文件
	 */
	public static final String CONDITION_VALUE_FILE = FileTransaction.CONDITION_VALUE_FILE;
	/**
	 * 查询条件值-文件夹
	 */
	public static final String CONDITION_VALUE_FOLDER = FileTransaction.CONDITION_VALUE_FOLDER;

	private String fileSign;

	public FileRepository() {
		this(Strings.VALUE_EMPTY);
	}

	public FileRepository(String fileSign) {
		this.fileSign = fileSign;
	}

	private volatile FileTransaction transaction;

	public synchronized final FileTransaction getTransaction() throws RepositoryException {
		return transaction;
	}

	protected synchronized void setTransaction(FileTransaction transaction) {
		this.transaction = transaction;
	}

	public synchronized final boolean inTransaction() throws RepositoryException {
		if (this.transaction == null) {
			return false;
		}
		return this.transaction.inTransaction();
	}

	public synchronized final boolean beginTransaction() throws RepositoryException {
		if (this.transaction == null) {
			this.initTransaction();
		}
		if (this.inTransaction()) {
			return false;
		}
		return this.transaction.beginTransaction();
	}

	public synchronized final void rollbackTransaction() throws RepositoryException {
		if (this.inTransaction()) {
			this.transaction.rollback();
		}
	}

	public synchronized final void commitTransaction() throws RepositoryException {
		if (this.inTransaction()) {
			this.transaction.commit();
		}
	}

	protected synchronized boolean initTransaction() throws RepositoryException {
		if (this.transaction != null) {
			return false;
		}
		FileTransaction transaction = FileFactory.createManager(fileSign).createTransaction(this.getRepositoryFolder());
		transaction.setGroupingFiles(this.isGroupingFiles());
		this.setTransaction(transaction);
		return true;
	}

	@Override
	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
	}

	@Override
	public synchronized void close() throws RuntimeException {
		try {
			if (this.transaction != null) {
				this.transaction.close();
			}
			super.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String repositoryFolder;

	/**
	 * 获取仓库工作目录
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

	/**
	 * 查询文件
	 * 
	 * @param criteria 查询条件
	 * @return
	 */
	public OperationResult<FileItem> fetch(ICriteria criteria) {
		try {
			this.initTransaction();
			OperationResult<FileItem> operationResult = new OperationResult<>();
			FileItem[] datas = this.getTransaction().fetch(FileItem.class, criteria);
			for (FileItem data : datas) {
				if (!(data instanceof FileItem)) {
					continue;
				}
				operationResult.addResultObjects(data);
				Logger.log("repository: fetch [%s], [%s].", FileItem.class.getName(), data.getPath());
			}
			return operationResult;
		} catch (Exception e) {
			Logger.log(e);
			return new OperationResult<>(e);
		}
	}

	/**
	 * 保存文件
	 * 
	 * @param fileData 文件数据
	 * @return
	 */
	public OperationResult<FileItem> save(FileData fileData) {
		try {
			Objects.requireNonNull(fileData);
			this.initTransaction();
			OperationResult<FileItem> operationResult = new OperationResult<>();
			for (Object data : this.getTransaction().save(new Object[] { fileData })) {
				if (!(data instanceof FileItem)) {
					continue;
				}
				operationResult.addResultObjects((FileItem) data);
				Logger.log("repository: writed [%s], [%s].", FileData.class.getName(),
						Strings.isNullOrEmpty(fileData.getOriginalName()) ? fileData.getLocation()
								: String.format("%s|%s", fileData.getOriginalName(), fileData.getLocation()));
			}
			return operationResult;
		} catch (Exception e) {
			Logger.log(e);
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
		try {
			this.initTransaction();
			OperationResult<FileItem> operationResult = this.fetch(criteria);
			if (operationResult.getResultCode() != 0) {
				return operationResult;
			}
			FileItem[] datas = operationResult.getResultObjects()
					.toArray(new FileItem[operationResult.getResultObjects().size()]);
			operationResult = new OperationResult<>();
			for (FileItem data : this.getTransaction().delete(datas)) {
				if (!(data instanceof FileItem)) {
					continue;
				}
				operationResult.addResultObjects(data);
				Logger.log("repository: deleted [%s], [%s].", FileData.class.getName(), data.getPath());
			}
			return operationResult;
		} catch (Exception e) {
			Logger.log(e);
			return new OperationResult<>(e);
		}

	}
}
