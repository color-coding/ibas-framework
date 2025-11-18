package org.colorcoding.ibas.bobas.message;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.task.Daemon;
import org.colorcoding.ibas.bobas.task.IDaemonTask;

/**
 * 消息记录者，到文件
 * 
 * @author Niuren.Zhu
 *
 */
public class MessageRecorder4File extends MessageRecorder {

	public MessageRecorder4File() {
		if (MyConfiguration.isDebugMode()) {
			// debug模式输出到控制台
			this.setPrint(!MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_DISABLE_CONSOLE_MESSAGES, false));
		}
	}

	public MessageRecorder4File(String sign) {
		this();
		this.setFileSign(sign);
	}

	private boolean print;

	public final boolean isPrint() {
		return print;
	}

	public final void setPrint(boolean print) {
		this.print = print;
	}

	private int limitSize;

	/**
	 * 获取-文件大小限制（兆）
	 * 
	 * @return
	 */
	public int getLimitSize() {
		return this.limitSize;
	}

	/**
	 * 设置-文件大小限制（兆）
	 * 
	 * @return
	 */
	public void setLimitSize(int limitSize) {
		this.limitSize = limitSize;
	}

	/**
	 * 获取日志文件路径
	 */
	private String workFolder;

	public String getWorkFolder() {
		if (this.workFolder == null || this.workFolder.isEmpty()) {
			this.workFolder = MyConfiguration.getLogFolder();
		}
		return this.workFolder;
	}

	public void setWorkFolder(String value) {
		this.workFolder = value;
	}

	private String fileSign;

	public String getFileSign() {
		return this.fileSign;
	}

	public void setFileSign(String value) {

		this.fileSign = value;
	}

	private volatile long taskId = -1;
	private volatile Queue<Message> messageQueue;

	/**
	 * 消息队列
	 * 
	 * @return
	 */
	protected Queue<Message> getMessageQueue() {
		if (this.messageQueue == null) {
			synchronized (this) {
				if (this.messageQueue == null) {
					this.messageQueue = new ConcurrentLinkedQueue<Message>();// 非阻塞队列，支持异步
				}
				// 开启异步输出
				if (this.taskId < 0) {
					// 注册日志输出文件任务
					this.taskId = Daemon.register(new IDaemonTask() {
						@Override
						public void run() {
							MessageRecorder4File.this.writeToFile();
						}

						@Override
						public String getName() {
							return "write message to file";
						}

						@Override
						public long getInterval() {
							return 1;
						}

						@Override
						public boolean isActivated() {
							if (MessageRecorder4File.this.messageQueue == null)
								return false;
							if (MessageRecorder4File.this.messageQueue.isEmpty())
								return false;
							return true;
						}

					}, false);
				}
			}
		}
		return this.messageQueue;
	}

	/**
	 * 获取记录文件名称
	 * 
	 * @throws IOException
	 */
	public String getFileName() throws IOException {
		String filePath = this.getWorkFolder() + File.separator;
		String partName = DateTimes.today().toString("yyyyMMdd");
		// 分文件
		int maxCount = 999;
		if (this.getLimitSize() <= 0) {
			// 错误的文件大小
			maxCount = 0;
		}
		for (int i = 1; i < maxCount; i++) {
			// 查找未写满的文件
			File file = new File(filePath + String.format(this.getFileSign(), String.format("%s_%03d", partName, i)));
			if (!file.exists()) {
				return file.getPath();
			}
			if (file.isFile()) {
				long size = file.length() / 1024 / 1024;
				if (size < this.getLimitSize()) {
					return file.getPath();
				}
			}
		}
		// 不分文件
		return filePath + String.format(this.getFileSign(), partName);
	}

	/**
	 * 将日志消息写入到文本中
	 */
	@Override
	public void record(Message message) {
		if (this.isPrint()) {
			// 调用基类，消息记录到控制台
			super.record(message);
		}
		// 消息记录到其他
		this.getMessageQueue().offer(message);
		// 异步or同步
		if (!(this.taskId > 0)) {
			this.writeToFile();
		}
	}

	/**
	 * 输出消息到文件
	 */
	public void writeToFile() {
		if (this.getMessageQueue().size() <= 0) {
			return;
		}
		try {
			File file = new File(this.getFileName());
			if (!file.exists()) {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				file.createNewFile();
			}
			Message message = null;
			try (FileWriter fileWriter = new FileWriter(file, true)) {
				while (!this.getMessageQueue().isEmpty()) {
					message = this.getMessageQueue().poll();
					if (message == null) {
						continue;
					}
					message.outString(fileWriter);
				}
			} catch (IOException e) {
				System.err.println(e);
				// 日志文件创建失败，日志输出到控制台
				do {
					if (message == null) {
						continue;
					}
					if (message.getLevel() == MessageLevel.ERROR || message.getLevel() == MessageLevel.FATAL) {
						message.outString(System.err);
					} else {
						message.outString(System.out);
					}
					message = this.getMessageQueue().poll();
				} while (!this.getMessageQueue().isEmpty());
			}
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}
