package org.colorcoding.ibas.bobas.core;

import java.io.File;
import java.io.FileWriter;

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
	protected static final String MSG_DAEMON_SINGLE_TASK_WORK_FOLDER_NOT_EXISTS = "daemon: single task work folder not exists.";

	public DaemonTaskWrapping(IDaemonTask task) throws InvalidDaemonTaskException {
		this();
		if (task == null) {
			throw new InvalidDaemonTaskException();
		}
		this.setTask(task);
		// 设置首次运行时间
		this.setNextRunTime(System.currentTimeMillis() + task.getInterval() * 1000);
	}

	public DaemonTaskWrapping() {
		this.setRunTimes(0);
		this.setRunning(false);
	}

	private long id;

	/**
	 * id
	 * 
	 * @return
	 */
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
		this.setRunTimes(this.getRunTimes() + 1);
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
			this.setNextRunTime(this.getLastRunTime() + this.getTask().getInterval() * 1000);
		} else {
			this.setNextRunTime(0);
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

	private String getLockFileName(String folder, ISingleDaemonTask task) {
		return folder + File.separator + "~ibas_" + task.getLockSignature() + ".lock";
	}

	/**
	 * 尝试运行
	 * 
	 * @param time 当前系统时间
	 * @return true，可以运行；false，不能运行。
	 */
	public boolean tryRun(long time) {
		if (this.getTask() == null) {
			// 无效的任务
			return false;
		}
		if (time < this.getNextRunTime()) {
			// 未到间隔周期
			return false;
		}
		if (this.getTask() instanceof ISingleDaemonTask) {
			ISingleDaemonTask singleTask = (ISingleDaemonTask) this.getTask();
			try {
				File folder = new File(MyConfiguration.getTempFolder());
				if (!folder.exists()) {
					folder.mkdirs();
				}
				if (!folder.isDirectory()) {
					Logger.log(MessageLevel.ERROR, MSG_DAEMON_SINGLE_TASK_WORK_FOLDER_NOT_EXISTS);
					return false;
				}
				File lockFile = new File(this.getLockFileName(folder.getPath(), singleTask));
				if (lockFile.exists()) {
					// 存在锁文件
					long fileTime = lockFile.lastModified();
					if (System.currentTimeMillis() < (fileTime + singleTask.getKeepTime() * 1000)) {
						// 处于锁的时间
						return false;
					}
					// 超过锁时间，删除此文件
					lockFile.delete();
				}
				try (FileWriter fw = new FileWriter(lockFile)) {
					fw.write(String.format("ibas lock file, create by %s.", singleTask.hashCode()));
					fw.flush();
				}
			} catch (Exception e) {
				// 创建锁文件失败，任务不运行
				Logger.log(e);
				return false;
			}
		}
		return true;
	}

	/**
	 * 运行任务
	 */
	public void run() {
		if (this.getTask() != null) {
			Thread.currentThread().setName(String.format("ibas-task|%s", this.getName()));
			this.addRunTimes();// 运行次数+1
			try {
				this.getTask().run();
			} catch (Exception e) {
				Logger.log(e);
			}
			this.setLastRunTime();// 记录运行时间
			this.setNextRunTime();// 设置下次运行时间
			this.setRunning(false);// 设置状态未运行
			if (this.getTask() instanceof ISingleDaemonTask) {
				// 单任务结束时，删除锁文件
				ISingleDaemonTask singleTask = (ISingleDaemonTask) this.getTask();
				File folder = new File(MyConfiguration.getTempFolder());
				File lockFile = new File(this.getLockFileName(folder.getPath(), singleTask));
				if (lockFile.exists()) {
					lockFile.delete();
				}
			}
			Thread.currentThread().setName("ibas-task|sleeping");
		}
	}

	@Override
	public String toString() {
		return String.format("{Task: %s}", this.getName());
	}
}
