package org.colorcoding.ibas.bobas.repository;

import java.lang.reflect.Array;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.ISqlQuery;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.IBusinessObjectListBase;
import org.colorcoding.ibas.bobas.core.ITrackStatusOperator;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.core.fields.IManageFields;
import org.colorcoding.ibas.bobas.data.KeyValue;
import org.colorcoding.ibas.bobas.db.BOParseException;
import org.colorcoding.ibas.bobas.db.DbException;
import org.colorcoding.ibas.bobas.db.IBOAdapter4Db;
import org.colorcoding.ibas.bobas.db.IDbCommand;
import org.colorcoding.ibas.bobas.db.IDbDataReader;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.util.ArrayList;

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
     * @param bo
     *            对象实例
     * @param recursion
     *            包含子项
     * @return 对象保存语句数组
     * @throws DbException
     * @throws BOParseException
     */
    private ISqlQuery[] parseSaveQueries(IBusinessObjectBase bo, boolean recursion)
            throws DbException, BOParseException {
        IBOAdapter4Db adapter4Db = this.createDbAdapter().createBOAdapter();
        ArrayList<ISqlQuery> sqlQueries = new ArrayList<>();
        // 不是更新状态，不做处理
        if (!bo.isDirty())
            return sqlQueries.toArray(new ISqlQuery[] {});
        // 存储标记
        this.tagStorage(bo);
        if (bo.isNew()) {
            // 新建的对象
            // 设置主键
            sqlQueries.add(adapter4Db.parseSqlInsert(bo));
        } else if (bo.isDeleted()) {
            // 删除对象
            sqlQueries.add(adapter4Db.parseSqlDelete(bo));
        } else {
            // 修改对象，先删除数据，再添加新的实例
            sqlQueries.add(adapter4Db.parseSqlDelete(bo));
            sqlQueries.add(adapter4Db.parseSqlInsert(bo));
        }
        if (recursion) {
            // 处理子项
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
                    if (fdValue instanceof IBusinessObjectBase) {
                        // 解析BO保存语句，并添加到批量命令
                        sqlQueries.addAll(this.parseSaveQueries((IBusinessObjectBase) fdValue, true));
                    } else if (fdValue.getClass().isArray()) {
                        // 对象数组
                        int length = Array.getLength(fdValue);
                        for (int i = 0; i < length; i++) {
                            Object child = Array.get(fdValue, i);
                            if (child instanceof IBusinessObjectBase) {
                                // 解析BO保存语句，并添加到批量命令
                                sqlQueries.addAll(this.parseSaveQueries((IBusinessObjectBase) child, true));
                            }
                        }
                    } else if (fdValue instanceof IBusinessObjectListBase<?>) {
                        // 对象列表
                        IBusinessObjectListBase<?> childs = (IBusinessObjectListBase<?>) fdValue;
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
     * @param bo
     *            对象
     * @param recursion
     *            包含子项
     * @return 保存的对象
     * @throws Exception
     */
    private final IBusinessObjectBase[] mySave(IBusinessObjectBase[] bos, boolean recursion) throws Exception {
        if (bos == null || bos.length <= 0) {
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
            KeyValue[] keys = null;// 主键信息
            int keyUsedCount = 0;// 主键使用的个数
            for (IBusinessObjectBase bo : bos) {
                if (bo == null)
                    continue;
                if (!bo.isDirty())
                    continue;
                if (keys == null) {
                    // 初始化主键
                    keys = adapter4Db.parsePrimaryKeys(bo, command);
                }
                if (bo.isNew()) {
                    // 新建的对象
                    // 设置主键
                    adapter4Db.setPrimaryKeys(bo, keys);
                    // 主键值增加
                    for (KeyValue key : keys) {
                        if (key.value instanceof Integer) {
                            key.value = Integer.sum((int) key.value, 1);
                        } else if (key.value instanceof Long) {
                            key.value = Long.sum((long) key.value, 1);
                        }
                    }
                    keyUsedCount++;// 使用了主键
                }
                // 解析BO保存语句，并添加到批量命令
                for (ISqlQuery sqlQuery : this.parseSaveQueries(bo, recursion)) {
                    command.addBatch(sqlQuery);
                }
                // 通知事务
                if (this.isPostTransaction()) {
                    if (bo.isNew()) {
                        // 新建对象
                        command.addBatch(adapter4Db.parseBOTransactionNotification(TransactionType.Add, bo));
                    } else if (bo.isDeleted()) {
                        // 删除对象
                        command.addBatch(adapter4Db.parseBOTransactionNotification(TransactionType.Delete, bo));
                    } else {
                        // 更新对象
                        command.addBatch(adapter4Db.parseBOTransactionNotification(TransactionType.Update, bo));
                    }
                }
                savedBOs.add(bo);
            }
            command.executeBatch();
            command.clearBatch();
            // 更新主键
            if (keyUsedCount > 0)
                adapter4Db.updatePrimaryKeyRecords(bos[0], keyUsedCount, command);
            if (myTrans)
                this.commitTransaction();// 自己打开的事务，关闭事务
        } catch (Exception e) {
            if (myTrans)
                this.rollbackTransaction();// 自己打开的事务，关闭事务
            throw e;
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
