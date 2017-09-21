package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.bo.IBOKeysManager;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.ISqlQuery;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.IBusinessObjectListBase;
import org.colorcoding.ibas.bobas.core.ITrackStatusOperator;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.core.SaveActionListener;
import org.colorcoding.ibas.bobas.core.SaveActionSupport;
import org.colorcoding.ibas.bobas.core.SaveActionType;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.core.fields.IManageFields;
import org.colorcoding.ibas.bobas.db.DbException;
import org.colorcoding.ibas.bobas.db.IBOAdapter4Db;
import org.colorcoding.ibas.bobas.db.IDbCommand;
import org.colorcoding.ibas.bobas.db.IDbDataReader;
import org.colorcoding.ibas.bobas.i18n.I18N;

/**
 * 基本数据库仓库-读写
 */
public class BORepository4Db extends BORepository4DbReadonly implements IBORepository4Db {

	public BORepository4Db() {
	}

	public BORepository4Db(String sign) {
		super(sign);
	}

	@Override
	public boolean beginTransaction() throws RepositoryException {
		try {
			boolean done = this.getDbConnection().beginTransaction();
			if (done) {
				// 开启新的事物才创建新的id
				this.setTransactionId();
			}
			return done;
		} catch (DbException e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public void rollbackTransaction() throws RepositoryException {
		try {
			this.getDbConnection().rollbackTransaction();
			this.setTransactionId(null);
		} catch (DbException e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public void commitTransaction() throws RepositoryException {
		try {
			this.getDbConnection().commitTransaction();
			this.setTransactionId(null);
		} catch (DbException e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public final boolean inTransaction() {
		try {
			return this.getDbConnection().inTransaction();
		} catch (DbException e) {
			return false;
		}
	}

	private volatile SaveActionSupport saveActionSupport;

	/**
	 * 通知事务
	 * 
	 * @param type
	 *            事务类型
	 * @param bo
	 *            发生业务对象
	 * @throws SaveActionException
	 *             运行时错误
	 */
	private void fireSaveAction(SaveActionType type, IBusinessObjectBase bo) throws RepositoryException {
		if (this.saveActionSupport == null) {
			return;
		}
		this.saveActionSupport.fireAction(type, bo);
	}

	/**
	 * 添加事务监听
	 * 
	 * @param listener
	 */
	@Override
	public final void registerListener(SaveActionListener listener) {
		if (this.saveActionSupport == null) {
			this.saveActionSupport = new SaveActionSupport(this);
		}
		this.saveActionSupport.registerListener(listener);
	}

	/**
	 * 移出事务监听
	 * 
	 * @param listener
	 */
	@Override
	public final void removeListener(SaveActionListener listener) {
		if (this.saveActionSupport == null) {
			return;
		}
		this.saveActionSupport.removeListener(listener);
	}

	@Override
	public <T extends IBusinessObjectBase> IOperationResult<T> save(T bo) {
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

	@Override
	public <T extends IBusinessObjectBase> IOperationResult<T> saveEx(T bo) {
		OperationResult<T> operationResult = new OperationResult<>();
		try {
			IBusinessObjectBase nBO = this.mySaveEx(bo);
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

	private IBOKeysManager keysManager;

	public final IBOKeysManager getKeysManager() {
		if (this.keysManager == null) {
			this.keysManager = this.getBOAdapter();
		}
		return keysManager;
	}

	public final void setBOKeysManager(IBOKeysManager value) {
		this.keysManager = value;
	}

	/**
	 * 保存对象，不包括子属性
	 * 
	 * @param bo
	 *            对象
	 * @param root
	 *            根对象
	 * @return 保存的对象
	 * @throws Exception
	 */
	private final IBusinessObjectBase mySave(IBusinessObjectBase bo) throws Exception {
		if (bo == null) {
			throw new RepositoryException(I18N.prop("msg_bobas_invalid_bo"));
		}
		if (bo.isDirty()) {
			// 仅修过的数据进行处理
			IBOAdapter4Db adapter4Db = this.getBOAdapter();
			IDbDataReader reader = null;
			IDbCommand command = null;
			boolean myOpenedDb = false;// 自己打开的数据库
			boolean myTrans = false;// 自己打开的事务
			try {
				ISqlQuery sqlQuery = null;
				// 开始保存数据
				myOpenedDb = this.openDbConnection();
				myTrans = this.beginTransaction();
				command = this.getDbConnection().createCommand();
				this.tagStorage(bo);// 存储标记
				if (bo.isNew()) {
					// 新建的对象
					this.getKeysManager().usePrimaryKeys(bo, command);// 获取并更新主键
					this.getKeysManager().useSeriesKey(bo, command);// 获取并更新系列号
					this.fireSaveAction(SaveActionType.BEFORE_ADDING, bo);
					sqlQuery = adapter4Db.parseInsertScript(bo);
				} else if (bo.isDeleted()) {
					// 删除对象
					this.fireSaveAction(SaveActionType.BEFORE_DELETING, bo);
					sqlQuery = adapter4Db.parseDeleteScript(bo);
				} else {
					// 修改对象，先删除数据，再添加新的实例
					this.fireSaveAction(SaveActionType.BEFORE_UPDATING, bo);
					sqlQuery = adapter4Db.parseDeleteScript(bo);
					command.executeUpdate(sqlQuery);// 执行删除副本
					sqlQuery = adapter4Db.parseInsertScript(bo);
				}
				// 运行保存语句
				command.executeUpdate(sqlQuery);
				// 通知事务
				if (bo.isNew()) {
					// 新建的对象
					this.fireSaveAction(SaveActionType.ADDED, bo);
				} else if (bo.isDeleted()) {
					// 删除对象
					this.fireSaveAction(SaveActionType.DELETED, bo);
				} else {
					// 修改对象
					this.fireSaveAction(SaveActionType.UPDATED, bo);
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
				throw new RepositoryException(I18N.prop("msg_bobas_to_save_bo_faild", e.getMessage()), e);
			} finally {
				if (reader != null) {
					reader.close();
				}
				if (command != null) {
					command.close();
				}
				reader = null;
				command = null;
				if (myOpenedDb) {
					// 自己开打自己关闭
					this.closeDbConnection();// 关闭数据库连接
				}
			}
		}
		return bo;
	}

	/**
	 * 保存对象，包括子属性
	 * 
	 * @param bo
	 *            对象
	 * @return 保存的对象
	 * @throws Exception
	 */
	private final IBusinessObjectBase mySaveEx(IBusinessObjectBase bo) throws Exception {
		if (bo == null) {
			throw new RepositoryException(I18N.prop("msg_bobas_invalid_bo"));
		}
		if (bo.isDirty()) {
			// 仅修过的数据进行处理
			boolean myOpenedDb = false;// 自己打开的数据库
			boolean myTrans = false;// 自己打开的事务
			try {
				// 开始保存数据
				myOpenedDb = this.openDbConnection();
				myTrans = this.beginTransaction();
				// 保存主对象
				this.mySave(bo);
				// 保存子项
				if (bo instanceof IManageFields) {
					IManageFields boFields = (IManageFields) bo;
					for (IFieldData fieldData : boFields.getFields()) {
						if (!fieldData.isSavable()) {
							// 不保存字段，继续下一个
							continue;
						}
						Object fdValue = fieldData.getValue();
						if (fdValue == null) {
							// 空值，继续下一个
							continue;
						}
						if (fdValue instanceof IBusinessObjectListBase<?>) {
							// 对象列表
							IBusinessObjectListBase<?> childs = (IBusinessObjectListBase<?>) fdValue;
							// 每个子项保存时，自主获取主键
							for (IBusinessObjectBase childBO : childs) {
								this.mySaveEx(childBO);
							}
						} else if (fdValue instanceof IBusinessObjectBase) {
							// 对象属性
							this.mySaveEx((IBusinessObjectBase) fdValue);// 继续带子项的保存
						}
						/*
						 * 不处理数组了
						 * 
						 * else if (fdValue.getClass().isArray()) { // 对象数组 int
						 * length = Array.getLength(fdValue); for (int i = 0; i
						 * < length; i++) { Object child = Array.get(fdValue,
						 * i); if (child instanceof IBusinessObjectBase) {
						 * IBusinessObjectBase childBO = (IBusinessObjectBase)
						 * child; this.mySaveEx(childBO, true, root);// 继续带子项的保存
						 * } } }
						 */
					}
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
			} finally {
				if (myOpenedDb) {
					// 自己开打自己关闭
					this.closeDbConnection();// 关闭数据库连接
				}
			}
		}
		return bo;
	}

}
