package org.colorcoding.ibas.bobas.db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.TimeZone;

import org.colorcoding.ibas.bobas.db.DbException;
import org.colorcoding.ibas.bobas.db.IBOAdapter;
import org.colorcoding.ibas.bobas.util.URIEncoder;

public class DbAdapter extends org.colorcoding.ibas.bobas.db.DbAdapter {

	@Override
	public Connection createConnection(String server, String dbName, String userName, String userPwd,
			String applicationName) throws DbException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String timeZone = TimeZone.getDefault().getID();
			if (timeZone == null || timeZone.isEmpty()) {
				timeZone = "UTC";
			}
			String dbURL = String.format(
					"jdbc:mysql://%s/%s?useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=%s",
					server, dbName, URIEncoder.encodeURIComponent(timeZone));
			Connection connection = DriverManager.getConnection(dbURL, userName, userPwd);
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
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
