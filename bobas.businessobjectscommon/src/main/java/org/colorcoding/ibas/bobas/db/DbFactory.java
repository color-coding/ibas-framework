package org.colorcoding.ibas.bobas.db;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.colorcoding.ibas.bobas.bo.BOFactory;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.message.Logger;

public class DbFactory {

	private static final String MSG_CONNECTION_USER_CONNECTED = "connection: user [%s] connected [%s|%s].";

	private DbFactory() {
	}

	private volatile static DbFactory instance;

	public synchronized static DbFactory create() {
		if (instance == null) {
			synchronized (DbFactory.class) {
				if (instance == null) {
					instance = new DbFactory();
				}
			}
		}
		return instance;
	}

	private Map<String, DbAdapter> adapters = new HashMap<>(8);

	public DbAdapter createAdapter(String type) throws Exception {
		if (!this.adapters.containsKey(type)) {
			String nameClass = Strings.concat(DbAdapter.class.getPackage().getName(), ".", type.toLowerCase(), ".",
					DbAdapter.class.getSimpleName());
			Class<?> adapterClass = BOFactory.loadClass(nameClass);
			if (adapterClass == null || adapterClass.isAssignableFrom(DbAdapter.class)) {
				throw new Exception(Strings.format("unavailable [%s].", nameClass));
			}
			this.adapters.put(type, (DbAdapter) adapterClass.newInstance());
		}
		DbAdapter adapter = this.adapters.get(type);
		if (adapter == null) {
			throw new Exception("not found db adapter.");
		}
		return adapter;
	}

	public Connection createConnection(String type, String server, String dbName, String userName, String userPwd)
			throws Exception {
		DbAdapter adapter = createAdapter(type);
		Connection connection = adapter.createConnection(server, dbName, userName, userPwd);
		Logger.log(MSG_CONNECTION_USER_CONNECTED, userName, server, dbName);
		if (connection != null && !this.adapters.containsKey(connection.getClass().getName())) {
			// 记录此数据库链接的解释器
			this.adapters.put(connection.getClass().getName(), adapter);
		}
		return connection;
	}
}
