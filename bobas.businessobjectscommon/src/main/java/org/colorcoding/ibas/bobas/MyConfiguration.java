package org.colorcoding.ibas.bobas;

import org.colorcoding.ibas.bobas.configuration.Configuration;

/**
 * 我的配置
 */
public class MyConfiguration extends Configuration {

	/**
	 * 框架核心命名空间
	 */
	public static final String NAMESPACE_BOBAS = "http://colorcoding.org/ibas/bobas";
	/**
	 * 框架核心命名空间
	 */
	public static final String NAMESPACE_BOBAS_CORE = NAMESPACE_BOBAS + "/core";
	/**
	 * 通用命名空间
	 */
	public static final String NAMESPACE_BOBAS_COMMON = NAMESPACE_BOBAS + "/common";
	/**
	 * 数据命名空间
	 */
	public static final String NAMESPACE_BOBAS_DATA = NAMESPACE_BOBAS + "/data";
	/**
	 * 业务对象命名空间
	 */
	public static final String NAMESPACE_BOBAS_BO = NAMESPACE_BOBAS + "/bo";
	/**
	 * 国际化语言命名空间
	 */
	public static final String NAMESPACE_BOBAS_I18N = NAMESPACE_BOBAS + "/i18n";
	/**
	 * 数据库命名空间
	 */
	public static final String NAMESPACE_BOBAS_DB = NAMESPACE_BOBAS + "/db";

	/**
	 * 配置项命名空间
	 */
	public static final String NAMESPACE_BOBAS_CONFIGURATION = NAMESPACE_BOBAS + "/configuration";
	/**
	 * 序列化命名空间
	 */
	public static final String NAMESPACE_BOBAS_SERIALIZATION = NAMESPACE_BOBAS + "/serialization";

	/**
	 * 配置项目-调试模式
	 */
	public final static String CONFIG_ITEM_DEBUG_MODE = "DebugMode";
	private static volatile short DEBUG_MODE = -1;

	/**
	 * 处于debug模式
	 * 
	 * @return
	 */
	public static boolean isDebugMode() {
		return DEBUG_MODE == 1 ? true : false;
	}

	/**
	 * 配置项目-配置方式
	 */
	public final static String CONFIG_ITEM_CONFIGURATION_WAY = "ConfigurationWay";

	/**
	 * 配置项目-公司标记
	 */
	public final static String CONFIG_ITEM_COMPANY = "Company";

	/**
	 * 配置项目-应用名称
	 */
	public final static String CONFIG_ITEM_APPLICATION_NAME = "AppName";

	/**
	 * 配置项目-数据库类型
	 */
	public final static String CONFIG_ITEM_DB_TYPE = "DbType";
	/**
	 * 配置项目-数据库地址
	 */
	public final static String CONFIG_ITEM_DB_SERVER = "DbServer";
	/**
	 * 配置项目-数据库名称
	 */
	public final static String CONFIG_ITEM_DB_NAME = "DbName";
	/**
	 * 配置项目-数据库用户名称
	 */
	public final static String CONFIG_ITEM_DB_USER_ID = "DbUserID";
	/**
	 * 配置项目-数据库用户密码
	 */
	public final static String CONFIG_ITEM_DB_USER_PASSWORD = "DbUserPassword";
	/**
	 * 配置项目-数据库语句批量数量
	 */
	public final static String CONFIG_ITEM_DB_STATEMENT_BATCH_COUNT = "DbBatchCount";
	/**
	 * 配置项目-数据库替换更新方式
	 */
	public final static String CONFIG_ITEM_DB_REPLACEMENT_UPDATE = "DbReplacementUpdate";
	/**
	 * 配置项目-数据库无用户事务通知
	 */
	public final static String CONFIG_ITEM_DB_NO_USER_TANSACTION_SP = "DbNoUserTansactionSP";
}
