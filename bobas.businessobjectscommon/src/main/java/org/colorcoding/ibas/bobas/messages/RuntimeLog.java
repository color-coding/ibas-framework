package org.colorcoding.ibas.bobas.messages;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.data.DateTime;

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
	 * 单例模式
	 */
	private static volatile RuntimeLog instance;
	private static Thread writeThread;

	public static RuntimeLog create() {
		if (instance == null) {
			synchronized (RuntimeLog.class) {
				if (instance == null) {
					instance = new RuntimeLog();
					writeThread = new Thread() {
						@SuppressWarnings("static-access")
						@Override
						public void run() {
							try {
								create().writeLog();
								Thread.currentThread().sleep(logOutFrequency());
								run();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					};
					writeThread.start();
				}
			}
		}
		return instance;
	}

	protected RuntimeLog() {
	}

	public static boolean isLogMessages() {
		return MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_DEBUG_MODE, false);
	}

	/**
	 * 从配置文件中获取写日记文件时间频率
	 * 
	 * @return 时间频率
	 */
	public static long logOutFrequency() {
		return MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_LOG_OUT_FREQUENCY, 5000);
	}

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
		e.printStackTrace();
		try {
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			e.printStackTrace(printWriter);
			log(stringWriter.toString());
		} catch (Exception e2) {
			e2.printStackTrace();
		}

	}

	public static void log(IMessage message) {
		if (!isLogMessages()) {
			return;
		}
		create().addMessage(message);
		System.out.println(message.outString());
	}

	public static void log(String message) {
		if (!isLogMessages()) {
			return;
		}
		log(MessageLevel.information, message, "");
	}

	public static void log(MessageLevel level, String message) {
		if (!isLogMessages()) {
			return;
		}
		log(level, message, "");
	}

	public static void log(MessageLevel level, String message, String tag) {
		if (!isLogMessages()) {
			return;
		}
		log(Message.create(level, message, tag));
	}

	public static void log(String message, Object... args) {
		if (!isLogMessages()) {
			return;
		}
		log(MessageLevel.information, message, args);
	}

	public static void log(MessageLevel level, String message, Object... args) {
		if (!isLogMessages()) {
			return;
		}
		log(level, String.format(message, args), "");
	}

	/**
	 * 记录系统消息
	 * 
	 * @param message
	 *            消息
	 */
	protected void addMessage(IMessage message) {
		if (message == null) {
			return;
		}
		this.getMessageQueue().offer(message);

	}

	/**
	 * 将日志消息写入到文本中
	 */
	protected void writeLog() {
		if (this.getMessageQueue().isEmpty()) {
			return;
		}
		FileHandler fileHandler = null;
		try {
			String filePath = this.getLogFilePath();
			String fileName = String.format("ibas_%s.log", DateTime.getNow().toString("yyyyMMdd"));
			String pattern = String.format("%s%s%s", filePath, File.separator, fileName);
			fileHandler = new FileHandler(pattern, true);
			Logger log = Logger.getLogger("AVATECH");
			log.setUseParentHandlers(false);
			fileHandler.setFormatter(new Formatter() {
				@Override
				public String format(LogRecord record) {
					return record.getMessage() + System.getProperty("line.separator");
				}
			});
			log.addHandler(fileHandler);
			while (!this.getMessageQueue().isEmpty()) {
				IMessage message = this.getMessageQueue().poll();
				log.info(message.outString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			fileHandler.close();
		}
	}

	/**
	 * 日志队列
	 */
	private Queue<IMessage> messageQueue;

	private Queue<IMessage> getMessageQueue() {
		if (this.messageQueue == null) {
			messageQueue = new ConcurrentLinkedQueue<IMessage>();// 非阻塞队列，支持异步
		}
		return this.messageQueue;
	}

	/**
	 * 获取或设置日志文件路径
	 */
	private String logFilePath;

	private String getLogFilePath() {
		if (logFilePath == null || logFilePath.equals("")) {
			logFilePath = "";
			String workFolder = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_WORK_FOLDER);
			String logPath = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_LOG_PATH);
			String path = String.format("%s/%s", workFolder, logPath);
			if (!path.equals("/")) {// 配置文件没有配置相关信息
				File file_WorkFolder = new File(path);
				if (file_WorkFolder.exists()) {
					logFilePath = file_WorkFolder.getPath();
					return logFilePath;
				}
			}
			String classPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
			logFilePath = String.format("%s%s%s", classPath, "log", File.separator);
			File file_classPath = new File(logFilePath);
			if (!file_classPath.exists()) {
				file_classPath.mkdirs();
			}
			logFilePath = file_classPath.getPath();
		}
		return logFilePath;
	}

	public void setLogFilePath(String logFilePath) {
		this.logFilePath = logFilePath;
	}

}
