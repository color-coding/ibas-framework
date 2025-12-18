package org.colorcoding.ibas.bobas.db.mssql;

import java.sql.Connection;
import java.sql.DriverManager;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.db.DbFieldType;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;

public class DbAdapter extends org.colorcoding.ibas.bobas.db.DbAdapter {

	@Override
	public Connection createConnection(String server, String dbName, String userName, String userPwd) {
		try {
			String application = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_APPLICATION_NAME);
			if (Strings.isNullOrEmpty(application)) {
				application = Strings.format("ibas_%s", this.hashCode());
			}
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String dbURL = String.format(
					"jdbc:sqlserver://%s;DatabaseName=%s;encrypt=false;ApplicationName=%s;useBulkCopyForBatchInsert=true",
					server, dbName, application);
			if (MyConfiguration.isDebugMode()) {
				Logger.log(MessageLevel.DEBUG, Strings.format("db adapter: %s", dbURL));
			}
			return DriverManager.getConnection(dbURL, userName, userPwd);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String sqlValueOf(Object value, int sqlType) {
		String sqlValue = super.sqlValueOf(value, sqlType);
		if (java.sql.Types.VARCHAR == sqlType || java.sql.Types.NVARCHAR == sqlType
				|| java.sql.Types.LONGNVARCHAR == sqlType || java.sql.Types.LONGVARCHAR == sqlType
				|| java.sql.Types.CHAR == sqlType || java.sql.Types.CLOB == sqlType || java.sql.Types.NCHAR == sqlType
				|| java.sql.Types.NCLOB == sqlType) {
			sqlValue = "N" + sqlValue;
		}
		return sqlValue;
	}

	@Override
	public String castAs(DbFieldType type, String alias) {
		if (type == DbFieldType.ALPHANUMERIC) {
			StringBuilder stringBuilder = new StringBuilder(64);
			stringBuilder.append("CONVERT");
			stringBuilder.append("(");
			stringBuilder.append("NVARCHAR");
			stringBuilder.append(",");
			stringBuilder.append(" ");
			stringBuilder.append(this.identifier());
			stringBuilder.append(alias);
			stringBuilder.append(this.identifier());
			stringBuilder.append(",");
			stringBuilder.append(" ");
			stringBuilder.append("120");
			stringBuilder.append(")");
			return stringBuilder.toString();
		}
		return super.castAs(type, alias);
	}
}
