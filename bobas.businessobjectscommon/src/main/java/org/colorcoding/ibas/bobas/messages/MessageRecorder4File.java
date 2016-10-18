package org.colorcoding.ibas.bobas.messages;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.core.Daemon;
import org.colorcoding.ibas.bobas.core.IDaemonTask;
import org.colorcoding.ibas.bobas.data.DateTime;

/**
 * 消息记录者，到文件
 * 
 * @author Niuren.Zhu
 *
 */
public class MessageRecorder4File extends MessageRecorder implements IMessageRecorder4File {

	public MessageRecorder4File() {

	}

	public MessageRecorder4File(String sign) {
		this.setFileSign(sign);
	}

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
				// 注册日志输出文件任务
				try {
					Daemon.register(new IDaemonTask() {

						@Override
						public void run() {
							write4File();
						}

						@Override
						public String getName() {
							return "write message to file.";
						}

						@Override
						public long getInterval() {
							return 1;
						}
					});
				} catch (Exception e) {
					RuntimeLog.log(MessageLevel.FATAL, e);
				}
			}
		}
		return this.messageQueue;
	}

	/**
	 * 获取日志文件路径
	 */
	private String workFolder;

	@Override
	public String getWorkFolder() {
		if (this.workFolder == null || this.workFolder.isEmpty()) {
			String folder = MyConfiguration.getWorkFolder();// 尾部不带文件路径分隔
			this.setWorkFolder(String.format("%s%s%s", folder, File.separator, "log"));
			File file = new File(this.workFolder);
			if (!file.exists()) {
				file.mkdirs();
			}
		}
		return this.workFolder;
	}

	@Override
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
		this.getMessageQueue().offer(message);
	}

	/**
	 * 输出消息到文件
	 */
	public void write4File() {
		if (this.getMessageQueue().size() <= 0) {
			return;
		}
		FileHandler fileHandler = null;
		try {
			// 限定当天日志文件存储不超过2G
			fileHandler = new FileHandler(this.getFileName(), 20480000, true);
			while (!this.getMessageQueue().isEmpty()) {
				IMessage message = this.getMessageQueue().poll();
				fileHandler.execute(message.outString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
