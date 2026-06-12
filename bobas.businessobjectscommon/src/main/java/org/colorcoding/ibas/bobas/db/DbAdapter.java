package org.colorcoding.ibas.bobas.db;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.BOFactory;
import org.colorcoding.ibas.bobas.bo.IBOUserFields;
import org.colorcoding.ibas.bobas.bo.IBusinessObjects;
import org.colorcoding.ibas.bobas.bo.UserFieldsFactory;
import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Decimals;
import org.colorcoding.ibas.bobas.common.Enums;
import org.colorcoding.ibas.bobas.common.IChildCriteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.ISort;
import org.colorcoding.ibas.bobas.common.Result;
import org.colorcoding.ibas.bobas.common.SortType;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.FieldedObject;
import org.colorcoding.ibas.bobas.core.IFieldedObject;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.DataConvert;
import org.colorcoding.ibas.bobas.data.DataTable;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.IDataTable;
import org.colorcoding.ibas.bobas.data.IDataTableColumn;
import org.colorcoding.ibas.bobas.data.IDataTableRow;
import org.colorcoding.ibas.bobas.data.KeyText;
import org.colorcoding.ibas.bobas.data.KeyValue;
import org.colorcoding.ibas.bobas.data.List;
import org.colorcoding.ibas.bobas.i18n.I18N;

/**
 * 数据库适配器
 */
public abstract class DbAdapter {

	private int batchCount = -1;

