package org.colorcoding.ibas.bobas.test.messages;

import org.colorcoding.ibas.bobas.messages.RuntimeLog;

import junit.framework.TestCase;

public class testRuntimeLog extends TestCase {

	public void testMultiLogSave() throws InterruptedException {
		int theadCount = 99;

		for (int i = 0; i < theadCount; i++) {
			Thread threads = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						testRuntimeLogSave();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}, String.valueOf(i));
			threads.start();
		}
		Thread.sleep(120000);
		System.out.println("主线程结束！");
	}

	public void testRuntimeLogSave() throws InterruptedException {
		for (int i = 0; i < 1000000; i++) {
			RuntimeLog.log("message [%s] by [%s]", i, Thread.currentThread().getName());
		}
		// Thread.sleep(10000);
	}
}