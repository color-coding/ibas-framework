package org.colorcoding.ibas.bobas.messages;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.colorcoding.ibas.bobas.MyConfiguration;

/**
 * 运行日志
 * 
 * @author Niuren.Zhu
 *
 */
public class RuntimeLog {

	public static final String MSG_PERMISSIONS_NOT_AUTHORIZED = "permissions: user [%s] call [%s - %s] is not authorized.";
	public static final String MSG_CONNECTION_USER_CONNECTED = "connection: user [%s] connected [%s|%s].";
	public static final String MSG_CONNECTION_USING_SERVER = "connection: using {%s}, user [%s] & server [%s] & db [%s].";
	public static final String MSG_SQL_SCRIPTS = "sql: %s";
	public static final String MSG_REPOSITORY_FETCH_AND_FILTERING = "repository: fetch [%s] [%s] times, result [%s] filtering [%s].";
	public static final String MSG_REPOSITORY_FETCHING_IN_CACHE = "repository: fetching [%s] in cache repository.";
	public static final String MSG_REPOSITORY_FETCHING_IN_DB = "repository: fetching [%s] in db repository.";
	public static final String MSG_REPOSITORY_FETCHING_IN_READONLY_REPOSITORY = "repository: fetching [%s] in readonly repository.";
	public static final String MSG_REPOSITORY_FETCHING_IN_MASTER_REPOSITORY = "repository: fetching [%s] in master repository.";
	public static final String MSG_REPOSITORY_DELETED_DATA_FILE = "repository: deleted data file [%s].";
	public static final String MSG_REPOSITORY_WRITED_DATA_FILE = "repository: writed data in file [%s].";
	public static final String MSG_REPOSITORY_CHANGED_USER = "repository: changed user [%s].";
	public static final String MSG_PROPERTIES_GET_TYPE_PROPERTIES = "properties: get type [%s]'s properties.";
	public static final String MSG_PROPERTIES_REGISTER_PROPERTIES = "properties: register type [%s]'s property [%s].";
	public static final String MSG_DB_POOL_USING_CONNECTION = "db pool: using connection [%s].";
	public static final String MSG_DB_POOL_RECYCLED_CONNECTION = "db pool: recycled connection [%s].";
	public static final String MSG_JUDGMENT_EXPRESSION = "judgment: expression %s = [%s]";
	public static final String MSG_JUDGMENT_RELATION = "judgment: relation %s = [%s]";
	public static final String MSG_DB_ADAPTER_CREATED = "db adapter: created db adapter [%s].";
	public static final String MSG_TRANSACTION_SP_VALUES = "transaction: sp [%s] [%s] [%s - %s]";
	public static final String MSG_USER_FIELDS_REGISTER = "user fields: register type [%s]'s user fields, count [%s].";
	public static final String MSG_USER_SET_FIELD_VALUE = "user fields: set field [%s]'s value [%s].";
	public static final String MSG_APPROVAL_PROCESS_MANAGER_CREATED = "ap manager: created approval process manager [%s].";
	public static final String MSG_BUSINESS_LOGICS_MANAGER_CREATED = "bl manager: created business logics manager [%s].";
	public static final String MSG_I18N_READ_FILE_DATA = "i18n: read file's data [%s].";
	public static final String MSG_I18N_RESOURCES_FOLDER = "i18n: use folder [%s].";
	public static final String MSG_CONFIG_READ_FILE_DATA = "config: read file's data [%s].";
	public static final String MSG_LOGICS_RUNNING_LOGIC_FORWARD = "logics: forward logic [%s] by data [%s].";
	public static final String MSG_LOGICS_RUNNING_LOGIC_REVERSE = "logics: reverse logic [%s] by data [%s].";
	public static final String MSG_LOGICS_EXISTING_CONTRACT = "logics: class [%s] existing contract [%s].";

	private static long debugMode = -1;// log类型线程安全

	/**
	 * 是否处于debug模式
	 * 
	 * @return
	 */
	protected static boolean isDebugMode() {
		// 访问频繁，提高下性能
		if (debugMode == -1) {
			synchronized (RuntimeLog.class) {
				if (debugMode == -1) {
					boolean value = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_DEBUG_MODE, false);
					if (value) {
						debugMode = 1;
					} else {
						debugMode = 0;
					}
				}
			}
		}
		return debugMode == 1 ? true : false;
	}

	private volatile static IMessageRecorder recorder;

	protected static IMessageRecorder getRecorder() {
		if (recorder == null) {
			synchronized (RuntimeLog.class) {
				if (recorder == null) {
					recorder = RecorderFactory.createRecorder("ibas_runtime_%s.log");
				}
			}
		}
		return recorder;
	}

	/**
	 * 日记记录主要方法
	 * 
	 * @param message
	 *            传递过来的消息
	 */
	public static void log(IMessage message) {
		if (message == null) {
			return;
		}
		if (isDebugMode()) {
			getRecorder().record(message);
		}
	}

	/**
	 * 记录消息
	 * 
	 * @param level
	 *            消息级别
	 * @param message
	 *            消息内容
	 * @param tag
	 *            消息标签
	 */
	public static void log(MessageLevel level, String message, String tag) {
		log(Message.create(level, message, tag));
	}

	/**
	 * 记录消息
	 * 
	 * @param message
	 *            消息内容
	 */
	public static void log(String message) {
		log(MessageLevel.information, message, "");
	}

	/**
	 * 记录消息
	 * 
	 * @param level
	 *            消息级别
	 * @param message
	 *            消息内容
	 */
	public static void log(MessageLevel level, String message) {
		log(level, message, "");
	}

	/**
	 * 记录消息，带格式参数（message %s.）
	 * 
	 * @param message
	 *            消息内容及格式
	 * @param args
	 *            格式中的参数
	 */
	public static void log(String message, Object... args) {
		log(MessageLevel.information, message, args);
	}

	/**
	 * 记录消息，带格式参数（message %s.）
	 * 
	 * @param level
	 *            消息级别
	 * @param message
	 *            消息内容及格式
	 * @param args
	 *            格式中的参数
	 */
	public static void log(MessageLevel level, String message, Object... args) {
		log(level, String.format(message, args), "");
	}

	/**
	 * 记录消息
	 * 
	 * @param exception
	 *            异常
	 */
	public static void log(Exception e) {
		if (e == null) {
			return;
		}
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);
		log(MessageLevel.error, stringWriter.toString());
	}
}
