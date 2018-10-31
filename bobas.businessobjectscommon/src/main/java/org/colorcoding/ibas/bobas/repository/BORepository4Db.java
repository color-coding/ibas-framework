package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.bo.IBOKeysManager;
import org.colorcoding.ibas.bobas.bo.IBOTagReferenced;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.ISqlQuery;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.IBusinessObjectsBase;
import org.colorcoding.ibas.bobas.core.ITrackStatusOperator;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.core.RepositorySaveEventType;
import org.colorcoding.ibas.bobas.core.RepositorySaveListener;
import org.colorcoding.ibas.bobas.core.RepositorySaveSupport;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.core.fields.IManagedFields;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.db.DbException;
import org.colorcoding.ibas.bobas.db.IBOAdapter;
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
			this.createKeysManager().usePrimaryKeys(bo);
		} else if (type == RepositorySaveEventType.BEFORE_UPDATING) {
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
		OperationResult<T> operationResult = new OperationResult<>();
		try {
			// 保存对象
			IBusinessObjectBase nBO = this.mySave(bo);
			if (nBO instanceof ITrackStatusOperator) {
				// 保存成功，标记对象为OLD
				ITrackStatusOperator operator = (ITrackStatusOperator) nBO;
				operator.markOld();
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
			// 保存对象
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

	protected IBOKeysManager createKeysManager() throws DbException {
		DbKeysManager keysManager = new DbKeysManager();
		keysManager.setDbConnection(this.getDbConnection());
		keysManager.setAdapter(this.getBOAdapter());
		return keysManager;
	}

	/**
	 * 保存对象，不包括子属性
	 * 
	 * @param bo   对象
	 * @param root 根对象
	 * @return 保存的对象
	 * @throws Exception
	 */
	private final IBusinessObjectBase mySave(IBusinessObjectBase bo) throws Exception {
		if (bo == null) {
			throw new RepositoryException(I18N.prop("msg_bobas_invalid_bo"));
		}
		if (!bo.isDirty() || !bo.isSavable()) {
			return bo;
		}
		// 仅修过的且可保存的数据进行处理
		IBOAdapter adapter = this.getBOAdapter();
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
			if (bo.isNew() && !bo.isDeleted()) {
				// 新建的对象
				this.fireRepositorySave(RepositorySaveEventType.BEFORE_ADDING, bo);
				sqlQuery = adapter.parseInsertScript(bo);
			} else if (bo.isDeleted()) {
				// 删除对象
				this.fireRepositorySave(RepositorySaveEventType.BEFORE_DELETING, bo);
				sqlQuery = adapter.parseDeleteScript(bo);
			} else {
				// 修改对象，先删除数据，再添加新的实例
				this.fireRepositorySave(RepositorySaveEventType.BEFORE_UPDATING, bo);
				sqlQuery = adapter.parseDeleteScript(bo);
				command.executeUpdate(sqlQuery);// 执行删除副本
				sqlQuery = adapter.parseInsertScript(bo);
			}
			// 运行保存语句
			command.executeUpdate(sqlQuery);
			// 通知事务
			if (bo.isNew()) {
				// 新建的对象
				this.fireRepositorySave(RepositorySaveEventType.ADDED, bo);
			} else if (bo.isDeleted()) {
				// 删除对象
				this.fireRepositorySave(RepositorySaveEventType.DELETED, bo);
			} else {
				// 修改对象
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
		return bo;
	}

	/**
	 * 保存对象，包括子属性
	 * 
	 * @param bo 对象
	 * @return 保存的对象
	 * @throws Exception
	 */
	private final IBusinessObjectBase mySaveEx(IBusinessObjectBase bo) throws Exception {
		if (bo == null) {
			throw new RepositoryException(I18N.prop("msg_bobas_invalid_bo"));
		}
		boolean myOpenedDb = false;// 自己打开的数据库
		boolean myTrans = false;// 自己打开的事务
		try {
			// 开始保存数据
			myOpenedDb = this.openDbConnection();
			myTrans = this.beginTransaction();
			// 保存主对象
			this.mySave(bo);
			// 保存子项
			if (bo instanceof IManagedFields) {
				IManagedFields boFields = (IManagedFields) bo;
				for (IFieldData fieldData : boFields.getFields()) {
					if (!fieldData.isSavable()) {
						// 不保存字段，继续下一个
						continue;
					}
					if (fieldData.getValue() instanceof IBusinessObjectsBase<?>) {
						// 对象列表，循环保存子项
						for (IBusinessObjectBase item : (IBusinessObjectsBase<?>) fieldData.getValue()) {
							this.mySaveEx(item);
						}
					} else if (fieldData.getValue() instanceof IBusinessObjectBase) {
						// 对象属性，继续带子项的保存
						this.mySaveEx((IBusinessObjectBase) fieldData.getValue());
					}
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
		return bo;
	}

}