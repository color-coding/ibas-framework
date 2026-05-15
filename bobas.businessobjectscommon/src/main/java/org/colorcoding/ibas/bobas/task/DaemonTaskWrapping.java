package org.colorcoding.ibas.bobas.task;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;

/**
 * 后台任务收纳盒
 *
 * @author Niuren.Zhu
 *
 */
class DaemonTaskWrapping {

	public DaemonTaskWrapping(IDaemonTask task) throws InvalidDaemonTaskException {
		this();
		if (task == null) {
			throw new InvalidDaemonTaskException();
		}
		this.setTask(task);
		// 设置首次运行时间
		this.setNextRunTime(System.currentTimeMillis() + (task.getInterval() * 1000l));
	}

	public DaemonTaskWrapping() {
		this.setRunTimes(0);
		this.setRunning(false);
	}

	private long id;

	public long getId() {
		return id;
	}

	void setId(long id) {
		this.id = id;
	}

	public String getName() {
		if (this.getTask() == null || this.getTask().getName() == null || this.getTask().getName().isEmpty()) {
			return "unknown";
		}
		return this.getTask().getName();
	}

	public boolean isActivated() {
		if (this.getTask() == null)
			return false;
		return this.getTask().isActivated();
	}

	private volatile boolean running;

	/**
	 * 运行中
	 *
	 * @return
	 */
	public boolean isRunning() {
		return running;
	}

	void setRunning(boolean running) {
		this.running = running;
	}

	private long runTimes;

	/**
	 * 已运行次数
	 */
	public long getRunTimes() {
		return runTimes;
	}

	private void setRunTimes(long runTimes) {
		this.runTimes = runTimes;
	}

	private void addRunTimes() {
		this.setRunTimes(this.getRunTimes() + 1l);
	}

	private long lastRunTime;

	/**
	 * 上次运行时间
	 */
	public long getLastRunTime() {
		return lastRunTime;
	}

	private void setLastRunTime(long lastRunTime) {
		this.lastRunTime = lastRunTime;
	}

	private void setLastRunTime() {
		this.setLastRunTime(System.currentTimeMillis());
	}

	private long nextRunTime;

	public long getNextRunTime() {
		return nextRunTime;
	}

	private void setNextRunTime(long nextRunTime) {
		this.nextRunTime = nextRunTime;
	}

	private void setNextRunTime() {
		if (this.getTask() != null) {
			if (this.getTask().getInterval() == 0l && this.getLastRunTime() > 0l) {
				// 间隔0秒，执行过则不在执行
				this.setNextRunTime(0l);
			} else {
				this.setNextRunTime(this.getLastRunTime() + (this.getTask().getInterval() * 1000l));
			}
		} else {
			this.setNextRunTime(0l);
		}
	}

	private boolean log;

	/**
	 * 是否记录日志
	 *
	 * @return
	 */
	public final boolean isLog() {
		return log;
	}

	public final void setLog(boolean isLog) {
		this.log = isLog;
	}

	private IDaemonTask task;

	/**
	 * 任务
	 *
	 * @return
	 */
	public IDaemonTask getTask() {
		return task;
	}

	protected void setTask(IDaemonTask task) {
		this.task = task;
	}

	/**
	 * 尝试运行
	 *
	 * @return true，可以运行；false，不能运行。
	 */
	public boolean tryRun() {
		return this.tryRun(System.currentTimeMillis());
	}

	/**
	 * 当前持有的单任务锁
	 */
	private volatile SingleTaskLock currentLock;

	/**
	 * 尝试运行
	 *
	 * @param time 当前系统时间
	 * @return true，可以运行；false，不能运行。
	 */
	public boolean tryRun(long time) {
		if (this.getTask() == null) {
			return false;
		}
		if (time < this.getNextRunTime()) {
			return false;
		}
		if (this.getTask() instanceof ISingleDaemonTask) {
			ISingleDaemonTask singleTask = (ISingleDaemonTask) this.getTask();
			File lockFile = new File(new File(MyConfiguration.getTempFolder()),
					"~ibas_" + singleTask.getLockSignature() + ".lock");
			SingleTaskLock lock = new SingleTaskLock(lockFile, singleTask.getKeepTime());
			if (!lock.tryAcquire()) {
				return false;
			}
			this.currentLock = lock;
		}
		return true;
	}

