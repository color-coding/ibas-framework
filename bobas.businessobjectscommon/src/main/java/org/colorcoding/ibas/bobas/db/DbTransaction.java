package org.colorcoding.ibas.bobas.db;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.BOFactory;
import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.bo.BusinessObject;
import org.colorcoding.ibas.bobas.bo.BusinessObjectUnit;
import org.colorcoding.ibas.bobas.bo.BusinessObjects;
import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.bo.IBusinessObjects;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Decimals;
import org.colorcoding.ibas.bobas.common.Enums;
import org.colorcoding.ibas.bobas.common.IChildCriteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.Numbers;
import org.colorcoding.ibas.bobas.common.Result;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.configuration.Configuration;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.IDataTable;
import org.colorcoding.ibas.bobas.data.IKeyText;
import org.colorcoding.ibas.bobas.data.List;
import org.colorcoding.ibas.bobas.exception.BasRuntimeException;
import org.colorcoding.ibas.bobas.expression.BOJudgmentLinkCondition;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;
import org.colorcoding.ibas.bobas.organization.IUser;
import org.colorcoding.ibas.bobas.organization.OrganizationFactory;
import org.colorcoding.ibas.bobas.repository.IUserAware;
import org.colorcoding.ibas.bobas.repository.RepositoryException;
import org.colorcoding.ibas.bobas.repository.Transaction;

public class DbTransaction extends Transaction implements IUserAware {

