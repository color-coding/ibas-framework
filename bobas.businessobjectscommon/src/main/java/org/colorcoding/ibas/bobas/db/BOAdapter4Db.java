package org.colorcoding.ibas.bobas.db;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.IBODocument;
import org.colorcoding.ibas.bobas.bo.IBODocumentLine;
import org.colorcoding.ibas.bobas.bo.IBOLine;
import org.colorcoding.ibas.bobas.bo.IBOMasterData;
import org.colorcoding.ibas.bobas.bo.IBOMasterDataLine;
import org.colorcoding.ibas.bobas.bo.IBOSimple;
import org.colorcoding.ibas.bobas.bo.IBOSimpleLine;
import org.colorcoding.ibas.bobas.bo.IBOStorageTag;
import org.colorcoding.ibas.bobas.bo.IBOUserFields;
import org.colorcoding.ibas.bobas.bo.ICustomPrimaryKeys;
import org.colorcoding.ibas.bobas.bo.IFieldMaxValueKey;
import org.colorcoding.ibas.bobas.bo.UserField;
import org.colorcoding.ibas.bobas.bo.UserFieldInfo;
import org.colorcoding.ibas.bobas.bo.UserFieldInfoList;
import org.colorcoding.ibas.bobas.bo.UserFieldsFactory;
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
import org.colorcoding.ibas.bobas.core.IBusinessObjectListBase;
import org.colorcoding.ibas.bobas.core.ITrackStatusOperator;
import org.colorcoding.ibas.bobas.core.PropertyInfo;
import org.colorcoding.ibas.bobas.core.PropertyInfoList;
import org.colorcoding.ibas.bobas.core.PropertyInfoManager;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.core.fields.IFieldDataDb;
import org.colorcoding.ibas.bobas.core.fields.IFieldDataDbs;
import org.colorcoding.ibas.bobas.core.fields.IManageFields;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.KeyValue;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.mapping.DbField;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;
import org.colorcoding.ibas.bobas.repository.TransactionType;
import org.colorcoding.ibas.bobas.util.ArrayList;
import org.colorcoding.ibas.bobas.util.StringBuilder;

public abstract class BOAdapter4Db implements IBOAdapter4Db {

