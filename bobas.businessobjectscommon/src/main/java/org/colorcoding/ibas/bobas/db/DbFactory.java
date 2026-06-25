package org.colorcoding.ibas.bobas.db;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.colorcoding.ibas.bobas.bo.BOFactory;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.message.Logger;

public class DbFactory {

	private static final String MSG_CONNECTION_USER_CONNECTED = "connection: user [%s] connected [%s|%s].";

	private DbFactory() {
	}

	private volatile static DbFactory instance;

	/**
	 * 创建数据库工厂实例（单例模式）
	 *
	 * @return 工厂实例
	 */
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

	private final Map<String, DbAdapter> adapters = new ConcurrentHashMap<>(8);

	/**
	 * 创建数据库适配器（按数据库类型名称，自动反射创建对应实现类）
	 *
	 * @param type 数据库类型名称（如mssql、mysql等）
	 * @return 数据库适配器实例
	 * @throws Exception 适配器类不存在或无法实例化时抛出
	 */
	public synchronized DbAdapter createAdapter(String type) throws Exception {
		if (!this.adapters.containsKey(type)) {
			String nameClass = Strings.concat(DbAdapter.class.getPackage().getName(), ".", type.toLowerCase(), ".",
					DbAdapter.class.getSimpleName());
			Class<?> adapterClass = BOFactory.loadClass(nameClass);
			if (adapterClass == null || !DbAdapter.class.isAssignableFrom(adapterClass)) {
				throw new Exception(Strings.format("unavailable [%s].", nameClass));
			}
			this.adapters.put(type, (DbAdapter) adapterClass.getDeclaredConstructor().newInstance());
		}
		DbAdapter adapter = this.adapters.get(type);
		if (adapter == null) {
			throw new Exception("not found db adapter.");
		}
		return adapter;
	}

	/**
	 * 创建数据库连接
	 *
	 * @param type     数据库类型名称
	 * @param server   服务器地址
	 * @param dbName   数据库名称
	 * @param userName 用户名
	 * @param userPwd  密码
	 * @return 数据库连接
	 * @throws Exception 连接创建失败时抛出
	 */
	public synchronized Connection createConnection(String type, String server, String dbName, String userName, String userPwd)
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
