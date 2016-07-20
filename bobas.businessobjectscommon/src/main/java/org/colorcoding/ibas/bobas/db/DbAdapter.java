package org.colorcoding.ibas.bobas.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;
import org.colorcoding.ibas.bobas.util.EncryptMD5;

/**
 * 数据库适配器
 * 
 * 包括连接池处理
 * 
 * @author Niuren.Zhu
 *
 */
public abstract class DbAdapter implements IDbAdapter {

	public DbAdapter() {

	}

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
		RuntimeLog.log(RuntimeLog.MSG_CONNECTION_USING_SERVER, sign, userID, dbServer, dbName);
		return this.createDbConnection(dbServer, dbName, userID, userPassword);
	}

	@Override
	public IDbConnection createDbConnection(String dbServer, String dbName, String dbUser, String dbPassword)
			throws DbException {
		String appName = String.format("ibas_%s", this.hashCode());
		String connectionSign = this.encrypt(appName, dbServer, dbName, dbUser, dbPassword);
		// 尝试从连接池中获取可用连接
		if (connectionSign != null && DbConnectionPool.isEnabled()) {
			// 获取到有效标记
			IDbConnection dbConnection = DbConnectionPool.getValid(connectionSign);
			if (dbConnection != null) {
				return dbConnection;
			}
		}
		// 创建新的数据库连接
		Connection connection = this.createConnection(dbServer, dbName, dbUser, dbPassword, appName);
		if (connection == null)
			// 没有有效的数据库连接
			throw new DbException(i18n.prop("msg_bobas_no_valid_database_connection"));
		RuntimeLog.log(RuntimeLog.MSG_CONNECTION_USER_CONNECTED, dbUser, dbServer, dbName);
		// 检查返回的数据库连接与要求是否一致
		String cURL = null, cUserName = null;
		try {
			// hana连接字符串特有
			if (connection instanceof ConnectionUrl) {
				cURL = ((ConnectionUrl) connection).getUrl();
			} else {
				cURL = connection.getMetaData().getURL();
			}
			cUserName = connection.getMetaData().getUserName();
		} catch (SQLException e) {
			throw new DbException(i18n.prop("msg_bobas_no_valid_database_connection"));
		}
		if (cURL == null || cUserName == null) {
			throw new DbException(i18n.prop("msg_bobas_no_valid_database_connection"));
		}
		if (cURL.indexOf(dbServer) < 0 || cURL.indexOf(dbName) < 0 || cUserName.indexOf(dbUser) < 0) {
			// 返回的连接与要求不匹配
			throw new DbException(i18n.prop("msg_bobas_no_valid_database_connection"));
		}
		DbConnection dbConnection = new DbConnection(connection);
		dbConnection.setConnectionSign(connectionSign);
		return dbConnection;
	}

	private String encrypt(String... args) {
		try {
			return EncryptMD5.md5(args);
		} catch (Exception e) {
			return null;
		}
	}

	protected abstract Connection createConnection(String server, String dbName, String userName, String userPwd,
			String applicationName) throws DbException;

	@Override
	public abstract IBOAdapter4Db createBOAdapter();

}
