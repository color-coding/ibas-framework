package org.colorcoding.ibas.bobas.repository;

import org.colorcoding.ibas.bobas.bo.IBOLine;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.ISqlQuery;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.IBusinessObjectListBase;
import org.colorcoding.ibas.bobas.core.ITrackStatusOperator;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.core.SaveActionsException;
import org.colorcoding.ibas.bobas.core.SaveActionsListener;
import org.colorcoding.ibas.bobas.core.SaveActionsSupport;
import org.colorcoding.ibas.bobas.core.SaveActionsType;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.core.fields.IManageFields;
import org.colorcoding.ibas.bobas.data.KeyValue;
import org.colorcoding.ibas.bobas.db.DbException;
import org.colorcoding.ibas.bobas.db.IBOAdapter4Db;
import org.colorcoding.ibas.bobas.db.IDbCommand;
import org.colorcoding.ibas.bobas.db.IDbDataReader;
import org.colorcoding.ibas.bobas.i18n.i18n;

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
    private void fireSaveActions(SaveActionsType type, IBusinessObjectBase bo, IBusinessObjectBase root)
            throws SaveActionsException {
        if (this.saveActionsSupport == null) {
            return;
        }
        this.saveActionsSupport.fireActions(type, bo, root);
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
        OperationResult<T> operationResult = new OperationResult<>();
        try {
            IBusinessObjectBase nBO = this.mySave(bo, true, null);
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
            IBusinessObjectBase nBO = this.mySaveEx(bo, true, null);
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

    /**
     * 保存对象，不包括子属性
     * 
     * @param bo
     *            对象
     * @param updateKeys
     *            更新主键
     * @param root
     *            根对象
     * @return 保存的对象
     * @throws Exception
     */
    private final IBusinessObjectBase mySave(IBusinessObjectBase bo, boolean updateKeys, IBusinessObjectBase root)
            throws Exception {
        if (bo == null) {
            throw new RepositoryException(i18n.prop("msg_bobas_invalid_bo"));
        }
        if (bo.isDirty()) {
            // 仅修过的数据进行处理
            IBOAdapter4Db adapter4Db = this.createDbAdapter().createBOAdapter();
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
                    if (updateKeys)
                        adapter4Db.usePrimaryKeys(bo, command);// 获取并更新主键
                    this.fireSaveActions(SaveActionsType.before_adding, bo, root);
                    sqlQuery = adapter4Db.parseSqlInsert(bo);
                } else if (bo.isDeleted()) {
                    // 删除对象
                    this.fireSaveActions(SaveActionsType.before_deleting, bo, root);
                    sqlQuery = adapter4Db.parseSqlDelete(bo);
                } else {
                    // 修改对象，先删除数据，再添加新的实例
                    this.fireSaveActions(SaveActionsType.before_updating, bo, root);
                    sqlQuery = adapter4Db.parseSqlDelete(bo);
                    command.executeUpdate(sqlQuery);// 执行删除副本
                    sqlQuery = adapter4Db.parseSqlInsert(bo);
                }
                // 运行保存语句
                command.executeUpdate(sqlQuery);
                // 通知事务
                if (bo.isNew()) {
                    // 新建的对象
                    this.fireSaveActions(SaveActionsType.added, bo, root);
                } else if (bo.isDeleted()) {
                    // 删除对象
                    this.fireSaveActions(SaveActionsType.deleted, bo, root);
                } else {
                    // 修改对象
                    this.fireSaveActions(SaveActionsType.updated, bo, root);
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
                throw new RepositoryException(i18n.prop("msg_bobas_to_save_bo_faild", e.getMessage()), e);
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
     * @param updateKeys
     *            更新主键
     * @return 保存的对象
     * @throws Exception
     */
    private final IBusinessObjectBase mySaveEx(IBusinessObjectBase bo, boolean updateKeys, IBusinessObjectBase root)
            throws Exception {
        if (bo == null) {
            throw new RepositoryException(i18n.prop("msg_bobas_invalid_bo"));
        }
        if (root == null) {
            // 设置触发事件的跟对象
            root = bo;
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
                this.mySave(bo, updateKeys, root);
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
                            if (!childs.isSmartPrimaryKeys() && bo.isNew()) {
                                // 不启用智能主键且对象是新建的，需要手工维护集合子项主键
                                for (IBusinessObjectBase childBO : childs) {
                                    this.mySaveEx(childBO, false, root);
                                }
                                continue;
                            } else {
                                if (childs.isSmartPrimaryKeys()) {
                                    // 智能处理子项主键
                                    IBOLine firstLine = (IBOLine) childs
                                            .firstOrDefault(item -> item.isNew() && item instanceof IBOLine);
                                    // 获取到第一个新建的子项
                                    if (firstLine != null) {
                                        KeyValue[] keys = this.createDbAdapter().createBOAdapter()
                                                .parsePrimaryKeys(firstLine, this.getDbConnection().createCommand());
                                        KeyValue lineKey = null;// LineId的值
                                        for (KeyValue keyValue : keys) {
                                            if (IBOLine.SECONDARY_PRIMARY_KEY_NAME.equals(keyValue.key)) {
                                                lineKey = keyValue;
                                                break;
                                            }
                                        }
                                        if (lineKey != null && lineKey.value instanceof Integer) {
                                            // 获取到了LineId的值
                                            int lineValue = (int) lineKey.value;
                                            for (IBusinessObjectBase childBO : childs) {
                                                if (childBO instanceof IBOLine) {
                                                    // IBOLine的处理主键
                                                    IBOLine line = (IBOLine) childBO;
                                                    if (line.isNew()) {
                                                        line.setLineId(lineValue);
                                                        lineValue++;
                                                    }
                                                    this.mySaveEx(childBO, false, root);
                                                } else {
                                                    // 其他对象类型
                                                    this.mySaveEx(childBO, true, root);
                                                }
                                            }
                                            continue;// 完成处理，退出操作
                                        }
                                    }
                                }
                            }
                            // 每个子项保存时，自主获取主键
                            for (IBusinessObjectBase childBO : childs) {
                                this.mySaveEx(childBO, true, root);
                            }
                        } else if (fdValue instanceof IBusinessObjectBase) {
                            // 对象属性
                            this.mySaveEx((IBusinessObjectBase) fdValue, true, root);// 继续带子项的保存
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
