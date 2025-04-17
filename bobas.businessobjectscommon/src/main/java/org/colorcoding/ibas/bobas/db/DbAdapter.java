package org.colorcoding.ibas.bobas.db;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.BOFactory;
import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.DataConvert;
import org.colorcoding.ibas.bobas.common.DateTimes;
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
			this.batchCount = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_DB_STATEMENT_BATCH_COUNT, 300);
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

	private Boolean noUserTansactionSP;

	public final Boolean isNoUserTansactionSP() {
		if (this.noUserTansactionSP == null) {
			this.noUserTansactionSP = MyConfiguration
					.getConfigValue(MyConfiguration.CONFIG_ITEM_DB_NO_USER_TANSACTION_SP, false);
		}
		return noUserTansactionSP;
	}

	protected final void setNoUserTansactionSP(Boolean noUserTansactionSP) {
		this.noUserTansactionSP = noUserTansactionSP;
	}

	/**
	 * 创建数据库链接
	 * 
	 * @param server   服务器地址
	 * @param dbName   数据库名称
	 * @param userName 用户名
	 * @param userPwd  密码
	 * @return
	 */
	public abstract Connection createConnection(String server, String dbName, String userName, String userPwd);

	/**
	 * 解析数据
	 * 
	 * @param <T>       对象类型
	 * @param boType    对象类型（Result、KeyText、KeyValue、BusinessObject、SPValues）
	 * @param resultSet 结果集
	 * @return
	 * @throws SQLException
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
				datas.add((T) new KeyValue<Object>(resultSet.getString(1), resultSet.getObject(2)));
			} else if (IFieldedObject.class.isAssignableFrom(boType)) {
				if (orderProperties == null) {
					List<IPropertyInfo<?>> propertyInfos = BOFactory.propertyInfos(boType);
					orderProperties = new IPropertyInfo<?>[resultSet.getMetaData().getColumnCount()];
					IPropertyInfo<?> propertyInfo;
					DbField dbField;
					int index;
					for (int i = 0; i < propertyInfos.size(); i++) {
						propertyInfo = propertyInfos.get(i);
						dbField = propertyInfo.getAnnotation(DbField.class);
						if (dbField == null || Strings.isNullOrEmpty(dbField.name())) {
							continue;
						}
						index = resultSet.findColumn(dbField.name());
						if (index > 0) {
							orderProperties[index - 1] = propertyInfo;
						}
					}
					propertyInfos = null;
					propertyInfo = null;
					dbField = null;
				}
				data = BOFactory.newInstance(boType);
				if (data instanceof IFieldedObject) {
					((IFieldedObject) data).setLoading(true);
				}
				data = this.setProperties(data, resultSet, orderProperties);
				if (data instanceof IFieldedObject) {
					((IFieldedObject) data).setLoading(false);
				}
				if (data instanceof FieldedObject) {
					((FieldedObject) data).markOld();
				}
				datas.add(data);
			}
		}
		return datas;
	}

	/**
	 * 设置对象属性值
	 * 
	 * @param <T>             类型
	 * @param data            对象实例
	 * @param resultSet       数据集
	 * @param orderProperties 排序的属性（按结果集）
	 * @return
	 * @throws SQLException
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
	 * 解析值
	 * 
	 * @param resultSet   结果集
	 * @param columnIndex 列索引
	 * @param dataType    目标类型
	 * @return
	 * @throws SQLException
	 */
	public Object parsingValue(ResultSet resultSet, int columnIndex, Class<?> dataType) throws SQLException {
		if (dataType == Integer.class) {
			return resultSet.getInt(columnIndex);
		} else if (dataType == String.class) {
			return resultSet.getString(columnIndex);
		} else if (dataType == BigDecimal.class) {
			return resultSet.getBigDecimal(columnIndex);
		} else if (dataType == Double.class) {
			return resultSet.getDouble(columnIndex);
		} else if (dataType == DateTime.class) {
			return DateTimes.valueOf(resultSet.getDate(columnIndex));
		} else if (dataType == Short.class) {
			return resultSet.getShort(columnIndex);
		} else if (dataType == Long.class) {
			return resultSet.getLong(columnIndex);
		} else if (dataType.isEnum()) {
			return Enums.valueOf(dataType, resultSet.getString(columnIndex));
		} else if (dataType == Float.class) {
			return resultSet.getFloat(columnIndex);
		} else if (dataType == Character.class) {
			return resultSet.getString(columnIndex);
		} else if (dataType == Boolean.class) {
			return resultSet.getBoolean(columnIndex);
		} else {
			return DataConvert.convert(dataType, resultSet.getObject(columnIndex));
		}
	}

	/**
	 * 解析数据集到表格
	 * 
	 * @param resultSet 数据集
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public IDataTable parsingDatas(ResultSet resultSet) throws SQLException, ClassNotFoundException {
		DataTable dataTable = new DataTable();
		ResultSetMetaData metaData = resultSet.getMetaData();
		if (metaData.getColumnCount() > 0) {
			// 设置表名
			dataTable.setName(metaData.getTableName(1));
			// 创建列
			IDataTableColumn dtColumn;
			String name;
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
			// 添加行数据
			while (resultSet.next()) {
				IDataTableRow row = dataTable.getRows().create();
				// 行的每列赋值
				for (int i = 0; i < dataTable.getColumns().size(); i++) {
					row.setValue(i, resultSet.getObject(i + 1, dataTable.getColumns().get(i).getDataType()));
				}
			}
		}
		return dataTable;
	}

	/**
	 * 返回sql类型
	 * 
	 * @param type 数据字段类型
	 * @return
	 */
	public int sqlTypeOf(DbFieldType type) {
		if (type == DbFieldType.ALPHANUMERIC) {
			return java.sql.Types.VARCHAR;
		} else if (type == DbFieldType.DECIMAL) {
			return java.sql.Types.DECIMAL;
		} else if (type == DbFieldType.DATE) {
			return java.sql.Types.DATE;
		} else if (type == DbFieldType.NUMERIC) {
			return java.sql.Types.INTEGER;
		} else if (type == DbFieldType.BYTES) {
			return java.sql.Types.BIT;
		} else if (type == DbFieldType.MEMO) {
			return java.sql.Types.LONGVARCHAR;
		}
		return java.sql.Types.OTHER;
	}

	/**
	 * 转化查询（属性名称、属性类型）
	 * 
	 * @param criteria 待处理查询
	 * @param boType   目标类型
	 * @return
	 */
	public ICriteria convert(ICriteria criteria, Class<?> boType) {
		return this.convert(criteria, BOFactory.propertyInfos(boType));
	}

	/**
	 * 转化查询（属性名称、属性类型）
	 * 
	 * @param criteria      待处理查询
	 * @param propertyInfos 对象属性
	 * @return
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
					condition.setAliasDataType(DbFieldType.valueOf(propertyInfo.getValueType()));
					// 枚举类型的转值
					if (propertyInfo.getValueType().isEnum()) {
						tmpValue = Enums.valueOf(propertyInfo.getValueType(), condition.getValue());
						if (tmpValue != null) {
							condition.setValue(Enums.annotationValue(tmpValue));
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
			for (IChildCriteria item : criteria.getChildCriterias()) {
				if (Strings.equalsIgnoreCase(item.getPropertyPath(), propertyInfo.getName())) {
					this.convert(item, propertyInfo.getValueType());
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

	public String where() {
		return "WHERE";
	}

	/**
	 * 语句-事务通知存储过程
	 * 
	 * @return
	 */
	public final String sp_transaction_notification() {
		return this.parsingStoredProcedure(Strings.format("%s_SP_TRANSACTION_NOTIFICATION", this.getCompanyId()),
				new String[this.isNoUserTansactionSP() ? 5 : 6]);
	}

	/**
	 * 语句-查询
	 * 
	 * @param boType 对象类型
	 * @return
	 */
	public final String parsingSelect(Class<?> boType) {
		return this.parsingSelect(boType, new Criteria());
	}

	/**
	 * 语句-查询
	 * 
	 * @param boType     对象类型
	 * @param conditions 查询条件
	 * @return
	 */
	public final String parsingSelect(Class<?> boType, Iterable<ICondition> conditions) {
		ICriteria criteria = new Criteria();
		if (conditions != null) {
			for (ICondition item : conditions) {
				criteria.getConditions().add(item);
			}
		}
		return this.parsingSelect(boType, criteria);
	}

	/**
	 * 语句-查询
	 * 
	 * @param boType   对象类型
	 * @param criteria 查询条件
	 * @return
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

	private Map<Class<?>, String> tables = new ConcurrentHashMap<>(256);

	/**
	 * 返回对象类型对应的表
	 * 
	 * @param boType 对象类型
	 * @return
	 */
	public String table(Class<?> boType) {
		try {
			if (this.tables.containsKey(boType)) {
				return this.tables.get(boType);
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
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 语句-查询
	 * 
	 * @param boType   对象类型
	 * @param criteria 查询条件
	 * @param withLock 是否带锁
	 * @return
	 */
	public String parsingSelect(Class<?> boType, ICriteria criteria, boolean withLock) {
		StringBuilder stringBuilder = new StringBuilder();
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
		// 如果加锁，则不能排序
		if (withLock == false && criteria.getSorts().size() > 0) {
			stringBuilder.append(" ");
			stringBuilder.append("ORDER BY");
			stringBuilder.append(" ");
			stringBuilder.append(this.parsingOrder(criteria.getSorts()));
		}
		return stringBuilder.toString();
	}

	/**
	 * 语句-转换类型
	 * 
	 * @param type  目标类型
	 * @param alias 转换字段
	 * @return
	 */
	public String castAs(DbFieldType type, String alias) {
		StringBuilder stringBuilder = new StringBuilder();
		if (type == DbFieldType.ALPHANUMERIC) {
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
		} else if (type == DbFieldType.DATE) {
			stringBuilder = new StringBuilder();
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
		} else if (type == DbFieldType.NUMERIC) {
			stringBuilder = new StringBuilder();
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
		} else if (type == DbFieldType.DECIMAL) {
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
	 * 语句-排序
	 * 
	 * @param type
	 * @return
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
	 * 语句-条件关系
	 * 
	 * @param value
	 * @return
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
	 * 语句-条件操作
	 * 
	 * @param value
	 * @return
	 */
	public String parsing(ConditionOperation value) {
		if (value == ConditionOperation.EQUAL) {
			return "=";
		} else if (value == ConditionOperation.NOT_EQUAL) {
			return "<>";
		} else if (value == ConditionOperation.GRATER_THAN) {
			return ">";
		} else if (value == ConditionOperation.LESS_THAN) {
			return "<";
		} else if (value == ConditionOperation.GRATER_EQUAL) {
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
		}
		throw new RuntimeException(I18N.prop("msg_bobas_value_can_not_be_resolved", value.toString()));
	}

	/**
	 * 语句-条件
	 * 
	 * @param value
	 * @return
	 */
	public String parsingWhere(Iterable<ICondition> conditions) {
		if (conditions == null) {
			return Strings.VALUE_EMPTY;
		}
		StringBuilder stringBuilder = new StringBuilder();
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
			// 字段名
			if (condition.getAliasDataType() == DbFieldType.MEMO) {
				stringBuilder.append(this.identifier());
				stringBuilder.append(condition.getAlias());
				stringBuilder.append(this.identifier());
			} else if ((condition.getAliasDataType() == DbFieldType.NUMERIC
					|| condition.getAliasDataType() == DbFieldType.DECIMAL
					|| condition.getAliasDataType() == DbFieldType.DATE)
					&& (condition.getOperation() == ConditionOperation.START
							|| condition.getOperation() == ConditionOperation.END
							|| condition.getOperation() == ConditionOperation.CONTAIN
							|| condition.getOperation() == ConditionOperation.NOT_CONTAIN)) {
				// 数值类型的字段且需要作为字符比较的
				stringBuilder.append(this.castAs(DbFieldType.ALPHANUMERIC, condition.getAlias()));
			} else if (condition.getComparedAlias() != null && !condition.getComparedAlias().isEmpty()) {
				// 字段之间比较，以主条件为比较类型
				stringBuilder.append(this.identifier());
				stringBuilder.append(condition.getAlias());
				stringBuilder.append(this.identifier());
				stringBuilder.append(" ");
				stringBuilder.append(this.parsing(condition.getOperation()));
				stringBuilder.append(" ");
				stringBuilder.append(this.castAs(condition.getAliasDataType(), condition.getAlias()));
			} else {
				stringBuilder.append(this.identifier());
				stringBuilder.append(condition.getAlias());
				stringBuilder.append(this.identifier());
			}
			if (Strings.isNullOrEmpty(condition.getComparedAlias())) {
				// 字段与值的比较
				stringBuilder.append(" ");
				if (condition.getOperation() == ConditionOperation.IS_NULL
						|| condition.getOperation() == ConditionOperation.NOT_NULL) {
					// 不需要值的比较，[ItemName] is NULL
					stringBuilder.append(this.parsing(condition.getOperation()));
				} else {
					// 与值比较，[ItemCode] = 'A000001'
					stringBuilder.append(this.parsing(condition.getOperation()));
					stringBuilder.append(" ");
					stringBuilder.append("?");
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
	 * 语句-排序
	 * 
	 * @param value
	 * @return
	 */
	public String parsingOrder(Iterable<ISort> sorts) {
		if (sorts == null) {
			return Strings.VALUE_EMPTY;
		}
		StringBuilder stringBuilder = new StringBuilder();
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
	 * 语句-条件
	 * 
	 * @param value
	 * @return
	 */
	public String parsingWhere(IFieldedObject boData) {
		DbField dbField = null;
		StringBuilder stringBuilder = new StringBuilder();
		for (IPropertyInfo<?> item : boData.properties().where(c -> c.isPrimaryKey())) {
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
	 * 语句-删除
	 * 
	 * @param value
	 * @return
	 */
	public String parsingDelete(IFieldedObject boData) {
		StringBuilder stringBuilder = new StringBuilder();
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
	 * 语句-更新
	 * 
	 * @param boData
	 * @return
	 */
	public String parsingUpdate(IFieldedObject boData) {
		StringBuilder stringBuilder = new StringBuilder();
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
		for (IPropertyInfo<?> item : boData.properties().where(c -> !c.isPrimaryKey())) {
			dbField = item.getAnnotation(DbField.class);
			if (dbField == null || Strings.isNullOrEmpty(dbField.name())) {
				continue;
			}
			if (!boData.isDirty(item)) {
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
	 * 语句-插入
	 * 
	 * @param value
	 * @return
	 */
	public String parsingInsert(IFieldedObject boData) {
		StringBuilder fieldsBuilder = new StringBuilder();
		StringBuilder valuesBuilder = new StringBuilder();

		DbField dbField = null;
		for (IPropertyInfo<?> item : boData.properties()) {
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

		StringBuilder stringBuilder = new StringBuilder();
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
	 * 语句-运行存储过程
	 * 
	 * @param value
	 * @return
	 */
	public String parsingStoredProcedure(String spName, String... args) {
		StringBuilder stringBuilder = new StringBuilder();
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
	 * 语句-获取最大值
	 * 
	 * @param value
	 * @return
	 */
	public String parsingMaxValue(MaxValue maxValue, Iterable<ICondition> conditions) {
		StringBuilder stringBuilder = new StringBuilder();
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
}