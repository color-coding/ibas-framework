package org.colorcoding.ibas.bobas.message;

import java.io.IOException;
import java.io.Writer;

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

	/**
	 * 输出格式化内容
	 * 
	 * @param writer
	 * @throws IOException
	 */
	void outString(Writer writer) throws IOException;
}
