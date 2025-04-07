package org.colorcoding.ibas.bobas.repository;

import java.sql.Timestamp;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.IBusinessObjects;
import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.IChildCriteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.ISqlQuery;
import org.colorcoding.ibas.bobas.common.ISqlStoredProcedure;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.core.BOFactory;
import org.colorcoding.ibas.bobas.core.BORepositoryBase;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.core.TrackableBase;
import org.colorcoding.ibas.bobas.core.fields.AssociatedFieldDataBase;
import org.colorcoding.ibas.bobas.core.fields.FieldRelation;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.core.fields.IManagedFields;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.IDataTable;
import org.colorcoding.ibas.bobas.data.SingleValue;
import org.colorcoding.ibas.bobas.db.DbAdapterFactory;
import org.colorcoding.ibas.bobas.db.DbException;
import org.colorcoding.ibas.bobas.db.IBOAdapter;
import org.colorcoding.ibas.bobas.db.IDbAdapter;
import org.colorcoding.ibas.bobas.db.IDbCommand;
import org.colorcoding.ibas.bobas.db.IDbConnection;
import org.colorcoding.ibas.bobas.db.IDbDataReader;
import org.colorcoding.ibas.bobas.db.ISqlScriptInspector;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.mapping.AssociationMode;
import org.colorcoding.ibas.bobas.message.Logger;

/**
 * 业务对象仓库-只读数据库
 * 
 * @author Niuren.Zhu
 *
 */
public class BORepository4DbReadonly extends BORepositoryBase implements IBORepository4DbReadonly {

	public BORepository4DbReadonly() {
	}

	public BORepository4DbReadonly(String sign) {
		this.dbSign = sign;
	}

	private String dbSign = "";

	public String getDbSign() {
		return this.dbSign;
	}

	private String dbType = null;

	protected String getDbType() {
		if (this.dbType == null || this.dbType.isEmpty()) {
			if (this.getDbSign() != null && !this.getDbSign().isEmpty()) {
				// 设置连接配置类型
				String configItem = this.getDbSign() + MyConfiguration.CONFIG_ITEM_DB_TYPE;
				this.dbType = MyConfiguration.getConfigValue(configItem);
			}

		}
		return this.dbType;
	}

	private void setDbType(String value) {
		this.dbType = value;
	}

	public IDbAdapter createDbAdapter() {
		return DbAdapterFactory.create().createAdapter(this.getDbType());
	}

	private volatile IDbConnection dbConnection = null;

	@Override
	public IDbConnection getDbConnection() throws DbException {
		if (this.dbConnection == null || this.dbConnection.isClosed()) {
			if (this.dbServer != null && this.dbName != null && this.dbUser != null) {
				// 手工连接的
				this.dbConnection = this.createDbAdapter().createDbConnection(this.dbServer, this.dbName, this.dbUser,
						this.dbPassword);
			} else {
				this.dbConnection = this.createDbAdapter().createDbConnection(this.getDbSign());
			}
		}
		return this.dbConnection;
	}

	@Override
	public void setDbConnection(IDbConnection connection) {
		this.dbConnection = connection;
	}

	private String dbServer;
	private String dbName;
	private String dbUser;
	private String dbPassword;

	@Override
	public void connectDb(String dbType, String dbServer, String dbName, String dbUser, String dbPassword)
			throws DbException {
		if (this.dbConnection != null) {
			throw new DbException(I18N.prop("msg_bobas_db_already_connected", this.dbConnection.getURL()));
		}
		this.setDbType(dbType);
		this.dbConnection = this.createDbAdapter().createDbConnection(dbServer, dbName, dbUser, dbPassword);
		this.dbServer = dbServer;
		this.dbName = dbName;
		this.dbUser = dbUser;
		this.dbPassword = dbPassword;
	}

	@Override
	public void connectDb(String dbServer, String dbName, String dbUser, String dbPassword) throws DbException {
		this.connectDb(null, dbServer, dbName, dbUser, dbPassword);
	}

	private IBOAdapter boAdapter;

	@Override
	public IBOAdapter getBOAdapter() {
		if (this.boAdapter == null) {
			this.boAdapter = this.createDbAdapter().createBOAdapter();
		}
		return this.boAdapter;
	}

	@Override
	public void setBOAdapter(IBOAdapter boAdapter) {
		this.boAdapter = boAdapter;
	}

