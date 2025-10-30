package org.colorcoding.ibas.bobas.message;

/**
 * 消息记录者，到控制台
 * 
 * @author Niuren.Zhu
 *
 */
public class MessageRecorder {

	/**
	 * 记录消息
	 * 
	 * 此方法记录消息到控制台，建议子类保留调用
	 * 
	 * @param message 消息
	 */
	public void record(Message message) {
		if (message == null) {
			return;
		}
		if (message.getLevel() == MessageLevel.ERROR) {
			System.err.println(message.outString());
		} else {
			System.out.println(message.outString());
		}
	}

}
