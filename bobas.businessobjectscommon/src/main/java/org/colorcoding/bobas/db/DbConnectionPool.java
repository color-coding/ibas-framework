package org.colorcoding.bobas.db;

import org.colorcoding.bobas.MyConfiguration;
import org.colorcoding.bobas.messages.RuntimeLog;

public class DbConnectionPool implements IDbConnectionPool {

	static int poolSize = -1;

	/**
	 * 获取-数据库连接缓存大小
	 * 
	 * @return
	 */
	static int getPoolSize() {
		if (poolSize == -1) {
			synchronized (DbConnectionPool.class) {
				if (poolSize == -1) {
					// 未被初始化，默认连接池数量10
					poolSize = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_DB_CONNECTION_POOL_SIZE, 10);
				}
			}
		}
		return poolSize;
	}

	void setPoolSize(int value) {
		poolSize = value;
	}

	/**
	 * 启用数据库连接池
	 * 
	 * @return
	 */
	public static boolean isEnabled() {
		if (DbConnectionPool.getPoolSize() > 0) {
			return true;
		}
		return false;
	}

	private volatile static IDbConnectionPool connectionPool = null;

	static final IDbConnectionPool getConnectionPool() {
		if (connectionPool == null) {
			synchronized (DbConnectionPool.class) {
				if (connectionPool == null) {
					connectionPool = new DbConnectionPool();
				}
			}
		}
		return connectionPool;
	}

	/**
	 * 还回资源
	 * 
	 * @param connection
	 *            数据库连接
	 * @throws DbException
	 */
	static boolean giveBack(IDbConnection connection) throws DbException {
		synchronized (DbConnectionPool.class) {
			if (isEnabled() && getConnectionPool() != null) {
				// 启动了连接池并且有效
				boolean done = getConnectionPool().recycling(connection);
				if (done) {
					RuntimeLog.log(RuntimeLog.MSG_DB_POOL_RECYCLED_CONNECTION, connection.hashCode());
				}
				return done;

			}
			return false;
		}
	}

	/**
	 * 获取连接
	 * 
	 * @param sign
	 *            连接标记
	 * @return 数据库连接
	 */
	static IDbConnection getValid(String sign) {
		synchronized (DbConnectionPool.class) {
			if (isEnabled() && getConnectionPool() != null) {
				// 启动了连接池并且有效
				IDbConnection connection = getConnectionPool().obtain(sign);
				if (connection != null) {
					if (connection instanceof DbConnection) {
						DbConnection dbConnection = (DbConnection) connection;
						dbConnection.setRecycled(false);
					}
					RuntimeLog.log(RuntimeLog.MSG_DB_POOL_USING_CONNECTION, connection.hashCode());
				}
				return connection;
			}
			return null;
		}
	}

	public DbConnectionPool() {
		if (isEnabled()) {
			this.availableConnections = new IDbConnection[getPoolSize()];
		}
	}

	private volatile IDbConnection[] availableConnections = null;

	@Override
	public IDbConnection obtain(String sign) {
		if (sign == null) {
			return null;
		}
		if (this.availableConnections != null) {
			for (int i = 0; i < this.availableConnections.length; i++) {
				try {
					IDbConnection connection = this.availableConnections[i];
					if (connection == null) {
						continue;
					}
					if (connection instanceof DbConnection) {
						DbConnection dbConnection = (DbConnection) connection;
						if (!dbConnection.isValid()) {
							// 不可用，移出可用列表
							this.availableConnections[i] = null;
							continue;
						}
						if (sign.equals(dbConnection.getConnectionSign())) {
							// 匹配的，移出可用列表并返回
							this.availableConnections[i] = null;
							return connection;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	@Override
	public boolean recycling(IDbConnection connection) {
		try {
			if (connection == null) {
				return false;
			}

			if (!(connection instanceof DbConnection)) {
				return false;
			}
			DbConnection dbConnection = (DbConnection) connection;
			if (!dbConnection.isValid()) {
				// 已关闭，无效的
				return false;
			}
			for (int i = 0; i < this.availableConnections.length; i++) {
				if (this.availableConnections[i] == null) {
					this.availableConnections[i] = connection;
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public int getSize() {
		return getPoolSize();
	}

	@Override
	public void setSize(int value) {
		setPoolSize(value);
	}

}
