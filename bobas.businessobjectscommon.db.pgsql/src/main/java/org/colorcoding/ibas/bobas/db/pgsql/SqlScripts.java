package org.colorcoding.ibas.bobas.db.pgsql;

import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ISqlQuery;
import org.colorcoding.ibas.bobas.common.SqlQuery;
import org.colorcoding.ibas.bobas.data.KeyValue;
import org.colorcoding.ibas.bobas.db.SqlScriptsException;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;
import org.colorcoding.ibas.bobas.util.StringBuilder;

public class SqlScripts extends org.colorcoding.ibas.bobas.db.SqlScripts {

	/**
	 * 无参构造函数
	 */
	public SqlScripts() {

	}

	@Override
	public ISqlQuery getServerTimeScript() {
		return new SqlQuery("NOW()");
	}

	@Override
	public String getFieldValueCastType(DbFieldType dbFieldType) {
		String result = "%s";
		if (dbFieldType != null) {

			if (dbFieldType == DbFieldType.db_Alphanumeric) {
				result = "CAST(%s AS VARCHAR)";
			} else if (dbFieldType == DbFieldType.db_Date) {
				result = "CAST(%s AS TIMESTAMP)";
			} else if (dbFieldType == DbFieldType.db_Numeric) {
				result = "CAST(%s AS INTEGER)";
			} else if (dbFieldType == DbFieldType.db_Decimal) {
				result = "CAST(%s AS INTEGER)";
			}
		}
		return result;
	}

	@Override
	public DbFieldType getDbFieldType(String dbType) {
		if (dbType.equals("varchar")) {
			return DbFieldType.db_Alphanumeric;
		} else if (dbType.equals("INTEGER")) {
			return DbFieldType.db_Numeric;
		} else if (dbType.equals("TIMESTAMP")) {
			return DbFieldType.db_Date;
		} else if (dbType.equals("INTEGER")) {
			return DbFieldType.db_Decimal;
		}
		return DbFieldType.db_Unknown;
	}

	@Override
	public String getFieldBreakSign() {
		return ",";
	}

	@Override
	public String getSqlString(ConditionOperation value, String opValue) throws SqlScriptsException {
		if (value == ConditionOperation.co_CONTAIN) {
			return String.format("LIKE '%%%s%%'", opValue);
		} else if (value == ConditionOperation.co_NOT_CONTAIN) {
			return String.format("NOT LIKE '%%%s%%'", opValue);
		} else if (value == ConditionOperation.co_END) {
			return String.format("LIKE '%%%s'", opValue);
		} else if (value == ConditionOperation.co_START) {
			return String.format("LIKE '%s%%'", opValue);
		} else if (value == ConditionOperation.co_EQUAL) {
			return "= %s";
		} else if (value == ConditionOperation.co_NOT_EQUAL) {
			return "!= %s";
		} else if (value == ConditionOperation.co_GRATER_EQUAL) {
			return ">= %s";
		} else if (value == ConditionOperation.co_GRATER_THAN) {
			return "> %s";
		} else if (value == ConditionOperation.co_IS_NULL) {
			return "IS NULL";
		} else if (value == ConditionOperation.co_NOT_NULL) {
			return "IS NOT NULL";
		} else if (value == ConditionOperation.co_LESS_EQUAL) {
			return "<= %s";
		} else if (value == ConditionOperation.co_LESS_THAN) {
			return "< %s";
		}
		throw new SqlScriptsException(i18n.prop("msg_bobas_value_can_not_be_resolved", value.toString()));
	}

	@Override
	public String getSqlString(String value) throws SqlScriptsException {
		if (value == null) {
			return this.getNullValue();
		}
		return String.format("'%s'", value);
	}

	@Override
	public String groupSelectQuery(String partSelect, String table, String partWhere, String partOrder, int result)
			throws SqlScriptsException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.appendFormat("SELECT * FROM %s", table);
		if (partWhere != null && !partWhere.isEmpty()) {
			stringBuilder.appendFormat(" WHERE %s", partWhere);
		}
		if (partOrder != null && !partOrder.isEmpty()) {
			stringBuilder.appendFormat(" ORDER BY %s", partOrder);
		}
		if (result >= 0) {
			stringBuilder.appendFormat(" LIMIT %s", result);
		}
		return stringBuilder.toString();
	}

	@Override
	public String getBOPrimaryKeyQuery(String boCode) throws SqlScriptsException {
		return String.format("SELECT \"AutoKey\" FROM \"%s_SYS_ONNM\" WHERE \"ObjectCode\" = '%s' FOR UPDATE",
				this.getCompanyId(), boCode);
	}

	@Override
	public String groupMaxValueQuery(String field, String table, String partWhere) throws SqlScriptsException {
		return String.format("SELECT Coalesce(MAX(%s),0) FROM %s WHERE %s", field, table, partWhere);
	}

	@Override
	public String getBOTransactionNotificationScript(String boCode, String type, int keyCount, String keyNames,
			String keyValues) throws SqlScriptsException {
		return String.format(
				"SELECT \"Error\",\"Message\" FROM \"%s_SP_TRANSACTION_NOTIFICATION\"(N'%s', N'%s', %s, N'%s', N'%s')",
				this.getCompanyId(), boCode, type, keyCount, keyNames, keyValues);
	}

	@Override
	public String groupStoredProcedure(String spName, KeyValue[] parameters) throws SqlScriptsException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT * FROM ");
		stringBuilder.append("\"");
		stringBuilder.append(spName);
		stringBuilder.append("\"(");
		for (int i = 0; i < parameters.length; i++) {
			KeyValue keyValue = parameters[i];
			if (i > 0) {
				stringBuilder.append(", ");
			}
			if (keyValue.value == null) {
				stringBuilder.append("\"\"");
			} else if (keyValue.value.getClass().equals(Integer.class)) {
				stringBuilder.append(keyValue.value.toString());
			} else if (keyValue.value.getClass().equals(Double.class)) {
				stringBuilder.append(keyValue.value.toString());
			} else {
				stringBuilder.append("N'");
				stringBuilder.append(keyValue.value.toString());
				stringBuilder.append("'");
			}
		}
		stringBuilder.append(")");
		return stringBuilder.toString();
	}
}
