package org.colorcoding.ibas.bobas.message;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
		if (MyConfiguration.isDebugMode()) {
			// debug模式输出到控制台
			this.setPrint(true);
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

	@Override
	public String getWorkFolder() {
		if (this.workFolder == null || this.workFolder.isEmpty()) {
			this.workFolder = MyConfiguration.getLogFolder();
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
		return this.fileSign;
	}

	@Override
	public void setFileSign(String value) {

		this.fileSign = value;
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
							writeFile();
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
							if (messageQueue == null)
								return false;
							if (messageQueue.isEmpty())
								return false;
							return true;
						}
					}, false);
				} catch (Exception e) {
					Logger.log(MessageLevel.FATAL, e);
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
		String partName = DateTime.getToday().toString("yyyyMMdd");
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
	public void record(IMessage message) {
		if (this.isPrint()) {
			// 调用基类，消息记录到控制台
			super.record(message);
		}
		// 消息记录到其他
		this.getMessageQueue().offer(message);
	}

	/**
	 * 输出消息到文件
	 */
	public void writeFile() {
		if (this.getMessageQueue().size() <= 0) {
			return;
		}
		FileWriter fileWriter = null;
		try {
			File file = new File(this.getFileName());
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			fileWriter = new FileWriter(file, true);
			while (!this.getMessageQueue().isEmpty()) {
				IMessage message = this.getMessageQueue().poll();
				if (message != null) {
					message.outString(fileWriter);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			// 日志文件创建失败，日志输出到控制台
			while (!this.getMessageQueue().isEmpty()) {
				IMessage message = this.getMessageQueue().poll();
				if (message != null) {
					System.err.println(message.toString());
				}
			}
		} finally {
			try {
				if (fileWriter != null) {
					fileWriter.close();
				}
				fileWriter = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
