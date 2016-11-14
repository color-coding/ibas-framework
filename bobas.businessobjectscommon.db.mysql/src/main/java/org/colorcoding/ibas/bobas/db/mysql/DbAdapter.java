package org.colorcoding.ibas.bobas.db.mysql;

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
			String dbURL = String.format("jdbc:mysql://%s/%s?useUnicode=true&characterEncoding=UTF-8&useSSL=false",
					server, dbName);
			String driverName = "com.mysql.jdbc.Driver";
			Class.forName(driverName);
			Connection connection = DriverManager.getConnection(dbURL, userName, userPwd);
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			return connection;
		} catch (Exception e) {
			// 连接数据库失败
			throw new DbException(i18n.prop("msg_bobas_connect_database_faild"), e);
		}
	}

	@Override
	public IBOAdapter4Db createBOAdapter() {
		return new BOAdapter();
	}

}
