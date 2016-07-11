package org.colorcoding.ibas.bobas.messages;

/**
 * 消息记录者，文件记录
 * 
 * @author Niuren.Zhu
 *
 */
public interface IMessageRecorder4File extends IMessageRecorder {

	/**
	 * 获取工作目录
	 * 
	 * @return
	 */
	String getWorkFolder();

	/**
	 * 设置工作目录
	 * 
	 * @param value
	 */
	void setWorkFolder(String value);

	/**
	 * 获取文件名称标记
	 * 
	 * ibas_runtime_%s.log (%s,20160606)
	 * 
	 * @return
	 */
	String getFileSign();

	/**
	 * 设置文件名称标记
	 * 
	 * @param value
	 *            标记(ibas_runtime_%s.log)
	 */
	void setFileSign(String value);
}
