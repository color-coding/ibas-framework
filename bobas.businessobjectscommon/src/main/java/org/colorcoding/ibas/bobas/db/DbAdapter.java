package org.colorcoding.ibas.bobas.db;

import java.sql.Connection;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;

/**
 * 数据库适配器
 * 
 * 包括连接池处理
 * 
 * @author Niuren.Zhu
 *
 */
public abstract class DbAdapter implements IDbAdapter {
	protected static final String MSG_CONNECTION_USER_CONNECTED = "connection: user [%s] connected [%s|%s].";

	@Override
	public IDbConnection createDbConnection() throws DbException {
		return this.createDbConnection("");
	}

	@Override
	public IDbConnection createDbConnection(String sign) throws DbException {
		String dbServer = MyConfiguration
				.getConfigValue(String.format("%s%s", sign, MyConfiguration.CONFIG_ITEM_DB_SERVER), "localhost");
		String dbName = MyConfiguration.getConfigValue(String.format("%s%s", sign, MyConfiguration.CONFIG_ITEM_DB_NAME),
				"ibas_demo");
		String userID = MyConfiguration
				.getConfigValue(String.format("%s%s", sign, MyConfiguration.CONFIG_ITEM_DB_USER_ID), "sa");
		String userPassword = MyConfiguration
				.getConfigValue(String.format("%s%s", sign, MyConfiguration.CONFIG_ITEM_DB_USER_PASSWORD), "1q2w3e");
		return this.createDbConnection(dbServer, dbName, userID, userPassword);
	}

	@Override
	public IDbConnection createDbConnection(String dbServer, String dbName, String dbUser, String dbPassword)
			throws DbException {
		// 创建新的数据库连接
		Connection connection = this.createConnection(dbServer, dbName, dbUser, dbPassword,
				String.format("ibas_%s", this.hashCode()));
		if (connection == null)
			// 没有有效的数据库连接
			throw new DbException(I18N.prop("msg_bobas_no_valid_database_connection"));
		Logger.log(MSG_CONNECTION_USER_CONNECTED, dbUser, dbServer, dbName);
		return new DbConnection(connection);
	}

	protected abstract Connection createConnection(String server, String dbName, String userName, String userPwd,
			String applicationName) throws DbException;

	@Override
	public abstract IBOAdapter createBOAdapter();

	@Override
	public ISqlScriptInspector createSqlInspector() {
		return new SqlScriptInspector();
	}

}
