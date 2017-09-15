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
public class Logger {

	private static int MESSAGE_LEVEL = -1;

	/**
	 * 是否处于debug模式
	 * 
	 * @return
	 */
	protected static int getMessageLevel() {
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

	private volatile static IMessageRecorder recorder;

	protected static IMessageRecorder getRecorder() {
		if (recorder == null) {
			synchronized (Logger.class) {
				if (recorder == null) {
					recorder = RecorderFactory.create().createRecorder();
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
		if (message.getLevel().ordinal() <= getMessageLevel()) {
			getRecorder().record(message);
		}
	}

	/**
	 * 记录消息
	 * 
	 * @param message
	 *            消息内容
	 */
	public static void log(String message) {
		log(MessageLevel.INFO, message, "");
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
		log(Message.create(level, message));
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
		log(MessageLevel.INFO, message, args);
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
		log(level, String.format(message, args));
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
		log(MessageLevel.ERROR, e);
	}

	/**
	 * 记录消息
	 * 
	 * @param level
	 *            消息级别
	 * @param exception
	 *            异常
	 */
	public static void log(MessageLevel level, Exception e) {
		if (e == null) {
			return;
		}
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);
		log(level, stringWriter.toString());
	}

}
