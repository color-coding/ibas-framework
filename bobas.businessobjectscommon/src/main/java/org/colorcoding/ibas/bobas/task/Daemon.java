package org.colorcoding.ibas.bobas.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;

/**
 * 框架守护进程
 *
 * 用途：处理定时任务
 *
 * @author Niuren.Zhu
 *
 */
public class Daemon {

	/**
	 * 注册后台任务
	 *
	 * @param task 任务
	 * @return 任务ID，小于0任务注册失败
	 * @throws InvalidDaemonTaskException
	 */
	public static long register(IDaemonTask task)  {
		return register(task, true);
	}

	/**
	 * 注册后台任务
	 *
	 * @param task 任务
	 * @param log  是否记录日志
	 * @return 任务ID，小于0任务注册失败
	 * @throws InvalidDaemonTaskException
	 */
	public static long register(IDaemonTask task, boolean log)  {
		synchronized (Daemon.class) {
			return create().add(task, log);
		}
	}

	/**
	 * 终止线程
	 */
	public static void destroy() {
		synchronized (Daemon.class) {
			if (daemon != null) {
				daemon.stop();
				daemon = null;
			}
		}
	}

	/**
	 * 移出任务
	 *
	 * @param id 注册时分配的id
	 * @return true，成功；false，失败
	 */
	public static boolean unregister(long id) {
		synchronized (Daemon.class) {
			// daemon 已销毁时不应因为注销一个任务而重新启动后台线程。
			return daemon != null && daemon.remove(id);
		}
	}

	private volatile static Daemon daemon;
	private static final AtomicLong TASK_ID_GENERATOR = new AtomicLong(0L);

