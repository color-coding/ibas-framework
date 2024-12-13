package org.colorcoding.ibas.bobas.db;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.bo.BOFactory;
import org.colorcoding.ibas.bobas.bo.BusinessObject;
import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.DataConvert;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Enums;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.ISort;
import org.colorcoding.ibas.bobas.common.SortType;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.IArrayList;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.logging.Logger;

/**
 * 数据库适配器
 */
public abstract class DbAdapter {

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
	 * @param boType    对象类型
	 * @param resultSet 结果集
	 * @return
	 * @throws SQLException
	 */
	public <T> IArrayList<T> parsingDatas(Class<T> boType, ResultSet resultSet) throws SQLException {
		ArrayList<T> datas = new ArrayList<>();
		IPropertyInfo<?>[] orderProperties = null;
		while (resultSet.next()) {
			if (orderProperties == null) {
				IArrayList<IPropertyInfo<?>> propertyInfos = BOFactory.propertyInfos(boType);
				orderProperties = new IPropertyInfo<?>[propertyInfos.size()];
				IPropertyInfo<?> propertyInfo;
				DbField dbField;
				int index;
				for (int i = 0; i < orderProperties.length; i++) {
					try {
						propertyInfo = propertyInfos.get(i);
						dbField = propertyInfo.getAnnotation(DbField.class);
						index = resultSet.findColumn(dbField.name());
						if (index >= 0) {
							orderProperties[i] = propertyInfo;
						}
					} catch (Exception e) {
						Logger.log(e);
					}
				}
				propertyInfos = null;
				propertyInfo = null;
				dbField = null;
			}
			datas.add(this.setProperties(BOFactory.newInstance(boType), resultSet, orderProperties));
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> T setProperties(T data, ResultSet resultSet, IPropertyInfo<?>[] orderProperties) throws SQLException {
		if (data instanceof BusinessObject<?>) {
			BusinessObject<?> boData = ((BusinessObject<?>) data);
			IPropertyInfo propertyInfo;
			boData.setLoading(true);
			for (int i = 0; i < orderProperties.length; i++) {
				propertyInfo = orderProperties[i];
				boData.setProperty(propertyInfo, this.parsingValue(resultSet, i, propertyInfo.getValueType()));
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

	public int toSqlTypes(DbFieldType type) {
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
		if (criteria == null) {
			criteria = new Criteria();
		} else {
			criteria = criteria.clone();
		}
		DbField dbField;
		for (ICondition condition : criteria.getConditions()) {
			for (IPropertyInfo<?> propertyInfo : BOFactory.propertyInfos(boType)) {
				if (Strings.equalsIgnoreCase(condition.getAlias(), propertyInfo.getName())) {
					// 属性名称转数据库字段
					dbField = propertyInfo.getAnnotation(DbField.class);
					if (dbField != null) {
						condition.setAlias(dbField.name());
					}
					condition.setAliasDataType(DbFieldType.valueOf(propertyInfo.getClass()));
				}
				if (Strings.equalsIgnoreCase(condition.getComparedAlias(), propertyInfo.getName())) {
					// 属性名称转数据库字段
					dbField = propertyInfo.getAnnotation(DbField.class);
					if (dbField != null) {
						condition.setComparedAlias(dbField.name());
					}
				}
				// like，改为字符串比较
				if (condition.getOperation() == ConditionOperation.CONTAIN
						|| condition.getOperation() == ConditionOperation.NOT_CONTAIN
						|| condition.getOperation() == ConditionOperation.START
						|| condition.getOperation() == ConditionOperation.END) {
					condition.setAliasDataType(DbFieldType.ALPHANUMERIC);
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

	public String parsing(SortType type) {
		if (type == SortType.ASCENDING) {
			return "ASC";
		} else if (type == SortType.DESCENDING) {
			return "DESC";
		}
		throw new RuntimeException(I18N.prop("msg_bobas_value_can_not_be_resolved", type.toString()));
	}

	public String parsing(ConditionRelationship value) {
		if (value == ConditionRelationship.AND) {
			return "AND";
		} else if (value == ConditionRelationship.OR) {
			return "OR";
		}
		return Strings.VALUE_EMPTY;
	}

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

	public String parsingSelect(Class<?> boType) {
		return this.parsingSelect(boType, new Criteria());
	}

	public String parsingSelect(Class<?> boType, Iterable<ICondition> conditions) {
		ICriteria criteria = new Criteria();
		if (conditions != null) {
			for (ICondition item : conditions) {
				criteria.getConditions().add(item);
			}
		}
		return this.parsingSelect(boType, criteria);
	}

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

	private Map<Class<?>, String> tables = new HashMap<>();

	public String table(Class<?> boType) {
		try {
			if (this.tables.containsKey(boType)) {
				return this.tables.get(boType);
			}
			DbField dbField;
			for (IPropertyInfo<?> item : BOFactory.propertyInfos(boType)) {
				dbField = item.getAnnotation(DbField.class);
				if (dbField != null && Strings.isNullOrEmpty(dbField.table())) {
					this.tables.put(boType, MyConfiguration.applyVariables(dbField.table()));
					return this.table(boType);
				}
			}
			throw new RuntimeException(I18N.prop("msg_bobas_value_can_not_be_resolved", boType.toString()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String parsingSelect(Class<?> boType, ICriteria criteria) {
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
		if (criteria.getConditions().size() > 0) {
			stringBuilder.append(" ");
			stringBuilder.append("WHERE");
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

}