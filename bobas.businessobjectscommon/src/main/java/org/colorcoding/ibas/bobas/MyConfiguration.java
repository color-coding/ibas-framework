package org.colorcoding.ibas.bobas;

import org.colorcoding.ibas.bobas.configuration.Configuration;

/**
 * 配置
 */
public class MyConfiguration extends Configuration {

	private static long DEBUG_MODE = -1;// log类型线程安全

	/**
	 * 是否处于debug模式
	 * 
	 * @return
	 */
	public static boolean isDebugMode() {
		// 访问频繁，提高下性能
		if (DEBUG_MODE == -1) {
			synchronized (MyConfiguration.class) {
				if (DEBUG_MODE == -1) {
					boolean value = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_DEBUG_MODE, false);
					if (value) {
						DEBUG_MODE = 1;
					} else {
						DEBUG_MODE = 0;
					}
				}
			}
		}
		return DEBUG_MODE == 1 ? true : false;
	}

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
	 * 配置项命名空间
	 */
	public static final String NAMESPACE_BOBAS_CONFIGURATION = NAMESPACE_BOBAS + "/configuration";
	/**
	 * 配置项目-本模块名称
	 */
	public final static String CONFIG_ITEM_MODULE_NAME = "ModuleName";
	/**
	 * 配置项目-任务线程池大小
	 */
	public final static String CONFIG_ITEM_TASK_THREAD_POOL_SIZE = "TaskThreadPoolSize";
	/**
	 * 配置项目-公司标记
	 */
	public final static String CONFIG_ITEM_COMPANY = "Company";
	/**
	 * 配置项目-调试模式
	 */
	public final static String CONFIG_ITEM_DEBUG_MODE = "DebugMode";
	/**
	 * 配置项目-开启自定义字段
	 */
	public final static String CONFIG_ITEM_BO_ENABLED_USER_FIELDS = "EnabledUserFields";
	/**
	 * 配置项目-关闭业务对象事务通知
	 */
	public final static String CONFIG_ITEM_BO_DISABLED_POST_TRANSACTION = "DisabledPostTransaction";
	/**
	 * 配置项目-关闭业务对象保存后重载
	 */
	public final static String CONFIG_ITEM_BO_DISABLED_REFETCH = "DisabledRefetch";
	/**
	 * 配置项目-关闭业务对象业务逻辑
	 */
	public final static String CONFIG_ITEM_BO_DISABLED_BUSINESS_LOGICS = "DisabledBusinessLogics";
	/**
	 * 配置项目-关闭业务对象业务规则
	 */
	public final static String CONFIG_ITEM_BO_DISABLED_BUSINESS_RULES = "DisabledBusinessRules";
	/**
	 * 配置项目-关闭业务对象审批
	 */
	public final static String CONFIG_ITEM_BO_DISABLED_BUSINESS_APPROVAL = "DisabledBusinessApproval";
	/**
	 * 配置项目-业务对象删除前查询
	 */
	public final static String CONFIG_ITEM_BO_REFETCH_BEFORE_DELETE = "RefetchBeforeDelete";
	/**
	 * 配置项目-开启只读业务仓库
	 */
	public final static String CONFIG_ITEM_ENABLED_READONLY_REPOSITORY = "EnabledReadonlyRepository";
	/**
	 * 配置项目-业务对象的文件仓库目录
	 */
	public final static String CONFIG_ITEM_BO_REPOSITORY_4_FILE_FOLDER = "BORepositoryFileFolder";
	/**
	 * 配置项目-文件仓库目录
	 */
	public final static String CONFIG_ITEM_FILE_REPOSITORY_FOLDER = "FileRepositoryFolder";
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
	 * 配置项目-数据库连接池大小
	 */
	public final static String CONFIG_ITEM_DB_CONNECTION_POOL_SIZE = "DbPoolSize";
	/**
	 * 配置项目-数据库连接的持有时间
	 */
	public final static String CONFIG_ITEM_DB_CONNECTION_HOLDING_TIME = "DbHoldingTime";
	/**
	 * 配置项目-国际化文件路径
	 */
	public final static String CONFIG_ITEM_I18N_PATH = "I18nFolder";
	/**
	 * 配置项目-语言编码
	 */
	public final static String CONFIG_ITEM_LANGUAGE_CODE = "LanguageCode";
	/**
	 * 配置项目-消息记录方式
	 */
	public final static String CONFIG_ITEM_MESSAGE_RECORDER_WAY = "RecorderWay";
	/**
	 * 配置项目-消息日志大小限制（单位兆）
	 */
	public final static String CONFIG_ITEM_LOG_FILE_SIZE_LIMIT = "LogFileSize";
	/**
	 * 配置项目-日志文件保存路径
	 */
	public final static String CONFIG_ITEM_LOG_FILE_FOLDER = "LogFileFolder";
	/**
	 * 序列化实现
	 */
	public final static String CONFIG_ITEM_SERIALIZER = "Serializer";
	/**
	 * 配置项目-日志消息级别
	 */
	public final static String CONFIG_ITEM_LOG_MESSAGE_LEVEL = "MsgLevel";
	/**
	 * 配置项目-日志文件输出频率(毫秒,默认5000)
	 */
	public final static String CONFIG_ITEM_LOG_OUT_FREQUENCY = "LogOutFrequency";
	/**
	 * 配置项目-审批方式
	 */
	public final static String CONFIG_ITEM_APPROVAL_WAY = "ApprovalWay";
	/**
	 * 配置项目-组织方式
	 */
	public final static String CONFIG_ITEM_ORGANIZATION_WAY = "OrganizationWay";
	/**
	 * 配置项目-业务逻辑方式
	 */
	public final static String CONFIG_ITEM_BUSINESS_LOGICS_WAY = "BizLogicsWay";
	/**
	 * 配置项目-权限判断方式
	 */
	public final static String CONFIG_ITEM_OWNERSHIP_WAY = "OwnershipWay";
	/**
	 * 配置项目-业务规则管理方式
	 */
	public final static String CONFIG_ITEM_BUSINESS_RULES_WAY = "BizRulesWay";
	/**
	 * 配置项目-序列化管理方式
	 */
	public final static String CONFIG_ITEM_SERIALIZATION_WAY = "SerializationWay";
	/**
	 * 配置项目-格式化输出
	 */
	public final static String CONFIG_ITEM_FORMATTED_OUTPUT = "FormattedOutput";
	/**
	 * 配置项目-SQL写入语句检查表达式
	 */
	public final static String CONFIG_ITEM_INSPECTOR_SQL_WRITE_REGEX = "SqlWriteRegex";
}