	private static Daemon create() {
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
	private Thread shutdownHook;

	public synchronized void initialize() {
		this.running = true;
		if (this.daemonThread == null) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						while (running) {
							try {
								checkRun();
								Thread.sleep(500);// 每500毫秒检查次任务
							} catch (InterruptedException e) {
								// 被中断，恢复中断状态并退出循环
								Thread.currentThread().interrupt();
								break;
							} catch (Exception e) {
								Logger.log(e);
							}
						}
					} finally {
						// 确保线程池被关闭
						Daemon.this.shutdownThreadPool();
					}
				}
			});
			thread.setName("ibas-framework-daemon");
			thread.setPriority(Thread.MAX_PRIORITY);
			thread.setDaemon(true);
			this.daemonThread = thread;
			this.daemonThread.start();
		}
		// 注册JVM关闭钩子，确保线程被正确终止
		if (this.shutdownHook == null) {
			this.shutdownHook = new Thread(new Runnable() {
				@Override
				public void run() {
					Daemon.destroy();
				}
			});
			this.shutdownHook.setName("ibas-framework-daemon-shutdown-hook");
			try {
				Runtime.getRuntime().addShutdownHook(this.shutdownHook);
			} catch (IllegalStateException e) {
				// JVM正在关闭，忽略
			}
		}
	}

	/**
	 * 运行中标记
	 */
	private volatile boolean running;

	public void stop() {
		this.running = false;
		// 中断守护线程并等待其终止
		Thread thread = this.daemonThread;
		if (thread != null) {
			thread.interrupt();
			try {
				thread.join(5000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			this.daemonThread = null;
		}
		// 先通知所有任务关闭，使运行中的任务有机会尽快结束。
		synchronized (this.getWrappings()) {
			for (DaemonTaskWrapping wrapping : this.getWrappings()) {
				if (wrapping == null) {
					continue;
				}
				wrapping.closeTask();
			}
			this.getWrappings().clear();
		}
		// 关闭线程池
		this.shutdownThreadPool();
		// 移除关闭钩子
		thread = this.shutdownHook;
		if (thread != null) {
			try {
				Runtime.getRuntime().removeShutdownHook(thread);
			} catch (IllegalStateException e) {
				// JVM正在关闭，忽略
			}
			this.shutdownHook = null;
		}
	}

	/**
	 * 安全关闭线程池
	 */
	private void shutdownThreadPool() {
		ThreadPoolExecutor pool = this.threadPool;
		if (pool != null) {
			pool.shutdown();
			try {
				if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
					this.cancelTasks(pool.shutdownNow());
				}
			} catch (InterruptedException e) {
				this.cancelTasks(pool.shutdownNow());
				Thread.currentThread().interrupt();
			}
			this.threadPool = null;
		}
	}

	private void cancelTasks(List<Runnable> tasks) {
		if (tasks == null) {
			return;
		}
		for (Runnable task : tasks) {
			if (task instanceof Daemon.TaskRunner) {
				((TaskRunner) task).cancel();
			}
		}
	}

	private final ArrayList<DaemonTaskWrapping> wrappings = new ArrayList<>();

	protected ArrayList<DaemonTaskWrapping> getWrappings() {
		return wrappings;
	}

	public long add(IDaemonTask task, boolean isLog) throws InvalidDaemonTaskException {
		if (task == null || task.getName() == null || task.getName().isEmpty()) {
			throw new InvalidDaemonTaskException();
		}
		if (task instanceof ISingleDaemonTask) {
			ISingleDaemonTask singleTask = (ISingleDaemonTask) task;
			if (singleTask.getKeepTime() < 1 || singleTask.getLockSignature() == null
					|| singleTask.getLockSignature().isEmpty()) {
				throw new InvalidDaemonTaskException();
			}
		}
		synchronized (this.getWrappings()) {
			DaemonTaskWrapping wrapping = new DaemonTaskWrapping(task);
			wrapping.setLog(isLog);
			long taskId = TASK_ID_GENERATOR.incrementAndGet();
			if (taskId <= 0L) {
				TASK_ID_GENERATOR.set(1L);
				taskId = TASK_ID_GENERATOR.getAndIncrement();
			}
			wrapping.setId(taskId);
			this.getWrappings().add(wrapping);
			Logger.log("daemon: register task id [%s], name [%s].", wrapping.getId(), wrapping.getName());
			return wrapping.getId();
		}
	}

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
					if (this.cancelQueued(wrapping)) {
						wrapping.cancelRun();
					}
					wrapping.closeTask();
					Logger.log("daemon: remove task id [%s], name [%s].", wrapping.getId(), wrapping.getName());
					return true;
				}
			}
		}
		return false;
	}

	private boolean cancelQueued(DaemonTaskWrapping wrapping) {
		ThreadPoolExecutor pool = this.threadPool;
		if (pool == null) {
			return false;
		}
		for (Runnable runnable : pool.getQueue()) {
			if (runnable instanceof Daemon.TaskRunner && ((TaskRunner) runnable).wrapping == wrapping) {
				return pool.remove(runnable);
			}
		}
		return false;
	}

	private volatile ThreadPoolExecutor threadPool;
	private static final AtomicLong THREAD_ID_GENERATOR = new AtomicLong(0L);

	public synchronized ExecutorService getThreadPool() {
		if (this.threadPool == null) {
			int cpu = Runtime.getRuntime().availableProcessors();
			int pSize = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_TASK_THREAD_POOL_SIZE, cpu);
			pSize = pSize < 3 ? 3 : pSize;
			int qSize = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_TASK_THREAD_QUEUE_SIZE, 3);
			qSize = qSize < 1 ? 1 : qSize;
			ThreadPoolExecutor executor = new ThreadPoolExecutor(pSize, pSize, 3, TimeUnit.SECONDS,
					new LinkedBlockingQueue<Runnable>(qSize), new ThreadFactory() {
						@Override
						public Thread newThread(Runnable runnable) {
							Thread thread = new Thread(runnable);
							thread.setName(String.format("ibas-task-worker-%s", THREAD_ID_GENERATOR.incrementAndGet()));
							thread.setDaemon(true);
							return thread;
						}
					});
			executor.allowCoreThreadTimeOut(true);
			this.threadPool = executor;
			Logger.log(MessageLevel.INFO, "daemon: cpu %s, thread pool size %s and queue size %s.", cpu, pSize, qSize);
		}
		return threadPool;
	}

	public void checkRun() {
		long time = System.currentTimeMillis();
		synchronized (this.getWrappings()) {
			for (DaemonTaskWrapping wrapping : this.getWrappings()) {
				if (wrapping == null) {
					continue;
				}
				if (wrapping.isRunning()) {
					continue;
				}
				// 可以运行否。单个任务配置异常不应中断本轮其他任务的调度。
				boolean done;
				try {
					if (!wrapping.isActivated()) {
						continue;
					}
					done = wrapping.tryRun(time);
				} catch (Exception e) {
					Logger.log(e);
					continue;
				}
				if (done) {
					// 在提交前设置运行状态，防止同一任务被重复提交
					wrapping.setRunning(true);
					try {
						// 从线程池中调用新的线程运行此任务
						this.getThreadPool().execute(new TaskRunner(wrapping));
					} catch (RejectedExecutionException e) {
						// 线程池已满或已关闭，回滚运行状态并释放已获取的单任务锁。
						wrapping.cancelRun();
						Logger.log(MessageLevel.WARN, "daemon: task [%s] rejected by thread pool.", wrapping.getName());
					}
				}
			}
		}
	}

	private class TaskRunner implements Runnable {

		private final DaemonTaskWrapping wrapping;

		TaskRunner(DaemonTaskWrapping wrapping) {
			this.wrapping = wrapping;
		}

		@Override
		public void run() {
			long start = System.currentTimeMillis();
			long times = this.wrapping.getRunTimes() + 1;
			if (this.wrapping.isLog() && MyConfiguration.isDebugMode()) {
				Logger.log(MessageLevel.DEBUG, "daemon: begin to run task [%s - %s], %sth running.",
						this.wrapping.getId(), this.wrapping.getName(), times);
			}
			this.wrapping.run();
			long end = System.currentTimeMillis();
			if (this.wrapping.isLog() && MyConfiguration.isDebugMode()) {
				Logger.log(MessageLevel.DEBUG,
						"daemon: end task [%s - %s] %sth running and for [%s] milliseconds.",
						this.wrapping.getId(), this.wrapping.getName(), times, (end - start));
			}
			if (this.wrapping.getNextRunTime() <= 0l) {
				Daemon.this.remove(this.wrapping.getId());
			}
		}

		void cancel() {
			this.wrapping.cancelRun();
			this.wrapping.closeTask();
		}
	}

}
