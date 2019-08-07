package org.colorcoding.ibas.bobas.repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.bo.IBOKeysManager;
import org.colorcoding.ibas.bobas.bo.IBOStorageTag;
import org.colorcoding.ibas.bobas.bo.IBOTagReferenced;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.ITrackStatusOperator;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.core.RepositorySaveEventType;
import org.colorcoding.ibas.bobas.core.RepositorySaveListener;
import org.colorcoding.ibas.bobas.core.RepositorySaveSupport;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;

public class BORepository4File extends BORepository4FileReadonly implements IBORepository4File {
	protected static final String MSG_REPOSITORY_DELETED_DATA_FILE = "repository: deleted data file [%s].";
	protected static final String MSG_REPOSITORY_WRITED_DATA_FILE = "repository: writed data in file [%s].";

	@Override
	public boolean beginTransaction() throws RepositoryException {
		if (this.inTransaction()) {
			return false;
		}
		this.inTransaction = true;
		this.setTransactionId();// 创建新的事物，才创建新id
		return true;
	}

	@Override
	public void rollbackTransaction() throws RepositoryException {
		this.inTransaction = false;
		this.setTransactionId(null);
	}

	@Override
	public void commitTransaction() throws RepositoryException {
		this.inTransaction = false;
		this.setTransactionId(null);
	}

	private boolean inTransaction;

	@Override
	public boolean inTransaction() {
		return this.inTransaction;
	}

	private volatile RepositorySaveSupport repositorySaveSupport;

	/**
	 * 通知仓库保存事件
	 * 
	 * @param type 事务类型
	 * @param bo   发生业务对象
	 * @throws Exception 运行时错误
	 */
	private void fireRepositorySave(RepositorySaveEventType type, IBusinessObjectBase bo) throws Exception {
		if (type == RepositorySaveEventType.BEFORE_ADDING) {
			// 添加前
			// 存储标记
			this.tagStorage(bo);
			// 获取并更新主键
			IBOKeysManager keysManager = this.createKeysManager();
			keysManager.usePrimaryKeys(bo);
		} else if (type == RepositorySaveEventType.BEFORE_UPDATING) {
			// 更新前
			// 存储标记
			this.tagStorage(bo);
		} else if (type == RepositorySaveEventType.BEFORE_DELETING) {
			// 删除前
			// 存储标记
			this.tagStorage(bo);
			if (bo instanceof IBOTagReferenced) {
				IBOTagReferenced refed = (IBOTagReferenced) bo;
				if (refed.getReferenced() == emYesNo.YES) {
					// 被引用的数据，不允许删除，可以标记删除
					throw new RepositoryException(I18N.prop("msg_bobas_not_allow_delete_referenced_bo", bo.toString()));
				}
			}
		}
		if (this.repositorySaveSupport == null) {
			return;
		}
		this.repositorySaveSupport.fireRepositorySave(type, bo);
	}

	/**
	 * 添加事务监听
	 * 
	 * @param listener
	 */
	@Override
	public final void registerListener(RepositorySaveListener listener) {
		if (this.repositorySaveSupport == null) {
			this.repositorySaveSupport = new RepositorySaveSupport(this);
		}
		this.repositorySaveSupport.registerListener(listener);
	}

	/**
	 * 移出事务监听
	 * 
	 * @param listener
	 */
	@Override
	public final void removeListener(RepositorySaveListener listener) {
		if (this.repositorySaveSupport == null) {
			return;
		}
		this.repositorySaveSupport.removeListener(listener);
	}

	@Override
	public <T extends IBusinessObjectBase> IOperationResult<T> save(T bo) {
		return this.saveEx(bo);
	}

	@Override
	public <T extends IBusinessObjectBase> IOperationResult<T> saveEx(T bo) {
		OperationResult<T> operationResult = new OperationResult<>();
		try {
			IBusinessObjectBase nBO = this.mySave(bo);
			if (nBO instanceof ITrackStatusOperator) {
				// 保存成功，标记对象为OLD
				ITrackStatusOperator operator = (ITrackStatusOperator) nBO;
				operator.markOld(true);
			}
			operationResult.addResultObjects(nBO);
		} catch (Exception e) {
			operationResult.setError(e);
		}
		return operationResult;
	}

