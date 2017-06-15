package org.colorcoding.ibas.bobas.db.hana;

import org.colorcoding.ibas.bobas.common.ISqlQuery;
import org.colorcoding.ibas.bobas.common.SqlQuery;
import org.colorcoding.ibas.bobas.data.KeyValue;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;
import org.colorcoding.ibas.bobas.util.StringBuilder;

public class SqlScripts extends org.colorcoding.ibas.bobas.db.SqlScripts {

	public SqlScripts() {

	}

	@Override
	public ISqlQuery getServerTimeQuery() {
		return new SqlQuery("SELECT  NOW() \"now\" FROM DUMMY;");
	}

	@Override
	public String getCastTypeString(DbFieldType dbFieldType) {
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
				stringBuilder.append("DATE");
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
				stringBuilder.append("DECIMAL");
				stringBuilder.append(")");
				return stringBuilder.toString();
			}
		}
		return stringBuilder.toString();
	}

	@Override
	public DbFieldType toDbFieldType(String dbType) {
		if (dbType.equals("NVARCHAR") || dbType.equals("NCLOB")) {
			return DbFieldType.ALPHANUMERIC;
		} else if (dbType.equals("INTEGER") || dbType.equals("SMALLINT")) {
			return DbFieldType.NUMERIC;
		} else if (dbType.equals("DATE")) {
			return DbFieldType.DATE;
		} else if (dbType.equals("DECIMAL")) {
			return DbFieldType.DECIMAL;
		}
		return DbFieldType.UNKNOWN;
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
				stringBuilder.append("''");
			} else if (keyValue.getValue().getClass().equals(Integer.class)) {
				stringBuilder.append(String.valueOf(keyValue.getValue()));
			} else if (keyValue.getValue().getClass().equals(Double.class)) {
				stringBuilder.append(String.valueOf(keyValue.getValue()));
			} else {
				stringBuilder.append("N'");
				stringBuilder.append(this.checkSecurity(String.valueOf(keyValue.getValue())));
				stringBuilder.append("'");
			}
		}
		stringBuilder.append(")");
		return stringBuilder.toString();
	}
}
