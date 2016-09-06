package org.colorcoding.ibas.bobas.db;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.ISqlQuery;
import org.colorcoding.ibas.bobas.common.SortType;
import org.colorcoding.ibas.bobas.common.SqlQuery;
import org.colorcoding.ibas.bobas.data.KeyValue;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;
import org.colorcoding.ibas.bobas.util.StringBuilder;

public class SqlScripts implements ISqlScripts {

	private String companyId = null;

	protected String getCompanyId() {
		if (companyId == null)
			companyId = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_COMPANY_ID, "CC");
		return companyId;
	}

	@Override
	public ISqlQuery getServerTimeScript() {
		return new SqlQuery("SELECT GETDATE() 'NOW'");
	}

	@Override
	public String getDbObjectSign() {
		return "\"%s\"";
	}

	@Override
	public String getScriptBreakSign() {
		return "";
	}

	@Override
	public String getNullValue() {
		return "NULL";
	}

	@Override
	public String getFieldValueCastType(DbFieldType dbFieldType) {
		String result = "%s";
		if (dbFieldType != null) {
			if (dbFieldType == DbFieldType.db_Alphanumeric) {
				result = "CAST(%s AS NVARCHAR)";
			} else if (dbFieldType == DbFieldType.db_Date) {
				result = "CAST(%s AS DATETIME)";
			} else if (dbFieldType == DbFieldType.db_Numeric) {
				result = "CAST(%s AS INT)";
			} else if (dbFieldType == DbFieldType.db_Decimal) {
				result = "CAST(%s AS NUMERIC)";
			}
		}
		return result;
	}

	@Override
	public DbFieldType getDbFieldType(String dbType) {
		if (dbType.equals("nvarchar")) {
			return DbFieldType.db_Alphanumeric;
		} else if (dbType.equals("int")) {
			return DbFieldType.db_Numeric;
		} else if (dbType.equals("datetime")) {
			return DbFieldType.db_Date;
		} else if (dbType.equals("numeric")) {
			return DbFieldType.db_Decimal;
		}
		return DbFieldType.db_Unknown;
	}

	@Override
	public String getFieldBreakSign() {
		return ", ";
	}

	@Override
	public String getAndSign() {
		return " AND ";
	}

	@Override
	public String getSqlString(ConditionRelationship value) throws SqlScriptsException {
		if (value == ConditionRelationship.cr_AND) {
			return "AND";
		} else if (value == ConditionRelationship.cr_OR) {
			return "OR";
		} else if (value == ConditionRelationship.cr_NONE) {
			return this.getFieldBreakSign();
		}
		throw new SqlScriptsException(i18n.prop("msg_bobas_value_can_not_be_resolved", value.toString()));
	}

	@Override
	public String getSqlString(ConditionOperation value) throws SqlScriptsException {
		return this.getSqlString(value, null);
	}

	@Override
	public String getSqlString(ConditionOperation value, String opValue) throws SqlScriptsException {
		if (value == ConditionOperation.co_CONTAIN) {
			return String.format("LIKE N'%%%s%%'", opValue);
		} else if (value == ConditionOperation.co_NOT_CONTAIN) {
			return String.format("NOT LIKE N'%%%s%%'", opValue);
		} else if (value == ConditionOperation.co_END) {
			return String.format("LIKE N'%%%s'", opValue);
		} else if (value == ConditionOperation.co_START) {
			return String.format("LIKE N'%s%%'", opValue);
		} else if (value == ConditionOperation.co_EQUAL) {
			return "= %s";
		} else if (value == ConditionOperation.co_NOT_EQUAL) {
			return "<> %s";
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
		return String.format("N'%s'", value);
	}

	@Override
	public String getSqlString(SortType value) throws SqlScriptsException {
		if (value == SortType.st_Ascending) {
			return "ASC";
		} else if (value == SortType.st_Descending) {
			return "DESC";
		}
		throw new SqlScriptsException(i18n.prop("msg_bobas_value_can_not_be_resolved", value.toString()));
	}

	@Override
	public String groupSelectQuery(String partSelect, String table, String partWhere, String partOrder, int result)
			throws SqlScriptsException {
		StringBuilder stringBuilder = new StringBuilder();
		if (result >= 0) {
			stringBuilder.appendFormat("SELECT TOP %s %s", result, partSelect);
		} else {
			stringBuilder.appendFormat("SELECT %s", partSelect);
		}
		stringBuilder.appendFormat(" FROM %s", table);
		if (partWhere != null && !partWhere.equals("")) {
			stringBuilder.appendFormat(" WHERE %s", partWhere);
		}
		if (partOrder != null && !partOrder.equals("")) {
			stringBuilder.appendFormat(" ORDER BY %s", partOrder);
		}
		return stringBuilder.toString();
	}

	@Override
	public String groupDeleteQuery(String table, String partWhere) throws SqlScriptsException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.appendFormat("DELETE FROM %s WHERE %s", table, partWhere);
		return stringBuilder.toString();
	}

	@Override
	public String groupUpdateQuery(String table, String partFieldValues, String partWhere) throws SqlScriptsException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.appendFormat("UPDATE %s SET %s WHERE %s", table, partFieldValues, partWhere);
		return stringBuilder.toString();
	}

	@Override
	public String groupInsertQuery(String table, String partFields, String partValues) throws SqlScriptsException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.appendFormat("INSERT INTO %s (%s) VALUES (%s)", table, partFields, partValues);
		return stringBuilder.toString();
	}

	@Override
	public String getBOPrimaryKeyQuery(String boCode) throws SqlScriptsException {
		return String.format("SELECT \"AutoKey\" FROM \"%s_SYS_ONNM\" WITH (UPDLOCK) WHERE \"ObjectCode\" = '%s'",
				this.getCompanyId(), boCode);
	}

	@Override
	public String getUpdateBOPrimaryKeyScript(String boCode) throws SqlScriptsException {
		return String.format("UPDATE \"%s_SYS_ONNM\" SET \"AutoKey\" = \"AutoKey\" + 1 WHERE \"ObjectCode\" = '%s'",
				this.getCompanyId(), boCode);
	}

	@Override
	public String groupMaxValueQuery(String field, String table, String partWhere) throws SqlScriptsException {
		return String.format("SELECT MAX(%s) FROM %s WHERE %s", field, table, partWhere);
	}

	@Override
	public String getBOTransactionNotificationScript(String boCode, String type, int keyCount, String keyNames,
			String keyValues) throws SqlScriptsException {
		return String.format("EXEC \"%s_SP_TRANSACTION_NOTIFICATION\" N'%s', N'%s', %s, N'%s', N'%s'",
				this.getCompanyId(), boCode, type, keyCount, keyNames, keyValues);
	}

	@Override
	public String groupStoredProcedure(String spName, KeyValue[] parameters) throws SqlScriptsException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("EXEC");
		stringBuilder.append(" ");
		stringBuilder.append("\"");
		stringBuilder.append(spName);
		stringBuilder.append("\" ");
		for (int i = 0; i < parameters.length; i++) {
			KeyValue keyValue = parameters[i];
			if (i > 0) {
				stringBuilder.append(", ");
			}
			if (keyValue.value == null) {
				stringBuilder.append("''");
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
		return stringBuilder.toString();
	}

}
