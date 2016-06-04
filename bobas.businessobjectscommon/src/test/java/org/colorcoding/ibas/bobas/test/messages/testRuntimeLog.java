/**
 * 
 */
/**
 * @author Eric Peng
 *
 */
package org.colorcoding.ibas.bobas.test.messages;

import org.colorcoding.ibas.bobas.messages.RuntimeLog;

import junit.framework.TestCase;

public class testRuntimeLog extends TestCase {

	public void testMultiLogSave() throws InterruptedException {
		for (int i = 0; i < 8; i++) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					testRuntimeLogSave();
				}
			}, String.valueOf(i));
			thread.start();
		}
		Thread.sleep(15000);

	}

	public void testRuntimeLogSave() {
		for (int i = 0; i < 100; i++) {
			RuntimeLog.log("message [%s] by [%s]", i, Thread.currentThread().getName());
		}
	}
}