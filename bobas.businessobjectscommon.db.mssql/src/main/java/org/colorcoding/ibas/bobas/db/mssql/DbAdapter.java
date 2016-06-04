package org.colorcoding.ibas.bobas.db.mssql;

import java.sql.Connection;
import java.sql.DriverManager;

import org.colorcoding.ibas.bobas.db.DbException;
import org.colorcoding.ibas.bobas.db.IBOAdapter4Db;
import org.colorcoding.ibas.bobas.i18n.i18n;

public class DbAdapter extends org.colorcoding.ibas.bobas.db.DbAdapter {

	@Override
	public Connection createConnection(String server, String dbName, String userName, String userPwd,
			String applicationName) throws DbException {
		try {
			String dbURL = String.format("jdbc:sqlserver://%s;DatabaseName=%s;ApplicationName=%s", server, dbName,
					applicationName);
			String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
			Class.forName(driverName);
			Connection connection = DriverManager.getConnection(dbURL, userName, userPwd);
			return connection;
		} catch (Exception e) {
			// 接数据库失败
			throw new DbException(i18n.prop("msg_bobas_connect_database_faild"), e);
		}
	}

	@Override
	public IBOAdapter4Db createBOAdapter() {
		return new BOAdapter();
	}

}
