package org.colorcoding.ibas.bobas.db.dm8;

import org.colorcoding.ibas.bobas.common.ConditionAliasDataType;
import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ISqlQuery;
import org.colorcoding.ibas.bobas.common.SqlQuery;
import org.colorcoding.ibas.bobas.data.KeyValue;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;

public class SqlScripts extends org.colorcoding.ibas.bobas.db.SqlScripts {

	/**
	 * 无参构造函数
	 */
	public SqlScripts() {

	}

	@Override
	public ISqlQuery getServerTimeQuery() {
		return new SqlQuery("SELECT CURRENT_DATE()");
	}

	@Override
	public String getCastTypeString(ConditionAliasDataType type) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("%s");
		if (type != null) {
			if (type == ConditionAliasDataType.ALPHANUMERIC) {
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
			} else if (type == ConditionAliasDataType.DATE) {
				stringBuilder = new StringBuilder();
				stringBuilder.append("CAST");
				stringBuilder.append("(");
				stringBuilder.append("%s");
				stringBuilder.append(" ");
				stringBuilder.append("AS");
				stringBuilder.append(" ");
				stringBuilder.append("DATE");
				stringBuilder.append(")");
				return stringBuilder.toString();
			} else if (type == ConditionAliasDataType.NUMERIC) {
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
			} else if (type == ConditionAliasDataType.DECIMAL) {
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
	public DbFieldType toDbFieldType(String dbType) {
		if (dbType.equalsIgnoreCase("VARCHAR") || dbType.equalsIgnoreCase("TEXT")) {
			return DbFieldType.ALPHANUMERIC;
		} else if (dbType.equalsIgnoreCase("INTEGER") || dbType.equalsIgnoreCase("SMALLINT")
				|| dbType.equalsIgnoreCase("BIGINT")) {
			return DbFieldType.NUMERIC;
		} else if (dbType.equalsIgnoreCase("DATE")) {
			return DbFieldType.DATE;
		} else if (dbType.equalsIgnoreCase("INTEGER")) {
			return DbFieldType.DECIMAL;
		}
		return DbFieldType.UNKNOWN;
	}

	@Override
	public String getFieldBreakSign() {
		return ",";
	}

	@Override
	public String getSqlString(ConditionOperation value) {
		if (value == ConditionOperation.NOT_EQUAL) {
			return "!=";
		} else {
			return super.getSqlString(value);
		}
	}

	@Override
	public String getSqlString(DbFieldType type, String value) {
		if (value == null) {
			return this.getNullSign();
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
			stringBuilder.append("'");
			stringBuilder.append(value);
			stringBuilder.append("'");
		}
		return stringBuilder.toString();
	}

	@Override
	public String groupSelectQuery(String partSelect, String table, String partWhere, String partOrder, int result) {
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
	public String getPrimaryKeyQuery(String boCode) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT");
		stringBuilder.append(" ");
		stringBuilder.append("\"AutoKey\"");
		stringBuilder.append(" ");
		stringBuilder.append("FROM");
		stringBuilder.append(" ");
		stringBuilder.append(String.format("\"%s_SYS_ONNM\"", this.getCompanyId()));
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
	public String groupMaxValueQuery(String field, String table, String partWhere) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT");
		stringBuilder.append(" ");
		stringBuilder.append("IFNULL");
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
	public String getTransactionNotificationQuery(String boCode, String type, int keyCount, String keyNames,
			String keyValues) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("CALL");
		stringBuilder.append(" ");
		stringBuilder.append(String.format("\"%s_SP_TRANSACTION_NOTIFICATION\"", this.getCompanyId()));
		stringBuilder.append(" ");
		stringBuilder.append("(");
		stringBuilder.append("'");
		stringBuilder.append(this.checkSecurity(boCode));
		stringBuilder.append("'");
		stringBuilder.append(",");
		stringBuilder.append(" ");
		stringBuilder.append("'");
		stringBuilder.append(this.checkSecurity(type));
		stringBuilder.append("'");
		stringBuilder.append(",");
		stringBuilder.append(" ");
		stringBuilder.append(keyCount);
		stringBuilder.append(",");
		stringBuilder.append(" ");
		stringBuilder.append("'");
		stringBuilder.append(this.checkSecurity(keyNames));
		stringBuilder.append("'");
		stringBuilder.append(",");
		stringBuilder.append(" ");
		stringBuilder.append("'");
		stringBuilder.append(this.checkSecurity(keyValues));
		stringBuilder.append("'");
		stringBuilder.append(")");
		return stringBuilder.toString();
	}

	@Override
	public String groupStoredProcedure(String spName, KeyValue[] parameters) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("CALL");
		stringBuilder.append(" ");
		stringBuilder.append("\"");
		stringBuilder.append(spName);
		stringBuilder.append("\"(");
		for (int i = 0; i < parameters.length; i++) {
			KeyValue keyValue = parameters[i];
			if (i > 0) {
				stringBuilder.append(", ");
			}
			if (keyValue.getValue() == null) {
				stringBuilder.append("\"\"");
			} else if (keyValue.getValue().getClass().equals(Integer.class)) {
				stringBuilder.append(String.valueOf(keyValue.getValue()));
			} else if (keyValue.getValue().getClass().equals(Double.class)) {
				stringBuilder.append(String.valueOf(keyValue.getValue()));
			} else {
				stringBuilder.append("'");
				stringBuilder.append(this.checkSecurity(String.valueOf(keyValue.getValue())));
				stringBuilder.append("'");
			}
		}
		stringBuilder.append(")");
		return stringBuilder.toString();
	}
}
