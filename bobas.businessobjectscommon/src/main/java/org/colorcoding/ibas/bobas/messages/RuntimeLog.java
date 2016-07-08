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
	public static final String MSG_REPOSITORY_FETCHING_IN_READONLY_DB = "repository: fetching [%s] in readonly db repository.";
	public static final String MSG_REPOSITORY_FETCHING_IN_MASTER_DB = "repository: fetching [%s] in master db repository.";
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

	/**
	 * 记录系统错误
	 * 
	 * @param exception
	 *            异常
	 */
	public static void log(Exception e) {
		if (e == null) {
			return;
		}
		try {
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			e.printStackTrace(printWriter);
			log(MessageLevel.error, stringWriter.toString());
		} catch (Exception e2) {
			e2.printStackTrace();
		}

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
		if (MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_DEBUG_MODE, false)) {
			IMessageRecord messageRecord = new MessageRecord();
			if (messageRecord != null) {
				messageRecord.addMessage(message);
			}
		}
		if (message.getLevel() == MessageLevel.error) {
			System.err.println(message.outString());
		} else {
			System.out.println(message.outString());
		}
	}

	public static void log(String message) {
		log(MessageLevel.information, message, "");
	}

	public static void log(MessageLevel level, String message) {

		log(level, message, "");
	}

	public static void log(MessageLevel level, String message, String tag) {

		log(Message.create(level, message, tag));
	}

	public static void log(String message, Object... args) {

		log(MessageLevel.information, message, args);
	}

	public static void log(MessageLevel level, String message, Object... args) {

		log(level, String.format(message, args), "");
	}
}
