package org.colorcoding.ibas.bobas.db.mssql;

import java.sql.Connection;
import java.sql.DriverManager;

import org.colorcoding.ibas.bobas.db.DbException;
import org.colorcoding.ibas.bobas.db.IBOAdapter;

public class DbAdapter extends org.colorcoding.ibas.bobas.db.DbAdapter {

	@Override
	public Connection createConnection(String server, String dbName, String userName, String userPwd,
			String applicationName) throws DbException {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String dbURL = String.format("jdbc:sqlserver://%s;DatabaseName=%s;ApplicationName=%s", server, dbName,
					applicationName);
			return DriverManager.getConnection(dbURL, userName, userPwd);
		} catch (Exception e) {
			// 接数据库失败
			throw new DbException(e);
		}
	}

	@Override
	public IBOAdapter createBOAdapter() {
		return new BOAdapter();
	}

}
