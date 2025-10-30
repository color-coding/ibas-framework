package org.colorcoding.ibas.bobas.task;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
public class Daemon implements IDaemon {

	/**
	 * 注册后台任务
	 * 
	 * @param task 任务
	 * @return 任务ID，小于0任务注册失败
	 * @throws InvalidDaemonTaskException
	 */
	public static long register(IDaemonTask task) throws InvalidDaemonTaskException {
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
	public static long register(IDaemonTask task, boolean log) throws InvalidDaemonTaskException {
		synchronized (Daemon.class) {
			return create().add(task, log);
		}
	}

	/**
	 * 终止线程
	 */
	public static void destory() {
		synchronized (Daemon.class) {
			if (daemon != null) {
				daemon.stop();
				daemon = null;
				System.gc();
			}
		}
	}

	/**
	 * 移出任务
	 * 
	 * @param id 注册时分配的id
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
		this.running = true;
		if (this.daemonThread == null) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (running) {
						try {
							checkRun();
							Thread.sleep(500);// 每500毫秒检查次任务
						} catch (Exception e) {
							System.err.println(e);
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

	/**
	 * 运行中标记
	 */
	private volatile boolean running;

	@Override
	public void stop() {
		this.running = false;
		if (this.daemonThread != null) {
			this.daemonThread.interrupt();
		}
		if (this.threadPool != null) {
			this.threadPool.shutdown();
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
			wrapping.setId(Math.abs(UUID.randomUUID().getLeastSignificantBits()));
			this.getWrappings().add(wrapping);
			Logger.log("daemon: register task id [%s], name [%s].", wrapping.getId(), wrapping.getName());
			return wrapping.getId();
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
					Logger.log("daemon: remove task id [%s], name [%s].", wrapping.getId(), wrapping.getName());
					return true;
				}
			}
		}
		return false;
	}

	private ExecutorService threadPool;

	public ExecutorService getThreadPool() {
		if (this.threadPool == null) {
			int cpu = Runtime.getRuntime().availableProcessors();
			int pSize = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_TASK_THREAD_POOL_SIZE, cpu);
			pSize = pSize < 3 ? 3 : pSize;
			int qSize = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_TASK_THREAD_QUEUE_SIZE, 3);
			this.threadPool = new ThreadPoolExecutor(1, pSize, 3, TimeUnit.SECONDS,
					new LinkedBlockingQueue<Runnable>(qSize));
			Logger.log(MessageLevel.INFO, "daemon: cpu %s, thread pool size %s and queue size %s.", cpu, pSize, qSize);
		}
		return threadPool;
	}

	@Override
	public void checkRun() {
		long time = System.currentTimeMillis();
		synchronized (this.getWrappings()) {
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
				// 可以运行否
				boolean done = wrapping.tryRun(time);
				if (done) {
					// 从线程池中调用新的线程运行此任务
					this.getThreadPool().execute(new Runnable() {

						@Override
						public void run() {
							if (wrapping.isRunning()) {
								// 如果已在执行则退出
								return;
							}
							// 设置状态为运行中
							wrapping.setRunning(true);
							long start = System.currentTimeMillis();
							long times = wrapping.getRunTimes() + 1;
							if (wrapping.isLog() && MyConfiguration.isDebugMode()) {
								Logger.log(MessageLevel.DEBUG, "daemon: begin to run task [%s - %s], %sth running.",
										wrapping.getId(), wrapping.getName(), times);
							}
							wrapping.run();
							long end = System.currentTimeMillis();
							if (wrapping.isLog() && MyConfiguration.isDebugMode()) {
								Logger.log(MessageLevel.DEBUG,
										"daemon: end task [%s - %s] %sth running and for [%s] milliseconds.",
										wrapping.getId(), wrapping.getName(), times, (end - start));
							}
							if (wrapping.getNextRunTime() <= 0l) {
								// 下次运行时间，小于等于0，则移出任务
								Daemon.this.remove(wrapping.getId());
							}
						}
					});
				}
			}
		}
	}

}