	/**
	 * 打开数据库连接
	 * 
	 * @return true，此次成功打开；false，已经打开
	 * @throws DbException 不能打开
	 */
	@Override
	public boolean openDbConnection() throws DbException {
		try {
			return this.getDbConnection().open();
		} catch (DbException e) {
			// 数据库错误，当前连接无效
			this.setDbConnection(null);// 丢弃当前连接
			throw e;
		}
	}

	@Override
	public void closeDbConnection() throws DbException {
		if (this.dbConnection != null) {
			this.dbConnection.close();
			this.dbConnection = null;
		}
	}

	@Override
	public void dispose() throws RepositoryException {
		try {
			this.closeDbConnection();
		} catch (DbException e) {
			throw new RepositoryException(e);
		}
	}

	/**
	 * 获取服务器时间
	 * 
	 * 不能从服务获取，则返回本地时间
	 */
	@Override
	public DateTime getServerTime() {
		try {
			IBOAdapter adapter = this.getBOAdapter();
			IOperationResult<SingleValue> operationResult = this.fetch(adapter.getServerTimeQuery());
			SingleValue data = operationResult.getResultObjects().firstOrDefault();
			if (data != null) {
				if (data.getValue() instanceof Timestamp) {
					Timestamp date = (Timestamp) data.getValue();
					return new DateTime(date.getTime());
				}
			}
		} catch (Exception e) {
			Logger.log(e);
		}
		return DateTime.getNow();
	}

	/**
	 * 获取对象副本，不填充子项值
	 */
	@Override
	public <T extends IBusinessObjectBase> IOperationResult<T> fetchCopy(T bo) {
		@SuppressWarnings("unchecked")
		Class<T> boType = (Class<T>) bo.getClass();
		return (IOperationResult<T>) this.fetch(bo.getCriteria(), boType);
	}

	/**
	 * 获取对象副本，并填充子项值
	 */
	@Override
	public <T extends IBusinessObjectBase> IOperationResult<T> fetchCopyEx(T bo) {
		@SuppressWarnings("unchecked")
		Class<T> boType = (Class<T>) bo.getClass();
		return (IOperationResult<T>) this.fetchEx(bo.getCriteria(), boType);
	}

