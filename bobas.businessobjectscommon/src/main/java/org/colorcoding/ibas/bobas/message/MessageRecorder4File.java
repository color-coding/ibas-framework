package org.colorcoding.ibas.bobas.message;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Files;
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
		this(DEFAULT_FILE_SIGN);
	}

	public MessageRecorder4File(String sign) {
		this.setFileSign(sign);
		if (MyConfiguration.isDebugMode()) {
			this.setPrint(!MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_DISABLE_CONSOLE_MESSAGES, false));
		}
	}

	public static final String DEFAULT_FILE_SIGN = "ibas_log_%s.log";

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
	 * @return 文件大小上限（MB）；0表示不限制
	 */
	public int getLimitSize() {
		return this.limitSize;
	}

	/**
	 * 设置-文件大小限制（兆）
	 *
	 * @param limitSize 文件大小上限（MB）
	 */
	public void setLimitSize(int limitSize) {
		this.limitSize = limitSize;
		this.cachedFileName = null;
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
		this.cachedFileName = null;
	}

	private String fileSign;

	public String getFileSign() {
		return this.fileSign;
	}

	public void setFileSign(String value) {
		this.fileSign = value;
		this.cachedFileName = null;
	}

	private final AtomicLong taskId = new AtomicLong(-1);
	private volatile Queue<Message> messageQueue;

	/**
	 * 消息队列（懒初始化，首次访问时启动异步写入任务）
	 *
	 * @return 消息队列
	 */
	protected Queue<Message> getMessageQueue() {
		if (this.messageQueue == null) {
			synchronized (this) {
				if (this.messageQueue == null) {
					this.messageQueue = new ConcurrentLinkedQueue<>();
				}
				if (this.taskId.get() < 0) {
					this.taskId.set(Daemon.register(new IDaemonTask() {
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
							Queue<Message> q = MessageRecorder4File.this.messageQueue;
							return q != null && !q.isEmpty();
						}

						@Override
						public void close() {
							MessageRecorder4File.this.flush();
						}
					}, false));
				}
			}
		}
		return this.messageQueue;
	}

	private volatile String cachedFileName;
	private volatile String cachedFileDate;

	/**
	 * 获取记录文件名称（按日期和序号分文件，带缓存）
	 *
	 * @return 日志文件路径
	 * @throws IOException 文件操作失败
	 */
	public String getFileName() throws IOException {
		String today = DateTimes.today().toString("yyyyMMdd");
		// 日期变化时清除缓存
		if (this.cachedFileName != null && !today.equals(this.cachedFileDate)) {
			this.cachedFileName = null;
		}
		if (this.cachedFileName != null) {
			// 检查缓存文件是否超限
			if (this.getLimitSize() > 0) {
				File file = new File(this.cachedFileName);
				if (file.exists() && file.length() / 1024 / 1024 >= this.getLimitSize()) {
					this.cachedFileName = null;
				}
			}
			if (this.cachedFileName != null) {
				return this.cachedFileName;
			}
		}
		String filePath = this.getWorkFolder();
		String result = null;
		if (this.getLimitSize() <= 0) {
			result = Files.valueOf(filePath, String.format(this.getFileSign(), today)).getPath();
		} else {
			File file;
			int maxCount = 999;
			for (int i = 1; i < maxCount; i++) {
				file = Files.valueOf(filePath, String.format(this.getFileSign(), String.format("%s_%03d", today, i)));
				if (!file.exists()) {
					result = file.getPath();
					break;
				}
				if (file.isFile() && file.length() / 1024 / 1024 < this.getLimitSize()) {
					result = file.getPath();
					break;
				}
				if (i == maxCount - 1) {
					result = Files.valueOf(filePath, String.format(this.getFileSign(), today)).getPath();
				}
			}
			// safety: if loop didn't set result (shouldn't happen with maxCount=999)
			if (result == null) {
				result = Files.valueOf(filePath, String.format(this.getFileSign(), today)).getPath();
			}
		}
		this.cachedFileName = result;
		this.cachedFileDate = today;
		return result;
	}

	/**
	 * 记录消息
	 */
	@Override
	public void record(Message message) {
		if (this.isPrint()) {
			super.record(message);
		}
		this.getMessageQueue().offer(message);
		// 未注册异步任务时同步写入
		if (this.taskId.get() <= 0) {
			this.writeToFile();
		}
	}

	/**
	 * 输出消息到文件
	 */
	public synchronized void writeToFile() {
		Queue<Message> queue = this.messageQueue;
		if (queue == null || queue.isEmpty()) {
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
			Message message;
			try (FileWriter fileWriter = new FileWriter(file, true)) {
				while (!queue.isEmpty()) {
					message = queue.poll();
					if (message == null) {
						continue;
					}
					message.outString(fileWriter);
				}
			} catch (IOException e) {
				System.err.println(e);
				// 日志文件写入失败，剩余消息输出到控制台
				Message msg;
				while ((msg = queue.poll()) != null) {
					if (msg.getLevel() == MessageLevel.ERROR || msg.getLevel() == MessageLevel.FATAL) {
						msg.outString(System.err);
					} else {
						msg.outString(System.out);
					}
				}
			}
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	/**
	 * 刷盘：将队列中剩余消息全部写入文件
	 */
	public void flush() {
		this.writeToFile();
	}
}
