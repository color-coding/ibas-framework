package org.colorcoding.ibas.bobas.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.IBODocument;
import org.colorcoding.ibas.bobas.bo.IBOMasterData;
import org.colorcoding.ibas.bobas.bo.IBOSimple;
import org.colorcoding.ibas.bobas.bo.IBOStorageTag;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.core.BOFactoryException;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.ITrackStatusOperator;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.core.SaveActionsException;
import org.colorcoding.ibas.bobas.core.SaveActionsListener;
import org.colorcoding.ibas.bobas.core.SaveActionsSupport;
import org.colorcoding.ibas.bobas.core.SaveActionsType;
import org.colorcoding.ibas.bobas.db.DbException;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.messages.MessageLevel;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;

public class BORepository4File extends BORepository4FileReadonly implements IBORepository4File {

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

	private volatile SaveActionsSupport saveActionsSupport;

	/**
	 * 通知事务
	 * 
	 * @param type
	 *            事务类型
	 * @param bo
	 *            发生业务对象
	 * @throws SaveActionsException
	 *             运行时错误
	 */
	private void notifyActions(SaveActionsType type, IBusinessObjectBase bo) throws SaveActionsException {
		if (this.saveActionsSupport == null) {
			return;
		}
		this.saveActionsSupport.fireActions(type, bo);
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
					if (boTag.getObjectCode() != null && !boTag.getObjectCode().isEmpty()) {
						boCode = boTag.getObjectCode().toLowerCase();
					}
				}
				String boFolder = this.getRepositoryFolder() + File.separator + boCode;
				// 开始保存数据
				myTrans = this.beginTransaction();
				this.tagStorage(bo);// 存储标记
				if (bo.isNew()) {
					// 新建的对象
					this.usePrimaryKeys(bo);
					this.notifyActions(SaveActionsType.before_adding, bo);
					String fileName = String.format("%s%s%s.bo", boFolder, File.separator, this.getFileName(bo));
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
					String fileName = String.format("%s%s%s.bo", boFolder, File.separator, this.getFileName(bo));
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
				throw new DbException(i18n.prop("msg_bobas_to_save_bo_faild", e.getMessage()), e);
			}
		}
		return bo;
	}

	protected void usePrimaryKeys(IBusinessObjectBase bo) throws IOException {
		if (bo instanceof IBOStorageTag) {
			IBOStorageTag tagBO = (IBOStorageTag) bo;
			String companyId = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_COMPANY_ID, "CC")
					.toLowerCase();
			File file = new File(this.getRepositoryFolder() + File.separator + companyId + "_sys" + File.separator
					+ "bo_keys.properties");
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			Properties props = new Properties();
			props.load(new FileInputStream(file));
			String value = props.getProperty(tagBO.getObjectCode());
			if (value == null || value.isEmpty()) {
				value = "1";
			}
			int key = 1, nextKey = 1;
			key = Integer.parseInt(value);
			nextKey = key + 1;
			if (bo instanceof IBODocument) {
				IBODocument item = (IBODocument) bo;
				item.setDocEntry(key);
			} else if (bo instanceof IBOMasterData) {
				IBOMasterData item = (IBOMasterData) bo;
				item.setDocEntry(key);
			} else if (bo instanceof IBOSimple) {
				IBOSimple item = (IBOSimple) bo;
				item.setObjectKey(key);
			}
			OutputStream fos = new FileOutputStream(file);
			props.setProperty(tagBO.getObjectCode(), String.valueOf(nextKey));
			props.store(fos, String.format("fixed by transaction [%s].", this.getTransactionId()));
		}
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
		File file = new File(this.getRepositoryFolder() + File.separator + boFile.getFilePath());
		if (file.exists()) {
			RuntimeLog.log(MessageLevel.DEBUG, RuntimeLog.MSG_REPOSITORY_DELETED_DATA_FILE, file.getPath());
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
		if (bo instanceof ITrackStatusOperator) {
			ITrackStatusOperator operator = (ITrackStatusOperator) bo;
			// 清理标记删除的数据
			operator.clearDeleted();
			// 重置状态
			operator.markOld(true);
		}
		out.write(bo.toString("xml").getBytes("utf-8"));
		out.close();
		String type = String.format("%s%s%s.type", file.getParentFile().getPath(), File.separator,
				bo.getClass().getName());
		file = new File(type);
		if (!file.exists()) {
			file.createNewFile();
		}
		RuntimeLog.log(MessageLevel.DEBUG, RuntimeLog.MSG_REPOSITORY_WRITED_DATA_FILE, path);
	}
}
