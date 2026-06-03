package org.colorcoding.ibas.bobas.test;

import java.math.BigDecimal;

import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Decimals;
import org.colorcoding.ibas.bobas.data.DataConvert;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emConditionOperation;
import org.colorcoding.ibas.bobas.data.emConditionRelationship;

import junit.framework.TestCase;

/**
 * 数据类型转换测试
 *
 * 测试范围：
 * 1. 基本类型转换（String/Integer/Long/Double/Float/BigDecimal/Boolean/DateTime）
 * 2. 枚举类型转换
 * 3. 同类型直接返回
 * 4. null值处理
 * 5. 条件枚举转换（emConditionOperation/emConditionRelationship ↔ ConditionOperation/ConditionRelationship）
 */
public class TestDataConvert extends TestCase {

	// ==================== 1. 基本类型转换 ====================

	/**
	 * 测试转换为String
	 * 覆盖：各种类型→String
	 */
	public void testConvertToString() {
		assertEquals("Integer to String. ", "123", DataConvert.convert(String.class, 123));
		assertEquals("BigDecimal to String. ", "99.99", DataConvert.convert(String.class, new BigDecimal("99.99")));
		assertEquals("Boolean to String. ", "true", DataConvert.convert(String.class, true));
	}

	/**
	 * 测试转换为Integer
	 * 覆盖：String→Integer、空值→0
	 */
	public void testConvertToInteger() {
		assertEquals("String to Integer. ", Integer.valueOf(123), DataConvert.convert(Integer.class, "123"));
		assertEquals("Empty string to Integer zero. ", Integer.valueOf(0), DataConvert.convert(Integer.class, ""));
		assertEquals("Null to Integer null. ", null, DataConvert.convert(Integer.class, null));
	}

	/**
	 * 测试转换为Long
	 * 覆盖：String→Long、空值→0
	 */
	public void testConvertToLong() {
		assertEquals("String to Long. ", Long.valueOf(123456L), DataConvert.convert(Long.class, "123456"));
		assertEquals("Empty string to Long zero. ", Long.valueOf(0L), DataConvert.convert(Long.class, ""));
	}

	/**
	 * 测试转换为Double
	 * 覆盖：String→Double
	 */
	public void testConvertToDouble() {
		assertEquals("String to Double. ", Double.valueOf(99.99), DataConvert.convert(Double.class, "99.99"));
		assertEquals("Empty string to Double zero. ", Double.valueOf(0d), DataConvert.convert(Double.class, ""));
	}

	/**
	 * 测试转换为Float
	 * 覆盖：String→Float
	 */
	public void testConvertToFloat() {
		assertEquals("String to Float. ", Float.valueOf(99.99f), DataConvert.convert(Float.class, "99.99"));
		assertEquals("Empty string to Float zero. ", Float.valueOf(0f), DataConvert.convert(Float.class, ""));
	}

	/**
	 * 测试转换为BigDecimal
	 * 覆盖：String→BigDecimal、空值→ZERO
	 */
	public void testConvertToBigDecimal() {
		assertEquals("String to BigDecimal. ", Decimals.valueOf("99.99"),
				DataConvert.convert(BigDecimal.class, "99.99"));
		assertEquals("Empty string to BigDecimal zero. ", Decimals.VALUE_ZERO,
				DataConvert.convert(BigDecimal.class, ""));
	}

	/**
	 * 测试转换为Boolean
	 * 覆盖：String→Boolean
	 */
	public void testConvertToBoolean() {
		assertEquals("String true to Boolean. ", Boolean.TRUE, DataConvert.convert(Boolean.class, "true"));
		assertEquals("String false to Boolean. ", Boolean.FALSE, DataConvert.convert(Boolean.class, "false"));
	}

	/**
	 * 测试转换为DateTime
	 * 覆盖：String→DateTime、Long→DateTime
	 */
	public void testConvertToDateTime() {
		DateTime date = DataConvert.convert(DateTime.class, "2025-06-01");
		assertNotNull("String to DateTime should not be null. ", date);

		// Long→DateTime
		long timestamp = DateTimes.valueOf("2025-06-01").getTime();
		DateTime fromTimestamp = DataConvert.convert(DateTime.class, timestamp);
		assertNotNull("Long to DateTime should not be null. ", fromTimestamp);
	}

	// ==================== 2. 特殊情况 ====================

