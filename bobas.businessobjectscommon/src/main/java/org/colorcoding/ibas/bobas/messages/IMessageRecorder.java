package org.colorcoding.ibas.bobas.messages;

/**
 * 消息记录者
 * 
 * @author Eric.peng
 *
 */
public interface IMessageRecorder {

	/**
	 * 记录
	 * 
	 * @param message
	 *            消息
	 */
	void record(IMessage message);

}
