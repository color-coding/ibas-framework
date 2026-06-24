package org.colorcoding.ibas.bobas.test;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.OperationMessage;

import junit.framework.TestCase;

/**
 * OperationMessage 测试
 *
 * 验证 setError 在多种场景下的行为：
 * 1. 自动设置 resultCode
 * 2. message 已经设置时不覆盖
 * 3. 顶层消息处理（含限长）
 * 4. debug 模式：cause 链遍历、分隔符、去重、深度上限、环路防护、空消息兜底
 * 5. null 异常处理
 *
 * 注意：MyConfiguration.isDebugMode() 在 JVM 内被静态缓存，所以测试以运行环境的实际值为准。
 */
public class TestOperationMessage extends TestCase {

	private final boolean debug = MyConfiguration.isDebugMode();

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
	 * setError 后 message 非空（顶层异常消息或 debug 多层消息）
	 */
	public void testMessageNonEmpty() {
		OperationMessage msg = new OperationMessage();
		msg.setError(new RuntimeException("hello"));
		assertNotNull(msg.getMessage());
		assertTrue("应含异常消息：" + msg.getMessage(), msg.getMessage().contains("hello"));
	}

	/**
	 * message 为空的异常：debug 时使用类名兜底，非 debug 时使用 "internal error"
	 */
	public void testEmptyMessageFallback() {
		OperationMessage msg = new OperationMessage();
		msg.setError(new RuntimeException());
		String result = msg.getMessage();
		assertNotNull(result);
		if (debug) {
			assertTrue("debug 模式应使用类名兜底：" + result, result.contains("RuntimeException"));
		} else {
			assertEquals("internal error", result);
		}
	}

	/**
	 * 超长消息处理：非 debug 截断到 1024；debug 不截断
	 */
	public void testLongMessage() {
		OperationMessage msg = new OperationMessage();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 2000; i++) {
			sb.append("x");
		}
		msg.setError(new RuntimeException(sb.toString()));
		String result = msg.getMessage();
		assertNotNull(result);
		if (debug) {
			// debug 不截断，保留完整消息
			assertEquals(2000, result.length());
		} else {
			assertEquals(1024, result.length());
		}
	}

	/**
	 * debug 模式下应遍历 cause 链并用换行符分隔（仅在 debug 启用时验证）
	 */
	public void testDebugCauseChain() {
		if (!debug) {
			return; // 非 debug 模式跳过
		}
		OperationMessage msg = new OperationMessage();
		RuntimeException a = new RuntimeException("level-a-message");
		RuntimeException b = new RuntimeException("level-b-message", a);
		RuntimeException c = new RuntimeException("level-c-message", b);
		msg.setError(c);
		String result = msg.getMessage();
		assertNotNull(result);
		assertTrue("应含 level-c：" + result, result.contains("level-c-message"));
		assertTrue("应含 level-b：" + result, result.contains("level-b-message"));
		assertTrue("应含 level-a：" + result, result.contains("level-a-message"));
		assertTrue("应有换行分隔：" + result, result.contains("\n"));
	}

	/**
	 * debug 模式：内层 message 是外层 message 的后缀时跳过外层
	 */
	public void testDebugSuffixDedup() {
		if (!debug) {
			return;
		}
		OperationMessage msg = new OperationMessage();
		RuntimeException inner = new RuntimeException("Z00002 already exists");
		RuntimeException outer = new RuntimeException("wrap: Z00002 already exists", inner);
		msg.setError(outer);
		assertEquals("Z00002 already exists", msg.getMessage());
	}

	/**
	 * debug 模式：cause 环路防护（不应死循环）
	 */
	public void testDebugCauseCycle() {
		if (!debug) {
			return;
		}
		OperationMessage msg = new OperationMessage();
		RuntimeException a = new RuntimeException("a-msg");
		RuntimeException b = new RuntimeException("b-msg");
		try {
			a.initCause(b);
			b.initCause(a);
		} catch (Exception ignored) {
			// 部分 JDK 拒绝环路构造，跳过
			return;
		}
		// 不应抛异常或死循环
		msg.setError(a);
		assertNotNull(msg.getMessage());
	}

	/**
	 * debug 模式：cause 链超过最大深度，最多收 5 层
	 */
	public void testDebugDepthLimit() {
		if (!debug) {
			return;
		}
		OperationMessage msg = new OperationMessage();
		RuntimeException curr = new RuntimeException("level-0");
		for (int i = 1; i < 10; i++) {
			curr = new RuntimeException("level-" + i, curr);
		}
		msg.setError(curr);
		String result = msg.getMessage();
		assertNotNull(result);
		// 最深 5 层（level-9 到 level-5）
		assertTrue("应含 level-9：" + result, result.contains("level-9"));
		assertTrue("应含 level-5：" + result, result.contains("level-5"));
		// 不应含 level-4 及更深层
		assertFalse("超过深度限制：" + result, result.contains("level-4"));
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
