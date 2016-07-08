package org.colorcoding.ibas.bobas.messages;

/**
 * 系统消息输出
 * 
 * @author Eric.peng
 *
 */
public interface IMessageRecord {
	/**
	 * 设置 消息输出路径
	 * 
	 * @param filePath
	 *            输出路径
	 * @return
	 */
	void setFilePath(String filePath);

	/**
	 * 将消息写入文件中
	 */
	void writeRecord();

	/**
	 * 添加消息到消息队列
	 * 
	 * @param message
	 *            消息
	 */
	void addMessage(IMessage message);

}
