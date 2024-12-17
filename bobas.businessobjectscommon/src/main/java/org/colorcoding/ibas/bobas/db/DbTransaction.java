package org.colorcoding.ibas.bobas.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

import org.colorcoding.ibas.bobas.bo.BOFactory;
import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.bo.BusinessObject;
import org.colorcoding.ibas.bobas.bo.BusinessObjectUnit;
import org.colorcoding.ibas.bobas.bo.BusinessObjects;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.bo.IBusinessObjects;
import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.Enums;
import org.colorcoding.ibas.bobas.common.IChildCriteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.Result;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.IArrayList;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.repository.RepositoryException;
import org.colorcoding.ibas.bobas.repository.Transaction;

public class DbTransaction extends Transaction {

	public DbTransaction(Connection connection) {
		Objects.requireNonNull(connection);
		this.connection = connection;
	}

	private volatile Connection connection;

	protected final synchronized Connection getConnection() throws SQLException {
		if (this.connection == null) {
			throw new SQLException(I18N.prop("msg_bobas_invaild_database_connection"));
		}
		return connection;
	}

	private DbAdapter adapter;

	protected final DbAdapter getAdapter() throws Exception {
		if (this.adapter == null) {
			this.adapter = DbFactory.create().createAdapter(this.connection.getClass().getName());
		}
		if (this.adapter == null) {
			throw new Exception("not found db adapter.");
		}
		return this.adapter;
	}

	public final synchronized boolean inTransaction() throws SQLException {
		return !this.getConnection().getAutoCommit();
	}