	public BOAdapter4Db() {
		this.setEnabledUserFields(
				MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_BO_ENABLED_USER_FIELDS, true));
	}

	private boolean isEnabledUserFields;

	/**
	 * 是否启用自定义字段
	 * 
	 * @return
	 */
	public boolean isEnabledUserFields() {
		return isEnabledUserFields;
	}

	public void setEnabledUserFields(boolean value) {
		this.isEnabledUserFields = value;
	}

	/**
	 * 复合字段开始索引（必须小于0）
	 */
	public final static int COMPLEX_FIELD_BEGIN_INDEX = -100000;

	/**
	 * 组合复合字段索引
	 * 
	 * @param index
	 *            字段索引
	 * @param subIndex
	 *            子项字段索引
	 * @return
	 */
	protected int groupComplexFieldIndex(int index, int subIndex) {
		return COMPLEX_FIELD_BEGIN_INDEX - index * 100 - subIndex;
	}

	/**
	 * 解析复合字段索引
	 * 
	 * @param groupIndex
	 *            组合的索引
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
	protected abstract ISqlScripts getSqlScripts();

	public Iterable<IFieldDataDb> getDbFields(IManageFields bo) {
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
	public ISqlQuery getServerTimeScript() throws BOParseException {
		try {

			ISqlScripts sqlScripts = this.getSqlScripts();
			if (sqlScripts == null) {
				throw new SqlScriptsException(i18n.prop("msg_bobas_invaild_sql_scripts"));
			}
			return sqlScripts.getServerTimeScript();
		} catch (Exception e) {
			throw new BOParseException(e);
		}
	}

	@Override
	public ISqlQuery parseSqlQuery(ICriteria criteria) throws BOParseException {
		if (criteria == null) {
			return null;
		}
		return this.parseSqlQuery(criteria.getConditions());
	}

	/**
	 * 获取查询条件语句
	 * 
	 * @param conditions
	 *            查询条件
	 * @return
	 * @throws SqlScriptsException
	 */
	protected ISqlQuery parseSqlQuery(IConditions conditions) throws BOParseException {
		try {
			if (conditions == null) {
				return null;
			}
			ISqlScripts sqlScripts = this.getSqlScripts();
			if (sqlScripts == null) {
				throw new SqlScriptsException(i18n.prop("msg_bobas_invaild_sql_scripts"));
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
				for (int i = 0; i < condition.getBracketOpenNum(); i++) {
					stringBuilder.append("(");
				}
				// 字段名
				if ((condition.getAliasDataType() == DbFieldType.db_Numeric
						|| condition.getAliasDataType() == DbFieldType.db_Decimal)
						&& (condition.getOperation() == ConditionOperation.co_START
								|| condition.getOperation() == ConditionOperation.co_END
								|| condition.getOperation() == ConditionOperation.co_CONTAIN
								|| condition.getOperation() == ConditionOperation.co_NOT_CONTAIN)) {
					// 数值类型的字段且需要作为字符比较的
					String toVarchar = sqlScripts.getFieldValueCastType(DbFieldType.db_Alphanumeric);
					stringBuilder.appendFormat(toVarchar, String.format(dbObject, condition.getAlias()));
				} else {
					stringBuilder.appendFormat(dbObject, condition.getAlias());
				}
				stringBuilder.append(" ");
				if (condition.getComparedAlias() != null && !condition.getComparedAlias().isEmpty()) {
					// 两字段间比较， [ItemCode] <> [ItemName]
					if (condition.getOperation() == ConditionOperation.co_NONE
							|| condition.getOperation() == ConditionOperation.co_START
							|| condition.getOperation() == ConditionOperation.co_END
							|| condition.getOperation() == ConditionOperation.co_IS_NULL
							|| condition.getOperation() == ConditionOperation.co_NOT_NULL
							|| condition.getOperation() == ConditionOperation.co_CONTAIN
							|| condition.getOperation() == ConditionOperation.co_NOT_CONTAIN) {
						// 字段间不支持以上操作
						throw new BOParseException(i18n.prop("msg_bobas_invaild_condition_operation"));
					}
					// 彭文磊 解决数字和字符串相比较的情况 如“<>”
					// 如果不是字符串，就将比较的字段类型转换成 条件字段的类型
					String toDbFiledType = sqlScripts.getFieldValueCastType(condition.getAliasDataType());
					stringBuilder.appendFormat(sqlScripts.getSqlString(condition.getOperation()),
							String.format(toDbFiledType, String.format(dbObject, condition.getComparedAlias())));

				} else {
					// 字段与值的比较
					if (condition.getOperation() == ConditionOperation.co_IS_NULL
							|| condition.getOperation() == ConditionOperation.co_NOT_NULL) {
						// 不需要值的比较，[ItemName] is NULL
						stringBuilder.append(sqlScripts.getSqlString(condition.getOperation()));
					} else if (condition.getOperation() == ConditionOperation.co_START
							|| condition.getOperation() == ConditionOperation.co_END
							|| condition.getOperation() == ConditionOperation.co_CONTAIN
							|| condition.getOperation() == ConditionOperation.co_NOT_CONTAIN) {
						// like 相关运算
						stringBuilder.append(sqlScripts.getSqlString(condition.getOperation(), condition.getCondVal()));
					} else {
						// 与值比较，[ItemCode] = 'A000001'
						if (condition.getAliasDataType() == DbFieldType.db_Numeric
								|| condition.getAliasDataType() == DbFieldType.db_Decimal) {
							// 数值类型的字段
							stringBuilder.appendFormat(sqlScripts.getSqlString(condition.getOperation()),
									condition.getCondVal());
						} else {
							// 非数值类型字段
							stringBuilder.appendFormat(sqlScripts.getSqlString(condition.getOperation()),
									sqlScripts.getSqlString(condition.getAliasDataType(), condition.getCondVal()));
						}
					}
				}
				// 闭括号
				for (int i = 0; i < condition.getBracketCloseNum(); i++) {
					stringBuilder.append(")");
				}
			}
			return new SqlQuery(stringBuilder.toString());
		} catch (Exception e) {
			throw new BOParseException(e);
		}
	}

	/**
	 * 获取排序语句
	 * 
	 * @param sorts
	 *            排序
	 * @return
	 * @throws SqlScriptsException
	 */
	protected ISqlQuery parseSqlQuery(ISorts sorts) throws BOParseException {
		try {

			if (sorts == null) {
				return null;
			}
			ISqlScripts sqlScripts = this.getSqlScripts();
			if (sqlScripts == null) {
				throw new SqlScriptsException(i18n.prop("msg_bobas_invaild_sql_scripts"));
			}
			StringBuilder stringBuilder = new StringBuilder();
			String dbObject = sqlScripts.getDbObjectSign();
			for (ISort sort : sorts) {
				if (stringBuilder.length() > 0) {
					stringBuilder.append(sqlScripts.getFieldBreakSign());
				}
				stringBuilder.appendFormat(dbObject, sort.getAlias());
				stringBuilder.append(" ");
				stringBuilder.append(sqlScripts.getSqlString(sort.getSortType()));
			}
			return new SqlQuery(stringBuilder.toString());
		} catch (Exception e) {
			throw new BOParseException(e);
		}
	}

	/**
	 * 修正查询条件（包括：db字段名，类型）
	 * 
	 * @param conditions
	 *            查询条件
	 * @param pInfoList
	 *            属性列表
	 * @return
	 */
	protected IConditions fixConditions(IConditions conditions, PropertyInfoList pInfoList) {
		DbField dbField = null;
		for (int i = 0; i < pInfoList.size(); i++) {
			PropertyInfo<?> cProperty = (PropertyInfo<?>) pInfoList.get(i);
			if (cProperty.getName() == null || cProperty.getName().isEmpty()) {
				continue;
			}
			Object annotation = cProperty.getAnnotation(DbField.class);
			if (annotation != null) {
				// 绑定数据库的字段
				dbField = (DbField) annotation;
				if (dbField.name() == null || dbField.name().isEmpty()) {
					continue;
				}
				// 修正查询条件的字段名称
				for (ICondition condition : conditions) {
					if (cProperty.getName().equals(condition.getAlias())) {
						condition.setAlias(dbField.name());
						DbFieldType dbFieldType = dbField.type();
						condition.setAliasDataType(dbFieldType);
					}
				}
			}
		}
		return conditions;
	}

	/**
	 * 修正排序条件
	 * 
	 * @param sorts
	 *            排序
	 * @param pInfoList
	 *            属性列表
	 * @return
	 */
	protected ISorts fixSorts(ISorts sorts, PropertyInfoList pInfoList) {
		DbField dbField = null;
		for (int i = 0; i < pInfoList.size(); i++) {
			PropertyInfo<?> cProperty = (PropertyInfo<?>) pInfoList.get(i);
			if (cProperty.getName() == null || cProperty.getName().isEmpty()) {
				continue;
			}
			Object annotation = cProperty.getAnnotation(DbField.class);
			if (annotation != null) {
				// 绑定数据库的字段
				dbField = (DbField) annotation;
				if (dbField.name() == null || dbField.name().isEmpty()) {
					continue;
				}
				// 修正排序的字段名称
				for (ISort sort : sorts) {
					if (cProperty.getName().equals(sort.getAlias())) {
						sort.setAlias(dbField.name());
					}
				}
			}
		}
		return sorts;
	}

	@Override
	public ISqlQuery parseSqlQuery(ICriteria criteria, Class<?> boType) throws BOParseException {
		try {

			// select top 100 * from "OUSR" where "Supper" = 'Y' order by "Code"
			// select * from "OUSR" where "Supper" = 'Y' order by "Code LIMIT
			// 100 "
			ISqlScripts sqlScripts = this.getSqlScripts();
			if (sqlScripts == null) {
				throw new SqlScriptsException(i18n.prop("msg_bobas_invaild_sql_scripts"));
			}
			String table = null;
			if (criteria == null) {
				criteria = new Criteria();
			}
			try {
				DbField dbField = null;
				boolean hasUserFields = false;
				PropertyInfoList pInfoList = PropertyInfoManager.getPropertyInfoList(boType);
				for (int i = 0; i < pInfoList.size(); i++) {
					PropertyInfo<?> cProperty = (PropertyInfo<?>) pInfoList.get(i);
					if (cProperty.getName() == null || cProperty.getName().isEmpty()) {
						continue;
					}
					Object annotation = cProperty.getAnnotation(DbField.class);
					if (annotation != null) {
						// 绑定数据库的字段
						dbField = (DbField) annotation;
						if (dbField.name() == null || dbField.name().isEmpty()) {
							continue;
						}
						if (dbField.primaryKey()) {
							table = dbField.table();
						}
						// 修正查询条件的字段名称
						for (ICondition condition : criteria.getConditions()) {
							if (cProperty.getName().equals(condition.getAlias())) {
								condition.setAlias(dbField.name());
								DbFieldType dbFieldType = dbField.type();
								condition.setAliasDataType(dbFieldType);
							}
							if (!hasUserFields && condition.getAlias().startsWith(UserField.USER_FIELD_PREFIX_SIGN)) {
								// 存在用户字段
								hasUserFields = true;
							}
						}
						// 修正排序的字段名称
						for (ISort sort : criteria.getSorts()) {
							if (cProperty.getName().equals(sort.getAlias())) {
								sort.setAlias(dbField.name());
							}
						}
					}
				}
				if (table == null && dbField != null) {
					// 没有主键
					table = dbField.table();
				}
				if (hasUserFields) {
					// 存在用户字段查询，修正查询字段类型
					UserFieldInfoList userFieldInfoList = UserFieldsFactory.create().getUserFieldInfoList(boType);
					if (userFieldInfoList != null) {
						for (UserFieldInfo userFieldInfo : userFieldInfoList) {
							for (ICondition condition : criteria.getConditions()) {
								if (userFieldInfo.getName().equals(condition.getAlias())) {
									condition.setAliasDataType(userFieldInfo.getValueType());
								}
							}
						}
					}
				}
			} catch (Exception e) {
				throw new SqlScriptsException(e.getMessage(), e);
			}
			if (table == null || table.isEmpty()) {
				throw new BOParseException(i18n.prop("msg_bobas_not_found_bo_table", boType.getName()));
			}
			int result = criteria.getResultCount();
			table = String.format(sqlScripts.getDbObjectSign(), table);
			String order = this.parseSqlQuery(criteria.getSorts()).getQueryString();
			String where = this.parseSqlQuery(criteria.getConditions()).getQueryString();
			return new SqlQuery(sqlScripts.groupSelectQuery("*", table, where, order, result));
		} catch (Exception e) {
			throw new BOParseException(e);
		}
	}

	@Override
	public ISqlQuery parseSqlInsert(IBusinessObjectBase bo) throws BOParseException {
		try {
			if (!(bo instanceof IManageFields)) {
				throw new BOParseException(i18n.prop("msg_bobas_invaild_bo"));
			}
			ISqlScripts sqlScripts = this.getSqlScripts();
			if (sqlScripts == null) {
				throw new SqlScriptsException(i18n.prop("msg_bobas_invaild_sql_scripts"));
			}
			IManageFields boFields = (IManageFields) bo;
			String table = this.getBOMasterTable(boFields);
			if (table == null || table.isEmpty()) {
				// 没有获取到表
				throw new BOParseException(i18n.prop("msg_bobas_not_found_bo_table", bo.getClass().getName()));
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
				fieldsBuilder.appendFormat(sqlScripts.getDbObjectSign(), dbItem.getDbField());
				Object value = dbItem.getValue();
				if (value == null) {
					valuesBuilder.append(sqlScripts.getNullValue());
				} else {
					valuesBuilder
							.appendFormat(sqlScripts.getSqlString(dbItem.getFieldType(), DataConvert.toDbValue(value)));
				}
			}
			if (fieldsBuilder.length() == 0) {
				// 没有字段
				throw new BOParseException(i18n.prop("msg_bobas_not_allow_sql_scripts"));
			}
			if (valuesBuilder.length() == 0) {
				// 没有字段值
				throw new BOParseException(i18n.prop("msg_bobas_not_allow_sql_scripts"));
			}
			return new SqlQuery(sqlScripts.groupInsertQuery(table, fieldsBuilder.toString(), valuesBuilder.toString()));
		} catch (Exception e) {
			throw new BOParseException(e);
		}
	}

	/**
	 * 获取字段及值语句
	 * 
	 * @param fields
	 *            字段列表
	 * @return 语句（"ItemCode" = 'A00001' ，"ItemName" = 'CPU I9'）
	 * @throws SqlScriptsException
	 */
	protected String getBOFieldValues(Iterable<IFieldDataDb> fields, String split) throws SqlScriptsException {
		ISqlScripts sqlScripts = this.getSqlScripts();
		if (sqlScripts == null) {
			throw new SqlScriptsException(i18n.prop("msg_bobas_invaild_sql_scripts"));
		}
		StringBuilder stringBuilder = new StringBuilder();
		for (IFieldDataDb dbItem : fields) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(split);
			}
			stringBuilder.appendFormat(sqlScripts.getDbObjectSign(), dbItem.getDbField());
			stringBuilder.append(" ");
			stringBuilder.append("=");
			stringBuilder.append(" ");
			Object value = dbItem.getValue();
			if (value == null) {
				stringBuilder.append(sqlScripts.getNullValue());
			} else {
				stringBuilder
						.appendFormat(sqlScripts.getSqlString(dbItem.getFieldType(), DataConvert.toDbValue(value)));
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * 获取字段的主表
	 * 
	 * @param boFields
	 *            字段列表
	 * @return 表名称（“OITM”）
	 */
	protected String getBOMasterTable(IManageFields boFields) {
		for (IFieldData item : boFields.getKeyFields()) {
			if (item instanceof IFieldDataDb) {
				IFieldDataDb dbItem = (IFieldDataDb) item;
				return dbItem.getDbTable();
			}
		}
		return null;
	}

	@Override
	public ISqlQuery parseSqlDelete(IBusinessObjectBase bo) throws BOParseException {
		try {
			if (!(bo instanceof IManageFields)) {
				throw new BOParseException(i18n.prop("msg_bobas_invaild_bo"));
			}
			ISqlScripts sqlScripts = this.getSqlScripts();
			if (sqlScripts == null) {
				throw new SqlScriptsException(i18n.prop("msg_bobas_invaild_sql_scripts"));
			}
			IManageFields boFields = (IManageFields) bo;
			String table = this.getBOMasterTable(boFields);
			if (table == null || table.isEmpty()) {
				// 没有获取到表
				throw new BOParseException(i18n.prop("msg_bobas_not_found_bo_table", bo.getClass().getName()));
			}
			table = String.format(sqlScripts.getDbObjectSign(), table);
			String partWhere = this.getBOFieldValues(this.getDbFields(boFields.getKeyFields()),
					sqlScripts.getAndSign());
			if (partWhere == null || partWhere.isEmpty()) {
				// 没有条件的删除不允许执行
				throw new BOParseException(i18n.prop("msg_bobas_not_allow_sql_scripts"));
			}
			return new SqlQuery(sqlScripts.groupDeleteQuery(table, partWhere));
		} catch (Exception e) {
			throw new BOParseException(e);
		}
	}

	@Override
	public ISqlQuery parseSqlUpdate(IBusinessObjectBase bo) throws BOParseException {
		try {

			if (!(bo instanceof IManageFields)) {
				throw new BOParseException(i18n.prop("msg_bobas_invaild_bo"));
			}
			ISqlScripts sqlScripts = this.getSqlScripts();
			if (sqlScripts == null) {
				throw new SqlScriptsException(i18n.prop("msg_bobas_invaild_sql_scripts"));
			}
			IManageFields boFields = (IManageFields) bo;
			String table = this.getBOMasterTable(boFields);
			if (table == null || table.isEmpty()) {
				// 没有获取到表
				throw new BOParseException(i18n.prop("msg_bobas_not_found_bo_table", bo.getClass().getName()));
			}
			table = String.format(sqlScripts.getDbObjectSign(), table);
			String partWhere = this.getBOFieldValues(this.getDbFields(boFields.getKeyFields()),
					sqlScripts.getAndSign());
			if (partWhere == null || partWhere.isEmpty()) {
				// 没有条件的删除不允许执行
				throw new BOParseException(i18n.prop("msg_bobas_not_allow_sql_scripts"));
			}
			ArrayList<IFieldDataDb> fieldDatas = new ArrayList<IFieldDataDb>();
			for (IFieldDataDb iFieldData : this.getDbFields(boFields)) {
				if (iFieldData.isDirty()) {
					fieldDatas.add(iFieldData);
				}
			}
			String partFieldValues = this.getBOFieldValues(fieldDatas, sqlScripts.getFieldBreakSign());
			return new SqlQuery(sqlScripts.groupUpdateQuery(table, partFieldValues, partWhere));
		} catch (Exception e) {
			throw new BOParseException(e);
		}
	}

	/**
	 * 获取匹配的索引（提升性能），此处包括对自定义字段的处理
	 * 
	 * @param reader
	 *            查询
	 * @param bo
	 *            对象
	 * @return
	 * @throws BOParseException
	 * @throws DbException
	 * @throws SQLException
	 * @throws SqlScriptsException
	 */
	protected int[] getFieldIndex(IDbDataReader reader, IManageFields bo)
			throws BOParseException, DbException, SQLException, SqlScriptsException {
		// 构建索引
		IFieldData[] boFields = bo.getFields();
		int[] dfIndex = null;// 数据字段索引数组
		ResultSetMetaData metaData = reader.getMetaData();
		dfIndex = new int[metaData.getColumnCount()];
		boolean hasUserFields = false;// 存在未被添加的自定义字段
		for (int i = 0; i < dfIndex.length; i++) {
			// 初始化索引
			dfIndex[i] = -1;
			// reader列索引
			int rCol = i + 1;
			// 当前列名称
			String name = metaData.getColumnName(rCol);// reader索引从1开始
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
		// 处理自定义字段
		if (hasUserFields && this.isEnabledUserFields()) {
			if (bo instanceof IBOUserFields) {
				ISqlScripts sqlScripts = this.getSqlScripts();
				if (sqlScripts == null) {
					throw new SqlScriptsException(i18n.prop("msg_bobas_invaild_sql_scripts"));
				}
				IBOUserFields uBO = (IBOUserFields) bo;
				// 开启了自定义字段功能
				for (int i = 0; i < dfIndex.length; i++) {
					int index = dfIndex[i];
					int rCol = i + 1;
					if (index == -1) {
						if (uBO.getUserFields() != null) {
							String name = metaData.getColumnName(rCol);
							if (name != null && name.startsWith(UserField.USER_FIELD_PREFIX_SIGN)) {
								uBO.getUserFields().addUserField(name,
										sqlScripts.getDbFieldType(metaData.getColumnTypeName(rCol)));
								dfIndex[i] = bo.getFields().length - 1;// 记录自定义字段编号
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
	 * @param reader
	 *            查询
	 * @param bo
	 *            业务对象
	 * @param dfIndex
	 *            匹配的索引
	 * @return
	 * @throws DbException
	 * @throws BOParseException
	 */
	protected IManageFields fillDatas(IDbDataReader reader, IManageFields bo, int[] dfIndex)
			throws DbException, BOParseException {
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
	 * @param reader
	 *            查询结果
	 * @param rCol
	 *            查询列名
	 * @param fieldData
	 *            复制的字段
	 * @throws DbException
	 * @throws BOParseException
	 */
	protected void fillDatas(IDbDataReader reader, int rCol, IFieldData fieldData)
			throws DbException, BOParseException {
		if (fieldData.getValueType() == Integer.class) {
			fieldData.setValue(reader.getInt(rCol));
		} else if (fieldData.getValueType() == String.class) {
			fieldData.setValue(reader.getString(rCol));
		} else if (fieldData.getValueType() == Decimal.class) {
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
	public IBusinessObjectBase[] parseBOs(IDbDataReader reader, IBusinessObjectListBase<?> bos)
			throws BOParseException {
		if (reader == null) {
			throw new BOParseException(i18n.prop("msg_bobas_invaild_data_reader"));
		}
		if (bos == null) {
			throw new BOParseException(i18n.prop("msg_bobas_invaild_bo_list"));
		}
		ArrayList<IBusinessObjectBase> childs = new ArrayList<IBusinessObjectBase>();
		try {
			int[] dfIndex = null;// 数据字段索引数组
			while (reader.next()) {
				IBusinessObjectBase bo = bos.create();
				if (bo == null) {
					throw new BOParseException(i18n.prop("msg_bobas_bo_list_not_support_create_element"));
				}
				if (!(bo instanceof IManageFields)) {
					throw new BOParseException(i18n.prop("msg_bobas_not_support_bo_type", bo.getClass().getName()));
				}
				IManageFields boFields = (IManageFields) bo;
				if (dfIndex == null) {
					dfIndex = this.getFieldIndex(reader, boFields);
				}
				this.fillDatas(reader, boFields, dfIndex);
				childs.add(bo);
			}
		} catch (Exception e) {
			throw new BOParseException(e.getMessage(), e);
		}
		return childs.toArray(new IBusinessObjectBase[] {});
	}

	@Override
	public IBusinessObjectBase[] parseBOs(IDbDataReader reader, Class<?> boType) throws BOParseException {
		if (reader == null) {
			throw new BOParseException(i18n.prop("msg_bobas_invaild_data_reader"));
		}
		if (boType == null) {
			throw new BOParseException(i18n.prop("msg_bobas_invaild_bo_type"));
		}
		ArrayList<IBusinessObjectBase> bos = new ArrayList<IBusinessObjectBase>();
		try {
			int[] dfIndex = null;// 数据字段索引数组
			IBOFactory iboFactory = BOFactory.create();
			while (reader.next()) {
				IBusinessObjectBase bo = (IBusinessObjectBase) iboFactory.createInstance(boType);
				if (!(bo instanceof IManageFields)) {
					throw new BOParseException(i18n.prop("msg_bobas_not_support_bo_type", boType.getName()));
				}
				IManageFields boFields = (IManageFields) bo;
				if (dfIndex == null) {
					dfIndex = this.getFieldIndex(reader, boFields);
				}
				this.fillDatas(reader, boFields, dfIndex);
				bos.add(bo);
			}
		} catch (Exception e) {
			throw new BOParseException(e.getMessage(), e);
		}
		return bos.toArray(new IBusinessObjectBase[] {});
	}

	/**
	 * 非预定义类型的转换方法
	 * 
	 * @param toType
	 *            转换到的类型
	 * @param value
	 *            值
	 * @return
	 * @throws BOParseException
	 */
	protected Object convertValue(Class<?> toType, Object value) throws BOParseException {
		throw new BOParseException(i18n.prop("msg_bobas_not_provide_convert_method", toType.getName()));
	}

	@Override
	public void setPrimaryKeys(IBusinessObjectBase bo, KeyValue[] keys) {
		if (bo instanceof IBODocument) {
			IBODocument boKey = (IBODocument) bo;
			for (KeyValue key : keys) {
				if (IBODocument.MASTER_PRIMARY_KEY_NAME.equals(key.key)) {
					boKey.setDocEntry((int) (key.value));
				}
			}
		} else if (bo instanceof IBODocumentLine) {
			IBODocumentLine boKey = (IBODocumentLine) bo;
			for (KeyValue key : keys) {
				if (IBODocumentLine.MASTER_PRIMARY_KEY_NAME.equals(key.key)) {
					boKey.setDocEntry((int) (key.value));
				} else if (IBODocumentLine.SECONDARY_PRIMARY_KEY_NAME.equals(key.key)) {
					boKey.setLineId((int) (key.value));
				}
			}
		} else if (bo instanceof IBOMasterData) {
			IBOMasterData boKey = (IBOMasterData) bo;
			for (KeyValue key : keys) {
				if (IBOMasterData.SERIAL_NUMBER_KEY_NAME.equals(key.key)) {
					boKey.setDocEntry((int) (key.value));
				} else if (IBOMasterData.MASTER_PRIMARY_KEY_NAME.equals(key.key)) {
					boKey.setCode(String.valueOf(key.value));
				}
			}
		} else if (bo instanceof IBOMasterDataLine) {
			IBOMasterDataLine boKey = (IBOMasterDataLine) bo;
			for (KeyValue key : keys) {
				if (IBOMasterDataLine.SECONDARY_PRIMARY_KEY_NAME.equals(key.key)) {
					boKey.setLineId((int) (key.value));
				} else if (IBOMasterDataLine.MASTER_PRIMARY_KEY_NAME.equals(key.key)) {
					boKey.setCode(String.valueOf(key.value));
				}
			}
		} else if (bo instanceof IBOSimple) {
			IBOSimple boKey = (IBOSimple) bo;
			for (KeyValue key : keys) {
				if (IBOSimple.MASTER_PRIMARY_KEY_NAME.equals(key.key)) {
					boKey.setObjectKey((int) (key.value));
				}
			}
		} else if (bo instanceof IBOSimpleLine) {
			IBOSimpleLine boKey = (IBOSimpleLine) bo;
			for (KeyValue key : keys) {
				if (IBOSimpleLine.MASTER_PRIMARY_KEY_NAME.equals(key.key)) {
					boKey.setObjectKey((int) (key.value));
				} else if (IBOSimpleLine.SECONDARY_PRIMARY_KEY_NAME.equals(key.key)) {
					boKey.setLineId((int) (key.value));
				}
			}
		} else if (bo instanceof ICustomPrimaryKeys) {
			// 自定义主键
			if (bo instanceof IFieldMaxValueKey) {
				IFieldMaxValueKey maxValueKey = (IFieldMaxValueKey) bo;
				IFieldDataDb dbField = maxValueKey.getMaxValueField();
				for (KeyValue key : keys) {
					if (dbField.getName().equals(key.key)) {
						dbField.setValue(key.value);
					}
				}
			}
		}
	}

	@Override
	public KeyValue[] usePrimaryKeys(IBusinessObjectBase bo, IDbCommand command) throws BOParseException {
		// 获取主键
		KeyValue[] keys = this.parsePrimaryKeys(bo, command);
		if (keys == null || keys.length == 0) {
			throw new BOParseException(i18n.prop("msg_bobas_not_found_bo_primary_keys", bo.getClass().getName()));
		}
		// 主键赋值
		this.setPrimaryKeys(bo, keys);
		// 更新主键
		this.updatePrimaryKeyRecords(bo, command);
		return keys;
	}

	@Override
	public KeyValue[] parsePrimaryKeys(IBusinessObjectBase bo, IDbCommand command) throws BOParseException {
		try {
			ISqlScripts sqlScripts = this.getSqlScripts();
			if (sqlScripts == null) {
				throw new SqlScriptsException(i18n.prop("msg_bobas_invaild_sql_scripts"));
			}
			ArrayList<KeyValue> keys = new ArrayList<KeyValue>();
			IDbDataReader reader = null;
			if (bo instanceof IBODocument) {
				// 业务单据主键
				IBODocument item = (IBODocument) bo;
				reader = command.executeReader(sqlScripts.getBOPrimaryKeyQuery(item.getObjectCode()));
				if (reader.next()) {
					keys.add(new KeyValue(IBODocument.MASTER_PRIMARY_KEY_NAME, reader.getInt(1)));
					reader.close();
				} else {
					reader.close();
					throw new SqlScriptsException(
							i18n.prop("msg_bobas_not_found_bo_primary_keys", bo.getClass().getName()));
				}
			} else if (bo instanceof IBODocumentLine) {
				// 业务单据行主键
				IBODocumentLine item = (IBODocumentLine) bo;
				ICriteria criteria = new Criteria();
				ICondition condition = criteria.getConditions().create();
				condition.setAlias(IBODocumentLine.MASTER_PRIMARY_KEY_NAME);
				condition.setAliasDataType(DbFieldType.db_Numeric);
				condition.setCondVal(item.getDocEntry().toString());
				String table = String.format(sqlScripts.getDbObjectSign(), this.getBOMasterTable((IManageFields) bo));
				String field = String.format(sqlScripts.getDbObjectSign(), IBODocumentLine.SECONDARY_PRIMARY_KEY_NAME);
				String where = this.parseSqlQuery(criteria.getConditions()).getQueryString();
				reader = command.executeReader(sqlScripts.groupMaxValueQuery(field, table, where));
				if (reader.next()) {
					keys.add(new KeyValue(IBODocumentLine.MASTER_PRIMARY_KEY_NAME, item.getDocEntry()));
					keys.add(new KeyValue(IBODocumentLine.SECONDARY_PRIMARY_KEY_NAME, reader.getInt(1) + 1));
					reader.close();
				} else {
					reader.close();
					throw new SqlScriptsException(
							i18n.prop("msg_bobas_not_found_bo_primary_keys", bo.getClass().getName()));
				}
			} else if (bo instanceof IBOMasterData) {
				// 主数据主键
				IBOMasterData item = (IBOMasterData) bo;
				reader = command.executeReader(sqlScripts.getBOPrimaryKeyQuery(item.getObjectCode()));
				if (reader.next()) {
					keys.add(new KeyValue(IBOMasterData.MASTER_PRIMARY_KEY_NAME, item.getCode()));
					keys.add(new KeyValue(IBOMasterData.SERIAL_NUMBER_KEY_NAME, reader.getInt(1)));
					reader.close();
				} else {
					reader.close();
					throw new SqlScriptsException(
							i18n.prop("msg_bobas_not_found_bo_primary_keys", bo.getClass().getName()));
				}
			} else if (bo instanceof IBOMasterDataLine) {
				// 主数据行主键
				IBOMasterDataLine item = (IBOMasterDataLine) bo;
				ICriteria criteria = new Criteria();
				ICondition condition = criteria.getConditions().create();
				condition.setAlias(IBOMasterDataLine.MASTER_PRIMARY_KEY_NAME);
				condition.setCondVal(item.getCode());
				String table = String.format(sqlScripts.getDbObjectSign(), this.getBOMasterTable((IManageFields) bo));
				String field = String.format(sqlScripts.getDbObjectSign(),
						IBOMasterDataLine.SECONDARY_PRIMARY_KEY_NAME);
				String where = this.parseSqlQuery(criteria.getConditions()).getQueryString();
				reader = command.executeReader(sqlScripts.groupMaxValueQuery(field, table, where));
				if (reader.next()) {
					keys.add(new KeyValue(IBOMasterDataLine.MASTER_PRIMARY_KEY_NAME, item.getCode()));
					keys.add(new KeyValue(IBOMasterDataLine.SECONDARY_PRIMARY_KEY_NAME, reader.getInt(1) + 1));
					reader.close();
				} else {
					reader.close();
					throw new SqlScriptsException(
							i18n.prop("msg_bobas_not_found_bo_primary_keys", bo.getClass().getName()));
				}
			} else if (bo instanceof IBOSimple) {
				// 简单对象主键
				IBOSimple item = (IBOSimple) bo;
				reader = command.executeReader(sqlScripts.getBOPrimaryKeyQuery(item.getObjectCode()));
				if (reader.next()) {
					keys.add(new KeyValue(IBOSimple.MASTER_PRIMARY_KEY_NAME, reader.getInt(1)));
					reader.close();
				} else {
					reader.close();
					throw new SqlScriptsException(
							i18n.prop("msg_bobas_not_found_bo_primary_keys", bo.getClass().getName()));
				}
			} else if (bo instanceof IBOSimpleLine) {
				// 简单对象行主键
				IBOSimpleLine item = (IBOSimpleLine) bo;
				ICriteria criteria = new Criteria();
				ICondition condition = criteria.getConditions().create();
				condition.setAlias(IBOSimpleLine.MASTER_PRIMARY_KEY_NAME);
				condition.setAliasDataType(DbFieldType.db_Numeric);
				condition.setCondVal(item.getObjectKey());
				String table = String.format(sqlScripts.getDbObjectSign(), this.getBOMasterTable((IManageFields) bo));
				String field = String.format(sqlScripts.getDbObjectSign(), IBOSimpleLine.SECONDARY_PRIMARY_KEY_NAME);
				String where = this.parseSqlQuery(criteria.getConditions()).getQueryString();
				reader = command.executeReader(sqlScripts.groupMaxValueQuery(field, table, where));
				if (reader.next()) {
					keys.add(new KeyValue(IBOSimpleLine.MASTER_PRIMARY_KEY_NAME, item.getObjectKey()));
					keys.add(new KeyValue(IBOSimpleLine.SECONDARY_PRIMARY_KEY_NAME, reader.getInt(1) + 1));
					reader.close();
				} else {
					reader.close();
					throw new SqlScriptsException(
							i18n.prop("msg_bobas_not_found_bo_primary_keys", bo.getClass().getName()));
				}

			} else {
				// 没有指定主键的获取方式
				/*
				 * throw new SqlScriptsException( i18n.prop(
				 * "msg_bobas_not_specify_primary_keys_obtaining_method",
				 * bo.getClass().getName()));
				 */
			}
			// 额外主键获取
			if (bo instanceof ICustomPrimaryKeys) {
				// 自定义主键
				if (bo instanceof IFieldMaxValueKey) {
					// 字段最大值
					IFieldMaxValueKey maxValueKey = (IFieldMaxValueKey) bo;
					IFieldDataDb dbField = maxValueKey.getMaxValueField();
					String table = String.format(sqlScripts.getDbObjectSign(), dbField.getDbTable());
					String field = String.format(sqlScripts.getDbObjectSign(), dbField.getDbField());
					PropertyInfoList pInfoList = PropertyInfoManager.getPropertyInfoList(bo.getClass());
					IConditions conditions = this.fixConditions(maxValueKey.getMaxValueConditions(), pInfoList);
					String where = this.parseSqlQuery(conditions).getQueryString();
					reader = command.executeReader(sqlScripts.groupMaxValueQuery(field, table, where));
					if (reader.next()) {
						keys.add(new KeyValue(dbField.getName(), reader.getInt(1) + 1));
						reader.close();
					} else {
						reader.close();
						throw new SqlScriptsException(
								i18n.prop("msg_bobas_not_found_bo_primary_keys", bo.getClass().getName()));
					}
				}
			}
			return keys.toArray(new KeyValue[] {});
		} catch (Exception e) {
			throw new BOParseException(e);
		}
	}

	@Override
	public void updatePrimaryKeyRecords(IBusinessObjectBase bo, IDbCommand command) throws BOParseException {
		this.updatePrimaryKeyRecords(bo, 1, command);
	}

	@Override
	public void updatePrimaryKeyRecords(IBusinessObjectBase bo, int addValue, IDbCommand command)
			throws BOParseException {
		try {
			if (bo instanceof IBOLine) {
				// 对象行，不做处理
				return;
			}
			ISqlScripts sqlScripts = this.getSqlScripts();
			if (sqlScripts == null) {
				throw new SqlScriptsException(i18n.prop("msg_bobas_invaild_sql_scripts"));
			}
			int nextValue = 0;
			String boCode = null;
			if (bo instanceof IBODocument) {
				// 业务单据主键
				IBODocument item = (IBODocument) bo;
				nextValue = item.getDocEntry() + addValue;
				boCode = item.getObjectCode();
			} else if (bo instanceof IBOMasterData) {
				// 主数据主键
				IBOMasterData item = (IBOMasterData) bo;
				nextValue = item.getDocEntry() + addValue;
				boCode = item.getObjectCode();
			} else if (bo instanceof IBOSimple) {
				// 简单对象主键
				IBOSimple item = (IBOSimple) bo;
				nextValue = item.getObjectKey() + addValue;
				boCode = item.getObjectCode();
			}
			if (boCode == null || nextValue == 0) {
				// 未能有效解析
				throw new BOParseException(i18n.prop("msg_bobas_not_specify_primary_keys_obtaining_method"));
			}
			// 更新数据记录
			command.executeUpdate(sqlScripts.getUpdateBOPrimaryKeyScript(boCode, addValue));
		} catch (Exception e) {
			throw new BOParseException(e);
		}
	}

	@Override
	public ISqlQuery parseBOTransactionNotification(TransactionType type, IBusinessObjectBase bo)
			throws BOParseException {
		try {
			ISqlScripts sqlScripts = this.getSqlScripts();
			if (sqlScripts == null) {
				throw new SqlScriptsException(i18n.prop("msg_bobas_invaild_sql_scripts"));
			}
			String boCode = ((IBOStorageTag) bo).getObjectCode();
			IFieldData[] keys = ((IManageFields) bo).getKeyFields();
			int keyCount = keys.length;
			StringBuilder keyNames = new StringBuilder(), keyValues = new StringBuilder();
			for (int i = 0; i < keys.length; i++) {
				if (keys[i] instanceof IFieldDataDb) {
					IFieldDataDb dbItem = (IFieldDataDb) keys[i];
					if (keyNames.length() > 0) {
						keyNames.append(",");
						keyNames.append(" ");
					}
					if (keyValues.length() > 0) {
						keyNames.append(",");
						keyNames.append(" ");
					}
					keyNames.append(dbItem.getDbField());
					keyValues.append(DataConvert.toString(dbItem.getValue()));
				}
			}
			return new SqlQuery(sqlScripts.getBOTransactionNotificationScript(boCode, DataConvert.toDbValue(type),
					keyCount, keyNames.toString(), keyValues.toString()));
		} catch (Exception e) {
			throw new BOParseException(e);
		}
	}

	@Override
	public ISqlQuery parseSqlQuery(ISqlStoredProcedure sp) throws BOParseException {
		if (sp == null) {
			return null;
		}
		try {
			ISqlScripts sqlScripts = this.getSqlScripts();
			if (sqlScripts == null) {
				throw new SqlScriptsException(i18n.prop("msg_bobas_invaild_sql_scripts"));
			}
			return new SqlQuery(
					sqlScripts.groupStoredProcedure(sp.getName(), sp.getParameters().toArray(new KeyValue[] {})));
		} catch (Exception e) {
			throw new BOParseException(e);
		}
	}
}
