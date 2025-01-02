package org.colorcoding.ibas.bobas.logging;

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

	public static void log(Exception e) {
		e.printStackTrace();
	}

	/**
	 * 记录消息，带格式参数（message %s.）
	 * 
	 * @param message 消息内容及格式
	 * @param args    格式中的参数
	 */
	public static void log(String message, Object... args) {
		log(LoggingLevel.INFO, message, args);
	}

	public static void log(LoggingLevel level, String message, Object... args) {
		if (level.ordinal() >= LoggingLevel.ERROR.ordinal()) {
			System.err.println(Strings.format(message, args));
		} else {
			System.out.println(Strings.format(message, args));
		}
	}

}