	protected IBOKeysManager createKeysManager() {
		FileKeysManager keysManager = new FileKeysManager();
		keysManager.setWorkFolder(this.getRepositoryFolder());
		keysManager.setTransactionId(this.getTransactionId());
		return keysManager;
	}

	private IBusinessObjectBase mySave(IBusinessObjectBase bo) throws Exception {
		if (bo == null) {
			throw new Exception(I18N.prop("msg_bobas_invalid_bo"));
		}
		if (bo.isDirty()) {
			// 仅修过的数据进行处理
			boolean myTrans = false;// 自己打开的事务
			try {
				// 开始保存数据
				myTrans = this.beginTransaction();
				if (bo.isNew()) {
					// 新建的对象
					this.fireRepositorySave(RepositorySaveEventType.BEFORE_ADDING, bo);
					this.writeBOFile(bo, this.getFileName(bo));
					this.fireRepositorySave(RepositorySaveEventType.ADDED, bo);
				} else if (bo.isDeleted()) {
					// 删除对象
					this.fireRepositorySave(RepositorySaveEventType.BEFORE_DELETING, bo);
					this.deleteBOFile(bo);
					this.fireRepositorySave(RepositorySaveEventType.DELETED, bo);
				} else {
					// 修改对象，先删除数据，再添加新的实例
					this.fireRepositorySave(RepositorySaveEventType.BEFORE_UPDATING, bo);
					this.deleteBOFile(bo);
					this.writeBOFile(bo, this.getFileName(bo));
					this.fireRepositorySave(RepositorySaveEventType.UPDATED, bo);
				}
				if (myTrans) {
					// 自己打开的事务
					this.commitTransaction();// 关闭事务
				}
			} catch (Exception e) {
				if (myTrans) {
					// 自己打开的事务
					this.rollbackTransaction();// 关闭事务
				}
				throw e;
			}
		}
		return bo;
	}

	private String getFileName(IBusinessObjectBase bo) {
		// 获取对象工作目录
		StringBuilder builder = new StringBuilder();
		builder.append(this.getRepositoryFolder());
		builder.append(File.separator);
		if (bo instanceof IBOStorageTag) {
			IBOStorageTag boTag = (IBOStorageTag) bo;
			if (boTag.getObjectCode() != null && !boTag.getObjectCode().isEmpty()) {
				builder.append(boTag.getObjectCode());
			}
		}
		if (builder.length() <= 2) {
			builder.append("not_classified");
		}
		builder.append(File.separator);
		builder.append(UUID.randomUUID().toString());
		builder.append(BO_DATA_FILE_EXTENSION);
		return builder.toString().toLowerCase();
	}

	private BOFile getBOFile(IBusinessObjectBase bo) throws Exception {
		BOFile[] boFiles = this.myFetchEx(bo.getCriteria(), bo.getClass());
		if (boFiles.length == 0) {
			throw new Exception(I18N.prop("msg_bobas_not_found_bo_copy", bo));
		}
		return boFiles[0];
	}

	private boolean deleteBOFile(IBusinessObjectBase bo) throws Exception {
		BOFile boFile = this.getBOFile(bo);
		File file = new File(this.getRepositoryFolder() + File.separator + boFile.getFilePath());
		if (file.exists()) {
			Logger.log(MSG_REPOSITORY_DELETED_DATA_FILE, file.getPath());
			return file.delete();
		}
		return false;
	}

	private void writeBOFile(IBusinessObjectBase bo, String path) throws IOException {
		File file = new File(path);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		try (FileOutputStream out = new FileOutputStream(file, false)) {
			if (bo instanceof ITrackStatusOperator) {
				ITrackStatusOperator operator = (ITrackStatusOperator) bo;
				// 清理标记删除的数据
				BOUtilities.removeDeleted(bo);
				// 重置状态
				operator.markOld(true);
			}
			out.write(bo.toString("xml").getBytes("utf-8"));
		}
		StringBuilder builder = new StringBuilder();
		builder.append(file.getParentFile().getPath());
		builder.append(File.separator);
		builder.append(bo.getClass().getName());
		builder.append(BO_TYPE_FILE_EXTENSION);
		file = new File(builder.toString());
		if (!file.exists()) {
			file.createNewFile();
		}
		Logger.log(MSG_REPOSITORY_WRITED_DATA_FILE, path);
	}

}
