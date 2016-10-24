package org.colorcoding.ibas.bobas.messages;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

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

	private int limit;

	public int getLimit() {
		return this.limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	private int count = 1;

	public int getCount() {
		return this.count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	private String moduleName;

	private String getModuleName() {
		return this.moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
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
		if (this.fileSign == null || this.fileSign.indexOf("%s") < 0 || this.fileSign.indexOf("%g") < 0) {
			// 文件标记格式不合法
			this.fileSign = "message_recorder_%s_%g.log";
		}
		return this.fileSign;
	}

	@Override
	public void setFileSign(String value) {

		this.fileSign = value;
	}

	public MessageRecorder4File() {
		this("");
	}

	public MessageRecorder4File(String sign) {
		this.setFileSign(sign);
	}

	private String fileName;

	/**
	 * 获取记录文件名称
	 */
	public String getFileName() {
		if (this.fileName == null || this.fileName.isEmpty()) {
			String str = this.getFileSign().replace("%s", DateTime.getToday().toString("yyyyMMdd"));
			if (this.getModuleName() != null && this.getModuleName().length() > 0) {
				str = str.replace("%m", "_" + this.getModuleName());
			} else {
				str = str.replace("%m", "");
			}
			this.fileName = String.format("%s%s%s", this.getWorkFolder(), File.separator, str);
		}
		return fileName;
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

	String line = System.getProperty("line.separator");

	/**
	 * 输出消息到文件
	 */
	public void write4File() {
		if (this.getMessageQueue().size() <= 0) {
			return;
		}
		FileHandler fileHandler = null;
		try {
			fileHandler = new FileHandler(this.getFileName(), limit, count, true);
			Logger logger = Logger.getLogger("ibas");
			logger.setUseParentHandlers(false);
			fileHandler.setFormatter(new Formatter() {
				public String format(LogRecord record) {
					return record.getMessage() + line;
				}
			});
			logger.addHandler(fileHandler);
			while (!this.getMessageQueue().isEmpty()) {
				IMessage message = this.getMessageQueue().poll();
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