	/**
	 * 运行任务
	 */
	public void run() {
		if (this.getTask() == null) {
			return;
		}
		Thread.currentThread().setName(String.format("ibas-task|%s", this.getName()));
		this.addRunTimes();
		try {
			this.getTask().run();
		} catch (Exception e) {
			Logger.log(e);
		} finally {
			this.setLastRunTime();
			this.setNextRunTime();
			this.setRunning(false);
			if (this.currentLock != null) {
				this.currentLock.release();
				this.currentLock = null;
			}
			Thread.currentThread().setName("ibas-task|sleeping");
		}
	}

	@Override
	public String toString() {
		return String.format("{task: %s}", this.getName());
	}

	/**
	 * 单任务文件锁
	 *
	 * 基于 FileLock + 时间戳双重机制实现跨进程互斥：
	 *
	 * <pre>
	 * 正常运行：
	 *   进程A获取锁(FileLock+写时间戳) → 运行任务 → 清空时间戳+释放FileLock → 删除锁文件
	 *   进程B尝试获取 → tryLock()返回null → 等待
	 *
	 * 崩溃恢复：
	 *   进程A获取锁后崩溃 → OS释放FileLock → 锁文件保留(含时间戳)
	 *   进程B获取锁 → 读取时间戳 → keepTime未过期则等待 → keepTime过期后接管
	 * </pre>
	 *
	 * 锁文件内容：8字节long，存储锁获取时刻的毫秒时间戳。
	 */
	static class SingleTaskLock {

		private final File lockFile;
		private final long keepTimeMs;
		private RandomAccessFile raf;
		private FileChannel channel;
		private FileLock fileLock;

		SingleTaskLock(File lockFile, long keepTimeSeconds) {
			this.lockFile = lockFile;
			this.keepTimeMs = keepTimeSeconds * 1000L;
		}

		/**
		 * 尝试获取锁
		 *
		 * @return true获取成功，false获取失败
		 */
		boolean tryAcquire() {
			File parent = lockFile.getParentFile();
			if (parent != null && !parent.exists() && !parent.mkdirs()) {
				Logger.log(MessageLevel.ERROR, "daemon: lock folder not exists [%s].", parent);
				return false;
			}
			if (parent != null && !parent.isDirectory()) {
				Logger.log(MessageLevel.ERROR, "daemon: lock folder not a directory [%s].", parent);
				return false;
			}
			try {
				// 以读写方式打开，不截断已有内容，保留崩溃前的时间戳
				raf = new RandomAccessFile(lockFile, "rw");
				channel = raf.getChannel();

				// 尝试获取独占文件锁
				fileLock = channel.tryLock();
				if (fileLock == null) {
					// 其他进程持有文件锁，正在执行任务
					close();
					return false;
				}

				// 读取上一次锁的时间戳（崩溃恢复场景）
				long lastTimestamp = 0;
				try {
					if (raf.length() >= 8) {
						lastTimestamp = raf.readLong();
					}
				} catch (IOException e) {
					// 文件内容损坏，视为无效时间戳
				}

				long now = System.currentTimeMillis();
				if (lastTimestamp > 0 && now < lastTimestamp + keepTimeMs) {
					// 前一个持有者崩溃，但keepTime未过期，需等待
					close();
					return false;
				}

				// 写入当前时间戳并强制刷盘，确保崩溃后时间戳可读
				raf.seek(0);
				raf.writeLong(now);
				raf.setLength(8);
				try {
					raf.getFD().sync();
				} catch (IOException e) {
					// sync失败不影响获取锁，仅降低崩溃恢复的可靠性
				}

				// FileLock持续持有直到release()，防止其他进程并发获取
				return true;

			} catch (OverlappingFileLockException e) {
				// 同一JVM内其他线程持有此文件锁
				close();
				return false;
			} catch (IOException e) {
				Logger.log(e);
				close();
				return false;
			}
		}

		/**
		 * 释放锁
		 */
		void release() {
			// 在持锁状态下清空时间戳，防止释放后其他进程读到残留时间戳误判
			try {
				raf.seek(0);
				raf.writeLong(0L);
				raf.setLength(8);
			} catch (Exception e) {
				// 忽略，后续close会释放资源
			}
			close();
			// 删除锁文件（已无FileLock，删除是安全的）
			lockFile.delete();
		}

		private void close() {
			if (fileLock != null) {
				try {
					fileLock.release();
				} catch (IOException e) {
					// ignore
				}
				fileLock = null;
			}
			if (channel != null) {
				try {
					channel.close();
				} catch (IOException e) {
					// ignore
				}
				channel = null;
			}
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
					// ignore
				}
				raf = null;
			}
		}
	}
}
