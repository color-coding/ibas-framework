package org.colorcoding.ibas.bobas.message;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Strings;

/**
 * 运行日志
 * 
 * @author Niuren.Zhu
 *
 */
public class Logger {

	private Logger() {

	}

	private static volatile int MESSAGE_LEVEL = -1;

	/**
	 * 是否处于debug模式
	 * 
	 * @return
	 */
	protected static int getLoggingLevel() {
		// 访问频繁，提高下性能
		if (MESSAGE_LEVEL == -1) {
			synchronized (Logger.class) {
				if (MESSAGE_LEVEL == -1) {
					if (MyConfiguration.isDebugMode()) {
						MESSAGE_LEVEL = MessageLevel.DEBUG.ordinal();
					} else {
						String value = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_LOG_MESSAGE_LEVEL);
						if (value != null && !value.isEmpty()) {
							MessageLevel level = MessageLevel.valueOf(value.toUpperCase());
							MESSAGE_LEVEL = level.ordinal();
						} else {
							MESSAGE_LEVEL = MessageLevel.ERROR.ordinal();
						}
					}
				}
			}
		}
		return MESSAGE_LEVEL;
	}

	private volatile static MessageRecorder recorder;

	protected static MessageRecorder getRecorder() {
		if (recorder == null) {
			synchronized (Logger.class) {
				if (recorder == null) {
					// 没有实例，则使用默认
					String logFolder = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_LOG_FILE_FOLDER);
					if (logFolder == null || logFolder.isEmpty()) {
						logFolder = MyConfiguration.getLogFolder();
					}
					MessageRecorder4File messageRecorder4File = new MessageRecorder4File();
					messageRecorder4File.setFileSign("ibas_runtime_%s.log");
					messageRecorder4File.setWorkFolder(logFolder);
					messageRecorder4File.setLimitSize(
							MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_LOG_FILE_SIZE_LIMIT, 50));
					recorder = messageRecorder4File;
				}
			}
		}
		return recorder;
	}

	/**
	 * 日记记录主要方法
	 * 
	 * @param message 传递过来的消息
	 */
	public static void log(Message message) {
		if (message == null) {
			return;
		}
		if (message.getLevel().ordinal() <= getLoggingLevel()) {
			getRecorder().record(message);
		}
	}

	/**
	 * 记录消息，带格式参数（message %s.）
	 * 
	 * @param message 消息内容及格式
	 * @param args    格式中的参数
	 */
	public static void log(String message, Object... args) {
		log(MessageLevel.INFO, message, args);
	}

	/**
	 * 记录消息，带格式参数（message %s.）
	 * 
	 * @param level   消息级别
	 * @param message 消息内容及格式
	 * @param args    格式中的参数
	 */
	public static void log(MessageLevel level, String message, Object... args) {
		log(new Message(level, Strings.format(message, args)));
	}

	/**
	 * 记录消息
	 * 
	 * @param exception 异常
	 */
	public static void log(Exception exception) {
		log(MessageLevel.ERROR, exception);
	}

	/**
	 * 记录消息
	 * 
	 * @param level     消息级别
	 * @param exception 异常
	 */
	public static void log(MessageLevel level, Exception exception) {
		if (exception == null) {
			return;
		}
		try (StringWriter stringWriter = new StringWriter()) {
			try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
				exception.printStackTrace(printWriter);
				log(level, stringWriter.toString());
			}
		} catch (IOException e) {
			System.err.println(e);
		}
	}

}
