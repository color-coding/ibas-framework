package org.colorcoding.ibas.bobas.core;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.colorcoding.ibas.bobas.messages.MessageLevel;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;

/**
 * 框架守护进程
 * 
 * 用途：处理定时任务
 * 
 * @author Niuren.Zhu
 *
 */
public class Daemon implements IDaemon {
	/**
	 * 注册后台任务
	 * 
	 * @param task
	 *            任务
	 * @return 任务ID，小于0任务注册失败
	 * @throws InvalidDaemonTask
	 */
	public static long register(IDaemonTask task) throws InvalidDaemonTask {
		synchronized (Daemon.class) {
			return create().add(task);
		}
	}

	/**
	 * 移出任务
	 * 
	 * @param id
	 *            注册时分配的id
	 * @return true，成功；false，失败
	 */
	public static boolean unRegister(long id) {
		synchronized (Daemon.class) {
			return create().remove(id);
		}
	}

	private volatile static IDaemon daemon;

	static IDaemon create() {
		if (daemon == null) {
			synchronized (Daemon.class) {
				if (daemon == null) {
					daemon = new Daemon();
					daemon.initialize();
				}
			}
		}
		return daemon;
	}

	private volatile Thread daemonThread;

	@Override
	public synchronized void initialize() {
		if (this.daemonThread == null) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						checkRun();
						try {
							Thread.sleep(500);// 每500毫秒检查次任务
						} catch (Exception e) {
							RuntimeLog.log(e);
						}
					}
				}
			});
			thread.setName("ibas-framework-daemon");
			thread.setPriority(Thread.MAX_PRIORITY);
			thread.setDaemon(true);
			thread.start();
		}
	}

	private ArrayList<DaemonTaskWrapping> wrappings;

	protected ArrayList<DaemonTaskWrapping> getWrappings() {
		if (wrappings == null) {
			wrappings = new ArrayList<>();
		}
		return wrappings;
	}

	@Override
	public long add(IDaemonTask task) throws InvalidDaemonTask {
		if (task != null && task.getName() != null && !task.getName().isEmpty()) {
			synchronized (this.getWrappings()) {
				DaemonTaskWrapping wrapping = new DaemonTaskWrapping(task);
				wrapping.setId(Math.abs(UUID.randomUUID().getLeastSignificantBits()));
				this.getWrappings().add(wrapping);
				RuntimeLog.log(RuntimeLog.MSG_DAEMON_REGISTER_TASK, wrapping.getId(), wrapping.getName());
				return wrapping.getId();
			}
		} else {
			throw new InvalidDaemonTask();
		}
	}

	@Override
	public boolean remove(long taskId) {
		if (taskId < 0) {
			return false;
		}
		synchronized (this.getWrappings()) {
			for (int i = this.getWrappings().size() - 1; i >= 0; i--) {
				DaemonTaskWrapping wrapping = this.getWrappings().get(i);
				if (wrapping == null) {
					continue;
				}
				if (wrapping.getId() == taskId) {
					this.getWrappings().remove(i);
				}
				RuntimeLog.log(RuntimeLog.MSG_DAEMON_REMOVE_TASK, wrapping.getId(), wrapping.getName());
			}
		}
		return false;
	}

	ExecutorService threadPool;

	public ExecutorService getThreadPool() {
		if (this.threadPool == null) {
			int cpu = Runtime.getRuntime().availableProcessors();
			this.threadPool = Executors.newFixedThreadPool(cpu);
		}
		return threadPool;
	}

	@Override
	public void checkRun() {
		long time = System.currentTimeMillis();
		for (DaemonTaskWrapping wrapping : this.getWrappings()) {
			if (wrapping == null) {
				continue;
			}
			if (wrapping.isRunning()) {
				// 同时仅启动一个任务
				continue;
			}
			if (!wrapping.isActivated()) {
				continue;
			}
			boolean done = wrapping.tryRun(time);
			if (done) {
				// 可以运行
				RuntimeLog.log(MessageLevel.DEBUG, RuntimeLog.MSG_DAEMON_RUN_TASK, wrapping.getId(), wrapping.getName(),
						wrapping.getRunTimes());
				// 从线程池中调用新的线程运行此任务
				this.getThreadPool().execute(new Runnable() {

					@Override
					public void run() {
						wrapping.run();
					}
				});
			}
		}
	}
}
