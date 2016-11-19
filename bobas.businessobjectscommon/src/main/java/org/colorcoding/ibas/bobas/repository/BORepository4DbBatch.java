package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.ITrackStatusOperator;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.data.KeyValue;
import org.colorcoding.ibas.bobas.db.IBOAdapter4Db;
import org.colorcoding.ibas.bobas.db.IDbCommand;
import org.colorcoding.ibas.bobas.db.IDbDataReader;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.util.ArrayList;

/**
 * 基本数据库仓库-读写(批量)
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
	public boolean isPostTransaction() {
		return postTransaction;
	}

	public void setPostTransaction(boolean value) {
		this.postTransaction = value;
	}

	@Override
	public IOperationResult<?> save(IBusinessObjectBase[] bos) {
		OperationResult<?> operationResult = new OperationResult<Object>();
		try {
			IBusinessObjectBase[] nBOs = this.mySave(bos);
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

	@Override
	public IOperationResult<?> saveEx(IBusinessObjectBase[] bos) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 保存对象，不包括子属性
	 * 
	 * @param bo
	 *            对象
	 * @param updateKeys
	 *            更新主键
	 * @return 保存的对象
	 * @throws Exception
	 */
	private final IBusinessObjectBase[] mySave(IBusinessObjectBase[] bos) throws Exception {
		if (bos == null && bos.length <= 0) {
			throw new RepositoryException(i18n.prop("msg_bobas_invalid_bo"));
		}
		IDbDataReader reader = null;
		IDbCommand command = null;
		boolean myOpenedDb = false;// 自己打开的数据库
		boolean myTrans = false;// 自己打开的事务
		IBOAdapter4Db adapter4Db = this.createDbAdapter().createBOAdapter();
		ArrayList<IBusinessObjectBase> savedBOs = new ArrayList<>();
		try {
			myOpenedDb = this.openDbConnection();
			myTrans = this.beginTransaction();
			command = this.getDbConnection().createCommand();
			// 主键处理
			KeyValue[] keys = adapter4Db.parsePrimaryKeys(bos[0], command);
			int keyUsedCount = 0;// 主键使用的个数
			for (IBusinessObjectBase bo : bos) {
				if (bo == null)
					continue;
				if (!bo.isDirty())
					continue;
				this.tagStorage(bo);// 存储标记
				if (bo.isNew()) {
					// 新建的对象
					// 设置主键
					adapter4Db.setPrimaryKeys(bo, keys);
					// 主键值增加
					for (KeyValue key : keys) {
						if (key.value instanceof Integer) {
							key.value = Integer.sum((int) key.value, 1);
						} else if (key.value instanceof Integer) {
							key.value = Long.sum((long) key.value, 1);
						}
					}
					keyUsedCount++;// 使用了主键
					command.addBatch(adapter4Db.parseSqlInsert(bo));
					if (this.isPostTransaction())
						command.addBatch(adapter4Db.parseBOTransactionNotification(TransactionType.Add, bo));
				} else if (bo.isDeleted()) {
					// 删除对象
					command.addBatch(adapter4Db.parseSqlDelete(bo));
					if (this.isPostTransaction())
						command.addBatch(adapter4Db.parseBOTransactionNotification(TransactionType.Update, bo));
				} else {
					// 修改对象，先删除数据，再添加新的实例
					command.addBatch(adapter4Db.parseSqlDelete(bo));
					command.addBatch(adapter4Db.parseSqlInsert(bo));
					if (this.isPostTransaction())
						command.addBatch(adapter4Db.parseBOTransactionNotification(TransactionType.Delete, bo));
				}
				savedBOs.add(bo);
			}
			command.executeBatch();
			command.clearBatch();
			// 更新主键
			adapter4Db.updatePrimaryKeyRecords(bos[0], keyUsedCount, command);
			if (myTrans)
				this.commitTransaction();// 自己打开的事务，关闭事务
		} catch (Exception e) {
			if (myTrans)
				this.rollbackTransaction();// 自己打开的事务，关闭事务
		} finally {
			if (reader != null)
				reader.close();
			if (command != null)
				command.close();
			reader = null;
			command = null;
			if (myOpenedDb)
				this.closeDbConnection();// 自己开打自己关闭，关闭数据库连接
		}
		return savedBOs.toArray(new IBusinessObjectBase[] {});
	}
}
