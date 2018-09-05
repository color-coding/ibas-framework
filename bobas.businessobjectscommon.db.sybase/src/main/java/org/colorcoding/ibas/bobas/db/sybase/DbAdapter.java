package org.colorcoding.ibas.bobas.db.sybase;

import java.sql.Connection;
import java.sql.DriverManager;

import org.colorcoding.ibas.bobas.db.DbException;
import org.colorcoding.ibas.bobas.db.IBOAdapter4Db;

public class DbAdapter extends org.colorcoding.ibas.bobas.db.DbAdapter {

	@Override
	public Connection createConnection(String server, String dbName, String userName, String userPwd,
			String applicationName) throws DbException {
		try {
			Class.forName("com.sybase.jdbc4.jdbc.SybDriver");
			if (server.indexOf(":") < 0) {
				// 没有添加端口
				server = String.format("%s:2638", server);
			}
			String dbURL = String.format("jdbc:sybase:Tds:%s?ServiceName=%s", server, dbName, applicationName);
			return DriverManager.getConnection(dbURL, userName, userPwd);
		} catch (Exception e) {
			// 接数据库失败
			throw new DbException(e);
		}
	}

	@Override
	public IBOAdapter4Db createBOAdapter() {
		return new BOAdapter();
	}

}
