package org.colorcoding.ibas.bobas.db.sybase;

import java.sql.Connection;
import java.sql.DriverManager;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.logging.Logger;

public class DbAdapter extends org.colorcoding.ibas.bobas.db.DbAdapter {

	@Override
	public Connection createConnection(String server, String dbName, String userName, String userPwd) {
		try {
			Class.forName("com.sybase.jdbc4.jdbc.SybDriver");
			if (server.indexOf(":") < 0) {
				// 没有添加端口
				server = String.format("%s:2638", server);
			}
			String dbURL = String.format("jdbc:sybase:Tds:%s?ServiceName=%s", server, dbName);
			if (MyConfiguration.isDebugMode()) {
				Logger.log(Strings.format("db adapter: %s", dbURL));
			}
			return DriverManager.getConnection(dbURL, userName, userPwd);
		} catch (Exception e) {
			// 接数据库失败
			throw new RuntimeException(e);
		}
	}

	@Override
	public String parsingStoredProcedure(String spName, String... args) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("CALL");
		stringBuilder.append(" ");
		stringBuilder.append(this.identifier());
		stringBuilder.append(MyConfiguration.applyVariables(spName));
		stringBuilder.append(this.identifier());
		if (args.length > 0) {
			stringBuilder.append(" ");
			stringBuilder.append("(");
			int count = stringBuilder.length();
			for (String arg : args) {
				if (stringBuilder.length() > count) {
					stringBuilder.append(this.separation());
				}
				if (Strings.isNullOrEmpty(arg)) {
					stringBuilder.append("?");
				} else {
					stringBuilder.append(arg);
					stringBuilder.append(" ");
					stringBuilder.append("=");
					stringBuilder.append(" ");
					stringBuilder.append("?");
				}
			}
			stringBuilder.append(")");
		}
		return stringBuilder.toString();
	}
}
