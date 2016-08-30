package org.colorcoding.ibas.bobas.messages;

/**
 * 消息类型（注意值要求大写）
 * 
 * @author Niuren.Zhu
 *
 */
public enum MessageLevel {
	/**
	 * 严重错误
	 */
	FATAL,
	/**
	 * 错误
	 */
	ERROR,
	/**
	 * 警告
	 */
	WARN,
	/**
	 * 消息
	 */
	INFO,
	/**
	 * 调试信息
	 */
	DEBUG,
}