	/**
	 * 查询对象值，不填充子项
	 */
	@Override
	public <T extends IBusinessObjectBase> IOperationResult<T> fetch(ICriteria criteria, Class<T> boType) {
		try {
			IBOAdapter adapter = this.getBOAdapter();
			ISqlQuery sqlQuery = adapter.parseSqlQuery(criteria, boType);
			return this.fetch(sqlQuery, boType);
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
	}

	/**
	 * 查询对象值，并填充子项
	 */
	@Override
	public <T extends IBusinessObjectBase> IOperationResult<T> fetchEx(ICriteria criteria, Class<T> boType) {
		OperationResult<T> operationResult = new OperationResult<>();
		try {
			IBusinessObjectBase[] bos = this.myFetchEx(criteria, boType);
			operationResult.addResultObjects(bos);
		} catch (Exception e) {
			operationResult.setError(e);
		}
		return operationResult;
	}

	/**
	 * 查询对象值，不填充子项
	 */
	@Override
	public <T extends IBusinessObjectBase> IOperationResult<T> fetch(ISqlQuery sqlQuery, Class<T> boType) {
		OperationResult<T> operationResult = new OperationResult<>();
		try {
			IBusinessObjectBase[] bos = this.myFetch(sqlQuery, boType);
			operationResult.addResultObjects(bos);
		} catch (Exception e) {
			operationResult.setError(e);
		}
		return operationResult;
	}

	/**
	 * 查询对象值，并填充子项
	 */
	@Override
	public <T extends IBusinessObjectBase> IOperationResult<T> fetchEx(ISqlQuery sqlQuery, Class<T> boType) {
		OperationResult<T> operationResult = new OperationResult<>();
		try {
			IBusinessObjectBase[] bos = this.myFetchEx(sqlQuery, boType);
			operationResult.addResultObjects(bos);
		} catch (Exception e) {
			operationResult.setError(e);
		}
		return operationResult;
	}

	/**
	 * 单值查询，仅返回第一行第一列的值
	 * 
	 * @param sqlQuery 查询语句
	 * @return 操作结果
	 */
	@Override
	public IOperationResult<SingleValue> fetch(ISqlQuery sqlQuery) {
		OperationResult<SingleValue> operationResult = new OperationResult<SingleValue>();
		try {
			IDbDataReader reader = null;
			IDbCommand command = null;
			boolean myOpenedDb = false;// 自己打开的数据库
			try {
				myOpenedDb = this.openDbConnection();// 打开数据库连接
				command = this.getDbConnection().createCommand();
				reader = command.executeReader(sqlQuery);
				if (reader.next()) {
					operationResult.addResultObjects(new SingleValue(reader.getObject(1)));
				}
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
		} catch (Exception e) {
			operationResult.setError(e);
		}
		return operationResult;
	}

	/**
	 * 查询，不填充子项
	 * 
	 * @param sqlQuery 语句
	 * @param bo       对象实例，可基于类型构造新实例
	 * @return 操作结果
	 * @throws Exception
	 */
	private final IBusinessObjectBase[] myFetch(ISqlQuery sqlQuery, Class<?> boType) throws Exception {
		if (boType == null) {
			throw new Exception(I18N.prop("msg_bobas_not_specify_bo_type"));
		}
		if (sqlQuery == null) {
			throw new Exception(I18N.prop("msg_bobas_invalid_sql_query"));
		}
		IBOAdapter adapter = this.getBOAdapter();
		IDbDataReader reader = null;
		IDbCommand command = null;
		boolean myOpenedDb = false;// 自己打开的数据库
		try {
			myOpenedDb = this.openDbConnection();
			command = this.getDbConnection().createCommand();
			reader = command.executeReader(sqlQuery);
			return adapter.parseBOs(reader, boType);
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

	/**
	 * 查询，并填充子项
	 * 
	 * @param criteria 查询
	 * @param boType   填充对象
	 * @return 操作结果
	 * @throws Exception
	 */
	private final IBusinessObjectBase[] myFetchEx(ICriteria criteria, Class<?> boType) throws Exception {
		if (boType == null) {
			throw new Exception(I18N.prop("msg_bobas_not_specify_bo_type"));
		}
		boolean myOpenedDb = false;// 自己打开的数据库
		try {
			myOpenedDb = this.openDbConnection();
			IBOAdapter adapter = this.getBOAdapter();
			ISqlQuery sqlQuery = adapter.parseSqlQuery(criteria, boType);
			IBusinessObjectBase[] mainBOs = this.myFetch(sqlQuery, boType);
			IBusinessObjectBase lastBO = mainBOs.length > 0 ? mainBOs[mainBOs.length - 1] : null;
			this.myFetchEx(mainBOs, criteria);// 加载子项
			// 存在子项过滤状况
			if (!criteria.getChildCriterias().isEmpty() && lastBO != null) {
				ICriteria oCriteria = criteria.clone();// 原始查询
				ArrayList<IBusinessObjectBase> tmpList = new ArrayList<>();
				// 使用对象过滤
				do {
					// 移出可能为空的返回值
					for (IBusinessObjectBase bo : mainBOs) {
						if (bo == null) {
							continue;
						}
						tmpList.add(bo);
					}
					// 满足查询数量或已查询全
					if (mainBOs.length < criteria.getResultCount() || tmpList.size() >= oCriteria.getResultCount()) {
						break;
					}
					// 下组数据的查询条件
					criteria = oCriteria.next(lastBO);
					criteria.setResultCount(oCriteria.getResultCount() - tmpList.size());
					sqlQuery = adapter.parseSqlQuery(criteria, boType);
					mainBOs = this.myFetch(sqlQuery, boType);
					lastBO = mainBOs.length > 0 ? mainBOs[mainBOs.length - 1] : null;
					this.myFetchEx(mainBOs, criteria);// 加载子项
				} while (lastBO != null);
				mainBOs = tmpList.toArray(new IBusinessObjectBase[] {});
			}
			return mainBOs;
		} finally {
			if (myOpenedDb) {
				// 自己开打自己关闭
				this.closeDbConnection();// 关闭数据库连接
			}
		}
	}

	/**
	 * 查询，并填充子项
	 * 
	 * @param sqlQuery 语句
	 * @param boType   填充对象
	 * @return 操作结果
	 * @throws Exception
	 */
	private final IBusinessObjectBase[] myFetchEx(ISqlQuery sqlQuery, Class<?> boType) throws Exception {
		if (boType == null) {
			throw new Exception(I18N.prop("msg_bobas_not_specify_bo_type"));
		}
		boolean myOpenedDb = false;// 自己打开的数据库
		try {
			myOpenedDb = this.openDbConnection();
			IBusinessObjectBase[] mainBOs = this.myFetch(sqlQuery, boType);
			this.myFetchEx(mainBOs, null);// 加载子项
			return mainBOs;
		} finally {
			if (myOpenedDb) {
				// 自己开打自己关闭
				this.closeDbConnection();// 关闭数据库连接
			}
		}
	}

	/**
	 * 查询已知对象的子项
	 * 
	 * @param bos      已知对象
	 * @param criteria 可能存在的子项查询
	 * @throws Exception
	 */
	private final void myFetchEx(IBusinessObjectBase[] bos, ICriteria criteria) throws Exception {
		if (bos == null) {
			return;
		}
		boolean myOpenedDb = false;// 自己打开的数据库
		IDbDataReader reader = null;
		IDbCommand command = null;
		IBOAdapter adapter = this.getBOAdapter();
		try {
			myOpenedDb = this.openDbConnection();
			for (int i = 0; i < bos.length; i++) {
				IBusinessObjectBase bo = bos[i];
				// 遍历BO
				if (!(bo instanceof IManagedFields)) {
					// 不能解析的对象
					continue;
				}
				IManagedFields boFields = ((IManagedFields) bo);
				if (bo instanceof TrackableBase) {
					// 赋值阶段，不跟踪状态变化
					((TrackableBase) bo).setLoading(true);
				}
				for (IFieldData fieldData : boFields.getFields()) {
					// 遍历BO的集合属性
					if (fieldData.getValue() instanceof IBusinessObjects<?, ?>) {
						// 集合类型
						IBusinessObjects<?, ?> listField = (IBusinessObjects<?, ?>) fieldData.getValue();
						Class<?> childBoType = listField.getElementType();
						IChildCriteria tmpCriteria = null;
						if (criteria != null) {
							tmpCriteria = criteria.getChildCriterias().getCriteria(fieldData.getName());
							// 跳过此项加载
							if (tmpCriteria != null && tmpCriteria.isNoChilds()) {
								continue;
							}
						}
						ICriteria childCriteria = listField.getElementCriteria();
						if (childCriteria != null) {
							// 添加子查询条件
							if (tmpCriteria != null) {
								// 设置了此属性的查询
								ICondition tmpCondition = tmpCriteria.getConditions().firstOrDefault();
								if (tmpCondition != null) {
									// 强制使用“与”关系
									if (tmpCondition.getRelationship() != ConditionRelationship.AND) {
										tmpCondition.setRelationship(ConditionRelationship.AND);
									}
								}
								childCriteria = childCriteria.copyFrom(tmpCriteria);
								if (tmpCriteria.getResultCount() > 0) {
									childCriteria.setResultCount(tmpCriteria.getResultCount());
								}
							}
							ISqlQuery childSqlQuery = adapter.parseSqlQuery(childCriteria, childBoType);
							command = this.getDbConnection().createCommand();
							reader = command.executeReader(childSqlQuery);
							if (tmpCriteria != null && !tmpCriteria.getConditions().isEmpty()
									&& tmpCriteria.isIncludingOtherChilds() && reader.isBeforeFirst()) {
								// 有结果，返回全部数据
								childCriteria = listField.getElementCriteria();
								childSqlQuery = adapter.parseSqlQuery(childCriteria, childBoType);
								reader = command.executeReader(childSqlQuery);
							}
							if (tmpCriteria != null && tmpCriteria.isOnlyHasChilds() && !reader.isBeforeFirst()) {
								// 要求带子项返回，但无子项结果
								bos[i] = null;
								break;// 退出当前数据处理
							}
							this.myFetchEx(adapter.parseBOs(reader, listField), tmpCriteria);
						}
					} else if (fieldData instanceof AssociatedFieldDataBase<?>) {
						// 关联对象类型
						AssociatedFieldDataBase<?> assoFieldData = (AssociatedFieldDataBase<?>) fieldData;
						if (!assoFieldData.isAutoLoading()) {
							// 不自动加载数据，则不在处理此字段
							continue;
						}
						Class<?> childBoType = assoFieldData.getValueType();
						// 创建子项查询条件，所有关联的字段
						ICriteria childCriteria = new Criteria();
						boolean breakAssociated = false;// 关联字段不能完成标记
						for (FieldRelation relation : assoFieldData.getAssociations()) {
							IFieldData tmpField = boFields.getField(relation.associatedField);
							if (tmpField == null) {
								// 没有关联的字段，则不在处理此字段
								breakAssociated = true;
								break;
							}
							ICondition condition = childCriteria.getConditions().create();
							condition.setAlias(relation.mappedField);
							condition.setOperation(ConditionOperation.EQUAL);
							condition.setValue(tmpField.getValue());
						}
						if (breakAssociated) {
							// 终止关联，处理下一个字段
							continue;
						}
						ISqlQuery childSqlQuery = adapter.parseSqlQuery(childCriteria, childBoType);
						command = this.getDbConnection().createCommand();
						reader = command.executeReader(childSqlQuery);
						// 填充数据
						IBusinessObjectBase[] cBOs = adapter.parseBOs(reader, childBoType);
						if (cBOs != null & cBOs.length > 0) {
							// 有子项结果
							if (assoFieldData.getAssociationMode() == AssociationMode.ONE_TO_MANY) {
								// 一对多关系
								assoFieldData.setValue(cBOs);
								// 查询填充后对象的子项
								this.myFetchEx(cBOs, null);
							} else if (assoFieldData.getAssociationMode() == AssociationMode.ONE_TO_ONE
									|| assoFieldData.getAssociationMode() == AssociationMode.ONE_TO_ZERO) {
								// 一对一
								IBusinessObjectBase cBO = cBOs[0];// 仅取第一个
								assoFieldData.setValue(cBO);
								// 查询填充后对象的子项
								this.myFetchEx(new IBusinessObjectBase[] { cBO }, null);
							}
						} else {
							// 没有结果
							if (assoFieldData.getAssociationMode() == AssociationMode.ONE_TO_ONE) {
								// 指定了一对一的关系，自动创建新的实例
								IBusinessObjectBase cBO = (IBusinessObjectBase) BOFactory.create()
										.createInstance(childBoType);
								assoFieldData.setValue(cBO);
							}
						}
					}
				}
				if (bo instanceof TrackableBase) {
					// 退出赋值阶段
					((TrackableBase) bo).setLoading(false);
				}
			}
		} finally {
			if (myOpenedDb) {
				// 自己开打自己关闭
				this.closeDbConnection();// 关闭数据库连接
			}
		}
	}

	@Override
	public IOperationResult<IDataTable> query(ISqlQuery sqlQuery) {
		OperationResult<IDataTable> operationResult = new OperationResult<IDataTable>();
		try {
			// 检查SQL
			ISqlScriptInspector inspector = this.createDbAdapter().createSqlInspector();
			if (inspector != null) {
				inspector.check(sqlQuery);
			}
			// 执行SQL
			IDbDataReader reader = null;
			IDbCommand command = null;
			boolean myOpenedDb = false;// 自己打开的数据库
			try {
				myOpenedDb = this.openDbConnection();
				command = this.getDbConnection().createCommand();
				reader = command.executeReader(sqlQuery);
				IDataTable dataTable = reader.toDataTable();
				operationResult.addResultObjects(dataTable);
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
		} catch (Exception e) {
			operationResult.setError(e);
		}
		return operationResult;
	}

	@Override
	public <T extends IBusinessObjectBase> IOperationResult<T> fetch(ISqlStoredProcedure sp, Class<T> boType) {
		try {
			IBOAdapter adapter = this.getBOAdapter();
			ISqlQuery sqlQuery = adapter.parseSqlQuery(sp);
			return this.fetch(sqlQuery, boType);
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
	}

	@Override
	public <T extends IBusinessObjectBase> IOperationResult<T> fetchEx(ISqlStoredProcedure sp, Class<T> boType) {
		try {
			IBOAdapter adapter = this.getBOAdapter();
			ISqlQuery sqlQuery = adapter.parseSqlQuery(sp);
			return this.fetchEx(sqlQuery, boType);
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
	}

	@Override
	public IOperationResult<SingleValue> fetch(ISqlStoredProcedure sp) {
		try {
			IBOAdapter adapter = this.getBOAdapter();
			ISqlQuery sqlQuery = adapter.parseSqlQuery(sp);
			return this.fetch(sqlQuery);
		} catch (Exception e) {
			return new OperationResult<SingleValue>(e);
		}
	}

}
