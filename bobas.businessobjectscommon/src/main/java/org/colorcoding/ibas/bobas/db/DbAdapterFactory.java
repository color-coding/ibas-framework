package org.colorcoding.ibas.bobas.db;

import java.util.HashMap;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;
import org.colorcoding.ibas.bobas.i18n.i18n;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;

/**
 * 数据库适配器工厂 根据配置创建适配器
 */
public class DbAdapterFactory extends ConfigurableFactory<IDbAdapter> {
	private DbAdapterFactory() {
	}

	private volatile static DbAdapterFactory instance;

	public synchronized static DbAdapterFactory create() {
		if (instance == null) {
			synchronized (DbAdapterFactory.class) {
				if (instance == null) {
					instance = new DbAdapterFactory();
				}
			}
		}
		return instance;
	}

	@Override
	protected IDbAdapter createDefault(String typeName) {
		return null;
	}

	/**
	 * 默认适配器，配置文件指定
	 */
	volatile private static IDbAdapter defaultDbAdapter = null;
	/**
	 * 可用适配器列表，加载过的
	 */
	volatile private static HashMap<String, IDbAdapter> dbAdapters = null;

	/**
	 * 创建适配器
	 */
	public synchronized IDbAdapter createAdapter() {
		if (defaultDbAdapter == null) {
			defaultDbAdapter = this.create(MyConfiguration.CONFIG_ITEM_DB_TYPE, "DbAdapter");
		}
		return defaultDbAdapter;
	}

	public synchronized IDbAdapter createAdapter(String type) throws DbException {
		if (type == null || type.isEmpty()) {
			// 空或没有指定，则返回默认
			return createAdapter();
		}
		String dbType = type.toLowerCase();// 转为小写
		if (dbAdapters == null) {
			dbAdapters = new HashMap<String, IDbAdapter>();
		}
		if (dbAdapters.containsKey(dbType)) {
			return dbAdapters.get(dbType);
		} else {
			try {
				IDbAdapter adapter = this.newInstance(dbType, "DbAdapter");// 数据库适配器
				if (adapter == null) {
					throw new NullPointerException();
				}
				RuntimeLog.log(RuntimeLog.MSG_DB_ADAPTER_CREATED, dbType);
				dbAdapters.put(dbType, adapter);
				return adapter;
			} catch (Exception e) {
				throw new DbException(i18n.prop("msg_bobas_create_db_adapter_falid"), e);
			}
		}
	}

}