	public final synchronized boolean beginTransaction() throws SQLException {
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

	public final synchronized boolean isClosed() throws SQLException {
		if (this.getConnection().isClosed()) {
			return true;
		}
		return false;
	}

	@Override
	public final synchronized void close() throws Exception {
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
	public final synchronized void rollback() throws RepositoryException {
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
	public final synchronized void commit() throws RepositoryException {
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
	@SuppressWarnings({ "unchecked" })
	public final <T extends IBusinessObject> T[] fetch(ICriteria criteria, Class<?> boType) throws RepositoryException {
		try {
			Objects.requireNonNull(boType);
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
								BusinessObject<?> cData = null;
								for (T data : datas) {
									propertyValue = ((BusinessObject<?>) data).getProperty(propertyInfo);
									if (BOUtilities.isBusinessObject(propertyValue)) {
										cData = (BusinessObject<?>) propertyValue;
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
								BusinessObjects<IBusinessObject, ?> cDatas = null;
								for (T data : datas) {
									propertyValue = ((BusinessObject<?>) data).getProperty(propertyInfo);
									if (BOUtilities.isBusinessObjects(propertyValue)) {
										cDatas = (BusinessObjects<IBusinessObject, ?>) propertyValue;
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
	@SuppressWarnings({ "unchecked" })
	public final <T extends IBusinessObject> T[] save(IBusinessObject[] bos) throws RepositoryException {
		try {
			Objects.requireNonNull(bos);
			if (bos.length == 0) {
				return (T[]) bos;
			}
			boolean mine = this.beginTransaction();
			try {
				Object cData = null;
				Class<?> boType = null;
				BusinessObject<?> boData = null;
				IArrayList<IBusinessObject> boChilds = new ArrayList<>();
				IArrayList<BusinessObject<?>> boDatas = new ArrayList<>();
				Map<Class<?>, IArrayList<BusinessObject<?>>> boDeletes = new HashMap<>(bos.length, 1);
				Map<Class<?>, IArrayList<BusinessObject<?>>> boUpdates = new HashMap<>(bos.length, 1);
				Map<Class<?>, IArrayList<BusinessObject<?>>> boInserts = new HashMap<>(bos.length, 1);

				// 分析数据，形成待处理集合
				for (IBusinessObject bo : bos) {
					if (!BOUtilities.isBusinessObject(bo)) {
						continue;
					}
					if (!BOUtilities.isSavable(bo)) {
						continue;
					}
					boData = (BusinessObject<?>) bo;
					boType = boData.getClass();
					if (!boDeletes.containsKey(boType)) {
						boDeletes.put(boType, new ArrayList<>());
					}
					if (!boUpdates.containsKey(boType)) {
						boUpdates.put(boType, new ArrayList<>());
					}
					if (!boInserts.containsKey(boType)) {
						boInserts.put(boType, new ArrayList<>());
					}
					// 分析待处理方式（按类型分组）
					if (bo.isSavable() && bo.isDirty()) {
						if (bo.isDeleted()) {
							boDeletes.get(boType).add(boData);
						} else if (bo.isNew()) {
							boInserts.get(boType).add(boData);
						} else {
							boUpdates.get(boType).add(boData);
							boDeletes.get(boType).add(boData);
							boInserts.get(boType).add(boData);
						}
						// 业务主对象
						if (boType.getAnnotation(BusinessObjectUnit.class) != null) {
							boDatas.add(boData);
						}
					}
					// 分析待处理子项
					for (IPropertyInfo<?> propertyInfo : BOFactory.propertyInfos(boType)) {
						if (propertyInfo.getValueType().isAssignableFrom(BusinessObject.class)) {
							cData = boData.getProperty(propertyInfo);
							if (cData instanceof IBusinessObject) {
								boChilds.add((IBusinessObject) cData);
							}
						} else if (propertyInfo.getValueType().isAssignableFrom(BusinessObjects.class)) {
							cData = boData.getProperty(propertyInfo);
							if (cData instanceof IBusinessObjects) {
								boChilds.addAll((IBusinessObjects<?, ?>) cData);
							}
						}
					}
				}
				cData = null;
				boType = null;
				boData = null;
				// 分配执行语句的方法
				int batchCount = this.getAdapter().getBatchCount();
				BiFunction<String, IArrayList<BusinessObject<?>>, Exception> sqlExecuter = new BiFunction<String, IArrayList<BusinessObject<?>>, Exception>() {
					@Override
					public Exception apply(String sql, IArrayList<BusinessObject<?>> datas) {
						try (PreparedStatement statement = DbTransaction.this.connection.prepareStatement(sql)) {
							int count = 0;
							int index = 0;
							DbField dbField;
							BusinessObject<?> data;
							IArrayList<IPropertyInfo<?>> propertyInfos;
							// 语句存在where，则主键参数最后
							boolean hasWhere = Strings.indexOf(sql, DbTransaction.this.getAdapter().where()) >= 0 ? true
									: false;
							for (int i = 0; i < datas.size(); i++) {
								index = 0;
								data = datas.get(i);
								propertyInfos = data.properties();

								if (hasWhere == true) {
									for (IPropertyInfo<?> propertyInfo : propertyInfos.where(c -> !c.isPrimaryKey())) {
										dbField = propertyInfo.getAnnotation(DbField.class);
										if (dbField == null || Strings.isNullOrEmpty(dbField.name())) {
											continue;
										}
										statement.setObject(index, data.getProperty(propertyInfo),
												DbTransaction.this.getAdapter().sqlTypeOf(dbField.type()));
										index += 1;
									}
									for (IPropertyInfo<?> propertyInfo : propertyInfos.where(c -> c.isPrimaryKey())) {
										dbField = propertyInfo.getAnnotation(DbField.class);
										if (dbField == null || Strings.isNullOrEmpty(dbField.name())) {
											continue;
										}
										statement.setObject(index, data.getProperty(propertyInfo),
												DbTransaction.this.getAdapter().sqlTypeOf(dbField.type()));
										index += 1;
									}
								} else {
									for (IPropertyInfo<?> propertyInfo : propertyInfos) {
										dbField = propertyInfo.getAnnotation(DbField.class);
										if (dbField == null || Strings.isNullOrEmpty(dbField.name())) {
											continue;
										}
										statement.setObject(index, data.getProperty(propertyInfo),
												DbTransaction.this.getAdapter().sqlTypeOf(dbField.type()));
										index += 1;
									}
								}
								statement.addBatch();
								count += 1;
								if (i >= batchCount) {
									statement.executeBatch();
									count = 0;
								}
							}
							if (count > 0) {
								statement.executeBatch();
							}
							dbField = null;
							data = null;
							propertyInfos = null;
						} catch (Exception e) {
							return e;
						}
						return null;
					}
				};
				// 处理删除内容
				Exception result;
				for (IArrayList<BusinessObject<?>> datas : boDeletes.values()) {
					if (datas.size() > 0) {
						result = sqlExecuter.apply(this.getAdapter().parsingDelete(datas.get(0)), datas);
						if (result instanceof Exception) {
							throw result;
						}
					}
				}
				// 处理更新内容
				for (IArrayList<BusinessObject<?>> datas : boUpdates.values()) {
					if (datas.size() > 0) {
						result = sqlExecuter.apply(this.getAdapter().parsingUpdate(datas.get(0)), datas);
						if (result instanceof Exception) {
							throw result;
						}
					}
				}
				// 处理新建内容
				for (IArrayList<BusinessObject<?>> datas : boInserts.values()) {
					if (datas.size() > 0) {
						result = sqlExecuter.apply(this.getAdapter().parsingInsert(datas.get(0)), datas);
						if (result instanceof Exception) {
							throw result;
						}
					}
				}
				// 处理事务存储过程
				sqlExecuter = new BiFunction<String, IArrayList<BusinessObject<?>>, Exception>() {
					@Override
					public Exception apply(String sql, IArrayList<BusinessObject<?>> datas) {
						int keyCount;
						DbField dbField;
						BusinessObject<?> data;
						StringBuilder fieldsBuilder;
						StringBuilder valuesBuilder;
						TransactionType transactionType;
						try (PreparedStatement statement = DbTransaction.this.connection.prepareStatement(sql)) {
							for (int i = 0; i < datas.size(); i++) {
								data = (BusinessObject<?>) datas.get(i);
								if (data.isDeleted()) {
									transactionType = TransactionType.DELETE;
								} else if (data.isNew()) {
									transactionType = TransactionType.ADD;
								} else {
									transactionType = TransactionType.UPDATE;
								}
								keyCount = 0;
								fieldsBuilder = new StringBuilder();
								valuesBuilder = new StringBuilder();
								for (IPropertyInfo<?> propertyInfo : data.properties().where(c -> c.isPrimaryKey())) {
									dbField = propertyInfo.getAnnotation(DbField.class);
									if (dbField == null || Strings.isNullOrEmpty(dbField.name())) {
										continue;
									}
									keyCount += 1;
									if (fieldsBuilder.length() > 0) {
										fieldsBuilder.append(DbTransaction.this.getAdapter().separation());
									}
									fieldsBuilder.append(dbField.name());
									if (valuesBuilder.length() > 0) {
										valuesBuilder.append(DbTransaction.this.getAdapter().separation());
									}
									valuesBuilder.append(Strings.valueOf(data.getProperty(propertyInfo)));
								}
								// 对象编码
								statement.setString(0, BOFactory.codeOf(data.getClass()));
								// 事务类型
								statement.setString(1, Enums.annotationValue(transactionType));
								// 主键个数
								statement.setInt(2, keyCount);
								// 主键名称
								statement.setString(3, fieldsBuilder.toString());
								// 主键值
								statement.setString(4, valuesBuilder.toString());
								// 操作用户
								statement.setInt(5, -1);

								// 运行语句，非0则抛异常
								try (ResultSet resultSet = statement.executeQuery()) {
									IArrayList<Result> results = DbTransaction.this.getAdapter()
											.parsingDatas(Result.class, resultSet);
									for (Result result : results) {
										if (result.getResultCode() != 0) {
											throw new TransactionException(Strings.format("%s - %s",
													result.getResultCode(), result.getMessage()));
										}
									}
								}
							}
							dbField = null;
							data = null;
							fieldsBuilder = null;
							valuesBuilder = null;
						} catch (Exception e) {
							return e;
						}
						return null;
					}
				};
				result = sqlExecuter.apply(this.getAdapter().sp_transaction_notification(), boDatas);
				if (result instanceof Exception) {
					throw result;
				}
				cData = null;
				boType = null;
				boData = null;
				boDeletes = null;
				boUpdates = null;
				boInserts = null;
				boDatas = null;
				sqlExecuter = null;
				// 处理子项
				if (!boChilds.isEmpty()) {
					this.save(boChilds.toArray(new IBusinessObject[] {}));
				}
				if (mine == true) {
					this.commit();
					mine = false;
				}
				// 标记状态为已保存
				for (IBusinessObject bo : bos) {
					if (!BOUtilities.isBusinessObject(bo)) {
						continue;
					}
					if (!BOUtilities.isSavable(bo)) {
						continue;
					}
					if (bo.isDirty() == false) {
						continue;
					}
					((BusinessObject<?>) bo).markOld();
				}
				return (T[]) bos;
			} catch (Exception e) {
				if (mine == true) {
					this.rollback();
					mine = false;
				}
				throw e;
			}
		} catch (Exception e) {
			throw new RepositoryException(e);
		}
	}

}
