package org.colorcoding.ibas.bobas.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import org.colorcoding.ibas.bobas.bo.BOFactory;
import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.bo.BusinessObject;
import org.colorcoding.ibas.bobas.bo.BusinessObjects;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.IChildCriteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.FieldedObject;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.IArrayList;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.logic.BusinessLogicChain;
import org.colorcoding.ibas.bobas.repository.RepositoryException;
import org.colorcoding.ibas.bobas.repository.Transaction;

public class DbTransaction extends Transaction {

	public DbTransaction(Connection connection) {
		Objects.requireNonNull(connection);
		this.connection = connection;
	}

	private Connection connection;

	protected synchronized final Connection getConnection() throws SQLException {
		if (this.connection == null) {
			throw new SQLException(I18N.prop("msg_bobas_invaild_database_connection"));
		}
		return connection;
	}

	private DbAdapter adapter;

	protected DbAdapter getAdapter() throws Exception {
		if (this.adapter == null) {
			this.adapter = DbFactory.create().createAdapter(this.connection.getClass().getName());
		}
		if (this.adapter == null) {
			throw new Exception("not found db adapter.");
		}
		return this.adapter;
	}

	public synchronized boolean inTransaction() throws SQLException {
		return !this.getConnection().getAutoCommit();
	}

	public synchronized boolean beginTransaction() throws SQLException {
		if (this.isClosed()) {
			throw new SQLException(I18N.prop("msg_bobas_database_connection_is_closed"));
		}
		if (this.inTransaction()) {
			return false;
		}
		// 手动提交事务
		this.getConnection().setAutoCommit(false);
		return true;
	}

	public synchronized boolean isClosed() throws SQLException {
		if (this.getConnection().isClosed()) {
			return true;
		}
		return false;
	}

	@Override
	public synchronized void close() throws Exception {
		try {
			if (this.inTransaction()) {
				// 处于事务中，不允许关闭数据库连接
				throw new SQLException(I18N.prop("msg_bobas_database_has_not_commit_transaction"));
			}
			if (this.isClosed() == false) {
				this.getConnection().close();
			}
			super.close();
		} catch (SQLException e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public synchronized void rollback() throws RepositoryException {
		try {
			if (this.isClosed()) {
				throw new SQLException(I18N.prop("msg_bobas_database_connection_is_closed"));
			}
			this.getConnection().rollback();
			this.getConnection().setAutoCommit(false);
		} catch (SQLException e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public synchronized void commit() throws RepositoryException {
		try {
			if (this.isClosed()) {
				throw new SQLException(I18N.prop("msg_bobas_database_connection_is_closed"));
			}
			this.getConnection().commit();
			this.getConnection().setAutoCommit(false);
		} catch (SQLException e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T extends IBusinessObject> T[] fetch(ICriteria criteria, Class<?> boType) throws RepositoryException {
		try {
			// 查询方法，不主动开启事务
			criteria = this.getAdapter().convert(criteria, boType);
			try (PreparedStatement statement = this.connection
					.prepareStatement(this.getAdapter().parsingSelect(boType, criteria))) {
				int index = 0;
				for (ICondition condition : criteria.getConditions()) {
					if (condition.getOperation() == ConditionOperation.IS_NULL
							|| condition.getOperation() == ConditionOperation.NOT_NULL) {
						// 空比较，不需要值
						continue;
					}
					if (!Strings.isNullOrEmpty(condition.getComparedAlias())) {
						// 字段间比较，不需要值
						continue;
					}
					// 填充语句参数值，注意：包含、开始等，类型应为字符，且需要补充%号
					if ((condition.getOperation() == ConditionOperation.START
							|| condition.getOperation() == ConditionOperation.END
							|| condition.getOperation() == ConditionOperation.CONTAIN
							|| condition.getOperation() == ConditionOperation.NOT_CONTAIN)) {
						statement.setObject(index, condition.getValue(),
								this.getAdapter().sqlTypeOf(DbFieldType.ALPHANUMERIC));
					} else {
						statement.setObject(index, condition.getValue(),
								this.getAdapter().sqlTypeOf(condition.getAliasDataType()));
					}
					index += 1;
				}
				// 运行查询
				try (ResultSet resultSet = statement.executeQuery()) {
					IArrayList<T> datas = this.getAdapter().parsingDatas(boType, resultSet);
					// 加载子对象
					if (!datas.isEmpty() && !criteria.isNoChilds()) {
						Object propertyValue = null;
						ICriteria cCriteria = null;
						for (IPropertyInfo<?> propertyInfo : BOFactory.propertyInfos(boType)) {
							if (propertyInfo.getValueType().isAssignableFrom(BusinessObject.class)) {
								BusinessObject cData = null;
								for (T data : datas) {
									propertyValue = ((FieldedObject) data).getProperty(propertyInfo);
									if (propertyValue instanceof BusinessObject) {
										cData = (BusinessObject) propertyValue;
										cCriteria = cData.getCriteria();
										if (cCriteria == null) {
											continue;
										}
										if (cCriteria.getConditions().isEmpty()) {
											continue;
										}
										for (IBusinessObject item : this.fetch(cCriteria, cData.getClass())) {
											cData.setProperty(propertyInfo, item);
										}
									}
								}
							} else if (propertyInfo.getValueType().isAssignableFrom(BusinessObjects.class)) {
								boolean onlyHasChilds = false;
								BusinessObjects cDatas = null;
								for (T data : datas) {
									propertyValue = ((FieldedObject) data).getProperty(propertyInfo);
									if (propertyValue instanceof BusinessObjects) {
										cDatas = (BusinessObjects) propertyValue;
										cCriteria = cDatas.getElementCriteria();
										if (cCriteria == null) {
											continue;
										}
										if (cCriteria.getConditions().isEmpty()) {
											continue;
										}
										// 复制指定的子项查询
										for (IChildCriteria item : criteria.getChildCriterias()) {
											if (propertyInfo.getName().equalsIgnoreCase(item.getPropertyPath())) {
												cCriteria.copyFrom(item);
												onlyHasChilds = item.isOnlyHasChilds();
											}
										}
										for (IBusinessObject item : this.fetch(cCriteria, cDatas.getElementType())) {
											cDatas.add(item);
										}
										// 要求子项必须有值
										if (onlyHasChilds && cDatas.isEmpty()) {
											data.delete();
										}
									}
								}
							}
						}
					}
					return (T[]) datas.where(c -> c.isDeleted() == false).toArray();
				}
			}
		} catch (Exception e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public <T extends IBusinessObject> T save(T bo) throws RepositoryException {
		Objects.requireNonNull(bo);

		T boCopy = null;
		if (bo.isSavable() && !bo.isNew()) {
			// 更新数据，先查数据库副本
			T[] datas = this.fetch(bo.getCriteria(), bo.getClass());
			if (datas != null && datas.length > 0) {
				boCopy = datas[0];
			}
			if (boCopy == null) {
				throw new RepositoryException(Strings.format("not found %s in database.", bo.toString()));
			}
			if (BOUtilities.isNewer(boCopy, bo)) {
				throw new RepositoryException(Strings.format("%s db copy is more newer.", bo.toString()));
			}
		}
		BusinessLogicChain logicChain = new BusinessLogicChain();
		logicChain.setTrigger(bo);
		logicChain.setTriggerCopy(boCopy);
		logicChain.setTransaction(this);

		return bo;
	}

}
