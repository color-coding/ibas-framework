package org.colorcoding.bobas.db.hana;

import org.colorcoding.bobas.common.ISqlQuery;
import org.colorcoding.bobas.common.SqlQuery;
import org.colorcoding.bobas.db.SqlScriptsException;
import org.colorcoding.bobas.mapping.db.DbFieldType;

public class SqlScripts extends org.colorcoding.bobas.db.SqlScripts {

	public SqlScripts() {

	}

	@Override
	public ISqlQuery getServerTimeScript() {
		return new SqlQuery("SELECT  NOW() \"now\" FROM DUMMY;");
	}

	@Override
	public String getFieldValueCastType(DbFieldType dbFieldType) {
		String result = "%s";
		if (dbFieldType != null) {
			if (dbFieldType == DbFieldType.db_Alphanumeric) {
				result = "CAST(%s AS NVARCHAR)";
			} else if (dbFieldType == DbFieldType.db_Date) {
				result = "CAST(%s AS DATE)";
			} else if (dbFieldType == DbFieldType.db_Numeric) {
				result = "CAST(%s AS INTEGER)";
			} else if (dbFieldType == DbFieldType.db_Decimal) {
				result = "CAST(%s AS DECIMAL)";
			}
		}
		return result;
	}

	@Override
	public DbFieldType getDbFieldType(String dbType) {
		if (dbType.equals("NVARCHAR")) {
			return DbFieldType.db_Alphanumeric;
		} else if (dbType.equals("INTEGER")) {
			return DbFieldType.db_Numeric;
		} else if (dbType.equals("DATE")) {
			return DbFieldType.db_Date;
		} else if (dbType.equals("DECIMAL")) {
			return DbFieldType.db_Decimal;
		}
		return DbFieldType.db_Unknown;
	}

	@Override
	public String getBOPrimaryKeyQuery(String boCode) throws SqlScriptsException {
		return String.format("SELECT \"AutoKey\" FROM \"AVA_SYS_ONNM\" WHERE \"ObjectCode\" = '%s' FOR UPDATE", boCode);
	}

	@Override
	public String groupMaxValueQuery(String field, String table, String partWhere) throws SqlScriptsException {
		return String.format("SELECT IFNULL(MAX(%s),0) FROM %s WHERE %s", field, table, partWhere);
	}

	@Override
	public String getBOTransactionNotificationScript(String boCode, String type, int keyCount, String keyNames,
			String keyValues) throws SqlScriptsException {
		return String.format("CALL \"AVA_SP_TRANSACTION_NOTIFICATION\"(N'%s', N'%s', %s, N'%s', N'%s')", boCode, type,
				keyCount, keyNames, keyValues);
	}

}
