package org.colorcoding.ibas.bobas.db;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.BOFactory;
import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.bo.BusinessObject;
import org.colorcoding.ibas.bobas.bo.BusinessObjectUnit;
import org.colorcoding.ibas.bobas.bo.BusinessObjects;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.bo.IBusinessObjects;
import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Enums;
import org.colorcoding.ibas.bobas.common.IChildCriteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.Numbers;
import org.colorcoding.ibas.bobas.common.Result;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.FieldedObject;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.IDataTable;
import org.colorcoding.ibas.bobas.data.List;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.logging.Logger;
import org.colorcoding.ibas.bobas.organization.IUser;
import org.colorcoding.ibas.bobas.repository.IUserGeter;
import org.colorcoding.ibas.bobas.repository.RepositoryException;
import org.colorcoding.ibas.bobas.repository.Transaction;

public abstract class DbTransaction extends Transaction implements IUserGeter {

	public DbTransaction(Connection connection) {
		Objects.requireNonNull(connection);
		this.connection = connection;
		this.replacementUpdate = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_DB_REPLACEMENT_UPDATE,
				true);
	}

	private boolean replacementUpdate;

	public final boolean isReplacementUpdate() {
		return replacementUpdate;
	}

	public final void setReplacementUpdate(boolean replacementUpdate) {
		this.replacementUpdate = replacementUpdate;
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

	public final synchronized boolean inTransaction() throws RepositoryException {
		try {
			return !this.getConnection().getAutoCommit();
		} catch (SQLException e) {
			throw new RepositoryException(e);
		}
	}

	public final synchronized boolean beginTransaction() throws RepositoryException {
		try {
			if (this.isClosed()) {
				throw new SQLException(I18N.prop("msg_bobas_database_connection_is_closed"));
			}
			if (this.inTransaction()) {
				return false;
			}
			// 手动提交事务
			this.getConnection().setAutoCommit(false);
			return true;
		} catch (SQLException e) {
			throw new RepositoryException(e);
		}
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
			if (!this.getConnection().getAutoCommit()) {
				this.getConnection().rollback();
				this.getConnection().setAutoCommit(true);
			}
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
			if (!this.getConnection().getAutoCommit()) {
				this.getConnection().commit();
				this.getConnection().setAutoCommit(true);
			}
		} catch (SQLException e) {
			throw new RepositoryException(e);
		}
	}

	/**
	 * 为语句填充参数
	 * 
	 * @param statement  语句
	 * @param conditions 条件参数
	 * @param index      参数开始索引
	 * @throws SQLException
	 * @throws Exception
	 */
	protected void fillingParameters(PreparedStatement statement, Iterable<ICondition> conditions, int index)
			throws SQLException, Exception {
		for (ICondition condition : conditions) {
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
				statement.setObject(index, condition.getValue(), this.getAdapter().sqlTypeOf(DbFieldType.ALPHANUMERIC));
			} else {
				statement.setObject(index, condition.getValue(),
						this.getAdapter().sqlTypeOf(condition.getAliasDataType()));
			}
			index += 1;
		}
	}

	@Override
	@SuppressWarnings({ "unchecked" })
	public final <T extends IBusinessObject> T[] fetch(Class<?> boType, ICriteria criteria) throws RepositoryException {
		try {
			Objects.requireNonNull(boType);
			// 查询方法，不主动开启事务
			criteria = this.getAdapter().convert(criteria, boType);
			String sql = this.getAdapter().parsingSelect(boType, criteria);
			if (MyConfiguration.isDebugMode()) {
				Logger.log(Strings.format("db sql: %s", sql));
			}
			try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
				// 填充参数
				this.fillingParameters(statement, criteria.getConditions(), 1);
				// 运行查询
				List<T> datas = null;
				try (ResultSet resultSet = statement.executeQuery();) {
					datas = this.getAdapter().parsingDatas(boType, resultSet);
				}
				// 加载子对象
				if (!datas.isEmpty() && !criteria.isNoChilds()) {
					Object propertyValue = null;
					ICriteria cCriteria = null;
					for (IPropertyInfo<?> propertyInfo : BOFactory.propertyInfos(boType)) {
						// 跳过值类型
						if (BOUtilities.isValueType(propertyInfo)) {
							continue;
						}
						if (IBusinessObject.class.isAssignableFrom(propertyInfo.getValueType())) {
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
									for (IBusinessObject item : this.fetch(cData.getClass(), cCriteria)) {
										cData.setProperty(propertyInfo, item);
									}
								}
							}
						} else if (IBusinessObjects.class.isAssignableFrom(propertyInfo.getValueType())) {
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
											// 设置不加载子项
											if (item.isNoChilds()) {
												cCriteria = null;
												break;
											}
										}
									}
									// 查询无效则跳过
									if (cCriteria == null || cCriteria.getConditions().isEmpty()) {
										continue;
									}
									for (IBusinessObject item : this.fetch(cDatas.getElementType(), cCriteria)) {
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
				return datas.where(c -> c.isDeleted() == false).toArray((T[]) Array.newInstance(boType, 0));
			}
		} catch (Exception e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public final <T extends IBusinessObject> T[] save(T[] bos) throws RepositoryException {
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
				List<IBusinessObject> boChilds = new ArrayList<>();
				List<BusinessObject<?>> boDatas = new ArrayList<>();
				Map<Class<?>, List<BusinessObject<?>>> boDeletes = new HashMap<>(bos.length, 1);
				Map<Class<?>, List<BusinessObject<?>>> boUpdates = new HashMap<>(bos.length, 1);
				Map<Class<?>, List<BusinessObject<?>>> boInserts = new HashMap<>(bos.length, 1);

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
							// 删除
							boDeletes.get(boType).add(boData);
						} else if (bo.isNew()) {
							// 新建
							boInserts.get(boType).add(boData);
						} else {
							// 更新
							if (this.isReplacementUpdate()) {
								// 替换更新：删除后新建
								boDeletes.get(boType).add(boData);
								boInserts.get(boType).add(boData);
							} else {
								// 更新修改项
								boUpdates.get(boType).add(boData);
							}
						}
						// 业务主对象
						if (boType.getAnnotation(BusinessObjectUnit.class) != null) {
							boDatas.add(boData);
						}
					}
					// 分析待处理子项
					for (IPropertyInfo<?> propertyInfo : BOFactory.propertyInfos(boType)) {
						// 跳过值类型
						if (BOUtilities.isValueType(propertyInfo)) {
							continue;
						}
						if (IBusinessObject.class.isAssignableFrom(propertyInfo.getValueType())) {
							cData = boData.getProperty(propertyInfo);
							if (cData instanceof IBusinessObject) {
								boChilds.add((IBusinessObject) cData);
							}
						} else if (IBusinessObjects.class.isAssignableFrom(propertyInfo.getValueType())) {
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
				BiFunction<TransactionType, List<BusinessObject<?>>, Exception> sqlExecuter = new BiFunction<TransactionType, List<BusinessObject<?>>, Exception>() {
					@Override
					public Exception apply(TransactionType type, List<BusinessObject<?>> datas) {
						String sql = null;
						BusinessObject<?> tpltData = datas.get(0);
						try {
							if (type == TransactionType.ADD) {
								sql = DbTransaction.this.getAdapter().parsingInsert(tpltData);
							} else if (type == TransactionType.DELETE) {
								sql = DbTransaction.this.getAdapter().parsingDelete(tpltData);
							} else if (type == TransactionType.UPDATE) {
								sql = DbTransaction.this.getAdapter().parsingUpdate(tpltData);
							}
						} catch (Exception e) {
							return e;
						}
						if (Strings.isNullOrEmpty(sql)) {
							return new SQLException("no sql statement.");
						}
						if (MyConfiguration.isDebugMode()) {
							Logger.log(Strings.format("db sql: %s", sql));
						}
						try (PreparedStatement statement = DbTransaction.this.connection.prepareStatement(sql)) {
							int count = 0;
							int index = 0;
							Object value;
							DbField dbField;
							BusinessObject<?> data;
							List<IPropertyInfo<?>> propertyInfos = tpltData.properties();
							for (int i = 0; i < datas.size(); i++) {
								index = 1;
								data = datas.get(i);
								propertyInfos = data.properties();

								if (type == TransactionType.DELETE) {
									// 设置主键条件值
									for (IPropertyInfo<?> propertyInfo : propertyInfos.where(c -> c.isPrimaryKey())) {
										dbField = propertyInfo.getAnnotation(DbField.class);
										if (dbField == null || Strings.isNullOrEmpty(dbField.name())) {
											continue;
										}
										value = data.getProperty(propertyInfo);
										if (propertyInfo.getValueType().isEnum()) {
											statement.setObject(index, Enums.annotationValue(value),
													DbTransaction.this.getAdapter().sqlTypeOf(dbField.type()));
										} else {
											statement.setObject(index, value,
													DbTransaction.this.getAdapter().sqlTypeOf(dbField.type()));
										}
										index += 1;
									}
								} else if (type == TransactionType.UPDATE) {
									// 设置更新值
									for (IPropertyInfo<?> propertyInfo : propertyInfos.where(c -> !c.isPrimaryKey())) {
										dbField = propertyInfo.getAnnotation(DbField.class);
										if (dbField == null || Strings.isNullOrEmpty(dbField.name())) {
											continue;
										}
										value = data.getProperty(propertyInfo);
										if (!propertyInfo.isPrimaryKey() && (value == DateTimes.VALUE_MIN
												|| value == Strings.VALUE_EMPTY || value == Numbers.SHORT_VALUE_ZERO
												|| value == Numbers.DOUBLE_VALUE_ZERO
												|| value == Numbers.FLOAT_VALUE_ZERO)) {
											// 默认值时存空
											value = null;
										}
										if (propertyInfo.getValueType().isEnum()) {
											statement.setObject(index, Enums.annotationValue(value),
													DbTransaction.this.getAdapter().sqlTypeOf(dbField.type()));
										} else {
											statement.setObject(index, value,
													DbTransaction.this.getAdapter().sqlTypeOf(dbField.type()));
										}
										index += 1;
									}
									// 设置主键条件值
									for (IPropertyInfo<?> propertyInfo : propertyInfos.where(c -> c.isPrimaryKey())) {
										dbField = propertyInfo.getAnnotation(DbField.class);
										if (dbField == null || Strings.isNullOrEmpty(dbField.name())) {
											continue;
										}
										value = data.getProperty(propertyInfo);
										if (propertyInfo.getValueType().isEnum()) {
											statement.setObject(index, Enums.annotationValue(value),
													DbTransaction.this.getAdapter().sqlTypeOf(dbField.type()));
										} else {
											statement.setObject(index, value,
													DbTransaction.this.getAdapter().sqlTypeOf(dbField.type()));
										}
										index += 1;
									}
								} else if (type == TransactionType.ADD) {
									for (IPropertyInfo<?> propertyInfo : propertyInfos) {
										dbField = propertyInfo.getAnnotation(DbField.class);
										if (dbField == null || Strings.isNullOrEmpty(dbField.name())) {
											continue;
										}
										value = data.getProperty(propertyInfo);
										if (!propertyInfo.isPrimaryKey() && (value == DateTimes.VALUE_MIN
												|| value == Strings.VALUE_EMPTY || value == Numbers.SHORT_VALUE_ZERO
												|| value == Numbers.DOUBLE_VALUE_ZERO
												|| value == Numbers.FLOAT_VALUE_ZERO)) {
											// 默认值时存空
											value = null;
										}
										if (propertyInfo.getValueType().isEnum()) {
											statement.setObject(index, Enums.annotationValue(value),
													DbTransaction.this.getAdapter().sqlTypeOf(dbField.type()));
										} else {
											statement.setObject(index, value,
													DbTransaction.this.getAdapter().sqlTypeOf(dbField.type()));
										}
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
				for (List<BusinessObject<?>> datas : boDeletes.values()) {
					if (datas.size() > 0) {
						result = sqlExecuter.apply(TransactionType.DELETE, datas);
						if (result instanceof Exception) {
							throw result;
						}
					}
				}
				// 处理更新内容
				for (List<BusinessObject<?>> datas : boUpdates.values()) {
					if (datas.size() > 0) {
						result = sqlExecuter.apply(TransactionType.UPDATE, datas);
						if (result instanceof Exception) {
							throw result;
						}
					}
				}
				// 处理新建内容
				for (List<BusinessObject<?>> datas : boInserts.values()) {
					if (datas.size() > 0) {
						result = sqlExecuter.apply(TransactionType.ADD, datas);
						if (result instanceof Exception) {
							throw result;
						}
					}
				}
				// 处理事务存储过程
				sqlExecuter = new BiFunction<TransactionType, List<BusinessObject<?>>, Exception>() {
					@Override
					public Exception apply(TransactionType type, List<BusinessObject<?>> datas) {
						int keyCount;
						DbField dbField;
						BusinessObject<?> data;
						StringBuilder fieldsBuilder;
						StringBuilder valuesBuilder;
						try (PreparedStatement statement = DbTransaction.this.connection
								.prepareStatement(DbTransaction.this.getAdapter().sp_transaction_notification())) {
							for (int i = 0; i < datas.size(); i++) {
								data = (BusinessObject<?>) datas.get(i);
								if (data.isDeleted()) {
									type = TransactionType.DELETE;
								} else if (data.isNew()) {
									type = TransactionType.ADD;
								} else {
									type = TransactionType.UPDATE;
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
								statement.setString(1, BOFactory.codeOf(data.getClass()));
								// 事务类型
								statement.setString(2, Enums.annotationValue(type));
								// 主键个数
								statement.setInt(3, keyCount);
								// 主键名称
								statement.setString(4, fieldsBuilder.toString());
								// 主键值
								statement.setString(5, valuesBuilder.toString());
								// 操作用户
								if (!DbTransaction.this.getAdapter().isNoUserTansactionSP()) {
									IUser user = DbTransaction.this.getUser();
									if (user != null) {
										statement.setInt(6, user.getId());
									} else {
										statement.setInt(6, -1);
									}
								}

								// 运行语句，非0则抛异常
								try (ResultSet resultSet = statement.executeQuery()) {
									List<Result> results = DbTransaction.this.getAdapter().parsingDatas(Result.class,
											resultSet);
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
				result = sqlExecuter.apply(null, boDatas);
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
					if (bo instanceof FieldedObject) {
						((FieldedObject) bo).markOld();
					}
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

	/**
	 * 查询最大值
	 * 
	 * @param maxValue 最大值内容
	 * @return
	 * @throws RepositoryException
	 */
	public final MaxValue fetch(MaxValue maxValue) throws RepositoryException {
		try {
			Objects.requireNonNull(maxValue);
			// 查询方法，不主动开启事务
			ICriteria criteria = new Criteria();
			for (IPropertyInfo<?> item : maxValue.getConditionFields()) {
				ICondition condition = criteria.getConditions().create();
				condition.setAlias(item.getName());
				condition.setValue(Strings.valueOf(maxValue.getProperty(item)));
			}
			criteria = this.getAdapter().convert(criteria, maxValue.getType());
			String sql = this.getAdapter().parsingMaxValue(maxValue, criteria.getConditions());
			if (MyConfiguration.isDebugMode()) {
				Logger.log(Strings.format("db sql: %s", sql));
			}
			try (PreparedStatement statement = this.connection.prepareStatement(sql)) {
				// 填充参数
				this.fillingParameters(statement, criteria.getConditions(), 1);
				// 运行查询
				try (ResultSet resultSet = statement.executeQuery()) {
					while (resultSet.next()) {
						maxValue = this.getAdapter().setProperties(maxValue, resultSet,
								new IPropertyInfo<?>[] { maxValue.getKeyField() });
					}
				}
			}
			return maxValue;
		} catch (Exception e) {
			throw new RepositoryException(e);
		}
	}

	/**
	 * 查询
	 * 
	 * @param sqlStatement 语句
	 * @return 数据表格
	 * @throws RepositoryException
	 */
	public final IDataTable fetch(ISqlStatement sqlStatement) throws RepositoryException {
		try {
			Objects.requireNonNull(sqlStatement);
			// 查询，不使用预编译方式
			try (Statement statement = this.connection.createStatement()) {
				Logger.log(Strings.format("db sql: run by user [%s].\n%s", this.getUser().getId(),
						sqlStatement.getContent()));
				try (ResultSet resultSet = statement.executeQuery(sqlStatement.getContent())) {
					return this.getAdapter().parsingDatas(resultSet);
				}
			}
		} catch (Exception e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public abstract Supplier<IUser> supplier();
}
