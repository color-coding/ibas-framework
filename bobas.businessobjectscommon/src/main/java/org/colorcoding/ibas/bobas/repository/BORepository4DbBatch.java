package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.IBOKeysManager;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.ISqlQuery;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.IBusinessObjectsBase;
import org.colorcoding.ibas.bobas.core.ITrackStatusOperator;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.core.fields.IManagedFields;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.db.DbException;
import org.colorcoding.ibas.bobas.db.IBOAdapter;
import org.colorcoding.ibas.bobas.db.IDbCommand;
import org.colorcoding.ibas.bobas.db.IDbDataReader;
import org.colorcoding.ibas.bobas.db.ParsingException;
import org.colorcoding.ibas.bobas.i18n.I18N;

/**
 * 基本数据库仓库-读写(批量)
 * 
 * 对象保存后调用BOTransactionNotification，可在此存储过程添加逻辑，此存储过程在某些数据库下不能有返回值。
 */
public class BORepository4DbBatch extends BORepository4Db implements IBORepository4DbBatch {

	public BORepository4DbBatch() {
		// 是否通知事务
		this.setPostTransaction(
				!MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_BO_DISABLED_POST_TRANSACTION, false));
	}

	public BORepository4DbBatch(String sign) {
		super(sign);
		// 是否通知事务
		this.setPostTransaction(
				!MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_BO_DISABLED_POST_TRANSACTION, false));
	}

	private boolean postTransaction;

	/**
	 * 是否通知事务
	 * 
	 * @return
	 */
	public final boolean isPostTransaction() {
		return postTransaction;
	}

	public final void setPostTransaction(boolean value) {
		this.postTransaction = value;
	}

	@Override
	public <T extends IBusinessObjectBase> IOperationResult<T> save(T[] bos) {
		OperationResult<T> operationResult = new OperationResult<>();
		try {
			IBusinessObjectBase[] nBOs = this.mySave(bos, false);
			for (IBusinessObjectBase nBO : nBOs) {
				if (nBO instanceof ITrackStatusOperator) {
					// 保存成功，标记对象为OLD
					ITrackStatusOperator operator = (ITrackStatusOperator) nBO;
					operator.markOld();
				}
			}
			operationResult.addResultObjects(nBOs);
		} catch (Exception e) {
			operationResult.setError(e);
		}
		return operationResult;
	}

	@Override
	public <T extends IBusinessObjectBase> IOperationResult<T> saveEx(T[] bos) {
		OperationResult<T> operationResult = new OperationResult<>();
		try {
			IBusinessObjectBase[] nBOs = this.mySave(bos, true);
			for (IBusinessObjectBase nBO : nBOs) {
				if (nBO instanceof ITrackStatusOperator) {
					// 保存成功，标记对象为OLD
					ITrackStatusOperator operator = (ITrackStatusOperator) nBO;
					operator.markOld(true);
				}
			}
			operationResult.addResultObjects(nBOs);
		} catch (Exception e) {
			operationResult.setError(e);
		}
		return operationResult;
	}

	/**
	 * 解析对象保存语句
	 * 
	 * @param bo        对象实例
	 * @param recursion 包含子项
	 * @return 对象保存语句数组
	 * @throws DbException
	 * @throws ParsingException
	 */
	private ISqlQuery[] parseSaveQueries(IBusinessObjectBase bo, boolean recursion)
			throws DbException, ParsingException {
		IBOAdapter adapter = this.getBOAdapter();
		ArrayList<ISqlQuery> sqlQueries = new ArrayList<>();
		// 不是更新状态，不做处理
		if (!bo.isDirty() || !bo.isSavable())
			return sqlQueries.toArray(new ISqlQuery[] {});
		// 存储标记
		this.tagStorage(bo);
		if (bo.isNew()) {
			// 新建的对象
			// 设置主键
			sqlQueries.add(adapter.parseInsertScript(bo));
		} else if (bo.isDeleted()) {
			// 删除对象
			sqlQueries.add(adapter.parseDeleteScript(bo));
		} else {
			// 修改对象，先删除数据，再添加新的实例
			sqlQueries.add(adapter.parseDeleteScript(bo));
			sqlQueries.add(adapter.parseInsertScript(bo));
		}
		if (recursion) {
			// 处理子项
			if (bo instanceof IManagedFields) {
				IManagedFields boFields = (IManagedFields) bo;
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
					if (fdValue instanceof IBusinessObjectBase) {
						// 解析BO保存语句，并添加到批量命令
						sqlQueries.addAll(this.parseSaveQueries((IBusinessObjectBase) fdValue, true));
					} else if (fdValue instanceof IBusinessObjectsBase<?>) {
						// 对象列表
						IBusinessObjectsBase<?> childs = (IBusinessObjectsBase<?>) fdValue;
						for (IBusinessObjectBase child : childs) {
							// 解析BO保存语句，并添加到批量命令
							sqlQueries.addAll(this.parseSaveQueries((IBusinessObjectBase) child, true));
						}
					}
				}
			}
		}
		return sqlQueries.toArray(new ISqlQuery[] {});
	}

	/**
	 * 保存对象
	 * 
	 * @param bo        对象
	 * @param recursion 包含子项
	 * @return 保存的对象
	 * @throws Exception
	 */
	private final IBusinessObjectBase[] mySave(IBusinessObjectBase[] bos, boolean recursion) throws Exception {
		if (bos == null || bos.length <= 0) {
			throw new RepositoryException(I18N.prop("msg_bobas_invalid_bo"));
		}
		IDbDataReader reader = null;
		IDbCommand command = null;
		boolean myOpenedDb = false;// 自己打开的数据库
		boolean myTrans = false;// 自己打开的事务
		IBusinessObjectBase[] savedBOs = new IBusinessObjectBase[bos.length];
		try {
			myOpenedDb = this.openDbConnection();
			myTrans = this.beginTransaction();
			IBOAdapter adapter = this.getBOAdapter();
			IBOKeysManager keysManager = this.createKeysManager();
			keysManager.usePrimaryKeys(bos);// 获取并更新主键
			keysManager.useSeriesKey(bos);// 获取并更新系列号
			command = this.getDbConnection().createCommand();
			for (int i = 0; i < bos.length; i++) {
				IBusinessObjectBase bo = bos[i];
				if (bo == null)
					continue;
				if (!bo.isDirty() || !bo.isSavable())
					continue;
				// 解析BO保存语句，并添加到批量命令
				for (ISqlQuery sqlQuery : this.parseSaveQueries(bo, recursion)) {
					command.addBatch(sqlQuery);
				}
				// 通知事务
				if (this.isPostTransaction()) {
					if (bo.isNew()) {
						// 新建对象
						command.addBatch(adapter.parseTransactionNotification(TransactionType.ADD, bo));
					} else if (bo.isDeleted()) {
						// 删除对象
						command.addBatch(adapter.parseTransactionNotification(TransactionType.DELETE, bo));
					} else {
						// 更新对象
						command.addBatch(adapter.parseTransactionNotification(TransactionType.UPDATE, bo));
					}
				}
				savedBOs[i] = bo;
			}
			command.executeBatch();
			command.clearBatch();
			if (myTrans)
				this.commitTransaction();// 自己打开的事务，关闭事务
		} catch (Exception e) {
			if (myTrans)
				this.rollbackTransaction();// 自己打开的事务，关闭事务
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
			if (myOpenedDb)
				this.closeDbConnection();// 自己开打自己关闭，关闭数据库连接
		}
		return savedBOs;
	}

}
