package org.colorcoding.ibas.bobas.test.core;

import org.colorcoding.ibas.bobas.core.Daemon;
import org.colorcoding.ibas.bobas.core.ISingleDaemonTask;
import org.colorcoding.ibas.bobas.core.InvalidDaemonTask;

import junit.framework.TestCase;

public class testDaemonTask extends TestCase {

	public void testSingleTask() throws InvalidDaemonTask, InterruptedException {
		long i;
		for (i = 0; i < 200; i++) {
			Daemon.register(new ISingleDaemonTask() {

				@Override
				public void run() {
					try {
						System.out.println(
								System.currentTimeMillis() + "  " + Thread.currentThread().getName() + " is running");
						Thread.sleep(3000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public boolean isActivated() {
					return true;
				}

				@Override
				public String getName() {
					return String.format("test task %s", this.hashCode());
				}

				@Override
				public long getInterval() {
					return 1;
				}

				public String getLockSignature() {
					return "single_task";
				}

				public long getKeepTime() {
					return 10;
				}
			});
		}
		Thread.sleep(50000);// 等待
	}
}
