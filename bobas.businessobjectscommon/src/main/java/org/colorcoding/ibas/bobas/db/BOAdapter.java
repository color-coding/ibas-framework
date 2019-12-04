package org.colorcoding.ibas.bobas.db;

import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.IBOStorageTag;
import org.colorcoding.ibas.bobas.bo.IBOUserFields;
import org.colorcoding.ibas.bobas.bo.IUserField;
import org.colorcoding.ibas.bobas.bo.UserField;
import org.colorcoding.ibas.bobas.common.ConditionAliasDataType;
import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.IConditions;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.ISort;
import org.colorcoding.ibas.bobas.common.ISorts;
import org.colorcoding.ibas.bobas.common.ISqlQuery;
import org.colorcoding.ibas.bobas.common.ISqlStoredProcedure;
import org.colorcoding.ibas.bobas.common.SqlQuery;
import org.colorcoding.ibas.bobas.core.BOFactory;
import org.colorcoding.ibas.bobas.core.IBOFactory;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.IBusinessObjectsBase;
import org.colorcoding.ibas.bobas.core.ITrackStatusOperator;
import org.colorcoding.ibas.bobas.core.PropertyInfo;
import org.colorcoding.ibas.bobas.core.PropertyInfoList;
import org.colorcoding.ibas.bobas.core.PropertyInfoManager;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.core.fields.IFieldDataDb;
import org.colorcoding.ibas.bobas.core.fields.IFieldDataDbs;
import org.colorcoding.ibas.bobas.core.fields.IManagedFields;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.KeyValue;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.mapping.DbField;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;
import org.colorcoding.ibas.bobas.repository.TransactionType;

public abstract class BOAdapter implements IBOAdapter {

