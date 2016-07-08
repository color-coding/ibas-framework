package org.colorcoding.ibas.bobas.messages;

import org.colorcoding.ibas.bobas.data.DateTime;

/**
 * 系统消息
 * 
 * @author Niuren.Zhu
 *
 */
public interface IMessage {
	/**
	 * 时间
	 * 
	 * @return
	 */
	DateTime getTime();

	/**
	 * 级别
	 * 
	 * @return
	 */
	MessageLevel getLevel();

	/**
	 * 内容
	 * 
	 * @return
	 */
	String getContent();

	/**
	 * 标签
	 * 
	 * @return
	 */
	String getTag();

	/**
	 * 输出格式化内容
	 * 
	 * @return
	 */
	String outString();
}
