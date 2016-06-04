package org.colorcoding.bobas.repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import org.colorcoding.bobas.bo.IBOStorageTag;
import org.colorcoding.bobas.common.IOperationResult;
import org.colorcoding.bobas.common.OperationResult;
import org.colorcoding.bobas.core.BOFactoryException;
import org.colorcoding.bobas.core.IBusinessObjectBase;
import org.colorcoding.bobas.core.ITrackStatusOperator;
import org.colorcoding.bobas.core.RepositoryException;
import org.colorcoding.bobas.core.SaveActionsException;
import org.colorcoding.bobas.core.SaveActionsListener;
import org.colorcoding.bobas.core.SaveActionsSupport;
import org.colorcoding.bobas.core.SaveActionsType;
import org.colorcoding.bobas.db.DbException;
import org.colorcoding.bobas.i18n.i18n;
import org.colorcoding.bobas.messages.RuntimeLog;

public class BORepository4File extends BORepository4FileReadonly implements IBORepository4File {

	@Override
	public boolean beginTransaction() throws RepositoryException {
		if (this.inTransaction()) {
			return false;
		}
		this.inTransaction = true;
		return true;
	}

	@Override
	public void rollbackTransaction() throws RepositoryException {
		this.inTransaction = false;
	}

	@Override
	public void commitTransaction() throws RepositoryException {
		this.inTransaction = false;
	}

	private boolean inTransaction;

	@Override
	public boolean inTransaction() {
		return this.inTransaction;
	}

	private volatile SaveActionsSupport saveActionsSupport;

	/**
	 * 通知事务
	 * 
	 * @param type
	 *            事务类型
	 * @param bo
	 *            发生业务对象
	 * @return 是否继续执行
	 * @throws SaveActionsException
	 *             运行时错误
	 */
	protected final boolean notifyActions(SaveActionsType type, IBusinessObjectBase bo) throws SaveActionsException {
		if (this.saveActionsSupport == null) {
			return true;
		}
		return this.saveActionsSupport.fireActions(type, bo);
	}

	/**
	 * 添加事务监听
	 * 
	 * @param listener
	 */
	@Override
	public final void addSaveActionsListener(SaveActionsListener listener) {
		if (this.saveActionsSupport == null) {
			this.saveActionsSupport = new SaveActionsSupport(this);
		}
		this.saveActionsSupport.addListener(listener);
	}

	/**
	 * 移出事务监听
	 * 
	 * @param listener
	 */
	@Override
	public final void removeSaveActionsListener(SaveActionsListener listener) {
		if (this.saveActionsSupport == null) {
			return;
		}
		this.saveActionsSupport.removeListener(listener);
	}

	@Override
	public IOperationResult<?> save(IBusinessObjectBase bo) {
		return this.saveEx(bo);
	}

	@Override
	public IOperationResult<?> saveEx(IBusinessObjectBase bo) {
		OperationResult<?> operationResult = new OperationResult<Object>();
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

	private IBusinessObjectBase mySave(IBusinessObjectBase bo) throws RepositoryException, DbException {
		if (bo == null) {
			throw new RepositoryException(i18n.prop("msg_bobas_invalid_bo"));
		}
		if (bo.isDirty()) {
			// 仅修过的数据进行处理
			boolean myTrans = false;// 自己打开的事务
			try {
				// 获取对象工作目录
				String boCode = "not_classified";
				if (bo instanceof IBOStorageTag) {
					IBOStorageTag boTag = (IBOStorageTag) bo;
					if (boTag.getObjectCode() != null && !boTag.equals("")) {
						boCode = boTag.getObjectCode().toLowerCase();
					}
				}
				String boFolder = String.format("%s\\%s", this.getRepositoryFolder(), boCode);
				// 开始保存数据
				myTrans = this.beginTransaction();
				this.tagStorage(bo);// 存储标记
				if (bo.isNew()) {
					// 新建的对象
					this.notifyActions(SaveActionsType.before_adding, bo);
					String fileName = String.format("%s\\%s.bo", boFolder, this.getFileName(bo));
					this.writeBOFile(bo, fileName);
					this.notifyActions(SaveActionsType.added, bo);
				} else if (bo.isDeleted()) {
					// 删除对象
					this.notifyActions(SaveActionsType.before_deleting, bo);
					this.deleteBOFile(bo);
					this.notifyActions(SaveActionsType.deleted, bo);
				} else {
					// 修改对象，先删除数据，再添加新的实例
					this.notifyActions(SaveActionsType.before_updating, bo);
					this.deleteBOFile(bo);
					String fileName = String.format("%s\\%s.bo", boFolder, this.getFileName(bo));
					this.writeBOFile(bo, fileName);
					this.notifyActions(SaveActionsType.updated, bo);
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
				throw new DbException(i18n.prop("msg_bobas_save_bo_faild", e.getMessage()), e);
			}
		}
		return bo;
	}

	private String getFileName(IBusinessObjectBase bo) {
		return UUID.randomUUID().toString();// bo.toString();
	}

	private BOFile getBOFile(IBusinessObjectBase bo) throws RepositoryException, BOFactoryException, JAXBException {
		BOFile[] boFiles = this.myFetchEx(bo.getCriteria(), bo.getClass());
		if (boFiles.length == 0) {
			throw new RepositoryException(i18n.prop("msg_bobas_not_found_bo_copy", bo));
		}
		return boFiles[0];
	}

	private boolean deleteBOFile(IBusinessObjectBase bo) throws RepositoryException, BOFactoryException, JAXBException {
		BOFile boFile = this.getBOFile(bo);
		File file = new File(String.format("%s\\%s", this.getRepositoryFolder(), boFile.getFilePath()));
		if (file.exists()) {
			RuntimeLog.log(RuntimeLog.MSG_REPOSITORY_DELETED_DATA_FILE, file.getPath());
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
		FileOutputStream out = new FileOutputStream(file, false);
		out.write(bo.toString("xml").getBytes("utf-8"));
		out.close();
		RuntimeLog.log(RuntimeLog.MSG_REPOSITORY_WRITED_DATA_FILE, path);
	}
}
