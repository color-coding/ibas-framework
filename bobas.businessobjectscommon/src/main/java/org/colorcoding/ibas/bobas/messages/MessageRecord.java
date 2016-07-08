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
 * 运行日志
 * 
 * @author Niuren.Zhu
 *
 */
public class MessageRecord implements IMessageRecord {

	/**
	 * 单例模式---始终用一个队列 记录日志
	 */
	private static volatile MessageRecord instance;

	public static MessageRecord create() {
		if (!enableLog())
			return null;
		if (instance == null) {
			synchronized (MessageRecord.class) {
				if (instance == null) {
					instance = new MessageRecord();
				}
			}
		}
		return instance;
	}

	/**
	 * 写日志线程
	 */
	private Thread writeThread;

	private void initializeThread() {
		if (enableLog()) {
			if (writeThread == null) {
				writeThread = new Thread() {
					@Override
					@SuppressWarnings("static-access")
					public void run() {
						try {
							create().writeRecord();
							Thread.currentThread().sleep(logOutFrequency());
							run();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				};
				writeThread.start();
			}
		} else {
			if (writeThread != null) {
				writeThread.interrupt();
			}
		}
	}

	/**
	 * 日志队列
	 */
	private Queue<IMessage> messageQueue;

	private Queue<IMessage> getMessageQueue() {
		if (this.messageQueue == null) {
			messageQueue = new ConcurrentLinkedQueue<IMessage>();// 非阻塞队列，支持异步
		}
		return this.messageQueue;
	}

	/**
	 * 获取日志文件路径
	 */
	private String logFilePath;

	private String getLogFilePath() {
		if (logFilePath == null || logFilePath.equals("")) {
			logFilePath = "";
			// 将日志文件记录到启动路径的相对位置。
			String startPath = MyConfiguration.getWorkFolder();// 尾部不带文件路径分隔
			logFilePath = String.format("%s%s%s%s", startPath, File.separator, "log", File.separator);
			File file_classPath = new File(logFilePath);
			if (!file_classPath.exists()) {
				file_classPath.mkdirs();
			}
			logFilePath = file_classPath.getPath();
		}
		return logFilePath;
	}

	@Override
	public void setFilePath(String filePath) {
		if (filePath == null || filePath.equals("")) {
			return;
		}
		this.logFilePath = filePath;
	}

	protected MessageRecord() {

	}

	/**
	 * 是否启用日志记录
	 * 
	 * @return true,false
	 */
	protected static boolean enableLog() {
		return MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_DEBUG_MODE, true);
	}

	/**
	 * 从配置文件中获取写日记文件时间频率
	 * 
	 * @return 时间频率
	 */
	protected static long logOutFrequency() {
		return MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_LOG_OUT_FREQUENCY, 5000);
	}

	/**
	 * 记录系统消息，
	 * 
	 * @param message
	 *            消息
	 */
	protected void addMessage(IMessage message) {
		if (message == null) {
			return;
		}
		this.getMessageQueue().offer(message);
		initializeThread();
	}

	/**
	 * 将日志消息写入到文本中
	 */
	@Override
	public void writeRecord() {
		if (this.getMessageQueue().isEmpty()) {
			return;
		}
		FileHandler fileHandler = null;
		try {
			String filePath = this.getLogFilePath();
			String fileName = String.format("ibas_%s.log", DateTime.getNow().toString("yyyyMMdd"));
			String pattern = String.format("%s%s%s", filePath, File.separator, fileName);
			fileHandler = new FileHandler(pattern, true);
			Logger log = Logger.getLogger("IBAS");
			log.setUseParentHandlers(false);
			fileHandler.setFormatter(new Formatter() {
				@Override
				public String format(LogRecord record) {
					return record.getMessage() + System.getProperty("line.separator");
				}
			});
			log.addHandler(fileHandler);
			while (!this.getMessageQueue().isEmpty()) {
				IMessage message = this.getMessageQueue().poll();
				log.info(message.outString());
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
