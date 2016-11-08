package org.colorcoding.ibas.bobas.core;

import org.colorcoding.ibas.bobas.messages.RuntimeLog;

/**
 * 后台任务收纳盒
 * 
 * @author Niuren.Zhu
 *
 */
class DaemonTaskWrapping {

	public DaemonTaskWrapping(IDaemonTask task) throws InvalidDaemonTask {
		this();
		if (task == null) {
			throw new InvalidDaemonTask();
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

	private boolean running;

	/**
	 * 运行中
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return running;
	}

	private void setRunning(boolean running) {
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
	 * 尝试运行
	 * 
	 * @param time
	 *            当前系统时间
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

		return true;
	}

	/**
	 * 运行任务
	 */
	public void run() {
		if (this.getTask() != null) {
			Thread.currentThread().setName(String.format("ibas-task|%s", this.getName()));
			this.setRunning(true);// 设置状态为运行中
			this.addRunTimes();// 运行次数+1
			try {
				this.getTask().run();
			} catch (Exception e) {
				RuntimeLog.log(e);
			}
			this.setLastRunTime();// 记录运行时间
			this.setNextRunTime();// 设置下次运行时间
			this.setRunning(false);// 设置状态未运行
			Thread.currentThread().setName("ibas-task|sleeping");
		}
	}
}
