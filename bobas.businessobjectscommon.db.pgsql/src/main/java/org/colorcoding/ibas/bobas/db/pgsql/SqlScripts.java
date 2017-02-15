package org.colorcoding.ibas.bobas.db.pgsql;

import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ISqlQuery;
import org.colorcoding.ibas.bobas.common.SqlQuery;
import org.colorcoding.ibas.bobas.data.KeyValue;
import org.colorcoding.ibas.bobas.db.SqlScriptsException;
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
				stringBuilder.append("VARCHAR");
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
				stringBuilder.append("TIMESTAMP");
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
				stringBuilder.append("INTEGER");
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
		if (dbType.equals("varchar")) {
			return DbFieldType.ALPHANUMERIC;
		} else if (dbType.equals("INTEGER")) {
			return DbFieldType.NUMERIC;
		} else if (dbType.equals("TIMESTAMP")) {
			return DbFieldType.DATE;
		} else if (dbType.equals("INTEGER")) {
			return DbFieldType.DECIMAL;
		}
		return DbFieldType.UNKNOWN;
	}

	@Override
	public String getFieldBreakSign() {
		return ",";
	}

	@Override
	public String getSqlString(ConditionOperation value, String opValue) throws SqlScriptsException {
		if (value == ConditionOperation.NOT_EQUAL) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("!");
			stringBuilder.append("=");
			stringBuilder.append(" ");
			stringBuilder.append("%s");
			return stringBuilder.toString();
		} else {
			return super.getSqlString(value, opValue);
		}
	}

	@Override
	public String getSqlString(DbFieldType type, String value) throws SqlScriptsException {
		if (type == DbFieldType.DATE && value != null) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("'");
			stringBuilder.append(value);
			stringBuilder.append("'");
			return stringBuilder.toString();
		}
		return super.getSqlString(type, value);
	}

	@Override
	public String groupSelectQuery(String partSelect, String table, String partWhere, String partOrder, int result)
			throws SqlScriptsException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT");
		stringBuilder.append(" ");
		stringBuilder.append(partSelect);
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
		if (result >= 0) {
			stringBuilder.append(" ");
			stringBuilder.append("LIMIT");
			stringBuilder.append(" ");
			stringBuilder.append(result);
		}
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
		stringBuilder.append("WHERE");
		stringBuilder.append(" ");
		stringBuilder.append("\"ObjectCode\"");
		stringBuilder.append(" ");
		stringBuilder.append("=");
		stringBuilder.append(" ");
		stringBuilder.append("'");
		stringBuilder.append(this.checkSecurity(boCode));
		stringBuilder.append("'");
		stringBuilder.append(" ");
		stringBuilder.append("FOR UPDATE");
		return stringBuilder.toString();
	}

	@Override
	public String groupMaxValueQuery(String field, String table, String partWhere) throws SqlScriptsException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT");
		stringBuilder.append(" ");
		stringBuilder.append("Coalesce");
		stringBuilder.append("(");
		stringBuilder.append("MAX");
		stringBuilder.append("(");
		stringBuilder.append(field);
		stringBuilder.append(")");
		stringBuilder.append(",");
		stringBuilder.append("0");
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
		stringBuilder.append("SELECT");
		stringBuilder.append(" ");
		stringBuilder.append("\"Error\"");
		stringBuilder.append(",");
		stringBuilder.append(" ");
		stringBuilder.append("\"Message\"");
		stringBuilder.append(" ");
		stringBuilder.append("FROM");
		stringBuilder.append(" ");
		stringBuilder.appendFormat("\"%s_SP_TRANSACTION_NOTIFICATION\"", this.getCompanyId());
		stringBuilder.append("(");
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
		stringBuilder.append(")");
		return stringBuilder.toString();
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
				stringBuilder.append(String.valueOf(keyValue.value));
			} else if (keyValue.value.getClass().equals(Double.class)) {
				stringBuilder.append(String.valueOf(keyValue.value));
			} else {
				stringBuilder.append("N'");
				stringBuilder.append(this.checkSecurity(String.valueOf(keyValue.value)));
				stringBuilder.append("'");
			}
		}
		stringBuilder.append(")");
		return stringBuilder.toString();
	}
}