	public DbTransaction(Connection connection) {
		Objects.requireNonNull(connection);
		this.connection = connection;
		this.setReplacementUpdate(
				MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_DB_REPLACEMENT_UPDATE, true));
		this.setBatchFetch(MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_DB_BATCH_FETCH, true));
		this.setTruncateDecimals(
				MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_TRUNCATE_DECIMALS_ON_SAVE, false));
	}

	private boolean replacementUpdate;

	public final boolean isReplacementUpdate() {
		return replacementUpdate;
	}

	public final void setReplacementUpdate(boolean replacementUpdate) {
		this.replacementUpdate = replacementUpdate;
	}

	private boolean batchFetch;

	public final boolean isBatchFetch() {
		return batchFetch;
	}

	public final void setBatchFetch(boolean batchFetch) {
		this.batchFetch = batchFetch;
	}

	private boolean truncateDecimals;

	public final boolean isTruncateDecimals() {
		return truncateDecimals;
	}

	/**
	 * 编辑类型小数位数
	 */
	private int[] edit_type_decimal_places;
	private final static String TEMPLATE_DECIMAL_PLACES = "decimalPlaces|";

	public final void setTruncateDecimals(boolean truncateDecimals) {
		this.truncateDecimals = truncateDecimals;
		// 给数组赋值
		this.edit_type_decimal_places = null;
		if (this.truncateDecimals) {
			IKeyText keyText;
			EditType[] types = EditType.values();
			List<IKeyText> configs = ArrayList.create(Configuration.create().getElements());
			this.edit_type_decimal_places = new int[types.length];
			for (EditType editType : types) {
				keyText = configs.firstOrDefault(c -> Strings.startsWith(c.getKey(), TEMPLATE_DECIMAL_PLACES, true)
						&& Strings.endsWith(c.getKey(), editType.toString(), true));
				if (keyText != null && Numbers.isNumeric(keyText.getText())) {
					this.edit_type_decimal_places[editType.ordinal()] = Numbers.toInteger(keyText.getText());
				} else {
					this.edit_type_decimal_places[editType.ordinal()] = -1;
				}
			}
		}
	}

	private volatile Connection connection;

	protected final synchronized Connection getConnection() throws SQLException {
		if (this.connection == null) {
			throw new SQLException(I18N.prop("msg_bobas_invalid_database_connection"));
		}
		return connection;
	}

	private DbAdapter adapter;

	public final DbAdapter getAdapter() throws Exception {
		if (this.adapter == null) {
			String adapterType = this.getConnection().getClass().getName();
			this.adapter = DbFactory.create().createAdapter(adapterType);
			if (this.adapter == null) {
				throw new BasRuntimeException(I18N.prop("msg_bobas_not_found_db_adapter", adapterType));
			}
		}
		return this.adapter;
	}

	/**
	 * 判断属性值是否为保存时应转为空值的默认值。
	 *
	 * @param propertyInfo 属性信息
	 * @param value        属性值
	 * @return 是默认空值返回true
	 */
	private boolean isDefaultNullValue(IPropertyInfo<?> propertyInfo, Object value) {
		if (propertyInfo == null || propertyInfo.isPrimaryKey() || propertyInfo.isUniqueKey()) {
			return false;
		}
		if (value == null) {
			return false;
		}
		// 按属性类型短路比较，避免对每个值盲目比较7种默认值
		Class<?> valueType = propertyInfo.getValueType();
		if (valueType == String.class) {
			return value.equals(Strings.VALUE_EMPTY);
		}
		if (valueType == Integer.class || valueType == int.class) {
			return value.equals(Numbers.INTEGER_VALUE_ZERO);
		}
		if (valueType == Short.class || valueType == short.class) {
			return value.equals(Numbers.SHORT_VALUE_ZERO);
		}
		if (valueType == Long.class || valueType == long.class) {
			return value.equals(Numbers.LONG_VALUE_ZERO);
		}
		if (valueType == Double.class || valueType == double.class) {
			return value.equals(Numbers.DOUBLE_VALUE_ZERO);
		}
		if (valueType == Float.class || valueType == float.class) {
			return value.equals(Numbers.FLOAT_VALUE_ZERO);
		}
		if (valueType == DateTime.class || (valueType != null && Date.class.isAssignableFrom(valueType))) {
			return value.equals(DateTimes.VALUE_MIN);
		}
		// 其他类型（BigDecimal等）不存在默认空值
		return false;
	}

	/**
	 * 将 SQL 异常翻译为友好的描述文本（用于包装异常）。
	 * <p>
	 * 调用当前数据库适配器的 {@link DbAdapter#translateException(SQLException)}： 各厂商子类会按
	 * ErrorCode/SQLState 识别并返回国际化消息， 未识别的异常包装为国际化兜底消息。 适配器不可用时，同样回退到兜底消息。
	 *
	 * @param exception SQL 异常
	 * @return 描述文本
	 */
	protected String translateSQLException(SQLException exception) {
		try {
			String translated = this.getAdapter().translateException(exception);
			if (!Strings.isNullOrEmpty(translated)) {
				return translated;
			}
		} catch (Exception ex) {
			// 适配器不可用，回退到兜底消息
		}
		String rawMessage = exception.getMessage();
		if (Strings.isNullOrEmpty(rawMessage)) {
			return I18N.prop("msg_bobas_db_unknown_error", exception.getClass().getSimpleName());
		}
		return I18N.prop("msg_bobas_db_unknown_error", rawMessage);
	}

	public final synchronized boolean inTransaction() throws RepositoryException {
		try {
			if (this.connection == null) {
				return false;
			}
			if (this.connection.isClosed()) {
				return false;
			}
			return !this.getConnection().getAutoCommit();
		} catch (SQLException e) {
			throw new RepositoryException(e.getMessage(), e);
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
			throw new RepositoryException(e.getMessage(), e);
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
				throw new SQLException(I18N.prop("msg_bobas_database_uncommitted_transaction_exists"));
			}
			if (this.isClosed() == false) {
				this.getConnection().close();
			}
			if (this.cacheDatas != null) {
				this.cacheDatas.clear();
			}
		} catch (SQLException e) {
			throw new RepositoryException(e.getMessage(), e);
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
			// 回滚后清理缓存，释放业务逻辑影响对象的引用
			if (this.cacheDatas != null) {
				this.cacheDatas.clear();
			}
		} catch (SQLException e) {
			throw new RepositoryException(e.getMessage(), e);
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
			// 提交后清理缓存，释放业务逻辑影响对象的引用
			if (this.cacheDatas != null) {
				this.cacheDatas.clear();
			}
		} catch (SQLException e) {
			throw new RepositoryException(e.getMessage(), e);
		}
	}

	@Override
	@SuppressWarnings({ "unchecked" })
	public final <T> T[] fetch(Class<?> boType, ICriteria criteria) throws RepositoryException {
		try {
			Objects.requireNonNull(boType);
			if (criteria == null) {
				criteria = new Criteria();
			}
			ICriteria nCriteria = criteria;
			ArrayList<T> datas = null;
			List<IBusinessObject> results = null;
			while ((datas == null || datas.size() < criteria.getResultCount()) && nCriteria != null) {
				if (datas == null) {
					datas = criteria.getResultCount() >= 0 ? new ArrayList<>(criteria.getResultCount())
							: new ArrayList<>();
				}
				// 查询方法，不主动开启事务
				nCriteria = this.getAdapter().convert(nCriteria, boType);
				String sql = this.inTransaction()
						// 事务中且锁表对象，则查询对象加锁
						? this.getAdapter().parsingSelect(boType, nCriteria, true)
						: this.getAdapter().parsingSelect(boType, nCriteria);
				if (MyConfiguration.isDebugMode()) {
					// Logger.log(MessageLevel.DEBUG, Strings.format("parameterized sql: %s", sql));
					SqlPreparedStatement sqlBuilder = new SqlPreparedStatement(sql);
					this.getAdapter().bindParameters(sqlBuilder, nCriteria.getConditions(), 1);
					Logger.log(MessageLevel.DEBUG,
							Strings.format("executable sql: %s", this.getAdapter().parsing(sqlBuilder)));
				}
				try (PreparedStatement statement = this.getConnection().prepareStatement(sql)) {
					// 填充参数
					this.getAdapter().bindParameters(statement, nCriteria.getConditions(), 1);
					// 运行查询
					try (ResultSet resultSet = statement.executeQuery();) {
						// 传入预期行数，预分配ArrayList容量以减少扩容拷贝
						results = this.getAdapter().parsingDatas(boType, resultSet, nCriteria.getResultCount());
					}
				}
				// 加载子项，并保留有效的
				if (!results.isEmpty() && !nCriteria.isNoChilds()) {
					this.fetchChilds(results, nCriteria);
				}
				for (IBusinessObject item : results) {
					if (!item.isValid()) {
						continue;
					}
					datas.add((T) item);
				}
				// 已是全部查询结果
				if (results.isEmpty() || results.size() < nCriteria.getResultCount()) {
					break;
				}
				// 查询结果已够
				if (datas.size() >= criteria.getResultCount()) {
					break;
				}
				// 查询下一组
				nCriteria = criteria.next(results.lastOrDefault());
				if (nCriteria != null) {
					nCriteria.setResultCount(criteria.getResultCount() - datas.size());
					if (nCriteria.getResultCount() <= 0) {
						break;
					}
				}
			}
			return datas.toArray((T[]) Array.newInstance(boType, datas.size()));
		} catch (RepositoryException e) {
			throw e;
		} catch (Exception e) {
			if (boType != null && e instanceof SQLException) {
				String message = this.translateSQLException((SQLException) e);
				throw new RepositoryException(
						I18N.prop("msg_bobas_to_fetch_bo_data_failed", boType.getSimpleName(), message), e);
			}
			throw new RepositoryException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	protected void fetchChilds(List<IBusinessObject> datas, ICriteria criteria) throws Exception {
		// 加载子对象
		Object propertyValue = null;
		ICriteria cCriteria = null;
		Class<?> boType = datas.firstOrDefault().getClass();
		for (IPropertyInfo<?> propertyInfo : BOFactory.propertyInfos(boType)) {
			// 跳过值类型
			if (BOUtilities.isValueType(propertyInfo)) {
				continue;
			}
			if (propertyInfo.getValueType() == null) {
				continue;
			}
			if (IBusinessObject.class.isAssignableFrom(propertyInfo.getValueType())) {
				BusinessObject<?> cData = null;
				for (IBusinessObject data : datas) {
					if (!data.isValid()) {
						// 跳过无效的数据
						continue;
					}
					propertyValue = ((BusinessObject<?>) data).getProperty(propertyInfo);
					if (BOUtilities.isBusinessObject(propertyValue)) {
						cData = (BusinessObject<?>) propertyValue;
						cCriteria = cData.getCriteria();
						if (cCriteria == null || cCriteria.getConditions().isEmpty()) {
							continue;
						}
						data.setLoading(true);
						for (Object item : this.fetch(cData.getClass(), cCriteria)) {
							if (!(item instanceof IBusinessObject)) {
								continue;
							}
							((BusinessObject<?>) data).setProperty(propertyInfo, item);
						}
						data.setLoading(false);
					}
				}
			} else if (IBusinessObjects.class.isAssignableFrom(propertyInfo.getValueType())) {
				Object[] results = null;
				ICriteria eCriteria = null;
				boolean onlyHasChilds = false;
				boolean includingOtherChilds = false;
				BusinessObjects<IBusinessObject, ?> cDatas = null;

				cCriteria = new Criteria();
				// 复制指定的子项查询
				for (IChildCriteria item : criteria.getChildCriterias()) {
					if (Strings.equalsIgnoreCase(propertyInfo.getName(), item.getPropertyPath())) {
						cCriteria = cCriteria.copyFrom(item);
						cCriteria.setNoChilds(item.isNoChilds());
						cCriteria.setResultCount(item.getResultCount());
						if (onlyHasChilds == false) {
							onlyHasChilds = item.isOnlyHasChilds();
						}
						if (includingOtherChilds == false) {
							includingOtherChilds = item.isIncludingOtherChilds();
						}
					}
				}
				// 不查询子项
				if (cCriteria.isNoChilds()) {
					continue;
				}
				if (!this.isBatchFetch() || datas.size() == 1 || includingOtherChilds) {
					// 单独查询子项（含非条件子项）
					if (!cCriteria.getConditions().isEmpty()) {
						if (cCriteria.getConditions().size() > 1) {
							cCriteria.getConditions().firstOrDefault().addBracketOpen();
							cCriteria.getConditions().lastOrDefault().addBracketClose();
						}
						cCriteria.getConditions().firstOrDefault().setRelationship(ConditionRelationship.AND);
					}
					for (IBusinessObject data : datas) {
						if (!data.isValid()) {
							// 跳过无效的数据
							continue;
						}
						propertyValue = ((BusinessObject<?>) data).getProperty(propertyInfo);
						if (BOUtilities.isBusinessObjects(propertyValue)) {
							cDatas = (BusinessObjects<IBusinessObject, ?>) propertyValue;
							eCriteria = cDatas.getElementCriteria();
							// 查询无效则跳过
							if (eCriteria == null || eCriteria.getConditions().isEmpty()) {
								continue;
							}
							eCriteria.getConditions().addAll(cCriteria.getConditions());
							// 子查询有效
							if (!eCriteria.getConditions().isEmpty()) {
								// 诺含有符合条件项，则全部返回（含其他项）
								if (includingOtherChilds == true) {
									eCriteria = this.getAdapter().convert(eCriteria, cDatas.getElementType());
									try (PreparedStatement checkStatement = this.getConnection().prepareStatement(
											this.getAdapter().parsingSelect(cDatas.getElementType(), eCriteria))) {
										this.getAdapter().bindParameters(checkStatement, eCriteria.getConditions(), 1);
										try (ResultSet resultSet = checkStatement.executeQuery()) {
											if (resultSet.isBeforeFirst()) {
												// 有结果，则使用全量查询
												eCriteria = cDatas.getElementCriteria();
											} else {
												// 无结果，则不查询
												eCriteria = null;
											}
										}
									}
								}
								// 子查询有效
								if (eCriteria != null && !eCriteria.getConditions().isEmpty()) {
									// 查询子项
									results = this.fetch(cDatas.getElementType(), eCriteria);
									data.setLoading(true);
									cDatas.ensureCapacity(cDatas.size() + results.length);
									for (Object item : results) {
										if (!(item instanceof IBusinessObject)) {
											continue;
										}
										cDatas.add((IBusinessObject) item);
									}
									// 要求子项必须有值
									if (onlyHasChilds && cDatas.isEmpty()) {
										data.setValid(false);
									}
									data.setLoading(false);
								}
							}
						}
					}
					cDatas = null;
					results = null;
					eCriteria = null;
					propertyValue = null;
				} else {
					// 批量查询子项（受数据库参数限制，查询的数据分批）
					Object result = null;
					ICriteria bCriteria = null;
					IBusinessObject data = null;
					BOJudgmentLinkCondition judgmentLink = null;
					int daIndex = 0;
					int toIndex = 0;
					int batchCount = this.getAdapter().getBatchCount();
					int crIndex = cCriteria.getConditions().size();

					do {
						if (bCriteria == null) {
							bCriteria = cCriteria.clone();
						}
						daIndex = 0;
						for (int index = toIndex; index < datas.size(); index++) {
							daIndex++;
							toIndex++;
							data = datas.get(index);

							if (!data.isValid()) {
								// 跳过无效的数据
								continue;
							}
							propertyValue = ((BusinessObject<?>) data).getProperty(propertyInfo);
							if (BOUtilities.isBusinessObjects(propertyValue)) {
								cDatas = (BusinessObjects<IBusinessObject, ?>) propertyValue;
								eCriteria = cDatas.getElementCriteria();
								// 查询无效则跳过
								if (eCriteria == null || eCriteria.getConditions().isEmpty()) {
									continue;
								}
								if (eCriteria.getConditions().size() > 1) {
									eCriteria.getConditions().firstOrDefault().addBracketOpen();
									eCriteria.getConditions().lastOrDefault().addBracketClose();
								}
								if (bCriteria.getConditions().size() > crIndex) {
									eCriteria.getConditions().firstOrDefault()
											.setRelationship(ConditionRelationship.OR);
								}
								bCriteria.getConditions().addAll(eCriteria.getConditions());
							}
							if (daIndex >= batchCount) {
								break;
							}
						}
						data = null;
						eCriteria = null;
						propertyValue = null;
						if (bCriteria.getConditions().size() <= crIndex) {
							// 无子项查询则继续
							bCriteria = null;
							continue;
						}
						bCriteria.getConditions().get(crIndex).setRelationship(ConditionRelationship.AND);
						if (crIndex > 0 && bCriteria.getConditions().size() > crIndex + 1) {
							bCriteria.getConditions().get(crIndex).addBracketOpen();
							bCriteria.getConditions().get(bCriteria.getConditions().size() - 1).addBracketClose();
						}
						results = this.fetch(cDatas.getElementType(), bCriteria);
						for (int index = toIndex - daIndex; index < toIndex; index++) {
							data = datas.get(index);
							if (!data.isValid()) {
								// 跳过无效的数据
								continue;
							}
							propertyValue = ((BusinessObject<?>) data).getProperty(propertyInfo);
							if (BOUtilities.isBusinessObjects(propertyValue)) {
								cDatas = (BusinessObjects<IBusinessObject, ?>) propertyValue;
								if (daIndex == 1) {
									// 查询目标唯一
									data.setLoading(true);
									cDatas.ensureCapacity(cDatas.size() + results.length);
									for (int i = 0; i < results.length; i++) {
										cDatas.add((IBusinessObject) results[i]);
									}
								} else if (results.length > 0) {
									eCriteria = cDatas.getElementCriteria();
									// 查询无效则跳过
									if (eCriteria == null || eCriteria.getConditions().isEmpty()) {
										continue;
									}
									// 匹配目标条件
									judgmentLink = new BOJudgmentLinkCondition();
									judgmentLink.parsingConditions(eCriteria.getConditions());
									data.setLoading(true);
									for (int i = 0; i < results.length; i++) {
										result = results[i];
										if (!(result instanceof IBusinessObject)) {
											continue;
										}
										if (judgmentLink.judge(result)) {
											cDatas.add((IBusinessObject) result);
											results[i] = null;
										}
									}
								}
								// 要求子项必须有值
								if (onlyHasChilds && cDatas.isEmpty()) {
									data.setValid(false);
								}
								data.setLoading(false);
							}
						}
						data = null;
						cDatas = null;
						result = null;
						results = null;
						eCriteria = null;
						bCriteria = null;
						judgmentLink = null;
						propertyValue = null;
					} while (toIndex < datas.size());
				}
			}
		}
	}

	@Override
	public final <T> T[] save(T[] datas) throws RepositoryException {
		try {
			Objects.requireNonNull(datas);
			if (datas.length == 0) {
				return datas;
			}
			Object cData = null;
			Class<?> boType = null;
			Exception sqlResult = null;
			BusinessObject<?> boData = null;
			List<IBusinessObject> boChilds = null;
			List<BusinessObject<?>> boDatas = null;
			// 保存语句执行者
			BiFunction<TransactionType, List<BusinessObject<?>>, Exception> sqlExecuter = null;
			// 事务通知语句执行者
			BiFunction<Boolean, List<BusinessObject<?>>, Exception> spExecuter = null;
			Map<Class<?>, List<BusinessObject<?>>> boDeletes = new LinkedHashMap<>(4, 1);
			Map<Class<?>, List<BusinessObject<?>>> boUpdates = new LinkedHashMap<>(4, 1);
			Map<Class<?>, List<BusinessObject<?>>> boInserts = new LinkedHashMap<>(4, 1);

			boolean mine = this.beginTransaction();
			try {
				// 分析数据，形成待处理集合
				for (Object data : datas) {
					if (!BOUtilities.isBusinessObject(data)) {
						continue;
					}
					boData = (BusinessObject<?>) data;
					boType = boData.getClass();
					// 分析待处理方式（按类型分组）
					if (boData.isSavable() && boData.isDirty()) {
						if (!boDeletes.containsKey(boType)) {
							boDeletes.put(boType, new ArrayList<>(datas.length));
						}
						if (!boUpdates.containsKey(boType)) {
							boUpdates.put(boType, new ArrayList<>(datas.length));
						}
						if (!boInserts.containsKey(boType)) {
							boInserts.put(boType, new ArrayList<>(datas.length));
						}
						if (boData.isDeleted()) {
							// 删除
							boDeletes.get(boType).add(boData);
						} else if (boData.isNew()) {
							// 新建
							boInserts.get(boType).add(boData);
						} else {
							// 更新
							if (this.isReplacementUpdate() && !(boData instanceof IDbTableUpdate)) {
								// 替换更新：删除后新建
								boDeletes.get(boType).add(boData);
								boInserts.get(boType).add(boData);
							} else {
								// 更新修改项
								boUpdates.get(boType).add(boData);
							}
						}
					}
					// 分析业务主对象标记
					if (boType.getAnnotation(BusinessObjectUnit.class) != null) {
						if (boDatas == null) {
							boDatas = new ArrayList<>(datas.length);
						}
						boDatas.add(boData);
					}
					// 分析待处理子项
					for (IPropertyInfo<?> propertyInfo : BOFactory.propertyInfos(boType)) {
						// 跳过值类型
						if (BOUtilities.isValueType(propertyInfo)) {
							continue;
						}
						if (propertyInfo.getValueType() == null) {
							continue;
						}
						if (IBusinessObject.class.isAssignableFrom(propertyInfo.getValueType())) {
							cData = boData.getProperty(propertyInfo);
							if (cData instanceof IBusinessObject) {
								if (boChilds == null) {
									boChilds = new ArrayList<>();
								}
								boChilds.add((IBusinessObject) cData);
							}
						} else if (IBusinessObjects.class.isAssignableFrom(propertyInfo.getValueType())) {
							cData = boData.getProperty(propertyInfo);
							if (cData instanceof IBusinessObjects) {
								if (boChilds == null) {
									boChilds = new ArrayList<>(((IBusinessObjects<?, ?>) cData).size());
								}
								boChilds.addAll((IBusinessObjects<?, ?>) cData);
							}
						}
					}
				}
				cData = null;
				boData = null;
				// 分配执行语句的方法
				int batchCount = this.getAdapter().getBatchCount();
				sqlExecuter = new BiFunction<TransactionType, List<BusinessObject<?>>, Exception>() {
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
							Logger.log(MessageLevel.DEBUG, Strings.format("parameterized sql: %s", sql));
						}
						Object value = null;
						DbField dbField = null;
						BusinessObject<?> data = null;
						List<IPropertyInfo<?>> propertyInfos = tpltData.properties();
						try (PreparedStatement statement = DbTransaction.this.getConnection().prepareStatement(sql)) {
							int count = 0;
							int index = 0;
							for (int i = 0; i < datas.size(); i++) {
								index = 1;
								data = datas.get(i);

								if (type == TransactionType.DELETE) {
									// 获取主键
									List<IPropertyInfo<?>> ptyKeys = propertyInfos.where(c -> c.isPrimaryKey());
									if (ptyKeys.isEmpty()) {
										throw new SQLException(I18N.prop("msg_bobas_operation_requires_primary_key"));
									}
									// 设置主键条件值
									for (IPropertyInfo<?> propertyInfo : ptyKeys) {
										dbField = propertyInfo.getAnnotation(DbField.class);
										if (dbField == null || Strings.isNullOrEmpty(dbField.name())) {
											continue;
										}
										value = data.getProperty(propertyInfo);
										if (propertyInfo.getValueType() != null
												&& propertyInfo.getValueType().isEnum()) {
											statement.setObject(index, Enums.annotationValue(value),
													DbTransaction.this.getAdapter().sqlTypeOf(dbField.type(), value));
										} else {
											statement.setObject(index, value,
													DbTransaction.this.getAdapter().sqlTypeOf(dbField.type(), value));
										}
										index += 1;
									}
									value = null;
									dbField = null;
									ptyKeys = null;
								} else if (type == TransactionType.UPDATE) {
									// 获取主键
									List<IPropertyInfo<?>> ptyKeys = propertyInfos.where(c -> c.isPrimaryKey());
									if (ptyKeys.isEmpty()) {
										throw new SQLException(I18N.prop("msg_bobas_operation_requires_primary_key"));
									}
									// 设置更新值
									for (IPropertyInfo<?> propertyInfo : propertyInfos.where(c -> !c.isPrimaryKey())) {
										dbField = propertyInfo.getAnnotation(DbField.class);
										if (dbField == null || Strings.isNullOrEmpty(dbField.name())) {
											continue;
										}
										value = data.getProperty(propertyInfo);
										if (DbTransaction.this.isDefaultNullValue(propertyInfo, value)) {
											// 默认值时存空
											value = null;
										} else if (DbTransaction.this.edit_type_decimal_places != null
												&& DbTransaction.this.edit_type_decimal_places[dbField.editType()
														.ordinal()] >= 0
												&& value instanceof BigDecimal) {
											// 截断小数位
											value = Decimals.round((BigDecimal) value,
													DbTransaction.this.edit_type_decimal_places[dbField.editType()
															.ordinal()]);
										}
										if (propertyInfo.getValueType() != null
												&& propertyInfo.getValueType().isEnum()) {
											statement.setObject(index, Enums.annotationValue(value),
													DbTransaction.this.getAdapter().sqlTypeOf(dbField.type(), value));
										} else {
											statement.setObject(index, value,
													DbTransaction.this.getAdapter().sqlTypeOf(dbField.type(), value));
										}
										index += 1;
									}
									// 设置主键条件值
									for (IPropertyInfo<?> propertyInfo : ptyKeys) {
										dbField = propertyInfo.getAnnotation(DbField.class);
										if (dbField == null || Strings.isNullOrEmpty(dbField.name())) {
											continue;
										}
										value = data.getProperty(propertyInfo);
										if (propertyInfo.getValueType() != null
												&& propertyInfo.getValueType().isEnum()) {
											statement.setObject(index, Enums.annotationValue(value),
													DbTransaction.this.getAdapter().sqlTypeOf(dbField.type(), value));
										} else {
											statement.setObject(index, value,
													DbTransaction.this.getAdapter().sqlTypeOf(dbField.type(), value));
										}
										index += 1;
									}
									value = null;
									dbField = null;
									ptyKeys = null;
								} else if (type == TransactionType.ADD) {
									for (IPropertyInfo<?> propertyInfo : propertyInfos) {
										dbField = propertyInfo.getAnnotation(DbField.class);
										if (dbField == null || Strings.isNullOrEmpty(dbField.name())) {
											continue;
										}
										value = data.getProperty(propertyInfo);
										if (DbTransaction.this.isDefaultNullValue(propertyInfo, value)) {
											// 默认值时存空
											value = null;
										} else if (DbTransaction.this.edit_type_decimal_places != null
												&& DbTransaction.this.edit_type_decimal_places[dbField.editType()
														.ordinal()] >= 0
												&& value instanceof BigDecimal) {
											// 截断小数位
											value = Decimals.round((BigDecimal) value,
													DbTransaction.this.edit_type_decimal_places[dbField.editType()
															.ordinal()]);
										}
										if (propertyInfo.getValueType() != null
												&& propertyInfo.getValueType().isEnum()) {
											statement.setObject(index, Enums.annotationValue(value),
													DbTransaction.this.getAdapter().sqlTypeOf(dbField.type(), value));
										} else {
											statement.setObject(index, value,
													DbTransaction.this.getAdapter().sqlTypeOf(dbField.type(), value));
										}
										index += 1;
									}
									value = null;
									dbField = null;
								}
								statement.addBatch();
								count += 1;
								if (count >= batchCount) {
									statement.executeBatch();
									statement.clearBatch();
									count = 0;
								}
							}
							if (count > 0) {
								statement.executeBatch();
								statement.clearBatch();
							}
						} catch (Exception e) {
							if (data != null) {
								if (e instanceof SQLException) {
									return new RepositoryException(I18N.prop("msg_bobas_to_save_bo_data_failed",
											data.getClass().getSimpleName(),
											DbTransaction.this.translateSQLException((SQLException) e)), e);
								} else {
									return new RepositoryException(I18N.prop("msg_bobas_to_save_bo_data_failed",
											data.getClass().getSimpleName(), e.getMessage()), e);
								}
							}
							return e;
						} finally {
							sql = null;
							data = null;
							tpltData = null;
							value = null;
							dbField = null;
							propertyInfos = null;
						}
						return null;
					}
				};
				// 处理事务存储过程
				if (boDatas != null && !boDatas.isEmpty()) {
					spExecuter = new BiFunction<Boolean, List<BusinessObject<?>>, Exception>() {
						@Override
						public Exception apply(Boolean direction, List<BusinessObject<?>> datas) {
							int keyCount;
							DbField dbField;
							TransactionType type;
							StringBuilder fieldsBuilder;
							StringBuilder valuesBuilder;
							List<IPropertyInfo<?>> keys;
							BusinessObject<?> data = null;
							try (PreparedStatement statement = DbTransaction.this.getConnection()
									.prepareStatement(DbTransaction.this.getAdapter().sp_transaction_notification())) {
								for (int i = 0; i < datas.size(); i++) {
									data = (BusinessObject<?>) datas.get(i);
									if (!data.isSavable()) {
										continue;
									}
									if (data.isDeleted()) {
										type = direction ? TransactionType.DELETE : TransactionType.BEFORE_DELETE;
									} else if (data.isNew()) {
										type = direction ? TransactionType.ADD : TransactionType.BEFORE_ADD;
									} else {
										type = direction ? TransactionType.UPDATE : TransactionType.BEFORE_UPDATE;
									}
									keyCount = 0;
									keys = data.properties().where(c -> c.isPrimaryKey());
									fieldsBuilder = new StringBuilder(keys.size() * 16);
									valuesBuilder = new StringBuilder(keys.size() * 16);
									for (IPropertyInfo<?> propertyInfo : keys) {
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
									if (!DbTransaction.this.getAdapter().isNoUserTransactionSP()) {
										IUser user = DbTransaction.this.getUser();
										if (user != null) {
											if (DbTransaction.this.cacheDatas != null
													&& DbTransaction.this.cacheDatas.containsAll(datas)) {
												// 缓存里有被保存对象，则认为是业务逻辑影响的，更新用户标记下
												statement.setInt(6, -900000 - user.getId());
											} else {
												statement.setInt(6, user.getId());
											}
										} else {
											statement.setInt(6, -1);
										}
									}

									// 运行语句，非0则抛异常
									try (ResultSet resultSet = statement.executeQuery()) {
										List<Result> results = DbTransaction.this.getAdapter()
												.parsingDatas(Result.class, resultSet);
										for (Result result : results) {
											if (result.getResultCode() != 0) {
												throw new TransactionException(
														I18N.prop("msg_bobas_user_transaction_check_results",
																result.getResultCode(), result.getMessage()));
											}
										}
									}
								}
							} catch (Exception e) {
								if (data != null) {
									if (e instanceof SQLException) {
										return new RepositoryException(I18N.prop("msg_bobas_to_save_bo_data_failed",
												data.getClass().getSimpleName(),
												DbTransaction.this.translateSQLException((SQLException) e)), e);
									} else {
										return new RepositoryException(I18N.prop("msg_bobas_to_save_bo_data_failed",
												data.getClass().getSimpleName(), e.getMessage()), e);
									}
								}
								return e;
							} finally {
								dbField = null;
								data = null;
								fieldsBuilder = null;
								valuesBuilder = null;
							}
							return null;
						}
					};
				}
				// 执行存储过程通知（保存前）
				if (spExecuter != null) {
					sqlResult = spExecuter.apply(false, boDatas);
					if (sqlResult instanceof Exception) {
						throw sqlResult;
					}
				}
				// 处理删除内容
				for (List<BusinessObject<?>> values : boDeletes.values()) {
					if (values.size() > 0) {
						sqlResult = sqlExecuter.apply(TransactionType.DELETE, values);
						if (sqlResult instanceof Exception) {
							throw sqlResult;
						}
					}
				}
				// 处理更新内容
				for (List<BusinessObject<?>> values : boUpdates.values()) {
					if (values.size() > 0) {
						sqlResult = sqlExecuter.apply(TransactionType.UPDATE, values);
						if (sqlResult instanceof Exception) {
							throw sqlResult;
						}
					}
				}
				// 处理新建内容
				for (List<BusinessObject<?>> values : boInserts.values()) {
					if (values.size() > 0) {
						sqlResult = sqlExecuter.apply(TransactionType.ADD, values);
						if (sqlResult instanceof Exception) {
							throw sqlResult;
						}
					}
				}
				// 处理子项
				if (boChilds != null && !boChilds.isEmpty()) {
					this.save(boChilds.toArray(new IBusinessObject[boChilds.size()]));
				}
				// 执行存储过程通知（保存后）
				if (spExecuter != null) {
					sqlResult = spExecuter.apply(true, boDatas);
					if (sqlResult instanceof Exception) {
						throw sqlResult;
					}
				}
				if (mine == true) {
					this.commit();
					mine = false;
				}
				// 标记状态为已保存
				for (Object data : datas) {
					if (!BOUtilities.isBusinessObject(data)) {
						continue;
					}
					boData = (BusinessObject<?>) data;
					if (boData.isDirty() == false) {
						continue;
					}
					boData.markOld();
				}
				return datas;
			} catch (Exception e) {
				if (mine == true) {
					try {
						this.rollback();
					} catch (Exception e1) {
						// 回滚失败时，将回滚异常附加到原始异常上，避免丢失真正的故障原因
						e.addSuppressed(e1);
					}
					mine = false;
				}
				throw e;
			} finally {
				cData = null;
				boType = null;
				boData = null;
				boDatas = null;
				boChilds = null;
				boDeletes = null;
				boUpdates = null;
				boInserts = null;
				sqlResult = null;
				sqlExecuter = null;
				spExecuter = null;
			}
		} catch (RepositoryException e) {
			throw e;
		} catch (Exception e) {
			throw new RepositoryException(e.getMessage(), e);
		}
	}

	/**
	 * 查询最大值
	 *
	 * @param maxValue 最大值查询配置
	 * @return 查询结果（更新maxValue中的值）
	 * @throws RepositoryException 查询失败时抛出
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
				// Logger.log(MessageLevel.DEBUG, Strings.format("parameterized sql: %s", sql));
				SqlPreparedStatement sqlBuilder = new SqlPreparedStatement(sql);
				this.getAdapter().bindParameters(sqlBuilder, criteria.getConditions(), 1);
				Logger.log(MessageLevel.DEBUG,
						Strings.format("executable sql: %s", this.getAdapter().parsing(sqlBuilder)));
			}
			try (PreparedStatement statement = this.getConnection().prepareStatement(sql)) {
				// 填充参数
				this.getAdapter().bindParameters(statement, criteria.getConditions(), 1);
				// 运行查询
				try (ResultSet resultSet = statement.executeQuery()) {
					while (resultSet.next()) {
						maxValue = this.getAdapter().setProperties(maxValue, resultSet,
								new IPropertyInfo<?>[] { maxValue.getKeyField() });
					}
					maxValue.markOld();
				}
			}
			return maxValue;
		} catch (RepositoryException e) {
			throw e;
		} catch (Exception e) {
			throw new RepositoryException(e.getMessage(), e);
		}
	}

	/**
	 * 查询（直接执行SQL语句，返回数据表格）
	 *
	 * @param sqlStatement SQL语句对象
	 * @return 数据表格
	 * @throws RepositoryException 查询失败时抛出
	 */
	public final IDataTable fetch(SqlStatement sqlStatement) throws RepositoryException {
		try {
			Objects.requireNonNull(sqlStatement);
			String sql = this.getAdapter().parsing(sqlStatement);
			Logger.log(MessageLevel.DEBUG,
					Strings.format("db sql: run by user [%s].\n%s", this.getUser().getId(), sql));
			// 运行查询，不使用预编译方式
			try (Statement statement = this.getConnection().createStatement()) {
				try (ResultSet resultSet = statement.executeQuery(sql)) {
					return this.getAdapter().parsingDatas(resultSet);
				}
			}
		} catch (RepositoryException e) {
			throw e;
		} catch (Exception e) {
			throw new RepositoryException(e.getMessage(), e);
		}
	}

	/**
	 * 查询（直接执行SQL语句，返回业务对象数组）
	 *
	 * @param <T>          返回类型
	 * @param boType       返回类型
	 * @param sqlStatement SQL语句对象
	 * @return 业务对象数组
	 * @throws RepositoryException 查询失败时抛出
	 */
	@SuppressWarnings("unchecked")
	public final <T> T[] fetch(Class<?> boType, SqlStatement sqlStatement) throws RepositoryException {
		try {
			Objects.requireNonNull(sqlStatement);
			List<IBusinessObject> datas;
			// 运行查询，不使用预编译方式
			String sql = this.getAdapter().parsing(sqlStatement);
			if (MyConfiguration.isDebugMode()) {
				Logger.log(MessageLevel.DEBUG, Strings.format("executable sql: %s", sql));
			}
			try (Statement statement = this.getConnection().createStatement()) {
				try (ResultSet resultSet = statement.executeQuery(sql)) {
					// 运行查询
					datas = this.getAdapter().parsingDatas(boType, resultSet);
				}
			}
			if (datas == null) {
				datas = new ArrayList<>();
			}
			// 加载子对象
			if (!datas.isEmpty()) {
				this.fetchChilds(datas, new Criteria());
			}
			return datas.toArray((T[]) Array.newInstance(boType, datas.size()));
		} catch (RepositoryException e) {
			throw e;
		} catch (Exception e) {
			if (boType != null && e instanceof SQLException) {
				String message = this.translateSQLException((SQLException) e);
				throw new RepositoryException(
						I18N.prop("msg_bobas_to_fetch_bo_data_failed", boType.getSimpleName(), message), e);
			}
			throw new RepositoryException(e.getMessage(), e);
		}
	}

	private volatile HashSet<Object> cacheDatas = null;

	/**
	 * 缓存数据
	 *
	 * @param data 待缓存数据
	 * @return true:缓存成功（新）; false:已缓存
	 */
	@Override
	public synchronized boolean cache(Object data) {
		Objects.requireNonNull(data);
		if (this.cacheDatas == null) {
			this.cacheDatas = new HashSet<>();
		}
		return this.cacheDatas.add(data);
	}

	/**
	 * 清理缓存数据，释放被引用的对象
	 */
	@Override
	public synchronized void clearCache() {
		if (this.cacheDatas != null) {
			this.cacheDatas.clear();
		}
	}

	/**
	 * 缓存中查询数据
	 *
	 * @param <T>      数据类型
	 * @param boType   类型
	 * @param criteria 查询条件
	 * @return 符合条件的数据数组
	 * @throws RepositoryException 查询失败时抛出
	 */
	@Override
	@SuppressWarnings("unchecked")
	public synchronized <T> T[] fetchInCache(Class<?> boType, ICriteria criteria) throws RepositoryException {
		try {
			Objects.requireNonNull(criteria);
			Objects.requireNonNull(boType);
			// 缓存数据迭代器（不一次取出）
			Iterator<?> iterator = new Iterator<Object>() {
				// 数据迭代器
				Iterator<?> current = DbTransaction.this.cacheDatas != null ? DbTransaction.this.cacheDatas.iterator()
						: null;

				@Override
				public boolean hasNext() {
					if (this.current != null && this.current.hasNext()) {
						return true;
					}
					return false;
				}

				@Override
				public Object next() {
					if (this.current != null && this.current.hasNext()) {
						Object data = this.current.next();
						if (boType.isInstance(data)) {
							return data;
						} else {
							return this.next();
						}
					}
					return null;
				}
			};
			return BOUtilities.fetch(iterator, criteria).toArray((T[]) Array.newInstance(boType, 0));
		} catch (Exception e) {
			throw new RepositoryException(e.getMessage(), e);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		this.connection = null;
		this.adapter = null;
		this.cacheDatas = null;
		this.currentUser = null;
		super.finalize();
	}

	private volatile IUser currentUser;

	@Override
	public IUser getUser() {
		if (this.currentUser == null) {
			return OrganizationFactory.UNKNOWN_USER;
		}
		return this.currentUser;
	}

	@Override
	public void setUser(IUser user) {
		this.currentUser = user;
	}
}
