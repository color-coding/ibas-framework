package org.colorcoding.ibas.bobas.db.mssql;

import java.sql.Connection;
import java.sql.DriverManager;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.logging.Logger;

public class DbAdapter extends org.colorcoding.ibas.bobas.db.DbAdapter {

	@Override
	public Connection createConnection(String server, String dbName, String userName, String userPwd) {
		try {
			String application = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_APPLICATION_NAME);
			if (Strings.isNullOrEmpty(application)) {
				application = Strings.format("ibas_%s", this.hashCode());
			}
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String dbURL = String.format("jdbc:sqlserver://%s;DatabaseName=%s;ApplicationName=%s", server, dbName,
					application);
			if (MyConfiguration.isDebugMode()) {
				Logger.log(Strings.format("db adapter: %s", dbURL));
			}
			return DriverManager.getConnection(dbURL, userName, userPwd);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
