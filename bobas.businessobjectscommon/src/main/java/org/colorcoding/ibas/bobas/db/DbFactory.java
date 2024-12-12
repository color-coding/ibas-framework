package org.colorcoding.ibas.bobas.db;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.colorcoding.ibas.bobas.common.Strings;

public class DbFactory {

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
			String namespace = DbAdapter.class.getName();
			namespace = namespace.substring(0, namespace.lastIndexOf(this.getClass().getSimpleName()));
			String nameClass = Strings.concat(namespace, type.toLowerCase());
			Class<?> adapterClass = Class.forName(nameClass);
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
		if (connection != null && !this.adapters.containsKey(connection.getClass().getName())) {
			// 记录此数据库链接的解释器
			this.adapters.put(connection.getClass().getName(), adapter);
		}
		return connection;
	}
}