	public final int getBatchCount() {
		if (this.batchCount < 0) {
			this.batchCount = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_DB_STATEMENT_BATCH_COUNT, 512);
		}
		return this.batchCount;
	}

	private String companyId;

	protected final String getCompanyId() {
		if (this.companyId == null) {
			this.companyId = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_COMPANY, "CC");
		}
		return this.companyId;
	}

	private Boolean noUserTransactionSP;

	public final Boolean isNoUserTransactionSP() {
		if (this.noUserTransactionSP == null) {
			this.noUserTransactionSP = MyConfiguration
					.getConfigValue(MyConfiguration.CONFIG_ITEM_DB_NO_USER_TRANSACTION_SP, false);
		}
		return noUserTransactionSP;
	}

	protected final void setNoUserTransactionSP(Boolean noUserTransactionSP) {
		this.noUserTransactionSP = noUserTransactionSP;
	}

	private Boolean registerUserFields;

	public Boolean isRegisterUserFields() {
		if (this.registerUserFields == null) {
			this.registerUserFields = MyConfiguration
					.getConfigValue(MyConfiguration.CONFIG_ITEM_AUTO_REGISTER_USER_FIELDS, true);
		}
		return registerUserFields;
	}

	public void setRegisterUserFields(Boolean autoRegisterUserFields) {
		this.registerUserFields = autoRegisterUserFields;
	}

	/**
	 * 创建数据库连接
	 *
	 * @param server   服务器地址
	 * @param dbName   数据库名称
	 * @param userName 用户名
	 * @param userPwd  密码
	 * @return 数据库连接
	 */
	public abstract Connection createConnection(String server, String dbName, String userName, String userPwd);

	/**
	 * 解析数据
	 *
	 * @param <T>       对象类型
	 * @param boType    对象类型（Result、KeyText、KeyValue、BusinessObject、SPValues）
	 * @param resultSet 结果集
	 * @return 解析后的数据列表
	 * @throws SQLException 数据库访问异常
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> parsingDatas(Class<?> boType, ResultSet resultSet) throws SQLException {
		T data = null;
		ArrayList<T> datas = new ArrayList<>();
		IPropertyInfo<?>[] orderProperties = null;
		while (resultSet.next()) {
			if (boType.equals(Result.class)) {
				datas.add((T) new Result(resultSet.getInt(1), resultSet.getString(2)));
			} else if (boType.equals(KeyText.class)) {
				datas.add((T) new KeyText(resultSet.getString(1), resultSet.getString(2)));
			} else if (boType.equals(KeyValue.class)) {
				datas.add((T) new KeyValue(resultSet.getString(1), resultSet.getObject(2)));
			} else if (IFieldedObject.class.isAssignableFrom(boType)) {
				if (orderProperties == null) {
					ResultSetMetaData metaData = resultSet.getMetaData();
					ArrayList<String> columnNames = new ArrayList<>(metaData.getColumnCount());
					for (int i = 1; i <= metaData.getColumnCount(); i++) {
						columnNames.add(metaData.getColumnLabel(i));
					}
					DbField dbField;
					String columnName;
					IPropertyInfo<?> propertyInfo;
					orderProperties = new IPropertyInfo<?>[columnNames.size()];
					List<IPropertyInfo<?>> propertyInfos = BOFactory.propertyInfos(boType);
					for (int i = 0; i < propertyInfos.size(); i++) {
						propertyInfo = propertyInfos.get(i);
						if (propertyInfo == null) {
							continue;
						}
						dbField = propertyInfo.getAnnotation(DbField.class);
						if (dbField == null || Strings.isNullOrEmpty(dbField.name())) {
							continue;
						}
						for (int j = 0; j < columnNames.size(); j++) {
							columnName = columnNames.get(j);
							if (columnName == null) {
								continue;
							}
							if (Strings.equalsIgnoreCase(columnName, dbField.name())) {
								orderProperties[j] = propertyInfo;
								columnNames.set(j, null);
							}
						}
					}
					if (columnNames.contains(c -> c != null)) {
						// 可能存在用户字段
						if (this.isRegisterUserFields() && IBOUserFields.class.isAssignableFrom(boType)) {
							for (int j = 0; j < columnNames.size(); j++) {
								columnName = columnNames.get(j);
								if (columnName == null) {
									continue;
								}
								if (Strings.startsWith(columnName, IBOUserFields.USER_FIELD_PREFIX_SIGN)) {
									orderProperties[j] = UserFieldsFactory.createManager().registerUserField(boType,
											columnName, this.dbFieldTypeOf(metaData.getColumnType(j + 1)));
									columnNames.set(j, null);
								}
							}
						}
					}
					propertyInfos = null;
					propertyInfo = null;
					columnNames = null;
					columnName = null;
					metaData = null;
					dbField = null;
				}
				data = BOFactory.newInstance(boType);
				data = this.setProperties(data, resultSet, orderProperties);
				if (data instanceof FieldedObject) {
					((FieldedObject) data).markOld();
				}
				datas.add(data);
			}
		}
		return datas;
	}

	/**
	 * 设置对象属性值（加载中模式设置，完成后自动markOld）
	 *
	 * @param <T>             类型
	 * @param data            对象实例
	 * @param resultSet       数据集
	 * @param orderProperties 排序的属性（按结果集列顺序）
	 * @return 设置属性后的对象实例
	 * @throws SQLException 数据库访问异常
	 */
	public <T> T setProperties(T data, ResultSet resultSet, IPropertyInfo<?>[] orderProperties) throws SQLException {
		if (data instanceof IFieldedObject) {
			IFieldedObject boData = (IFieldedObject) data;
			boData.setLoading(true);
			IPropertyInfo<?> propertyInfo;
			for (int i = 0; i < orderProperties.length; i++) {
				propertyInfo = orderProperties[i];
				if (propertyInfo == null) {
					continue;
				}
				boData.setProperty(propertyInfo, this.parsingValue(resultSet, i + 1, propertyInfo.getValueType()));
			}
			boData.setLoading(false);
		}
		return data;
	}

	/**
	 * 解析值（String/BigDecimal/DateTime类型数据库null值返回null，数值类型返回0，枚举按名称匹配）
	 *
	 * @param resultSet   结果集
	 * @param columnIndex 列索引（从1开始）
	 * @param dataType    目标类型
	 * @return 解析后的值
	 * @throws SQLException 数据库访问异常
	 */
	public Object parsingValue(ResultSet resultSet, int columnIndex, Class<?> dataType) throws SQLException {
		if (dataType == Integer.class) {
			return resultSet.getInt(columnIndex);
		} else if (dataType == String.class) {
			String value = resultSet.getString(columnIndex);
			return value == null ? null : Strings.valueOf(value);
		} else if (dataType == BigDecimal.class) {
			BigDecimal value = resultSet.getBigDecimal(columnIndex);
			return value == null ? null : Decimals.valueOf(value);
		} else if (dataType == Double.class) {
			return resultSet.getDouble(columnIndex);
		} else if (dataType == DateTime.class) {
			Date value = resultSet.getDate(columnIndex);
			return value == null ? null : DateTimes.valueOf(value);
		} else if (dataType == Short.class) {
			return resultSet.getShort(columnIndex);
		} else if (dataType == Long.class) {
			return resultSet.getLong(columnIndex);
		} else if (dataType != null && dataType.isEnum()) {
			return Enums.valueOf(dataType, resultSet.getString(columnIndex));
		} else if (dataType == Float.class) {
			return resultSet.getFloat(columnIndex);
		} else if (dataType == Character.class) {
			String value = resultSet.getString(columnIndex);
			return value == null ? 0 : value.charAt(0);
		} else if (dataType == Boolean.class) {
			return resultSet.getBoolean(columnIndex);
		} else {
			return DataConvert.convert(dataType, resultSet.getObject(columnIndex));
		}
	}

	/**
	 * 解析数据集到表格（自动去重字符串/日期/小数值以节省内存）
	 *
	 * @param resultSet 数据集（null或空结果集返回空DataTable）
	 * @return 数据表格
	 * @throws SQLException           数据库访问异常
	 * @throws ClassNotFoundException 列类型无法识别时抛出
	 */
	public IDataTable parsingDatas(ResultSet resultSet) throws SQLException, ClassNotFoundException {
		if (resultSet == null || resultSet.getMetaData().getColumnCount() <= 0) {
			return new DataTable();
		}
		String name;
		IDataTableColumn dtColumn;
		DataTable dataTable = new DataTable();
		ResultSetMetaData metaData = resultSet.getMetaData();
		// 设置表名
		dataTable.setName(metaData.getTableName(1));
		// 创建列
		for (int i = 1; i <= metaData.getColumnCount(); i++) {
			dtColumn = dataTable.getColumns().create();
			name = metaData.getColumnName(i);
			if (Strings.isNullOrEmpty(name)) {
				name = String.format("col_%s", i);
			}
			dtColumn.setName(name);
			if (!dtColumn.getName().equalsIgnoreCase(metaData.getColumnLabel(i))) {
				dtColumn.setDescription(metaData.getColumnLabel(i));
			}
			dtColumn.setDataType(Class.forName(metaData.getColumnClassName(i)));
		}
		metaData = null;
		dtColumn = null;
		name = null;
		// 按列构建值缓存（压缩内存消耗，字符串/日期/小数等引用类型去重）
		int columnCount = dataTable.getColumns().size();
		@SuppressWarnings("unchecked")
		Map<Object, Object>[] columnCaches = new Map[columnCount];
		for (int i = 0; i < columnCount; i++) {
			Class<?> dataType = dataTable.getColumns().get(i).getDataType();
			// 仅对引用类型建立缓存（基本类型包装类由JVM缓存，无需去重）
			if (dataType == String.class || dataType == DateTime.class
					|| dataType == BigDecimal.class) {
				columnCaches[i] = new java.util.HashMap<>(64);
			}
		}
		// 添加行数据
		IDataTableRow row;
		Object value;
		while (resultSet.next()) {
			row = dataTable.getRows().create();
			// 行的每列赋值
			for (int i = 0; i < columnCount; i++) {
				value = this.parsingValue(resultSet, i + 1, dataTable.getColumns().get(i).getDataType());
				if (value != null && columnCaches[i] != null) {
					// 使用列级HashMap缓存去重，O(1)查找替代原线性扫描
					Object cached = columnCaches[i].get(value);
					if (cached != null) {
						value = cached;
					} else {
						columnCaches[i].put(value, value);
					}
				}
				row.setValue(i, value);
			}
		}
		row = null;
		value = null;
		columnCaches = null;
		return dataTable;
	}

	/**
	 * 返回数据库字段类型
	 *
	 * @param sqlType java.sql.Types中的SQL类型常量
	 * @return 对应的DataType
	 */
	public DataType dbFieldTypeOf(int sqlType) {
		if (java.sql.Types.VARCHAR == sqlType || java.sql.Types.NVARCHAR == sqlType
				|| java.sql.Types.LONGNVARCHAR == sqlType || java.sql.Types.LONGVARCHAR == sqlType
				|| java.sql.Types.CHAR == sqlType || java.sql.Types.NCHAR == sqlType) {
			return DataType.ALPHANUMERIC;
		} else if (java.sql.Types.CLOB == sqlType || java.sql.Types.NCLOB == sqlType) {
			return DataType.MEMO;
		} else if (java.sql.Types.DECIMAL == sqlType || java.sql.Types.DOUBLE == sqlType
				|| java.sql.Types.FLOAT == sqlType || java.sql.Types.REAL == sqlType
				|| java.sql.Types.NUMERIC == sqlType) {
			return DataType.DECIMAL;
		} else if (java.sql.Types.DATE == sqlType || java.sql.Types.TIMESTAMP == sqlType
				|| java.sql.Types.TIMESTAMP_WITH_TIMEZONE == sqlType || java.sql.Types.TIME == sqlType
				|| java.sql.Types.TIME_WITH_TIMEZONE == sqlType) {
			return DataType.DATE;
		} else if (java.sql.Types.INTEGER == sqlType || java.sql.Types.SMALLINT == sqlType
				|| java.sql.Types.TINYINT == sqlType || java.sql.Types.BIGINT == sqlType) {
			return DataType.NUMERIC;
		} else if (java.sql.Types.BIT == sqlType || java.sql.Types.BOOLEAN == sqlType
				|| java.sql.Types.BINARY == sqlType || java.sql.Types.VARBINARY == sqlType
				|| java.sql.Types.LONGVARBINARY == sqlType || java.sql.Types.BLOB == sqlType) {
			return DataType.BYTES;
		}
		return DataType.UNKNOWN;
	}

	/**
	 * 返回SQL类型常量
	 *
	 * @param type 数据字段类型
	 * @return java.sql.Types中的常量
	 */
	public int sqlTypeOf(DataType type) {
		if (type == DataType.ALPHANUMERIC) {
			return java.sql.Types.VARCHAR;
		} else if (type == DataType.DECIMAL) {
			return java.sql.Types.DECIMAL;
		} else if (type == DataType.DATE) {
			return java.sql.Types.DATE;
		} else if (type == DataType.NUMERIC) {
			return java.sql.Types.INTEGER;
		} else if (type == DataType.BYTES) {
			return java.sql.Types.VARBINARY;
		} else if (type == DataType.MEMO) {
			return java.sql.Types.LONGVARCHAR;
		}
		return java.sql.Types.OTHER;
	}

	/**
	 * 返回SQL类型
	 * 
	 * @param type  数据字段类型
	 * @param value 当前值
	 * @return java.sql.Types中的常量
	 */
	public int sqlTypeOf(DataType type, Object value) {
		return this.sqlTypeOf(type);
	}

	/**
	 * 转化查询（属性名称、属性类型），criteria为null时创建新实例
	 *
	 * @param criteria 待处理查询
	 * @param boType   目标类型
	 * @return 转换后的查询条件（克隆副本）
	 */
	public ICriteria convert(ICriteria criteria, Class<?> boType) {
		return this.convert(criteria, BOFactory.propertyInfos(boType));
	}

	/**
	 * 转化查询（属性名称、属性类型），criteria为null时创建新实例
	 *
	 * @param criteria      待处理查询
	 * @param propertyInfos 对象属性
	 * @return 转换后的查询条件（克隆副本）
	 */
	public ICriteria convert(ICriteria criteria, List<IPropertyInfo<?>> propertyInfos) {
		if (criteria == null) {
			criteria = new Criteria();
		} else {
			criteria = criteria.clone();
		}
		Object tmpValue;
		DbField dbField;
		for (IPropertyInfo<?> propertyInfo : propertyInfos) {
			for (ICondition condition : criteria.getConditions()) {
				if (Strings.equalsIgnoreCase(condition.getAlias(), propertyInfo.getName())) {
					// 属性名称转数据库字段
					dbField = propertyInfo.getAnnotation(DbField.class);
					if (dbField != null) {
						condition.setAlias(dbField.name());
					}
					if (propertyInfo.getValueType() != null) {
						condition.setAliasDataType(DataType.valueOf(propertyInfo.getValueType()));
						// 枚举类型的转值
						if (propertyInfo.getValueType().isEnum()) {
							tmpValue = Enums.valueOf(propertyInfo.getValueType(), condition.getValue());
							if (tmpValue != null) {
								condition.setValue(Enums.annotationValue(tmpValue));
							}
						}
					}
				}
				if (Strings.equalsIgnoreCase(condition.getComparedAlias(), propertyInfo.getName())) {
					// 属性名称转数据库字段
					dbField = propertyInfo.getAnnotation(DbField.class);
					if (dbField != null) {
						condition.setComparedAlias(dbField.name());
					}
				}
			}
			for (ISort sort : criteria.getSorts()) {
				if (Strings.equalsIgnoreCase(sort.getAlias(), propertyInfo.getName())) {
					// 属性名称转数据库字段
					dbField = propertyInfo.getAnnotation(DbField.class);
					if (dbField != null) {
						sort.setAlias(dbField.name());
					}
				}
			}
			// 不处理属性集合（待被使用时处理）
			if (criteria.getChildCriterias().size() < 0) {
				for (IChildCriteria item : criteria.getChildCriterias()) {
					if (Strings.equalsIgnoreCase(item.getPropertyPath(), propertyInfo.getName())) {
						if (IBusinessObjects.class.isAssignableFrom(propertyInfo.getValueType())) {
							// 属性是对象集合
							if (propertyInfo.getValueType().isInterface()) {
								for (Type type : propertyInfo.getValueType().getGenericInterfaces()) {
									if (type instanceof ParameterizedType) {
										ParameterizedType pType = (ParameterizedType) type;
										// 获取实际的类型参数
										this.convert(item,
												BOFactory.loadClass(pType.getActualTypeArguments()[0].getTypeName()));
										break;
									}
								}
							} else {
								Type type = propertyInfo.getValueType().getGenericSuperclass();
								if (type instanceof ParameterizedType) {
									ParameterizedType pType = (ParameterizedType) type;
									// 获取实际的类型参数
									this.convert(item,
											BOFactory.loadClass(pType.getActualTypeArguments()[0].getTypeName()));
								}
							}
						} else {
							this.convert(item, propertyInfo.getValueType());
						}
					}
				}
			}
		}
		return criteria;
	}

	public String identifier() {
		return "\"";
	}

	public String separation() {
		return ", ";
	}

	public String escape() {
		return "\\";
	}

	public String where() {
		return "WHERE";
	}

	/**
	 * 处理特殊字符（在目标字符串中转义指定字符和转义符自身）
	 *
	 * @param target   目标字符串
	 * @param specials 待处理的特殊字符
	 * @return 转义后的字符串（target为空时原样返回）
	 */
	public String escape(String target, char... specials) {
		if (Strings.isNullOrEmpty(target)) {
			return target;
		}
		if (Strings.isNullOrEmpty(this.escape())) {
			return target;
		}
		char[] sChar = Arrays.copyOf(specials, specials.length + 1);
		sChar[sChar.length - 1] = this.escape().charAt(0);
		StringBuilder builder = new StringBuilder(target.length() + sChar.length * (this.escape().length() + 2));
		for (char tItem : target.toCharArray()) {
			for (char sItem : sChar) {
				if (tItem == sItem) {
					builder.append(this.escape());
				}
			}
			builder.append(tItem);
		}
		return builder.toString();
	}

	/**
	 * 语句-事务通知存储过程
	 *
	 * @return EXEC SQL字符串
	 */
	final String sp_transaction_notification() {
		return this.parsingStoredProcedure(Strings.format("%s_SP_TRANSACTION_NOTIFICATION", this.getCompanyId()),
				new String[this.isNoUserTransactionSP() ? 5 : 6]);
	}

	/**
	 * 语句-查询（无条件）
	 *
	 * @param boType 对象类型
	 * @return SELECT SQL字符串
	 */
	public final String parsingSelect(Class<?> boType) {
		return this.parsingSelect(boType, new Criteria());
	}

	/**
	 * 语句-查询（条件集合）
	 *
	 * @param boType     对象类型
	 * @param conditions 查询条件（null时查询全部）
	 * @return SELECT SQL字符串
	 */
	public final String parsingSelect(Class<?> boType, Collection<ICondition> conditions) {
		ICriteria criteria = new Criteria();
		if (conditions != null) {
			for (ICondition item : conditions) {
				criteria.getConditions().add(item);
			}
		}
		return this.parsingSelect(boType, criteria);
	}

	/**
	 * 语句-查询（SPValues类型生成存储过程调用）
	 *
	 * @param boType   对象类型
	 * @param criteria 查询条件
	 * @return SELECT SQL字符串或EXEC SQL字符串
	 */
	public final String parsingSelect(Class<?> boType, ICriteria criteria) {
		if (SPValues.class.isAssignableFrom(boType)) {
			// 存储过程赋值对象
			DbProcedure procedure = boType.getAnnotation(DbProcedure.class);
			if (procedure == null) {
				throw new RuntimeException(I18N.prop("msg_bobas_value_can_not_be_resolved", boType.toString()));
			}
			return this.parsingStoredProcedure(procedure.name(), new String[criteria.getConditions().size()]);
		}
		return this.parsingSelect(boType, criteria, IDbTableLock.class.isAssignableFrom(boType));
	}

	private Map<Class<?>, String> tables = new ConcurrentHashMap<>(128);

	/**
	 * 返回对象类型对应的表名（从@DbTable或@DbField注解获取，支持${Variable}变量替换）
	 *
	 * @param boType 对象类型
	 * @return 表名称（找不到时抛RuntimeException）
	 */
	public String table(Class<?> boType) {
		if (this.tables.containsKey(boType)) {
			return this.tables.get(boType);
		}
		DbTable dbTable = boType.getAnnotation(DbTable.class);
		if (dbTable != null) {
			this.tables.put(boType, MyConfiguration.applyVariables(dbTable.name()));
			return this.table(boType);
		}
		DbField dbField;
		for (IPropertyInfo<?> item : BOFactory.propertyInfos(boType)) {
			dbField = item.getAnnotation(DbField.class);
			if (dbField != null && !Strings.isNullOrEmpty(dbField.table())) {
				this.tables.put(boType, MyConfiguration.applyVariables(dbField.table()));
				return this.table(boType);
			}
		}
		throw new RuntimeException(I18N.prop("msg_bobas_not_found_bo_table", boType.toString()));
	}

	/**
	 * 语句-查询（带锁选项）
	 *
	 * @param boType   对象类型
	 * @param criteria 查询条件
	 * @param withLock 是否加行锁
	 * @return SELECT SQL字符串
	 */
	public String parsingSelect(Class<?> boType, ICriteria criteria, boolean withLock) {
		StringBuilder stringBuilder = new StringBuilder(
				(3 + criteria.getConditions().size() + criteria.getSorts().size()) * 32);
		stringBuilder.append("SELECT");
		if (criteria.getResultCount() > 0) {
			stringBuilder.append(" ");
			stringBuilder.append("TOP");
			stringBuilder.append(" ");
			stringBuilder.append(criteria.getResultCount());
		}
		stringBuilder.append(" ");
		stringBuilder.append("*");
		stringBuilder.append(" ");
		stringBuilder.append("FROM");
		stringBuilder.append(" ");
		stringBuilder.append(this.identifier());
		stringBuilder.append(this.table(boType));
		stringBuilder.append(this.identifier());
		if (withLock) {
			stringBuilder.append(" ");
			stringBuilder.append("WITH (ROWLOCK, UPDLOCK)");
		}
		if (criteria.getConditions().size() > 0) {
			stringBuilder.append(" ");
			stringBuilder.append(this.where());
			stringBuilder.append(" ");
			stringBuilder.append(this.parsingWhere(criteria.getConditions()));
		}
		if (criteria.getSorts().size() > 0) {
			stringBuilder.append(" ");
			stringBuilder.append("ORDER BY");
			stringBuilder.append(" ");
			stringBuilder.append(this.parsingOrder(criteria.getSorts()));
		}
		return stringBuilder.toString();
	}

	/**
	 * 语句-类型转换（CAST字段为目标数据库类型）
	 *
	 * @param type  目标数据库字段类型
	 * @param alias 字段别名
	 * @return CAST表达式字符串
	 */
	public String castAs(DataType type, String alias) {
		StringBuilder stringBuilder = new StringBuilder(64);
		if (type == DataType.ALPHANUMERIC) {
			stringBuilder.append("CAST");
			stringBuilder.append("(");
			stringBuilder.append(this.identifier());
			stringBuilder.append(alias);
			stringBuilder.append(this.identifier());
			stringBuilder.append(" ");
			stringBuilder.append("AS");
			stringBuilder.append(" ");
			stringBuilder.append("NVARCHAR");
			stringBuilder.append(")");
		} else if (type == DataType.DATE) {
			stringBuilder.append("CAST");
			stringBuilder.append("(");
			stringBuilder.append(this.identifier());
			stringBuilder.append(alias);
			stringBuilder.append(this.identifier());
			stringBuilder.append(" ");
			stringBuilder.append("AS");
			stringBuilder.append(" ");
			stringBuilder.append("DATETIME");
			stringBuilder.append(")");
		} else if (type == DataType.NUMERIC) {
			stringBuilder.append("CAST");
			stringBuilder.append("(");
			stringBuilder.append(this.identifier());
			stringBuilder.append(alias);
			stringBuilder.append(this.identifier());
			stringBuilder.append(" ");
			stringBuilder.append("AS");
			stringBuilder.append(" ");
			stringBuilder.append("INT");
			stringBuilder.append(")");
			return stringBuilder.toString();
		} else if (type == DataType.DECIMAL) {
			stringBuilder.append("CAST");
			stringBuilder.append("(");
			stringBuilder.append(this.identifier());
			stringBuilder.append(alias);
			stringBuilder.append(this.identifier());
			stringBuilder.append(" ");
			stringBuilder.append("AS");
			stringBuilder.append(" ");
			stringBuilder.append("NUMERIC(19, 6)");
			stringBuilder.append(")");
		} else {
			stringBuilder.append(this.identifier());
			stringBuilder.append(alias);
			stringBuilder.append(this.identifier());
		}
		return stringBuilder.toString();
	}

	/**
	 * 语句-排序方向
	 *
	 * @param type 排序类型
	 * @return SQL排序关键字（ASC/DESC，未知类型抛RuntimeException）
	 */
	public String parsing(SortType type) {
		if (type == SortType.ASCENDING) {
			return "ASC";
		} else if (type == SortType.DESCENDING) {
			return "DESC";
		}
		throw new RuntimeException(I18N.prop("msg_bobas_value_can_not_be_resolved", type.toString()));
	}

	/**
	 * 语句-条件关系（AND/OR）
	 *
	 * @param value 条件关系
	 * @return SQL关系关键字
	 */
	public String parsing(ConditionRelationship value) {
		if (value == ConditionRelationship.AND) {
			return "AND";
		} else if (value == ConditionRelationship.OR) {
			return "OR";
		}
		return Strings.VALUE_EMPTY;
	}

	/**
	 * 语句-条件操作（=/>/</LIKE/IN等）
	 *
	 * @param value 条件操作
	 * @return SQL操作符（未知操作抛RuntimeException）
	 */
	public String parsing(ConditionOperation value) {
		if (value == ConditionOperation.EQUAL) {
			return "=";
		} else if (value == ConditionOperation.NOT_EQUAL) {
			return "<>";
		} else if (value == ConditionOperation.GREATER_THAN) {
			return ">";
		} else if (value == ConditionOperation.LESS_THAN) {
			return "<";
		} else if (value == ConditionOperation.GREATER_EQUAL) {
			return ">=";
		} else if (value == ConditionOperation.LESS_EQUAL) {
			return "<=";
		} else if (value == ConditionOperation.IS_NULL) {
			return "IS NULL";
		} else if (value == ConditionOperation.NOT_NULL) {
			return "IS NOT NULL";
		} else if (value == ConditionOperation.CONTAIN) {
			return "LIKE";
		} else if (value == ConditionOperation.NOT_CONTAIN) {
			return "NOT LIKE";
		} else if (value == ConditionOperation.START) {
			return "LIKE";
		} else if (value == ConditionOperation.END) {
			return "LIKE";
		} else if (value == ConditionOperation.IN) {
			return "IN";
		} else if (value == ConditionOperation.NOT_IN) {
			return "NOT IN";
		}
		throw new RuntimeException(I18N.prop("msg_bobas_value_can_not_be_resolved", value.toString()));
	}

	/**
	 * 语句-WHERE条件子句（conditions为null时返回空字符串）
	 *
	 * @param conditions 条件集合
	 * @return WHERE子句字符串
	 */
	public String parsingWhere(Collection<ICondition> conditions) {
		if (conditions == null) {
			return Strings.VALUE_EMPTY;
		}
		StringBuilder stringBuilder = new StringBuilder(conditions.size() * 36);
		for (ICondition condition : conditions) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(" ");
				stringBuilder.append(this.parsing(condition.getRelationship()));
				stringBuilder.append(" ");
			}
			// 开括号
			for (int i = 0; i < condition.getBracketOpen(); i++) {
				stringBuilder.append("(");
			}
			// 比较左侧
			if (Strings.isNullOrEmpty(condition.getAlias())) {
				// 字段名为空，则为值(ComparedAlias)与值(Value)的比较
				stringBuilder.append("?");
			} else {
				// 输出字段名
				if ((condition.getAliasDataType() == DataType.NUMERIC
						|| condition.getAliasDataType() == DataType.DECIMAL
						|| condition.getAliasDataType() == DataType.DATE)
						&& (condition.getOperation() == ConditionOperation.START
								|| condition.getOperation() == ConditionOperation.END
								|| condition.getOperation() == ConditionOperation.CONTAIN
								|| condition.getOperation() == ConditionOperation.NOT_CONTAIN
								|| condition.getOperation() == ConditionOperation.IN
								|| condition.getOperation() == ConditionOperation.NOT_IN)) {
					// 数值类型的字段且需要作为字符比较的
					stringBuilder.append(this.castAs(DataType.ALPHANUMERIC, condition.getAlias()));
				} else {
					stringBuilder.append(this.identifier());
					stringBuilder.append(condition.getAlias());
					stringBuilder.append(this.identifier());
				}
			}
			// 操作符
			stringBuilder.append(" ");
			stringBuilder.append(this.parsing(condition.getOperation()));
			// 空和非空判断，忽略比较值
			if (!(condition.getOperation() == ConditionOperation.IS_NULL
					|| condition.getOperation() == ConditionOperation.NOT_NULL)) {
				// 比较右侧
				stringBuilder.append(" ");
				if (!Strings.isNullOrEmpty(condition.getAlias())
						&& !Strings.isNullOrEmpty(condition.getComparedAlias())) {
					// 字段与字段的比较：[Alias] = CAST(ComparedAlias)
					stringBuilder.append(this.castAs(condition.getAliasDataType(), condition.getComparedAlias()));
				} else if (condition.getOperation() == ConditionOperation.IN
						|| condition.getOperation() == ConditionOperation.NOT_IN) {
					// IN、NOT IN，值拆成数组
					stringBuilder.append("(");
					int count = Strings.split(condition.getValue()).length;
					for (int i = 0; i < count; i++) {
						if (i > 0) {
							stringBuilder.append(Strings.VALUE_COMMA);
							stringBuilder.append(" ");
						}
						stringBuilder.append("?");
					}
					stringBuilder.append(")");
				} else {
					// 直接比较值
					stringBuilder.append("?");
				}
				if ((condition.getOperation() == ConditionOperation.START
						|| condition.getOperation() == ConditionOperation.END
						|| condition.getOperation() == ConditionOperation.CONTAIN
						|| condition.getOperation() == ConditionOperation.NOT_CONTAIN)
						&& !Strings.isNullOrEmpty(condition.getAlias())) {
					stringBuilder.append(" ");
					stringBuilder.append("ESCAPE");
					stringBuilder.append(" ");
					stringBuilder.append("'");
					stringBuilder.append(this.escape());
					stringBuilder.append("'");
				}
			}
			// 闭括号
			for (int i = 0; i < condition.getBracketClose(); i++) {
				stringBuilder.append(")");
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * 语句-ORDER BY排序子句（sorts为null时返回空字符串）
	 *
	 * @param sorts 排序集合
	 * @return ORDER BY子句字符串
	 */
	public String parsingOrder(Collection<ISort> sorts) {
		if (sorts == null) {
			return Strings.VALUE_EMPTY;
		}
		StringBuilder stringBuilder = new StringBuilder(sorts.size() * 24);
		for (ISort item : sorts) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(this.separation());
			}
			stringBuilder.append(this.identifier());
			stringBuilder.append(item.getAlias());
			stringBuilder.append(this.identifier());
			stringBuilder.append(" ");
			stringBuilder.append(this.parsing(item.getSortType()));
		}
		return stringBuilder.toString();
	}

	/**
	 * 语句-主键条件（根据主键属性生成WHERE子句，无主键时抛异常）
	 *
	 * @param boData 业务对象数据
	 * @return WHERE条件字符串
	 */
	public String parsingWhere(IFieldedObject boData) {
		DbField dbField = null;
		List<IPropertyInfo<?>> keys = boData.properties().where(c -> c.isPrimaryKey());
		StringBuilder stringBuilder = new StringBuilder(keys.size() * 32);
		for (IPropertyInfo<?> item : keys) {
			dbField = item.getAnnotation(DbField.class);
			if (dbField == null || Strings.isNullOrEmpty(dbField.name())) {
				continue;
			}
			if (stringBuilder.length() > 0) {
				stringBuilder.append(" ");
				stringBuilder.append(this.parsing(ConditionRelationship.AND));
				stringBuilder.append(" ");
			}
			stringBuilder.append(this.identifier());
			stringBuilder.append(dbField.name());
			stringBuilder.append(this.identifier());
			stringBuilder.append(" ");
			stringBuilder.append("=");
			stringBuilder.append(" ");
			stringBuilder.append("?");
		}
		if (stringBuilder.length() == 0) {
			throw new RuntimeException(I18N.prop("msg_bobas_bo_not_found_primarykeys", boData.toString()));
		}
		return stringBuilder.toString();
	}

	/**
	 * 语句-删除（根据主键生成DELETE语句）
	 *
	 * @param boData 业务对象数据
	 * @return DELETE SQL字符串
	 */
	public String parsingDelete(IFieldedObject boData) {
		StringBuilder stringBuilder = new StringBuilder(96);
		stringBuilder.append("DELETE");
		stringBuilder.append(" ");
		stringBuilder.append("FROM");
		stringBuilder.append(" ");
		stringBuilder.append(this.identifier());
		stringBuilder.append(this.table(boData.getClass()));
		stringBuilder.append(this.identifier());
		stringBuilder.append(" ");
		stringBuilder.append(this.where());
		stringBuilder.append(" ");
		stringBuilder.append(this.parsingWhere(boData));
		return stringBuilder.toString();
	}

	/**
	 * 语句-更新（所有非主键属性）
	 *
	 * @param boData 业务对象数据
	 * @return UPDATE SQL字符串
	 */
	public String parsingUpdate(IFieldedObject boData) {
		return this.parsingUpdate(boData, false);
	}

	/**
	 * 语句-更新
	 *
	 * @param boData       业务对象数据
	 * @param onlyModified 仅更新被修改的属性
	 * @return UPDATE SQL字符串
	 */
	public String parsingUpdate(IFieldedObject boData, boolean onlyModified) {
		List<IPropertyInfo<?>> properties = boData.properties();
		StringBuilder stringBuilder = new StringBuilder(properties.size() * 20 + 64);
		stringBuilder.append("UPDATE");
		stringBuilder.append(" ");
		stringBuilder.append(this.identifier());
		stringBuilder.append(this.table(boData.getClass()));
		stringBuilder.append(this.identifier());
		stringBuilder.append(" ");
		stringBuilder.append("SET");
		stringBuilder.append(" ");
		DbField dbField = null;
		int count = stringBuilder.length();
		for (IPropertyInfo<?> item : properties.where(c -> !c.isPrimaryKey())) {
			dbField = item.getAnnotation(DbField.class);
			if (dbField == null || Strings.isNullOrEmpty(dbField.name())) {
				continue;
			}
			// 跳过未修改的
			if (onlyModified && !boData.isModified(item)) {
				continue;
			}
			if (stringBuilder.length() > count) {
				stringBuilder.append(this.separation());
			}
			stringBuilder.append(this.identifier());
			stringBuilder.append(dbField.name());
			stringBuilder.append(this.identifier());
			stringBuilder.append(" ");
			stringBuilder.append("=");
			stringBuilder.append(" ");
			stringBuilder.append("?");
		}
		stringBuilder.append(" ");
		stringBuilder.append(this.where());
		stringBuilder.append(" ");
		stringBuilder.append(this.parsingWhere(boData));
		return stringBuilder.toString();
	}

	/**
	 * 语句-插入（根据所有属性生成INSERT语句，无属性时抛异常）
	 *
	 * @param boData 业务对象数据
	 * @return INSERT SQL字符串
	 */
	public String parsingInsert(IFieldedObject boData) {
		List<IPropertyInfo<?>> properties = boData.properties();
		StringBuilder fieldsBuilder = new StringBuilder(properties.size() * 16);
		StringBuilder valuesBuilder = new StringBuilder(properties.size() * 4);

		DbField dbField = null;
		for (IPropertyInfo<?> item : properties) {
			dbField = item.getAnnotation(DbField.class);
			if (dbField == null || Strings.isNullOrEmpty(dbField.name())) {
				continue;
			}

			if (fieldsBuilder.length() > 0) {
				fieldsBuilder.append(this.separation());
			}
			fieldsBuilder.append(this.identifier());
			fieldsBuilder.append(dbField.name());
			fieldsBuilder.append(this.identifier());

			if (valuesBuilder.length() > 0) {
				valuesBuilder.append(this.separation());
			}
			valuesBuilder.append("?");
		}
		if (fieldsBuilder.length() == 0 || valuesBuilder.length() == 0) {
			throw new RuntimeException(I18N.prop("msg_bobas_value_can_not_be_resolved", boData.toString()));
		}

		StringBuilder stringBuilder = new StringBuilder(properties.size() * 20 + 64);
		stringBuilder.append("INSERT");
		stringBuilder.append(" ");
		stringBuilder.append("INTO");
		stringBuilder.append(" ");
		stringBuilder.append(this.identifier());
		stringBuilder.append(this.table(boData.getClass()));
		stringBuilder.append(this.identifier());
		stringBuilder.append(" ");
		stringBuilder.append("(");
		stringBuilder.append(fieldsBuilder);
		stringBuilder.append(")");
		stringBuilder.append(" ");
		stringBuilder.append("VALUES");
		stringBuilder.append(" ");
		stringBuilder.append("(");
		stringBuilder.append(valuesBuilder);
		stringBuilder.append(")");
		return stringBuilder.toString();
	}

	/**
	 * 语句-运行存储过程（空参数名用?占位）
	 *
	 * @param spName 存储过程名称（支持${Variable}变量替换）
	 * @param args   参数名称数组（空字符串用?占位）
	 * @return EXEC SQL字符串
	 */
	public String parsingStoredProcedure(String spName, String... args) {
		StringBuilder stringBuilder = new StringBuilder(spName.length() + args.length * 16 + 32);
		stringBuilder.append("EXEC");
		stringBuilder.append(" ");
		stringBuilder.append(this.identifier());
		stringBuilder.append(MyConfiguration.applyVariables(spName));
		stringBuilder.append(this.identifier());
		if (args.length > 0) {
			stringBuilder.append(" ");
			int count = stringBuilder.length();
			for (String arg : args) {
				if (stringBuilder.length() > count) {
					stringBuilder.append(this.separation());
				}
				if (Strings.isNullOrEmpty(arg)) {
					stringBuilder.append("?");
				} else {
					stringBuilder.append(arg);
					stringBuilder.append(" ");
					stringBuilder.append("=");
					stringBuilder.append(" ");
					stringBuilder.append("?");
				}
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * 语句-获取最大值（IDbTableLock类型加UPDLOCK锁）
	 *
	 * @param maxValue   最大值查询配置
	 * @param conditions 查询条件
	 * @return SELECT MAX SQL字符串
	 */
	public String parsingMaxValue(MaxValue maxValue, Collection<ICondition> conditions) {
		StringBuilder stringBuilder = new StringBuilder(conditions.size() * 32 + 96);
		stringBuilder.append("SELECT");
		stringBuilder.append(" ");
		stringBuilder.append("Max");
		stringBuilder.append("(");
		stringBuilder.append(this.identifier());
		stringBuilder.append(maxValue.getKeyField().getName());
		stringBuilder.append(this.identifier());
		stringBuilder.append(")");
		stringBuilder.append(" ");
		stringBuilder.append("FROM");
		stringBuilder.append(" ");
		stringBuilder.append(this.identifier());
		stringBuilder.append(this.table(maxValue.getType()));
		stringBuilder.append(this.identifier());
		if (maxValue instanceof IDbTableLock) {
			stringBuilder.append(" ");
			stringBuilder.append("WITH (UPDLOCK)");
		}
		stringBuilder.append(" ");
		stringBuilder.append(this.where());
		stringBuilder.append(" ");
		stringBuilder.append(this.parsingWhere(conditions));
		return stringBuilder.toString();
	}

	/**
	 * 语句-可执行（占位符?替换为实际值）
	 * 
	 * @param sqlStatement SQL语句对象
	 * @return 可执行的SQL字符串（空语句返回空字符串）
	 */
	public String parsing(SqlStatement sqlStatement) {
		String sqlString = sqlStatement.getContent();
		if (sqlStatement instanceof SqlStoredProcedure) {
			// 存储过程：先生成EXEC语句（含?占位符）
			SqlStoredProcedure storedProcedure = (SqlStoredProcedure) sqlStatement;
			sqlString = this.parsingStoredProcedure(storedProcedure.getName(),
					new String[storedProcedure.getParameters().size()]);
		}
		// 含参数的语句：替换?占位符为参数值
		if (sqlStatement instanceof SqlPreparedStatement) {
			SqlPreparedStatement statement = (SqlPreparedStatement) sqlStatement;
			if (statement.getParameters().size() > 0) {
				StringBuilder sqlBuilder = new StringBuilder(
						sqlString.length() + (statement.getParameters().size() * 16));
				int index = 1;
				// 如果索引小于默认值，则使用最小
				for (Integer item : statement.getParameters().keySet()) {
					if (item < index) {
						index = item;
					}
				}
				SqlPreparedStatement.Parameter parameter = null;
				Map<Integer, SqlPreparedStatement.Parameter> parameters = statement.getParameters();
				for (char item : sqlString.toCharArray()) {
					if (item == '?') {
						if (parameters.containsKey(index)) {
							parameter = parameters.get(index);
							if (parameter.targetType != null && parameter.targetType != DataType.UNKNOWN) {
								// 使用DataType推导SQL类型
								sqlBuilder
										.append(this.sqlValueOf(parameter.value, this.sqlTypeOf(parameter.targetType)));
							} else {
								// 未知类型，根据Java值类型回退判断
								if (parameter.value == null) {
									sqlBuilder.append(this.sqlValueOf(parameter.value, java.sql.Types.NULL));
								} else if (parameter.value instanceof BigDecimal) {
									sqlBuilder.append(this.sqlValueOf(parameter.value, java.sql.Types.DECIMAL));
								} else if (parameter.value instanceof java.util.Date
										|| parameter.value instanceof java.sql.Date) {
									sqlBuilder.append(this.sqlValueOf(parameter.value, java.sql.Types.DATE));
								} else if (parameter.value instanceof Short) {
									sqlBuilder.append(this.sqlValueOf(parameter.value, java.sql.Types.TINYINT));
								} else if (parameter.value instanceof Long) {
									sqlBuilder.append(this.sqlValueOf(parameter.value, java.sql.Types.BIGINT));
								} else if (parameter.value instanceof Double) {
									sqlBuilder.append(this.sqlValueOf(parameter.value, java.sql.Types.DOUBLE));
								} else if (parameter.value instanceof Float) {
									sqlBuilder.append(this.sqlValueOf(parameter.value, java.sql.Types.FLOAT));
								} else if (parameter.value instanceof Number) {
									sqlBuilder.append(this.sqlValueOf(parameter.value, java.sql.Types.INTEGER));
								} else {
									sqlBuilder.append(this.sqlValueOf(parameter.value, java.sql.Types.VARCHAR));
								}
							}
						} else {
							sqlBuilder.append(this.sqlValueOf(null, java.sql.Types.NULL));
						}
						index++;
					} else if (item == '\'') {
						// 需要转义的字符
						sqlBuilder.append(item);
						sqlBuilder.append(item);
					} else {
						sqlBuilder.append(item);
					}
				}
				// 替换被重复处理的转义字符
				if (!Strings.isNullOrEmpty(this.escape())) {
					String oldString = String.format("''%s''", this.escape());
					String newString = oldString.substring(1, oldString.length() - 1);
					index = sqlBuilder.indexOf(oldString);
					while (index != -1) {
						sqlBuilder.replace(index, index + oldString.length(), newString);
						index = sqlBuilder.indexOf(oldString);
					}
				}
				sqlString = sqlBuilder.toString();
			}
		}
		return sqlString;
	}

	private static String TEMPLATE_SQL_VALUE = "'%s'";

	/**
	 * 返回SQL值（null和VALUE_MIN返回"NULL"，枚举按注解值或ordinal转换，
	 * 字符串处理单引号转义及安全防护：空字符替换、转义字符处理，防止SQL注入）
	 *
	 * @param value   原值
	 * @param sqlType 目标SQL类型（java.sql.Types常量）
	 * @return SQL值字符串
	 */
	public String sqlValueOf(Object value, int sqlType) {
		if (java.sql.Types.NULL == sqlType) {
			return "NULL";
		}
		if (value == null) {
			return this.sqlValueOf(null, java.sql.Types.NULL);
		}
		if (value == DateTimes.VALUE_MIN) {
			return this.sqlValueOf(null, java.sql.Types.NULL);
		}
		if (value.getClass().isEnum()) {
			if (this.dbFieldTypeOf(sqlType) == DataType.ALPHANUMERIC
					|| this.dbFieldTypeOf(sqlType) == DataType.MEMO) {
				return Strings.format(TEMPLATE_SQL_VALUE, Enums.annotationValue(value));
			} else if (this.dbFieldTypeOf(sqlType) == DataType.NUMERIC) {
				if (value instanceof Enum<?>) {
					Enum<?> itemValue = (Enum<?>) value;
					return Strings.valueOf(itemValue.ordinal());
				}
			}
		}
		String sqlValue = null;
		if (value instanceof String) {
			// 字符串类型，处理特殊字符（防SQL注入：单引号双写、空字符替换）
			String strValue = value.toString();
			StringBuilder sb = new StringBuilder(strValue.length() + 8);
			for (int i = 0; i < strValue.length(); i++) {
				char c = strValue.charAt(i);
				if (c == '\'') {
					// 单引号双写转义
					sb.append("''");
				} else if (c == '\0') {
					// 空字符替换为空格，防止截断攻击
					sb.append(' ');
				} else {
					sb.append(c);
				}
			}
			sqlValue = sb.toString();
		} else {
			sqlValue = Strings.valueOf(value);
		}
		DataType fieldType = this.dbFieldTypeOf(sqlType);
		if (fieldType == DataType.ALPHANUMERIC || fieldType == DataType.MEMO) {
			sqlValue = Strings.format(TEMPLATE_SQL_VALUE, sqlValue);
		} else if (fieldType == DataType.DATE) {
			sqlValue = Strings.format(TEMPLATE_SQL_VALUE, sqlValue);
		}
		return sqlValue;
	}

	/**
	 * 为语句填充参数
	 * 
	 * @param statement  语句
	 * @param conditions 条件参数
	 * @param index      参数开始索引
	 * @throws SQLException
	 */
	public void bindParameters(PreparedStatement statement, Iterable<ICondition> conditions, int index)
			throws SQLException {
		for (ICondition condition : conditions) {
			// 比较左侧
			if (Strings.isNullOrEmpty(condition.getAlias())) {
				// 字段名为空，则为值(ComparedAlias)与值(Value)的比较
				statement.setObject(index, condition.getComparedAlias(), this.sqlTypeOf(condition.getAliasDataType()));
				index += 1;
			}
			if (condition.getOperation() == ConditionOperation.IS_NULL
					|| condition.getOperation() == ConditionOperation.NOT_NULL) {
				// 空和非空判断，忽略比较值
				continue;
			}
			if (!Strings.isNullOrEmpty(condition.getAlias()) && !Strings.isNullOrEmpty(condition.getComparedAlias())) {
				// 字段与字段的比较，不需要值
				continue;
			}
			// 比较右侧
			if (condition.getOperation() == ConditionOperation.IN
					|| condition.getOperation() == ConditionOperation.NOT_IN) {
				// IN、NOT IN，值拆成数组
				String[] values = Strings.split(condition.getValue());
				for (String value : values) {
					statement.setObject(index, value.trim(), this.sqlTypeOf(DataType.ALPHANUMERIC));
					index += 1;
				}
			} else if (condition.getOperation() == ConditionOperation.START) {
				statement.setObject(index, this.escape(condition.getValue(), '_', '%') + "%",
						this.sqlTypeOf(DataType.ALPHANUMERIC));
				index += 1;
			} else if (condition.getOperation() == ConditionOperation.END) {
				statement.setObject(index, "%" + this.escape(condition.getValue(), '_', '%'),
						this.sqlTypeOf(DataType.ALPHANUMERIC));
				index += 1;
			} else if (condition.getOperation() == ConditionOperation.CONTAIN) {
				statement.setObject(index, "%" + this.escape(condition.getValue(), '_', '%') + "%",
						this.sqlTypeOf(DataType.ALPHANUMERIC));
				index += 1;
			} else if (condition.getOperation() == ConditionOperation.NOT_CONTAIN) {
				statement.setObject(index, "%" + this.escape(condition.getValue(), '_', '%') + "%",
						this.sqlTypeOf(DataType.ALPHANUMERIC));
				index += 1;
			} else {
				// 直接比较值
				statement.setObject(index, condition.getValue(), this.sqlTypeOf(condition.getAliasDataType()));
				index += 1;
			}
		}
	}
}