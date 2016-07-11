package org.colorcoding.ibas.bobas.messages;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.data.DateTime;

/**
 * 消息记录者，到文件
 * 
 * @author Niuren.Zhu
 *
 */
public class MessageRecorder4File extends MessageRecorder implements IMessageRecorder4File {

	private volatile Queue<IMessage> messageQueue;

	/**
	 * 消息队列
	 * 
	 * @return
	 */
	protected Queue<IMessage> getMessageQueue() {
		if (this.messageQueue == null) {
			synchronized (this) {
				if (this.messageQueue == null) {
					messageQueue = new ConcurrentLinkedQueue<IMessage>();// 非阻塞队列，支持异步
				}
			}
		}
		return this.messageQueue;
	}

	/**
	 * 获取日志文件路径
	 */
	private String workFolder;

	public String getWorkFolder() {
		if (this.workFolder == null || this.workFolder.equals("")) {
			String folder = MyConfiguration.getWorkFolder();// 尾部不带文件路径分隔
			this.setWorkFolder(String.format("%s%s%s%s", folder, File.separator, "log", File.separator));
		}
		return this.workFolder;
	}

	public void setWorkFolder(String value) {
		this.workFolder = value;
	}

	private String fileSign;

	@Override
	public String getFileSign() {
		if (fileSign == null || fileSign.indexOf("%s") < 0) {
			// 文件标记格式不合法
			fileSign = "ibas_%s.log";
		}
		return fileSign;
	}

	@Override
	public void setFileSign(String value) {
		this.fileSign = value;
	}

	/**
	 * 获取记录文件名称
	 */
	public String getFileName() {
		// /var/log/ibas_%s.log
		String fileName = String.format(this.getFileSign(), DateTime.getToday().toString("yyyyMMdd"));
		return String.format("%s%s%s", this.getWorkFolder(), File.separator, fileName);
	}

	/**
	 * 将日志消息写入到文本中
	 */
	@Override
	public void record(IMessage message) {
		// 调用基类，消息记录到控制台
		super.record(message);
		// 消息记录到其他
		if (this.getMessageQueue().isEmpty()) {
			return;
		}
		FileHandler fileHandler = null;
		try {
			fileHandler = new FileHandler(this.getFileName(), true);
			Logger logger = Logger.getLogger("ibas");
			logger.setUseParentHandlers(false);
			fileHandler.setFormatter(new Formatter() {
				@Override
				public String format(LogRecord record) {
					return record.getMessage() + System.getProperty("line.separator");
				}
			});
			logger.addHandler(fileHandler);
			while (!this.getMessageQueue().isEmpty()) {
				message = this.getMessageQueue().poll();
				logger.info(message.outString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fileHandler != null) {
				fileHandler.close();
			}
		}
	}

}
