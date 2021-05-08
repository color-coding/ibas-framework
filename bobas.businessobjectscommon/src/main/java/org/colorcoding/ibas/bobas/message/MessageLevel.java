package org.colorcoding.ibas.bobas.message;

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
	DEBUG;

	public static MessageLevel valueOf(int value) {
		return values()[value];
	}

	public static MessageLevel valueOf(String value, boolean ignoreCase) {
		if (ignoreCase) {
			for (Object item : MessageLevel.class.getEnumConstants()) {
				if (item.toString().equalsIgnoreCase(value)) {
					return (MessageLevel) item;
				}
			}
		}
		return MessageLevel.valueOf(value);
	}
}
