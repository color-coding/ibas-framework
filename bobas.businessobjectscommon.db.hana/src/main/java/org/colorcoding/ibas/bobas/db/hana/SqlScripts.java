package org.colorcoding.ibas.bobas.db.hana;

import org.colorcoding.ibas.bobas.common.ISqlQuery;
import org.colorcoding.ibas.bobas.common.SqlQuery;
import org.colorcoding.ibas.bobas.db.SqlScriptsException;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;

public class SqlScripts extends org.colorcoding.ibas.bobas.db.SqlScripts {

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
		return String.format("SELECT \"AutoKey\" FROM \"%s_SYS_ONNM\" WHERE \"ObjectCode\" = '%s' FOR UPDATE",
				this.getCompanyId(), boCode);
	}

	@Override
	public String groupMaxValueQuery(String field, String table, String partWhere) throws SqlScriptsException {
		return String.format("SELECT IFNULL(MAX(%s),0) FROM %s WHERE %s", field, table, partWhere);
	}

	@Override
	public String getBOTransactionNotificationScript(String boCode, String type, int keyCount, String keyNames,
			String keyValues) throws SqlScriptsException {
		return String.format("CALL \"%s_SP_TRANSACTION_NOTIFICATION\"(N'%s', N'%s', %s, N'%s', N'%s')",
				this.getCompanyId(), this.getCompanyId(), boCode, type, keyCount, keyNames, keyValues);
	}

}
