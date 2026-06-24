package org.colorcoding.ibas.bobas.test;

import java.sql.Connection;
import java.sql.SQLException;

import org.colorcoding.ibas.bobas.db.DbAdapter;

import junit.framework.TestCase;

/**
 * 数据库异常翻译测试（基类行为）
 *
 * 基类 DbAdapter.translateException 不做任何分析，直接返回异常的原始消息；
 * 各厂商的子类负责按自己的 ErrorCode/SQLState 识别并返回国际化消息。
 */
public class TestDbExceptionTranslate extends TestCase {

	/** 测试用的 DbAdapter（基类行为） */
	private static class TestAdapter extends DbAdapter {
		@Override
		public Connection createConnection(String server, String dbName, String userName, String userPwd) {
			return null;
		}
	}

	private final DbAdapter adapter = new TestAdapter();

	/**
	 * 基类应原样返回异常消息
	 */
	public void testReturnsOriginalMessage() {
		String raw = "Cannot insert duplicate key row in object 'dbo.CC_MM_OITM' with unique index 'UK_CC_MM_OITM'. The duplicate key value is (Z00002).";
		SQLException ex = new SQLException(raw, "23000", 2627);
		String translated = adapter.translateException(ex);
		assertEquals(raw, translated);
	}

	/**
	 * 任意非约束类异常也应原样返回消息
	 */
	public void testNonIntegrityReturnsMessage() {
		SQLException ex = new SQLException("syntax error", "42000", 102);
		String translated = adapter.translateException(ex);
		assertEquals("syntax error", translated);
	}

	/**
	 * null 输入返回空字符串
	 */
	public void testNullException() {
		assertEquals("", adapter.translateException(null));
	}

	/**
	 * 消息为 null 时返回 null（getMessage 的原始结果）
	 */
	public void testNullMessage() {
		SQLException ex = new SQLException();
		assertNull(adapter.translateException(ex));
	}

}
