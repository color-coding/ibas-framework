package org.colorcoding.ibas.bobas.task;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.colorcoding.ibas.bobas.MyConfiguration;

import junit.framework.TestCase;

/**
 * 后台任务调度测试。
 */
public class TestDaemon extends TestCase {

	@Override
	protected void tearDown() throws Exception {
		Daemon.destroy();
		super.tearDown();
	}

	public void testRunOnceTask() throws Exception {
		CountDownLatch ran = new CountDownLatch(1);
		AtomicInteger runTimes = new AtomicInteger();
		long taskId = Daemon.register(new TestTask("run once", 0) {
			@Override
			public void run() {
				runTimes.incrementAndGet();
				ran.countDown();
			}
		}, false);

		assertTrue("Interval zero task should run. ", ran.await(3, TimeUnit.SECONDS));
		Thread.sleep(700);
		assertEquals("Interval zero task should run only once. ", 1, runTimes.get());
		assertFalse("Completed one-time task should be removed. ", Daemon.unregister(taskId));
	}

	public void testUnregisterQueuedTask() throws Exception {
		int workerCount = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_TASK_THREAD_POOL_SIZE,
				Runtime.getRuntime().availableProcessors());
		workerCount = workerCount < 3 ? 3 : workerCount;
		CountDownLatch started = new CountDownLatch(workerCount);
		CountDownLatch release = new CountDownLatch(1);
		for (int i = 0; i < workerCount; i++) {
			Daemon.register(new BlockingTask("blocker " + i, started, release), false);
		}
		assertTrue("Worker tasks should start. ", started.await(3, TimeUnit.SECONDS));

		AtomicInteger runTimes = new AtomicInteger();
		AtomicInteger closeTimes = new AtomicInteger();
		long taskId = Daemon.register(new TestTask("queued", 0) {
			@Override
			public void run() {
				runTimes.incrementAndGet();
			}

			@Override
			public void close() {
				closeTimes.incrementAndGet();
			}
		}, false);

		Thread.sleep(700);
		assertTrue("Queued task should be unregistered. ", Daemon.unregister(taskId));
		release.countDown();
		Thread.sleep(700);
		assertEquals("Unregistered queued task should not run. ", 0, runTimes.get());
		assertEquals("Unregistered task should close once. ", 1, closeTimes.get());
	}

	public void testIntervalFailureReleasesState() {
		AtomicInteger calls = new AtomicInteger();
		DaemonTaskWrapping wrapping = new DaemonTaskWrapping(new TestTask("bad interval", 0) {
			@Override
			public long getInterval() {
				if (calls.incrementAndGet() > 1) {
					throw new IllegalStateException("interval failure");
				}
				return 0;
			}

			@Override
			public void run() {
			}
		});
		wrapping.setRunning(true);
		wrapping.run();

		assertFalse("Task state should be released after interval failure. ", wrapping.isRunning());
		assertEquals("Task with invalid next interval should not run again. ", 0L, wrapping.getNextRunTime());
	}

	public void testSingleTaskLockFileIsStable() throws Exception {
		File lockFile = File.createTempFile("ibas-daemon-test-", ".lock");
		DaemonTaskWrapping.SingleTaskLock lock = new DaemonTaskWrapping.SingleTaskLock(lockFile, 1);
		assertTrue("Lock should be acquired. ", lock.tryAcquire());
		lock.release();
		assertTrue("Lock file should remain to preserve file identity. ", lockFile.exists());

		try (RandomAccessFile file = new RandomAccessFile(lockFile, "rw");
				FileLock nextLock = file.getChannel().tryLock()) {
			assertNotNull("Released lock should be acquirable. ", nextLock);
		}
		assertTrue(lockFile.delete());
	}

	private static class TestTask implements IDaemonTask {

		private final String name;
		private final long interval;

		TestTask(String name, long interval) {
			this.name = name;
			this.interval = interval;
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public long getInterval() {
			return this.interval;
		}

		@Override
		public void run() {
		}
	}

	private static class BlockingTask extends TestTask {

		private final CountDownLatch started;
		private final CountDownLatch release;

		BlockingTask(String name, CountDownLatch started, CountDownLatch release) {
			super(name, 0);
			this.started = started;
			this.release = release;
		}

		@Override
		public void run() {
			this.started.countDown();
			try {
				this.release.await(5, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}
