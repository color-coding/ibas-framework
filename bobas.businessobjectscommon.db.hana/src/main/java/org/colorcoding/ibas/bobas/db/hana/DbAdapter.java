package org.colorcoding.ibas.bobas.db.hana;

import java.sql.Connection;
import java.sql.DriverManager;

import org.colorcoding.ibas.bobas.db.DbException;
import org.colorcoding.ibas.bobas.db.IBOAdapter;

public class DbAdapter extends org.colorcoding.ibas.bobas.db.DbAdapter {

	@Override
	public Connection createConnection(String server, String dbName, String userName, String userPwd,
			String applicationName) throws DbException {
		try {
			Class.forName("com.sap.db.jdbc.Driver");
			if (server.indexOf(":") < 0) {
				// 没有添加端口
				server = String.format("%s:30015", server);
			}
			String dbURL = String.format("jdbc:sap://%s/?currentschema=\"%s\"", server, dbName);
			return DriverManager.getConnection(dbURL, userName, userPwd);
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