	public BOAdapter() {
		this.setEnabledUserFields(
				MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_BO_ENABLED_USER_FIELDS, true));
	}

	private boolean enabledUserFields;

	/**
	 * 是否启用用户字段
	 * 
	 * @return
	 */
	public boolean isEnabledUserFields() {
		return enabledUserFields;
	}

	public void setEnabledUserFields(boolean value) {
		this.enabledUserFields = value;
	}

	/**
	 * 复合字段开始索引（必须小于0）
	 */
	public final static int COMPLEX_FIELD_BEGIN_INDEX = -100000;

	/**
	 * 组合复合字段索引
	 * 
	 * @param index    字段索引
	 * @param subIndex 子项字段索引
	 * @return
	 */
	protected int groupComplexFieldIndex(int index, int subIndex) {
		return COMPLEX_FIELD_BEGIN_INDEX - index * 100 - subIndex;
	}

	/**
	 * 解析复合字段索引
	 * 
	 * @param groupIndex 组合的索引
	 * @return 拆解的数组，[2]{F,S} F为字段索引，S为子项索引
	 */
	protected int[] parseComplexFieldIndex(int groupIndex) {
		if (groupIndex <= COMPLEX_FIELD_BEGIN_INDEX) {
			int[] indexs = new int[2];
			int tmp = Math.abs(groupIndex - COMPLEX_FIELD_BEGIN_INDEX);
			indexs[0] = tmp / 100;
			indexs[1] = tmp - indexs[0] * 100;
			return indexs;
		}
		return null;
	}

	/**
	 * 获取脚本库
	 * 
	 * @return
	 */
	public abstract ISqlScripts getSqlScripts();

	public Iterable<IFieldDataDb> getDbFields(IManagedFields bo) {
		ArrayList<IFieldDataDb> dbFields = new ArrayList<IFieldDataDb>();
		for (IFieldData item : bo.getFields()) {
			if (item instanceof IFieldDataDb) {
				dbFields.add((IFieldDataDb) item);
			} else if (item instanceof IFieldDataDbs) {
				for (IFieldDataDb sub : ((IFieldDataDbs) item)) {
					dbFields.add(sub);
				}
			}
		}
		return dbFields;
	}

	public Iterable<IFieldDataDb> getDbFields(IFieldData[] fields) {
		ArrayList<IFieldDataDb> dbFields = new ArrayList<IFieldDataDb>();
		for (IFieldData item : fields) {
			if (item instanceof IFieldDataDb) {
				dbFields.add((IFieldDataDb) item);
			} else if (item instanceof IFieldDataDbs) {
				for (IFieldDataDb sub : ((IFieldDataDbs) item)) {
					dbFields.add(sub);
				}
			}
		}
		return dbFields;
	}

	@Override
	public ISqlQuery getServerTimeQuery() throws ParsingException {
		try {
			ISqlScripts sqlScripts = this.getSqlScripts();
			if (sqlScripts == null) {
				throw new SqlScriptException(I18N.prop("msg_bobas_invaild_sql_scripts"));
			}
			return sqlScripts.getServerTimeQuery();
		} catch (Exception e) {
			throw new ParsingException(e);
		}
	}

	@Override
	public ISqlQuery parseSqlQuery(ICriteria criteria) throws ParsingException {
		if (criteria == null) {
			return null;
		}
		return this.parseSqlQuery(criteria.getConditions());
	}

	/**
	 * 获取查询条件语句
	 * 
	 * @param conditions 查询条件
	 * @return
	 * @throws SqlScriptException
	 */
	public ISqlQuery parseSqlQuery(IConditions conditions) throws ParsingException {
		try {
			if (conditions == null) {
				return null;
			}
			ISqlScripts sqlScripts = this.getSqlScripts();
			if (sqlScripts == null) {
				throw new SqlScriptException(I18N.prop("msg_bobas_invaild_sql_scripts"));
			}
			StringBuilder stringBuilder = new StringBuilder();
			String dbObject = sqlScripts.getDbObjectSign();
			for (ICondition condition : conditions) {
				if (stringBuilder.length() > 0) {
					stringBuilder.append(" ");
					stringBuilder.append(sqlScripts.getSqlString(condition.getRelationship()));
					stringBuilder.append(" ");
				}
				// 开括号
				for (int i = 0; i < condition.getBracketOpen(); i++) {
					stringBuilder.append("(");
				}
				// 字段名
				if (condition.getAliasDataType() == ConditionAliasDataType.FREE_TEXT) {
					stringBuilder.append(sqlScripts.getSqlString(DbFieldType.ALPHANUMERIC, condition.getAlias()));
				} else if ((condition.getAliasDataType() == ConditionAliasDataType.NUMERIC
						|| condition.getAliasDataType() == ConditionAliasDataType.DECIMAL
						|| condition.getAliasDataType() == ConditionAliasDataType.DATE)
						&& (condition.getOperation() == ConditionOperation.START
								|| condition.getOperation() == ConditionOperation.END
								|| condition.getOperation() == ConditionOperation.CONTAIN
								|| condition.getOperation() == ConditionOperation.NOT_CONTAIN)) {
					// 数值类型的字段且需要作为字符比较的
					String toVarchar = sqlScripts.getCastTypeString(ConditionAliasDataType.ALPHANUMERIC);
					stringBuilder.append(String.format(toVarchar, String.format(dbObject, condition.getAlias())));
				} else if (condition.getComparedAlias() != null && !condition.getComparedAlias().isEmpty()) {
					// 字段之间比较，以主条件为比较类型
					String toVarchar = sqlScripts.getCastTypeString(condition.getAliasDataType());
					stringBuilder.append(String.format(toVarchar, String.format(dbObject, condition.getAlias())));
					stringBuilder.append(" ");
					stringBuilder.append(sqlScripts.getSqlString(condition.getOperation()));
					stringBuilder.append(" ");
					stringBuilder
							.append(String.format(toVarchar, String.format(dbObject, condition.getComparedAlias())));
				} else {
					stringBuilder.append(String.format(dbObject, condition.getAlias()));
				}
				if (condition.getComparedAlias() == null || condition.getComparedAlias().isEmpty()) {
					// 字段与值的比较
					stringBuilder.append(" ");
					if (condition.getOperation() == ConditionOperation.IS_NULL
							|| condition.getOperation() == ConditionOperation.NOT_NULL) {
						// 不需要值的比较，[ItemName] is NULL
						stringBuilder.append(sqlScripts.getSqlString(condition.getOperation(), condition.getValue()));
					} else if (condition.getOperation() == ConditionOperation.START
							|| condition.getOperation() == ConditionOperation.END
							|| condition.getOperation() == ConditionOperation.CONTAIN
							|| condition.getOperation() == ConditionOperation.NOT_CONTAIN) {
						// like 相关运算
						stringBuilder.append(sqlScripts.getSqlString(condition.getOperation(), condition.getValue()));
					} else {
						// 与值比较，[ItemCode] = 'A000001'
						if (condition.getAliasDataType() == ConditionAliasDataType.NUMERIC
								|| condition.getAliasDataType() == ConditionAliasDataType.DECIMAL) {
							// 数值类型的字段
							stringBuilder.append(sqlScripts.getSqlString(condition.getOperation()));
							stringBuilder.append(" ");
							stringBuilder.append(condition.getValue());
						} else if (condition.getAliasDataType() == ConditionAliasDataType.DATE) {
							// 日期类型
							stringBuilder.append(sqlScripts.getSqlString(condition.getOperation()));
							stringBuilder.append(" ");
							stringBuilder.append(sqlScripts.getSqlString(DbFieldType.DATE, condition.getValue()));
						} else {
							// 非数值类型字段
							stringBuilder.append(sqlScripts.getSqlString(condition.getOperation()));
							stringBuilder.append(" ");
							stringBuilder
									.append(sqlScripts.getSqlString(DbFieldType.ALPHANUMERIC, condition.getValue()));
						}
					}
				}
				// 闭括号
				for (int i = 0; i < condition.getBracketClose(); i++) {
					stringBuilder.append(")");
				}
			}
			return new SqlQuery(stringBuilder.toString());
		} catch (SqlScriptException e) {
			throw new ParsingException(e);
		}
	}

	/**
	 * 获取排序语句
	 * 
	 * @param sorts 排序
	 * @return
	 * @throws SqlScriptException
	 */
	@Override
	public ISqlQuery parseSqlQuery(ISorts sorts) throws ParsingException {
		try {
			if (sorts == null) {
				return null;
			}
			ISqlScripts sqlScripts = this.getSqlScripts();
			if (sqlScripts == null) {
				throw new SqlScriptException(I18N.prop("msg_bobas_invaild_sql_scripts"));
			}
			StringBuilder stringBuilder = new StringBuilder();
			String dbObject = sqlScripts.getDbObjectSign();
			for (ISort sort : sorts) {
				if (stringBuilder.length() > 0) {
					stringBuilder.append(sqlScripts.getFieldBreakSign());
				}
				stringBuilder.append(String.format(dbObject, sort.getAlias()));
				stringBuilder.append(" ");
				stringBuilder.append(sqlScripts.getSqlString(sort.getSortType()));
			}
			return new SqlQuery(stringBuilder.toString());
		} catch (Exception e) {
			throw new ParsingException(e);
		}
	}

	@Override
	public ISqlQuery parseSqlQuery(ICriteria criteria, Class<?> boType) throws ParsingException {
		try {
			ISqlScripts sqlScripts = this.getSqlScripts();
			if (sqlScripts == null) {
				throw new SqlScriptException(I18N.prop("msg_bobas_invaild_sql_scripts"));
			}
			if (criteria == null) {
				criteria = new Criteria();
			}
			// 获取主表
			String table = this.getMasterTable(PropertyInfoManager.getPropertyInfoList(boType));
			if (table == null || table.isEmpty()) {
				throw new ParsingException(I18N.prop("msg_bobas_not_found_bo_table", boType.getName()));
			}
			// 修正其中公司变量
			table = MyConfiguration.applyVariables(String.format(sqlScripts.getDbObjectSign(), table));
			// 修正属性
			criteria.check(boType);
			// 拼接语句
			String order = this.parseSqlQuery(criteria.getSorts()).getQueryString();
			String where = this.parseSqlQuery(criteria.getConditions()).getQueryString();
			return new SqlQuery(sqlScripts.groupSelectQuery("*", table, where, order, criteria.getResultCount()));
		} catch (ParsingException e) {
			throw e;
		} catch (SqlScriptException e) {
			throw new ParsingException(e);
		}
	}

	@Override
	public ISqlQuery parseInsertScript(IBusinessObjectBase bo) throws ParsingException {
		try {
			if (!(bo instanceof IManagedFields)) {
				throw new ParsingException(I18N.prop("msg_bobas_invaild_bo"));
			}
			ISqlScripts sqlScripts = this.getSqlScripts();
			if (sqlScripts == null) {
				throw new SqlScriptException(I18N.prop("msg_bobas_invaild_sql_scripts"));
			}
			IManagedFields boFields = (IManagedFields) bo;
			String table = this.getMasterTable(boFields);
			if (table == null || table.isEmpty()) {
				// 没有获取到表
				throw new ParsingException(I18N.prop("msg_bobas_not_found_bo_table", bo.getClass().getName()));
			}
			table = String.format(sqlScripts.getDbObjectSign(), table);
			StringBuilder fieldsBuilder = new StringBuilder();
			StringBuilder valuesBuilder = new StringBuilder();
			for (IFieldDataDb dbItem : this.getDbFields(boFields)) {
				if (fieldsBuilder.length() > 0) {
					fieldsBuilder.append(sqlScripts.getFieldBreakSign());
				}
				if (valuesBuilder.length() > 0) {
					valuesBuilder.append(sqlScripts.getFieldBreakSign());
				}
				fieldsBuilder.append(String.format(sqlScripts.getDbObjectSign(), dbItem.getDbField()));
				Object value = dbItem.getValue();
				if (value == null) {
					valuesBuilder.append(sqlScripts.getNullSign());
				} else {
					valuesBuilder.append(sqlScripts.getSqlString(dbItem.getFieldType(), DataConvert.toDbValue(value)));
				}
			}
			if (fieldsBuilder.length() == 0) {
				// 没有字段
				throw new ParsingException(I18N.prop("msg_bobas_not_allow_sql_scripts"));
			}
			return new SqlQuery(
					sqlScripts.groupInsertScript(table, fieldsBuilder.toString(), valuesBuilder.toString()));
		} catch (ParsingException e) {
			throw e;
		} catch (SqlScriptException e) {
			throw new ParsingException(e);
		}
	}

	/**
	 * 获取字段及值语句
	 * 
	 * @param fields 字段列表
	 * @return 语句（"ItemCode" = 'A00001' ，"ItemName" = 'CPU I9'）
	 * @throws SqlScriptException
	 */
	protected String getFieldValues(Iterable<IFieldDataDb> fields, String split) throws SqlScriptException {
		ISqlScripts sqlScripts = this.getSqlScripts();
		if (sqlScripts == null) {
			throw new SqlScriptException(I18N.prop("msg_bobas_invaild_sql_scripts"));
		}
		StringBuilder stringBuilder = new StringBuilder();
		for (IFieldDataDb dbItem : fields) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(split);
			}
			Object value = dbItem.getValue();
			stringBuilder.append(String.format(sqlScripts.getDbObjectSign(), dbItem.getDbField()));
			stringBuilder.append(" ");
			stringBuilder.append(value != null ? sqlScripts.getSqlString(ConditionOperation.EQUAL)
					: sqlScripts.getSqlString(ConditionOperation.IS_NULL));
			if (value != null) {
				stringBuilder.append(" ");
				stringBuilder.append(sqlScripts.getSqlString(dbItem.getFieldType(), DataConvert.toDbValue(value)));
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * 获取字段的主表
	 * 
	 * @param boFields 字段列表
	 * @return 表名称（“OITM”）
	 */
	public String getMasterTable(IManagedFields boFields) {
		for (IFieldData item : boFields.getFields(c -> c.isPrimaryKey())) {
			if (item instanceof IFieldDataDb) {
				IFieldDataDb dbItem = (IFieldDataDb) item;
				return dbItem.getDbTable();
			}
		}
		return null;
	}

	/**
	 * 获取属性集合的主表
	 * 
	 * @param pInfoList
	 * @return
	 */
	public String getMasterTable(PropertyInfoList pInfoList) {
		String table = null;
		for (int i = 0; i < pInfoList.size(); i++) {
			PropertyInfo<?> cProperty = (PropertyInfo<?>) pInfoList.get(i);
			if (cProperty.getName() == null || cProperty.getName().isEmpty()) {
				continue;
			}
			DbField dbField = cProperty.getAnnotation(DbField.class);
			if (dbField == null) {
				continue;
			}
			if (dbField.name() == null || dbField.name().isEmpty()) {
				continue;
			}
			if (dbField.primaryKey()) {
				return dbField.table();
			}
			table = dbField.table();
		}
		return table;
	}

	@Override
	public ISqlQuery parseDeleteScript(IBusinessObjectBase bo) throws ParsingException {
		try {
			if (!(bo instanceof IManagedFields)) {
				throw new ParsingException(I18N.prop("msg_bobas_invaild_bo"));
			}
			ISqlScripts sqlScripts = this.getSqlScripts();
			if (sqlScripts == null) {
				throw new SqlScriptException(I18N.prop("msg_bobas_invaild_sql_scripts"));
			}
			IManagedFields boFields = (IManagedFields) bo;
			String table = this.getMasterTable(boFields);
			if (table == null || table.isEmpty()) {
				// 没有获取到表
				throw new ParsingException(I18N.prop("msg_bobas_not_found_bo_table", bo.getClass().getName()));
			}
			table = String.format(sqlScripts.getDbObjectSign(), table);
			String partWhere = null;
			if (bo.isNew()) {
				// 新建状态删除，使用唯一属性
				partWhere = this.getFieldValues(this.getDbFields(boFields.getFields(c -> c.isUniqueKey())),
						sqlScripts.getAndSign());
			} else {
				// 非新建删除，使用主键属性
				partWhere = this.getFieldValues(this.getDbFields(boFields.getFields(c -> c.isPrimaryKey())),
						sqlScripts.getAndSign());
			}
			if (partWhere == null || partWhere.isEmpty()) {
				// 没有条件的删除不允许执行
				throw new ParsingException(I18N.prop("msg_bobas_not_allow_sql_scripts"));
			}
			return new SqlQuery(sqlScripts.groupDeleteScript(table, partWhere));
		} catch (ParsingException e) {
			throw e;
		} catch (SqlScriptException e) {
			throw new ParsingException(e);
		}
	}

	/**
	 * 获取匹配的索引（提升性能），此处包括对用户字段的处理
	 * 
	 * @param reader 查询
	 * @param bo     对象
	 * @return
	 * @throws ParsingException
	 * @throws DbException
	 * @throws SQLException
	 * @throws SqlScriptException
	 */
	protected int[] getFieldIndex(IDbDataReader reader, IManagedFields bo)
			throws ParsingException, DbException, SQLException, SqlScriptException {
		// 构建索引
		IFieldData[] boFields = bo.getFields();
		int[] dfIndex = null;// 数据字段索引数组
		ResultSetMetaData metaData = reader.getMetaData();
		dfIndex = new int[metaData.getColumnCount()];
		boolean hasUserFields = false;// 存在未被添加的用户字段
		for (int i = 0; i < dfIndex.length; i++) {
			// 初始化索引
			dfIndex[i] = -1;
			// reader列索引
			int rCol = i + 1;
			// 当前列名称
			String name = metaData.getColumnLabel(rCol);// reader索引从1开始
			if (name == null || name.isEmpty()) {
				name = metaData.getColumnName(rCol);
			}
			// 获取当前列对应的索引
			for (int j = 0; j < boFields.length; j++) {
				// 遍历BO字段
				IFieldData iFieldData = boFields[j];
				if (iFieldData instanceof IFieldDataDb) {
					// 数据库字段
					IFieldDataDb dbField = (IFieldDataDb) iFieldData;
					if (dbField.getDbField().equals(name)) {
						dfIndex[i] = j;// 设置当前列索引
						break;
					}
				} else if (iFieldData instanceof IFieldDataDbs) {
					// 数据库集合字段
					int k = 0;
					for (IFieldDataDb dbField : ((IFieldDataDbs) iFieldData)) {
						if (dbField.getDbField().equals(name)) {
							dfIndex[i] = this.groupComplexFieldIndex(j, k);// 设置当前列索引
							break;
						}
						k++;
					}
				}
			}
			if (!hasUserFields && dfIndex[i] == -1 && name.startsWith(UserField.USER_FIELD_PREFIX_SIGN)) {
				hasUserFields = true;
			}
		}
		// 处理用户字段
		if (hasUserFields && this.isEnabledUserFields()) {
			if (bo instanceof IBOUserFields) {
				ISqlScripts sqlScripts = this.getSqlScripts();
				if (sqlScripts == null) {
					throw new SqlScriptException(I18N.prop("msg_bobas_invaild_sql_scripts"));
				}
				int fieldCount = boFields.length;
				IBOUserFields uBO = (IBOUserFields) bo;
				// 开启了用户字段功能
				for (int i = 0; i < dfIndex.length; i++) {
					int index = dfIndex[i];
					int rCol = i + 1;
					if (index == -1) {
						if (uBO.getUserFields() != null) {
							String name = metaData.getColumnName(rCol);
							if (name != null && name.startsWith(UserField.USER_FIELD_PREFIX_SIGN)) {
								IUserField userField = uBO.getUserFields().register(name,
										sqlScripts.toDbFieldType(metaData.getColumnTypeName(rCol)));
								dfIndex[i] = fieldCount + uBO.getUserFields().indexOf(userField);// 记录用户字段编号
							}
						}
					}
				}
				// 注册用户字段
				uBO.getUserFields().register();
			}
		}
		return dfIndex;
	}

	/**
	 * 填充业务对象数据
	 * 
	 * @param reader  查询
	 * @param bo      业务对象
	 * @param dfIndex 匹配的索引
	 * @return
	 * @throws DbException
	 * @throws ParsingException
	 */
	protected IManagedFields fillDatas(IDbDataReader reader, IManagedFields bo, int[] dfIndex)
			throws DbException, ParsingException {
		IFieldData[] boFields = bo.getFields();
		// 填充BO数据
		for (int i = 0; i < dfIndex.length; i++) {
			int index = dfIndex[i];
			int rCol = i + 1;// reader列索引
			if (index >= 0) {
				this.fillDatas(reader, rCol, boFields[index]);
			} else if (index < COMPLEX_FIELD_BEGIN_INDEX) {
				// 复合字段索引
				int[] indexs = this.parseComplexFieldIndex(index);
				if (indexs != null) {
					IFieldData fieldData = boFields[indexs[0]];
					int k = 0;
					for (IFieldDataDb item : (IFieldDataDbs) fieldData) {
						if (k == indexs[1]) {
							this.fillDatas(reader, rCol, item);
						}
						k++;
					}
				}
			}
		}
		if (bo instanceof ITrackStatusOperator) {
			// 标记对象为OLD
			ITrackStatusOperator opStatus = (ITrackStatusOperator) bo;
			opStatus.markOld();
		}
		return bo;
	}

	/**
	 * 填充数据
	 * 
	 * @param reader    查询结果
	 * @param rCol      查询列名
	 * @param fieldData 复制的字段
	 * @throws DbException
	 * @throws ParsingException
	 */
	protected void fillDatas(IDbDataReader reader, int rCol, IFieldData fieldData)
			throws DbException, ParsingException {
		if (fieldData.getValueType() == Integer.class) {
			fieldData.setValue(reader.getInt(rCol));
		} else if (fieldData.getValueType() == String.class) {
			fieldData.setValue(reader.getString(rCol));
		} else if (fieldData.getValueType() == BigDecimal.class) {
			fieldData.setValue(reader.getDecimal(rCol));
		} else if (fieldData.getValueType() == Double.class) {
			fieldData.setValue(reader.getDouble(rCol));
		} else if (fieldData.getValueType() == DateTime.class) {
			fieldData.setValue(reader.getDateTime(rCol));
		} else if (fieldData.getValueType() == Short.class) {
			fieldData.setValue(reader.getShort(rCol));
		} else if (fieldData.getValueType().isEnum()) {
			fieldData.setValue(DataConvert.toEnumValue(fieldData.getValueType(), reader.getString(rCol)));
		} else if (fieldData.getValueType() == Float.class) {
			fieldData.setValue(reader.getFloat(rCol));
		} else if (fieldData.getValueType() == Character.class) {
			fieldData.setValue(reader.getString(rCol));
		} else if (fieldData.getValueType() == Boolean.class) {
			fieldData.setValue(reader.getBoolean(rCol));
		} else {
			fieldData.setValue(this.convertValue(fieldData.getValueType(), reader.getObject(rCol)));
		}
	}

	@Override
	public IBusinessObjectBase[] parseBOs(IDbDataReader reader, IBusinessObjectsBase<?> bos) throws ParsingException {
		if (reader == null) {
			throw new ParsingException(I18N.prop("msg_bobas_invaild_data_reader"));
		}
		if (bos == null) {
			throw new ParsingException(I18N.prop("msg_bobas_invaild_bo_list"));
		}
		ArrayList<IBusinessObjectBase> childs = new ArrayList<IBusinessObjectBase>();
		try {
			int[] dfIndex = null;// 数据字段索引数组
			while (reader.next()) {
				IBusinessObjectBase bo = bos.create();
				if (bo == null) {
					throw new ParsingException(I18N.prop("msg_bobas_bo_list_not_support_create_element"));
				}
				if (!(bo instanceof IManagedFields)) {
					throw new ParsingException(I18N.prop("msg_bobas_not_support_bo_type", bo.getClass().getName()));
				}
				IManagedFields boFields = (IManagedFields) bo;
				if (dfIndex == null) {
					dfIndex = this.getFieldIndex(reader, boFields);
				}
				this.fillDatas(reader, boFields, dfIndex);
				childs.add(bo);
			}
		} catch (ParsingException e) {
			throw e;
		} catch (SqlScriptException e) {
			throw new ParsingException(e);
		} catch (DbException e) {
			throw new ParsingException(e);
		} catch (SQLException e) {
			throw new ParsingException(e);
		}
		return childs.toArray(new IBusinessObjectBase[] {});
	}

	@Override
	public IBusinessObjectBase[] parseBOs(IDbDataReader reader, Class<?> boType) throws ParsingException {
		if (reader == null) {
			throw new ParsingException(I18N.prop("msg_bobas_invaild_data_reader"));
		}
		if (boType == null) {
			throw new ParsingException(I18N.prop("msg_bobas_invaild_bo_type"));
		}
		ArrayList<IBusinessObjectBase> bos = new ArrayList<IBusinessObjectBase>();
		try {
			int[] dfIndex = null;// 数据字段索引数组
			IBOFactory iboFactory = BOFactory.create();
			while (reader.next()) {
				IBusinessObjectBase bo = (IBusinessObjectBase) iboFactory.createInstance(boType);
				if (!(bo instanceof IManagedFields)) {
					throw new ParsingException(I18N.prop("msg_bobas_not_support_bo_type", boType.getName()));
				}
				IManagedFields boFields = (IManagedFields) bo;
				if (dfIndex == null) {
					dfIndex = this.getFieldIndex(reader, boFields);
				}
				this.fillDatas(reader, boFields, dfIndex);
				bos.add(bo);
			}
		} catch (SqlScriptException e) {
			throw new ParsingException(e);
		} catch (DbException e) {
			throw new ParsingException(e);
		} catch (SQLException e) {
			throw new ParsingException(e);
		} catch (InstantiationException e) {
			throw new ParsingException(e);
		} catch (IllegalAccessException e) {
			throw new ParsingException(e);
		}
		return bos.toArray(new IBusinessObjectBase[] {});
	}

	/**
	 * 非预定义类型的转换方法
	 * 
	 * @param toType 转换到的类型
	 * @param value  值
	 * @return
	 * @throws ParsingException
	 */
	protected Object convertValue(Class<?> toType, Object value) throws ParsingException {
		throw new ParsingException(I18N.prop("msg_bobas_not_provide_convert_method", toType.getName()));
	}

	@Override
	public ISqlQuery parseTransactionNotification(TransactionType type, IBusinessObjectBase bo)
			throws ParsingException {
		try {
			ISqlScripts sqlScripts = this.getSqlScripts();
			if (sqlScripts == null) {
				throw new SqlScriptException(I18N.prop("msg_bobas_invaild_sql_scripts"));
			}
			String boCode = null;
			if (bo instanceof IBOStorageTag) {
				boCode = ((IBOStorageTag) bo).getObjectCode();
			} else {
				boCode = bo.getClass().getSimpleName();
			}
			int keyCount = 0;
			StringBuilder keyNames = new StringBuilder(), keyValues = new StringBuilder();
			if (bo instanceof IManagedFields) {
				IFieldData[] keys = ((IManagedFields) bo).getFields(c -> c.isPrimaryKey());
				keyCount = keys.length;
				for (int i = 0; i < keys.length; i++) {
					if (keys[i] instanceof IFieldDataDb) {
						IFieldDataDb dbItem = (IFieldDataDb) keys[i];
						if (keyNames.length() > 0) {
							keyNames.append(",");
							keyNames.append(" ");
						}
						if (keyValues.length() > 0) {
							keyValues.append(",");
							keyValues.append(" ");
						}
						keyNames.append(dbItem.getDbField());
						keyValues.append(DataConvert.toString(dbItem.getValue()));
					}
				}
			}
			return new SqlQuery(sqlScripts.getTransactionNotificationQuery(boCode, DataConvert.toDbValue(type),
					keyCount, keyNames.toString(), keyValues.toString()));
		} catch (SqlScriptException e) {
			throw new ParsingException(e);
		}
	}

	@Override
	public ISqlQuery parseSqlQuery(ISqlStoredProcedure sp) throws ParsingException {
		if (sp == null) {
			return null;
		}
		try {
			ISqlScripts sqlScripts = this.getSqlScripts();
			if (sqlScripts == null) {
				throw new SqlScriptException(I18N.prop("msg_bobas_invaild_sql_scripts"));
			}
			return new SqlQuery(
					sqlScripts.groupStoredProcedure(sp.getName(), sp.getParameters().toArray(new KeyValue[] {})));
		} catch (SqlScriptException e) {
			throw new ParsingException(e);
		}
	}

}