	/**
	 * 测试null类型返回null
	 */
	public void testConvertNullType() {
		assertNull("Null type should return null. ", DataConvert.convert(null, "hello"));
	}

	/**
	 * 测试null值返回null
	 */
	public void testConvertNullValue() {
		assertNull("Null value should return null. ", DataConvert.convert(String.class, null));
	}

	/**
	 * 测试同类型直接返回
	 */
	public void testConvertSameType() {
		String value = "hello";
		String result = DataConvert.convert(String.class, value);
		assertSame("Same type should return same instance. ", value, result);
	}

	/**
	 * 测试不支持类型抛异常
	 */
	public void testConvertUnsupportedType() {
		try {
			DataConvert.convert(Thread.class, "hello");
			fail("Should throw ClassCastException for unsupported type.");
		} catch (ClassCastException e) {
			// expected
		}
	}

	// ==================== 3. 条件枚举转换 ====================

	/**
	 * 测试emConditionRelationship → ConditionRelationship
	 * 覆盖：AND/OR/NONE
	 */
	public void testToConditionRelationship() {
		assertEquals("AND. ", ConditionRelationship.AND,
				DataConvert.toRelationship(emConditionRelationship.AND));
		assertEquals("OR. ", ConditionRelationship.OR,
				DataConvert.toRelationship(emConditionRelationship.OR));
		assertEquals("NONE. ", ConditionRelationship.NONE,
				DataConvert.toRelationship(emConditionRelationship.NONE));
	}

	/**
	 * 测试emConditionOperation → ConditionOperation
	 * 覆盖：主要操作符
	 */
	public void testToConditionOperation() {
		assertEquals("EQUAL. ", ConditionOperation.EQUAL,
				DataConvert.toOperation(emConditionOperation.EQUAL));
		assertEquals("NOT_EQUAL. ", ConditionOperation.NOT_EQUAL,
				DataConvert.toOperation(emConditionOperation.NOT_EQUAL));
		assertEquals("CONTAIN. ", ConditionOperation.CONTAIN,
				DataConvert.toOperation(emConditionOperation.CONTAIN));
		assertEquals("GRATER_EQUAL. ", ConditionOperation.GRATER_EQUAL,
				DataConvert.toOperation(emConditionOperation.GRATER_EQUAL));
		assertEquals("LESS_THAN. ", ConditionOperation.LESS_THAN,
				DataConvert.toOperation(emConditionOperation.LESS_THAN));
		assertEquals("BEGIN_WITH → START. ", ConditionOperation.START,
				DataConvert.toOperation(emConditionOperation.BEGIN_WITH));
		assertEquals("END_WITH → END. ", ConditionOperation.END,
				DataConvert.toOperation(emConditionOperation.END_WITH));
		assertEquals("IN. ", ConditionOperation.IN,
				DataConvert.toOperation(emConditionOperation.IN));
	}

	/**
	 * 测试ConditionRelationship → emConditionRelationship
	 * 覆盖：反向转换
	 */
	public void testFromConditionRelationship() {
		assertEquals("AND reverse. ", emConditionRelationship.AND,
				DataConvert.toRelationship(ConditionRelationship.AND));
		assertEquals("OR reverse. ", emConditionRelationship.OR,
				DataConvert.toRelationship(ConditionRelationship.OR));
	}

	/**
	 * 测试ConditionOperation → emConditionOperation
	 * 覆盖：反向转换
	 */
	public void testFromConditionOperation() {
		assertEquals("EQUAL reverse. ", emConditionOperation.EQUAL,
				DataConvert.toOperation(ConditionOperation.EQUAL));
		assertEquals("START → BEGIN_WITH. ", emConditionOperation.BEGIN_WITH,
				DataConvert.toOperation(ConditionOperation.START));
		assertEquals("END → END_WITH. ", emConditionOperation.END_WITH,
				DataConvert.toOperation(ConditionOperation.END));
	}

	/**
	 * 测试条件枚举null抛异常
	 */
	public void testConditionNullThrows() {
		try {
			DataConvert.toRelationship((emConditionRelationship) null);
			fail("Should throw NullPointerException.");
		} catch (NullPointerException e) {
			// expected
		}
		try {
			DataConvert.toOperation((emConditionOperation) null);
			fail("Should throw NullPointerException.");
		} catch (NullPointerException e) {
			// expected
		}
	}
}
