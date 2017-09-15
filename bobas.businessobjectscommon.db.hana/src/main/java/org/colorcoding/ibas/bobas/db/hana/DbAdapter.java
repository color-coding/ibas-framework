package org.colorcoding.ibas.bobas.db.hana;

import java.sql.Connection;

import org.colorcoding.ibas.bobas.db.DbException;
import org.colorcoding.ibas.bobas.db.IBOAdapter4Db;
import org.colorcoding.ibas.bobas.i18n.I18N;

public class DbAdapter extends org.colorcoding.ibas.bobas.db.DbAdapter {

	@Override
	public Connection createConnection(String server, String dbName, String userName, String userPwd,
			String applicationName) throws DbException {
		try {
			if (server.indexOf(":") < 0) {
				// 没有添加端口
				server = String.format("%s:30015", server);
			}
			String dbURL = String.format("jdbc:sap://%s/?currentschema=\"%s\"", server, dbName);
			String driverName = "com.sap.db.jdbc.Driver";
			Class.forName(driverName);
			Connection connection = new HanaConnection(dbURL, userName, userPwd);
			return connection;
		} catch (Exception e) {
			// 连接数据库失败
			throw new DbException(I18N.prop("msg_bobas_connect_database_faild"), e);
		}
	}

	@Override
	public IBOAdapter4Db createBOAdapter() {
		return new BOAdapter();
	}

}
