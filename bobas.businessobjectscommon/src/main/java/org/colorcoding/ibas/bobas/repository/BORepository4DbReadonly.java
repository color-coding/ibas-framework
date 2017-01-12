package org.colorcoding.ibas.bobas.repository;

import java.sql.SQLException;
import java.sql.Timestamp;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.IBusinessObjects;
import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.IChildCriteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.IOperationResult;
import org.colorcoding.ibas.bobas.common.ISqlQuery;
import org.colorcoding.ibas.bobas.common.ISqlStoredProcedure;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.core.BOFactory;
import org.colorcoding.ibas.bobas.core.BOFactoryException;
import org.colorcoding.ibas.bobas.core.BORepositoryBase;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.RepositoryException;
import org.colorcoding.ibas.bobas.core.fields.AssociatedFieldDataBase;
import org.colorcoding.ibas.bobas.core.fields.FieldRelation;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.core.fields.IManageFields;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.IDataTable;
import org.colorcoding.ibas.bobas.data.SingleValue;
import org.colorcoding.ibas.bobas.db.BOParseException;
import org.colorcoding.ibas.bobas.db.DbAdapterFactory;
import org.colorcoding.ibas.bobas.db.DbException;
import org.colorcoding.ibas.bobas.db.IBOAdapter4Db;
import org.colorcoding.ibas.bobas.db.IDbAdapter;
import org.colorcoding.ibas.bobas.db.IDbCommand;
import org.colorcoding.ibas.bobas.db.IDbConnection;
import org.colorcoding.ibas.bobas.db.IDbDataReader;
import org.colorcoding.ibas.bobas.db.SqlScriptsException;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.mapping.AssociationMode;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;
import org.colorcoding.ibas.bobas.util.ArrayList;

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

	private volatile IDbConnection dbConnection = null;

	@Override
	public IDbConnection getDbConnection() throws DbException {
		if (this.dbConnection == null) {
			this.dbConnection = this.createDbAdapter().createDbConnection(this.getDbSign());
		} else if (this.dbConnection.isClosed()) {
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
			throw new DbException(i18n.prop("msg_bobas_db_already_connected", this.dbConnection.getURL()));
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

	private IDbAdapter dbAdapter;

	@Override
	public IDbAdapter createDbAdapter() throws DbException {
		if (this.dbAdapter == null) {
			this.dbAdapter = DbAdapterFactory.createAdapter(this.getDbType());
		}
		return this.dbAdapter;
	}

	public void setDbAdapter(IDbAdapter dbAdapter) throws DbException {
		this.dbAdapter = dbAdapter;
	}

	/**
	 * 打开数据库连接
	 * 
	 * @return true，此次成功打开；false，已经打开
	 * @throws DbException
	 *             不能打开
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
		} catch (Exception e) {
			throw new RepositoryException(e.getMessage(), e);
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
			IBOAdapter4Db adapter4Db = this.createDbAdapter().createBOAdapter();
			IOperationResult<SingleValue> operationResult = this.fetch(adapter4Db.getServerTimeScript());
			SingleValue data = operationResult.getResultObjects().firstOrDefault();
			if (data != null) {
				if (data.getValue() instanceof Timestamp) {
					Timestamp date = (Timestamp) data.getValue();
					return new DateTime(date.getTime());
				}
			}
		} catch (Exception e) {
			RuntimeLog.log(e);
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
			IBOAdapter4Db adapter4Db = this.createDbAdapter().createBOAdapter();
			ISqlQuery sqlQuery = adapter4Db.parseSqlQuery(criteria, boType);
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
	 * @param sqlQuery
	 *            查询语句
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
	 * @param sqlQuery
	 *            语句
	 * @param bo
	 *            对象实例，可基于类型构造新实例
	 * @return 操作结果
	 * @throws RepositoryException
	 * @throws DbException
	 * @throws SQLException
	 * @throws BOParseException
	 */
	private final IBusinessObjectBase[] myFetch(ISqlQuery sqlQuery, Class<?> boType)
			throws RepositoryException, DbException, SQLException, BOParseException {
		if (boType == null) {
			throw new RepositoryException(i18n.prop("msg_bobas_not_specify_bo_type"));
		}
		if (sqlQuery == null) {
			throw new RepositoryException(i18n.prop("msg_bobas_invalid_sql_query"));
		}
		IBOAdapter4Db adapter4Db = this.createDbAdapter().createBOAdapter();
		IDbDataReader reader = null;
		IDbCommand command = null;
		boolean myOpenedDb = false;// 自己打开的数据库
		try {
			myOpenedDb = this.openDbConnection();
			command = this.getDbConnection().createCommand();
			reader = command.executeReader(sqlQuery);
			return adapter4Db.parseBOs(reader, boType);
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
	 * @param criteria
	 *            查询
	 * @param boType
	 *            填充对象
	 * @return 操作结果
	 * @throws RepositoryException
	 * @throws DbException
	 * @throws SQLException
	 * @throws BOParseException
	 * @throws SqlScriptsException
	 * @throws ClassNotFoundException
	 * @throws BOFactoryException
	 */
	private final IBusinessObjectBase[] myFetchEx(ICriteria criteria, Class<?> boType)
			throws RepositoryException, DbException, SQLException, BOParseException, SqlScriptsException,
			ClassNotFoundException, BOFactoryException {
		if (boType == null) {
			throw new RepositoryException(i18n.prop("msg_bobas_not_specify_bo_type"));
		}
		boolean myOpenedDb = false;// 自己打开的数据库
		try {
			myOpenedDb = this.openDbConnection();
			IBOAdapter4Db adapter4Db = this.createDbAdapter().createBOAdapter();
			ISqlQuery sqlQuery = adapter4Db.parseSqlQuery(criteria, boType);
			IBusinessObjectBase[] mainBOs = this.myFetch(sqlQuery, boType);
			this.myFetchEx(mainBOs, criteria);// 加载子项
			if (criteria.getChildCriterias().size() > 0) {
				// 存在子项过滤状况，移出可能为空的返回值
				ArrayList<IBusinessObjectBase> tmpList = new ArrayList<>();
				for (IBusinessObjectBase bo : mainBOs) {
					if (bo == null) {
						continue;
					}
					tmpList.add(bo);
				}
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
	 * @param sqlQuery
	 *            语句
	 * @param boType
	 *            填充对象
	 * @return 操作结果
	 * @throws RepositoryException
	 * @throws DbException
	 * @throws SQLException
	 * @throws BOParseException
	 * @throws SqlScriptsException
	 * @throws ClassNotFoundException
	 * @throws BOFactoryException
	 */
	private final IBusinessObjectBase[] myFetchEx(ISqlQuery sqlQuery, Class<?> boType)
			throws RepositoryException, DbException, SQLException, BOParseException, SqlScriptsException,
			ClassNotFoundException, BOFactoryException {
		if (boType == null) {
			throw new RepositoryException(i18n.prop("msg_bobas_not_specify_bo_type"));
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
	 * @param bos
	 *            已知对象
	 * @param criteria
	 *            可能存在的子项查询
	 * @throws DbException
	 * @throws SqlScriptsException
	 * @throws BOParseException
	 * @throws ClassNotFoundException
	 * @throws BOFactoryException
	 */
	private final void myFetchEx(IBusinessObjectBase[] bos, ICriteria criteria)
			throws DbException, SqlScriptsException, BOParseException, ClassNotFoundException, BOFactoryException {
		boolean myOpenedDb = false;// 自己打开的数据库
		IDbDataReader reader = null;
		IDbCommand command = null;
		IBOAdapter4Db adapter4Db = this.createDbAdapter().createBOAdapter();
		try {
			if (bos == null) {
				return;
			}
			myOpenedDb = this.openDbConnection();
			for (int i = 0; i < bos.length; i++) {
				IBusinessObjectBase bo = bos[i];
				// 遍历BO
				if (!(bo instanceof IManageFields)) {
					// 不能解析的对象
					continue;
				}
				IManageFields boFields = ((IManageFields) bo);
				for (IFieldData fieldData : boFields.getFields()) {
					// 遍历BO的集合属性
					if (fieldData.getValue() instanceof IBusinessObjects<?, ?>) {
						// 集合类型
						IBusinessObjects<?, ?> listField = (IBusinessObjects<?, ?>) fieldData.getValue();
						Class<?> childBoType = listField.getElementType();
						ICriteria childCriteria = listField.getElementCriteria();
						if (childCriteria != null) {
							// 获取到了子项查询
							IChildCriteria tmpCriteria = null;
							if (criteria != null) {
								// 添加子查询条件
								tmpCriteria = criteria.getChildCriterias().getCriteria(fieldData.getName());
								if (tmpCriteria != null) {
									// 设置了此属性的查询
									childCriteria = childCriteria.copyFrom(tmpCriteria);
								}
							}
							ISqlQuery childSqlQuery = adapter4Db.parseSqlQuery(childCriteria, childBoType);
							command = this.getDbConnection().createCommand();
							reader = command.executeReader(childSqlQuery);
							IBusinessObjectBase[] childs = adapter4Db.parseBOs(reader, listField);
							if (tmpCriteria != null && (childs == null || childs.length == 0)) {
								// 没有匹配的子项数据
								if (tmpCriteria.getFatherMustHasResluts()) {
									// 要求父项必须有结果，但此时没有结果，则设置返回数组为空
									bos[i] = null;
									break;// 退出当前数据处理
								}
							}
							this.myFetchEx(childs, tmpCriteria);
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
							condition.setOperation(ConditionOperation.co_EQUAL);
							condition.setCondVal(tmpField.getValue());
						}
						if (breakAssociated) {
							// 终止关联，处理下一个字段
							continue;
						}
						ISqlQuery childSqlQuery = adapter4Db.parseSqlQuery(childCriteria, childBoType);
						command = this.getDbConnection().createCommand();
						reader = command.executeReader(childSqlQuery);
						// 填充数据
						IBusinessObjectBase[] cBOs = adapter4Db.parseBOs(reader, childBoType);
						if (cBOs != null & cBOs.length > 0) {
							// 有子项结果
							if (assoFieldData.getAssociationMode() == AssociationMode.OneToMany) {
								// 一对多关系
								assoFieldData.setValue(cBOs);
								// 查询填充后对象的子项
								this.myFetchEx(cBOs, null);
							} else if (assoFieldData.getAssociationMode() == AssociationMode.OneToOne
									|| assoFieldData.getAssociationMode() == AssociationMode.OneToZero) {
								// 一对一
								IBusinessObjectBase cBO = cBOs[0];// 仅取第一个
								assoFieldData.setValue(cBO);
								// 查询填充后对象的子项
								this.myFetchEx(new IBusinessObjectBase[] { cBO }, null);
							}
						} else {
							// 没有结果
							if (assoFieldData.getAssociationMode() == AssociationMode.OneToOne) {
								// 指定了一对一的关系，自动创建新的实例
								IBusinessObjectBase cBO = (IBusinessObjectBase) BOFactory.create()
										.createInstance(childBoType);
								assoFieldData.setValue(cBO);
							}
						}
					}
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
			IBOAdapter4Db adapter4Db = this.createDbAdapter().createBOAdapter();
			ISqlQuery sqlQuery = adapter4Db.parseSqlQuery(sp);
			return this.fetch(sqlQuery, boType);
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
	}

	@Override
	public <T extends IBusinessObjectBase> IOperationResult<T> fetchEx(ISqlStoredProcedure sp, Class<T> boType) {
		try {
			IBOAdapter4Db adapter4Db = this.createDbAdapter().createBOAdapter();
			ISqlQuery sqlQuery = adapter4Db.parseSqlQuery(sp);
			return this.fetchEx(sqlQuery, boType);
		} catch (Exception e) {
			return new OperationResult<>(e);
		}
	}

	@Override
	public IOperationResult<SingleValue> fetch(ISqlStoredProcedure sp) {
		try {
			IBOAdapter4Db adapter4Db = this.createDbAdapter().createBOAdapter();
			ISqlQuery sqlQuery = adapter4Db.parseSqlQuery(sp);
			return this.fetch(sqlQuery);
		} catch (Exception e) {
			return new OperationResult<SingleValue>(e);
		}
	}
}
