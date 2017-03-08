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
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("%s");
		if (dbFieldType != null) {
			if (dbFieldType == DbFieldType.ALPHANUMERIC) {
				stringBuilder = new StringBuilder();
				stringBuilder.append("CAST");
				stringBuilder.append("(");
				stringBuilder.append("%s");
				stringBuilder.append(" ");
				stringBuilder.append("AS");
				stringBuilder.append(" ");
				stringBuilder.append("NVARCHAR");
				stringBuilder.append(")");
				return stringBuilder.toString();
			} else if (dbFieldType == DbFieldType.DATE) {
				stringBuilder = new StringBuilder();
				stringBuilder.append("CAST");
				stringBuilder.append("(");
				stringBuilder.append("%s");
				stringBuilder.append(" ");
				stringBuilder.append("AS");
				stringBuilder.append(" ");
				stringBuilder.append("DATETIME");
				stringBuilder.append(")");
				return stringBuilder.toString();
			} else if (dbFieldType == DbFieldType.NUMERIC) {
				stringBuilder = new StringBuilder();
				stringBuilder.append("CAST");
				stringBuilder.append("(");
				stringBuilder.append("%s");
				stringBuilder.append(" ");
				stringBuilder.append("AS");
				stringBuilder.append(" ");
				stringBuilder.append("INT");
				stringBuilder.append(")");
				return stringBuilder.toString();
			} else if (dbFieldType == DbFieldType.DECIMAL) {
				stringBuilder = new StringBuilder();
				stringBuilder.append("CAST");
				stringBuilder.append("(");
				stringBuilder.append("%s");
				stringBuilder.append(" ");
				stringBuilder.append("AS");
				stringBuilder.append(" ");
				stringBuilder.append("NUMERIC");
				stringBuilder.append(")");
				return stringBuilder.toString();
			}
		}
		return stringBuilder.toString();
	}

	@Override
	public DbFieldType getDbFieldType(String dbType) {
		if (dbType.equals("nvarchar") || dbType.equals("ntext")) {
			return DbFieldType.ALPHANUMERIC;
		} else if (dbType.equals("int") || dbType.equals("smallint")) {
			return DbFieldType.NUMERIC;
		} else if (dbType.equals("datetime")) {
			return DbFieldType.DATE;
		} else if (dbType.equals("numeric")) {
			return DbFieldType.DECIMAL;
		} else if (dbType.equals("bit")) {
			return DbFieldType.BYTES;
		}
		return DbFieldType.UNKNOWN;
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
		if (value == ConditionRelationship.AND) {
			return "AND";
		} else if (value == ConditionRelationship.OR) {
			return "OR";
		} else if (value == ConditionRelationship.NONE) {
			return this.getFieldBreakSign();
		}
		throw new SqlScriptsException(i18n.prop("msg_bobas_value_can_not_be_resolved", value.toString()));
	}

	@Override
	public String getSqlString(ConditionOperation value) throws SqlScriptsException {
		return this.getSqlString(value, null);
	}

	/**
	 * 数据的安全性检查
	 * 
	 * @param data
	 */
	protected String checkSecurity(String data) {
		if (data == null || data.isEmpty()) {
			return data;
		}
		// 处理单引号
		return data.replace("'", "''");
	}

	@Override
	public String getSqlString(ConditionOperation value, String opValue) throws SqlScriptsException {
		opValue = this.checkSecurity(opValue);
		if (value == ConditionOperation.CONTAIN) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("LIKE");
			stringBuilder.append(" ");
			stringBuilder.append("N'");
			stringBuilder.append("%");
			stringBuilder.append("%");
			stringBuilder.append(opValue);
			stringBuilder.append("%");
			stringBuilder.append("%");
			stringBuilder.append("'");
			return stringBuilder.toString();
		} else if (value == ConditionOperation.NOT_CONTAIN) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("NOT");
			stringBuilder.append(" ");
			stringBuilder.append("LIKE");
			stringBuilder.append(" ");
			stringBuilder.append("N'");
			stringBuilder.append("%");
			stringBuilder.append("%");
			stringBuilder.append(opValue);
			stringBuilder.append("%");
			stringBuilder.append("%");
			stringBuilder.append("'");
			return stringBuilder.toString();
		} else if (value == ConditionOperation.END) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("LIKE");
			stringBuilder.append(" ");
			stringBuilder.append("N'");
			stringBuilder.append("%");
			stringBuilder.append("%");
			stringBuilder.append(opValue);
			stringBuilder.append("'");
			return stringBuilder.toString();
		} else if (value == ConditionOperation.START) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("LIKE");
			stringBuilder.append(" ");
			stringBuilder.append("N'");
			stringBuilder.append(opValue);
			stringBuilder.append("%");
			stringBuilder.append("%");
			stringBuilder.append("'");
			return stringBuilder.toString();
		} else if (value == ConditionOperation.EQUAL) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("=");
			stringBuilder.append(" ");
			stringBuilder.append("%s");
			return stringBuilder.toString();
		} else if (value == ConditionOperation.NOT_EQUAL) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("<");
			stringBuilder.append(">");
			stringBuilder.append(" ");
			stringBuilder.append("%s");
			return stringBuilder.toString();
		} else if (value == ConditionOperation.GRATER_EQUAL) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(">");
			stringBuilder.append("=");
			stringBuilder.append(" ");
			stringBuilder.append("%s");
			return stringBuilder.toString();
		} else if (value == ConditionOperation.GRATER_THAN) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(">");
			stringBuilder.append(" ");
			stringBuilder.append("%s");
			return stringBuilder.toString();
		} else if (value == ConditionOperation.IS_NULL) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("IS");
			stringBuilder.append(" ");
			stringBuilder.append("NULL");
			return stringBuilder.toString();
		} else if (value == ConditionOperation.NOT_NULL) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("IS");
			stringBuilder.append(" ");
			stringBuilder.append("NOT");
			stringBuilder.append(" ");
			stringBuilder.append("NULL");
			return stringBuilder.toString();
		} else if (value == ConditionOperation.LESS_EQUAL) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("<");
			stringBuilder.append("=");
			stringBuilder.append(" ");
			stringBuilder.append("%s");
			return stringBuilder.toString();
		} else if (value == ConditionOperation.LESS_THAN) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("<");
			stringBuilder.append(" ");
			stringBuilder.append("%s");
			return stringBuilder.toString();
		}
		throw new SqlScriptsException(i18n.prop("msg_bobas_value_can_not_be_resolved", value.toString()));
	}

	@Override
	public String getSqlString(DbFieldType type, String value) throws SqlScriptsException {
		if (value == null) {
			return this.getNullValue();
		}
		value = this.checkSecurity(value);
		StringBuilder stringBuilder = new StringBuilder();
		if (type == DbFieldType.NUMERIC || type == DbFieldType.DECIMAL) {
			stringBuilder.append(value);
		} else if (type == DbFieldType.DATE) {
			stringBuilder.append("'");
			stringBuilder.append(value);
			stringBuilder.append("'");
		} else {
			stringBuilder.append("N'");
			stringBuilder.append(value);
			stringBuilder.append("'");
		}
		return stringBuilder.toString();
	}

	@Override
	public String getSqlString(SortType value) throws SqlScriptsException {
		if (value == SortType.ASCENDING) {
			return "ASC";
		} else if (value == SortType.DESCENDING) {
			return "DESC";
		}
		throw new SqlScriptsException(i18n.prop("msg_bobas_value_can_not_be_resolved", value.toString()));
	}

	@Override
	public String groupSelectQuery(String partSelect, String table, String partWhere, String partOrder, int result)
			throws SqlScriptsException {
		StringBuilder stringBuilder = new StringBuilder();
		if (result >= 0) {
			stringBuilder.append("SELECT");
			stringBuilder.append(" ");
			stringBuilder.append("TOP");
			stringBuilder.append(" ");
			stringBuilder.append(result);
			stringBuilder.append(" ");
			stringBuilder.append(partSelect);
		} else {
			stringBuilder.append("SELECT");
			stringBuilder.append(" ");
			stringBuilder.append(partSelect);
		}
		stringBuilder.append(" ");
		stringBuilder.append("FROM");
		stringBuilder.append(" ");
		stringBuilder.append(table);
		if (partWhere != null && !partWhere.isEmpty()) {
			stringBuilder.append(" ");
			stringBuilder.append("WHERE");
			stringBuilder.append(" ");
			stringBuilder.append(partWhere);
		}
		if (partOrder != null && !partOrder.isEmpty()) {
			stringBuilder.append(" ");
			stringBuilder.append("ORDER BY");
			stringBuilder.append(" ");
			stringBuilder.append(partOrder);
		}
		return stringBuilder.toString();
	}

	@Override
	public String groupDeleteQuery(String table, String partWhere) throws SqlScriptsException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("DELETE");
		stringBuilder.append(" ");
		stringBuilder.append("FROM");
		stringBuilder.append(" ");
		stringBuilder.append(table);
		stringBuilder.append(" ");
		stringBuilder.append("WHERE");
		stringBuilder.append(" ");
		stringBuilder.append(partWhere);
		return stringBuilder.toString();
	}

	@Override
	public String groupUpdateQuery(String table, String partFieldValues, String partWhere) throws SqlScriptsException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("UPDATE");
		stringBuilder.append(" ");
		stringBuilder.append(table);
		stringBuilder.append(" ");
		stringBuilder.append("SET");
		stringBuilder.append(" ");
		stringBuilder.append(partFieldValues);
		stringBuilder.append(" ");
		stringBuilder.append("WHERE");
		stringBuilder.append(" ");
		stringBuilder.append(partWhere);
		return stringBuilder.toString();
	}

	@Override
	public String groupInsertQuery(String table, String partFields, String partValues) throws SqlScriptsException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("INSERT");
		stringBuilder.append(" ");
		stringBuilder.append("INTO");
		stringBuilder.append(" ");
		stringBuilder.append(table);
		stringBuilder.append(" ");
		stringBuilder.append("(");
		stringBuilder.append(partFields);
		stringBuilder.append(")");
		stringBuilder.append(" ");
		stringBuilder.append("VALUES");
		stringBuilder.append(" ");
		stringBuilder.append("(");
		stringBuilder.append(partValues);
		stringBuilder.append(")");
		return stringBuilder.toString();
	}

	@Override
	public String getBOPrimaryKeyQuery(String boCode) throws SqlScriptsException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT");
		stringBuilder.append(" ");
		stringBuilder.append("\"AutoKey\"");
		stringBuilder.append(" ");
		stringBuilder.append("FROM");
		stringBuilder.append(" ");
		stringBuilder.appendFormat("\"%s_SYS_ONNM\"", this.getCompanyId());
		stringBuilder.append(" ");
		stringBuilder.append("WITH (UPDLOCK)");
		stringBuilder.append(" ");
		stringBuilder.append("WHERE");
		stringBuilder.append(" ");
		stringBuilder.append("\"ObjectCode\"");
		stringBuilder.append(" ");
		stringBuilder.append("=");
		stringBuilder.append(" ");
		stringBuilder.append("'");
		stringBuilder.append(this.checkSecurity(boCode));
		stringBuilder.append("'");
		return stringBuilder.toString();
	}

	@Override
	public String getUpdateBOPrimaryKeyScript(String boCode, int addValue) throws SqlScriptsException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("UPDATE");
		stringBuilder.append(" ");
		stringBuilder.appendFormat("\"%s_SYS_ONNM\"", this.getCompanyId());
		stringBuilder.append(" ");
		stringBuilder.append("SET");
		stringBuilder.append(" ");
		stringBuilder.append("\"AutoKey\"");
		stringBuilder.append(" ");
		stringBuilder.append("=");
		stringBuilder.append(" ");
		stringBuilder.append("\"AutoKey\"");
		stringBuilder.append(" ");
		stringBuilder.append("+");
		stringBuilder.append(" ");
		stringBuilder.append(addValue);
		stringBuilder.append(" ");
		stringBuilder.append("WHERE");
		stringBuilder.append(" ");
		stringBuilder.append("\"ObjectCode\"");
		stringBuilder.append(" ");
		stringBuilder.append("=");
		stringBuilder.append(" ");
		stringBuilder.append("'");
		stringBuilder.append(this.checkSecurity(boCode));
		stringBuilder.append("'");
		return stringBuilder.toString();
	}

	@Override
	public String groupMaxValueQuery(String field, String table, String partWhere) throws SqlScriptsException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT");
		stringBuilder.append(" ");
		stringBuilder.append("MAX");
		stringBuilder.append("(");
		stringBuilder.append(field);
		stringBuilder.append(")");
		stringBuilder.append(" ");
		stringBuilder.append("FROM");
		stringBuilder.append(" ");
		stringBuilder.append(table);
		stringBuilder.append(" ");
		stringBuilder.append("WHERE");
		stringBuilder.append(" ");
		stringBuilder.append(partWhere);
		return stringBuilder.toString();
	}

	@Override
	public String getBOTransactionNotificationScript(String boCode, String type, int keyCount, String keyNames,
			String keyValues) throws SqlScriptsException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("EXEC");
		stringBuilder.append(" ");
		stringBuilder.appendFormat("\"%s_SP_TRANSACTION_NOTIFICATION\"", this.getCompanyId());
		stringBuilder.append(" ");
		stringBuilder.append("N'");
		stringBuilder.append(this.checkSecurity(boCode));
		stringBuilder.append("'");
		stringBuilder.append(",");
		stringBuilder.append(" ");
		stringBuilder.append("N'");
		stringBuilder.append(this.checkSecurity(type));
		stringBuilder.append("'");
		stringBuilder.append(",");
		stringBuilder.append(" ");
		stringBuilder.append(keyCount);
		stringBuilder.append(",");
		stringBuilder.append(" ");
		stringBuilder.append("N'");
		stringBuilder.append(this.checkSecurity(keyNames));
		stringBuilder.append("'");
		stringBuilder.append(",");
		stringBuilder.append(" ");
		stringBuilder.append("N'");
		stringBuilder.append(this.checkSecurity(keyValues));
		stringBuilder.append("'");
		return stringBuilder.toString();
	}

	@Override
	public String groupStoredProcedure(String spName, KeyValue[] parameters) throws SqlScriptsException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("EXEC");
		stringBuilder.append(" ");
		stringBuilder.append("\"");
		stringBuilder.append(spName);
		stringBuilder.append("\"");
		stringBuilder.append(" ");
		for (int i = 0; i < parameters.length; i++) {
			KeyValue keyValue = parameters[i];
			if (i > 0) {
				stringBuilder.append(",");
				stringBuilder.append(" ");
			}
			if (keyValue.value == null) {
				stringBuilder.append("''");
			} else if (keyValue.value.getClass().equals(Integer.class)) {
				stringBuilder.append(String.valueOf(keyValue.value));
			} else if (keyValue.value.getClass().equals(Double.class)) {
				stringBuilder.append(String.valueOf(keyValue.value));
			} else {
				stringBuilder.append("N'");
				stringBuilder.append(this.checkSecurity(String.valueOf(keyValue.value)));
				stringBuilder.append("'");
			}
		}
		return stringBuilder.toString();
	}

}
