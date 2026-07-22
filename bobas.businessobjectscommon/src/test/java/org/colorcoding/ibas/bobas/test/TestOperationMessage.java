package org.colorcoding.ibas.bobas.test;

import org.colorcoding.ibas.bobas.common.OperationMessage;
import org.colorcoding.ibas.bobas.i18n.I18N;

import junit.framework.TestCase;

/**
 * OperationMessage 测试
 *
 * 验证 setError 在多种场景下的行为： 1. 自动设置 resultCode 2. message 已经设置时不覆盖 3. 顶层消息处理（含限长）
 * 4. 空消息兜底 5. null 异常处理
 */
public class TestOperationMessage extends TestCase {

	/**
	 * setError null：不应改变 resultCode；message 由 Result 默认机制处理。
	 */
	public void testSetNullError() {
		OperationMessage msg = new OperationMessage();
		int beforeCode = msg.getResultCode();
		String beforeMsg = msg.getMessage();
		msg.setError(null);
		assertEquals(beforeCode, msg.getResultCode());
		assertEquals(beforeMsg, msg.getMessage());
	}

	/**
	 * resultCode 自动置为 -1
	 */
	public void testAutoResultCode() {
		OperationMessage msg = new OperationMessage();
		msg.setError(new RuntimeException("boom"));
		assertEquals(-1, msg.getResultCode());
	}

	/**
	 * 已有非空 message 时不覆盖：先把 resultCode 设为非 0，并显式 setMessage，再 setError。
	 */
	public void testKeepExistingMessage() {
		OperationMessage msg = new OperationMessage();
		msg.setResultCode(-1);
		msg.setMessage("custom message");
		msg.setError(new RuntimeException("should be ignored"));
		assertEquals("custom message", msg.getMessage());
	}

	/**
	 * setError 后 message 非空（顶层异常消息）
	 */
	public void testMessageNonEmpty() {
		OperationMessage msg = new OperationMessage();
		msg.setError(new RuntimeException("hello"));
		assertNotNull(msg.getMessage());
		assertTrue("应含异常消息：" + msg.getMessage(), msg.getMessage().contains("hello"));
	}

	/**
	 * message 为空的异常：使用 i18n "未知异常" 兜底
	 */
	public void testEmptyMessageFallback() {
		OperationMessage msg = new OperationMessage();
		msg.setError(new RuntimeException());
		String result = msg.getMessage();
		assertNotNull(result);
		assertEquals(I18N.prop("msg_bobas_unknown_exception"), result);
	}

	/**
	 * getError 在 resultCode != 0 且 error 为空时自动构造异常
	 */
	public void testGetErrorAutoConstruct() {
		OperationMessage msg = new OperationMessage(-1, "some error");
		Exception err = msg.getError();
		assertNotNull(err);
		assertEquals("some error", err.getMessage());
	}

	/**
	 * 构造时传入异常：error 和 message 应被同步设置
	 */
	public void testConstructWithException() {
		OperationMessage msg = new OperationMessage(new RuntimeException("constructed"));
		assertEquals(-1, msg.getResultCode());
		assertNotNull(msg.getError());
		assertNotNull(msg.getMessage());
		assertTrue(msg.getMessage().contains("constructed"));
	}

}
