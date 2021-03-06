package org.colorcoding.ibas.bobas.db.sqlite;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.db.DbException;
import org.colorcoding.ibas.bobas.db.IBOAdapter;

public class DbAdapter extends org.colorcoding.ibas.bobas.db.DbAdapter {

	@Override
	public Connection createConnection(String server, String dbName, String userName, String userPwd,
			String applicationName) throws DbException {
		try {
			Class.forName("org.sqlite.JDBC");
			File file = new File(dbName);
			if (file.getParentFile() == null || !file.getParentFile().isDirectory()) {
				file = new File(MyConfiguration.getWorkFolder() + File.separator + file.getName());
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
			}
			String dbURL = String.format("jdbc:sqlite:%s?date_string_format=%s", file.getAbsolutePath(),
					DateTime.FORMAT_DATE);
			Connection connection = DriverManager.getConnection(dbURL, userName, userPwd);
			return connection;
		} catch (Exception e) {
			// 连接数据库失败
			throw new DbException(e);
		}
	}

	@Override
	public IBOAdapter createBOAdapter() {
		return new BOAdapter();
	}

}
