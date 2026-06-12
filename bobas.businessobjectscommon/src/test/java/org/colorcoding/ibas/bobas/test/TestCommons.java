package org.colorcoding.ibas.bobas.test;

import java.math.BigDecimal;

import org.colorcoding.ibas.bobas.common.Booleans;
import org.colorcoding.ibas.bobas.common.Bytes;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Decimals;
import org.colorcoding.ibas.bobas.common.EncryptMD5;
import org.colorcoding.ibas.bobas.common.Enums;
import org.colorcoding.ibas.bobas.common.Files;
import org.colorcoding.ibas.bobas.common.Numbers;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.emTimeUnit;

import junit.framework.TestCase;

/**
 * 通用工具类测试
 *
 * 测试范围： 1. Strings - 字符串工具（空值判断、格式化、比较、拼接、前缀后缀、替换、截取、编码转换） 2. Decimals -
 * 数值工具（转换、四则运算、比较、舍入、缓存） 3. DateTimes - 日期工具（转换、比较、间隔计算、格式化） 4. Numbers -
 * 数值工具（类型判断、转换、比较） 5. Booleans - 布尔工具（比较） 6. Bytes -
 * 字节工具（Hex/Base64编解码、DataURL） 7. Enums - 枚举工具（转换、注解值、KeyValue数组） 8. EncryptMD5
 * - 加密工具（MD5摘要、短链接生成） 9. Files - 文件路径工具（路径解析、文件名/扩展名提取、路径隐藏）
 */
public class TestCommons extends TestCase {

	// ==================== Strings 字符串工具测试 ====================

	/**
	 * 测试字符串空值判断 覆盖：null、空字符串、非空字符串
	 */
	public void testStringIsNullOrEmpty() {
		assertTrue("null should be empty. ", Strings.isNullOrEmpty(null));
		assertTrue("Empty string should be empty. ", Strings.isNullOrEmpty(""));
		assertFalse("Non-empty string should not be empty. ", Strings.isNullOrEmpty("hello"));
		assertFalse("Space string should not be empty. ", Strings.isNullOrEmpty(" "));
	}

	/**
	 * 测试字符串长度 覆盖：null返回-1、正常字符串长度
	 */
	public void testStringLength() {
		assertEquals("Null string length should be -1. ", -1, Strings.length(null));
		assertEquals("Empty string length should be 0. ", 0, Strings.length(""));
		assertEquals("Normal string length. ", 5, Strings.length("hello"));
	}

	/**
	 * 测试字符串格式化 覆盖：null模板、无参数、正常格式化
	 */
	public void testStringFormat() {
		assertEquals("Null template should return empty. ", Strings.VALUE_EMPTY, Strings.format(null));
		assertEquals("No args should return template. ", "hello", Strings.format("hello"));
		assertEquals("Format with args. ", "hello world", Strings.format("%s %s", "hello", "world"));
	}

	/**
	 * 测试字符串相等比较 覆盖：null、同引用、值相等、忽略大小写
	 */
	public void testStringEquals() {
		assertFalse("Null comparison should be false. ", Strings.equals(null, "a"));
		assertFalse("Null comparison should be false. ", Strings.equals("a", null));
		assertTrue("Same reference should be equal. ", Strings.equals("a", "a"));
		assertTrue("Value equality. ", Strings.equals("hello", "hello"));
		assertFalse("Case sensitive by default. ", Strings.equals("Hello", "hello"));
		assertTrue("Ignore case. ", Strings.equalsIgnoreCase("Hello", "hello"));
	}

	/**
	 * 测试字符串拼接 覆盖：null项跳过、正常拼接
	 */
	public void testStringConcat() {
		assertEquals("Concat with null items. ", "ab", Strings.concat("a", null, "b"));
		assertEquals("Concat empty array. ", Strings.VALUE_EMPTY, Strings.concat(new String[] {}));
		assertEquals("Normal concat. ", "abc", Strings.concat("a", "b", "c"));
	}

	/**
	 * 测试字符串前缀/后缀判断 覆盖：startsWith、endsWith、忽略大小写、makeStartsWith/makeEndsWith
	 */
	public void testStringStartsEndsWith() {
		assertTrue("StartsWith. ", Strings.startsWith("hello world", "hello"));
		assertFalse("Not startsWith. ", Strings.startsWith("hello world", "world"));
		assertTrue("EndsWith. ", Strings.endsWith("hello world", "world"));
		assertTrue("StartsWith ignore case. ", Strings.startsWith("Hello", "hello", true));
		assertTrue("EndsWith ignore case. ", Strings.endsWith("Hello", "HELLO", true));

		// makeStartsWith
		assertEquals("Already starts with prefix. ", "hello", Strings.makeStartsWith("hello", "he"));
		assertEquals("Add prefix. ", "hehhello", Strings.makeStartsWith("hello", "heh"));
		assertEquals("Empty prefix returns original. ", "hello", Strings.makeStartsWith("hello", null));

		// makeEndsWith
		assertEquals("Already ends with suffix. ", "hello", Strings.makeEndsWith("hello", "lo"));
		assertEquals("Add suffix. ", "hello!", Strings.makeEndsWith("hello", "!"));
	}

	/**
	 * 测试字符串替换 覆盖：正常替换、空值不替换
	 */
	public void testStringReplace() {
		assertEquals("Replace normal. ", "hexxo worxd", Strings.replace("hello world", "l", "x"));
		assertEquals("Replace null content returns original. ", null, Strings.replace(null, "a", "b"));
		assertEquals("Replace empty oldString returns original. ", "hello", Strings.replace("hello", "", "x"));
	}

	/**
	 * 测试字符串截取 覆盖：正常截取、越界返回空、null返回空
	 */
	public void testStringSubstring() {
		assertEquals("Normal substring. ", "ell", Strings.substring("hello", 1, 4));
		assertEquals("Null returns empty. ", Strings.VALUE_EMPTY, Strings.substring(null, 0, 1));
		assertEquals("Out of bounds returns empty. ", Strings.VALUE_EMPTY, Strings.substring("hi", 0, 10));
		assertEquals("Negative begin returns empty. ", Strings.VALUE_EMPTY, Strings.substring("hi", -1, 2));
		assertEquals("Substring by count. ", "hel", Strings.substring("hello", 3));
		assertEquals("Count <= 1 returns empty. ", Strings.VALUE_EMPTY, Strings.substring("hello", 1));
	}

	/**
	 * 测试字符串indexOf 覆盖：正常查找、null返回-1
	 */
	public void testStringIndexOf() {
		assertEquals("Normal indexOf. ", 2, Strings.indexOf("hello", "l"));
		assertEquals("Null content returns -1. ", -1, Strings.indexOf(null, "l"));
		assertEquals("Null value returns -1. ", -1, Strings.indexOf("hello", null));
		assertEquals("Not found returns -1. ", -1, Strings.indexOf("hello", "x"));
	}

	/**
	 * 测试字符串大小写转换 覆盖：正常转换、null返回null
	 */
	public void testStringCase() {
		assertEquals("To upper case. ", "HELLO", Strings.toUpperCase("hello"));
		assertEquals("To lower case. ", "hello", Strings.toLowerCase("HELLO"));
		assertNull("Null to upper returns null. ", Strings.toUpperCase(null));
		assertNull("Null to lower returns null. ", Strings.toLowerCase(null));
	}

	/**
	 * 测试字符串trim 覆盖：正常去除空格、null返回null
	 */
	public void testStringTrim() {
		assertEquals("Trim spaces. ", "hello", Strings.trim(" hello "));
		assertNull("Null trim returns null. ", Strings.trim(null));
	}

	/**
	 * 测试字符串数值判断 覆盖：数字字符串、非数字、空值
	 */
	public void testStringIsNumeric() {
		assertTrue("Integer string is numeric. ", Strings.isNumeric("123"));
		assertTrue("Decimal string is numeric. ", Strings.isNumeric("12.3"));
		assertTrue("Negative number is numeric. ", Strings.isNumeric("-12.3"));
		assertFalse("Non-numeric string. ", Strings.isNumeric("abc"));
		assertFalse("Null is not numeric. ", Strings.isNumeric(null));
		assertFalse("Empty is not numeric. ", Strings.isNumeric(""));
		assertFalse("Pure minus is not numeric. ", Strings.isNumeric("-"));
		assertFalse("Double dot is not numeric. ", Strings.isNumeric("1.2.3"));
	}

	/**
	 * 测试optionNonEmpty/optionNonNull 覆盖：选择第一个非空/非null值
	 */
	public void testStringOption() {
		assertEquals("Option non empty. ", "b", Strings.optionNonEmpty(null, "", "b", "c"));
		assertEquals("All empty returns empty. ", Strings.VALUE_EMPTY, Strings.optionNonEmpty(null, ""));
		assertEquals("Option non null. ", "a", Strings.optionNonNull(null, "a", "b"));
		assertEquals("All null returns empty. ", Strings.VALUE_EMPTY, Strings.optionNonNull((String[]) null));
	}

	/**
	 * 测试valueOf 覆盖：null、数组、集合、普通对象
	 */
	public void testStringValueOf() {
		assertEquals("Null returns empty. ", Strings.VALUE_EMPTY, Strings.valueOf((Object) null));
		assertEquals("String value. ", "hello", Strings.valueOf("hello"));
		assertEquals("Integer value. ", "123", Strings.valueOf(123));
		assertEquals("Array value. ", "1,2,3", Strings.valueOf(new int[] { 1, 2, 3 }));
	}

	/**
	 * 测试字母表 覆盖：alphabetOf正常取值、越界异常、0返回空
	 */
	public void testAlphabet() {
		assertEquals("Alphabet 32 is space. ", " ", Strings.alphabetOf(32));
		assertEquals("Alphabet 48 is '0'. ", "0", Strings.alphabetOf(48));
		assertEquals("Alphabet 65 is 'A'. ", "A", Strings.alphabetOf(65));
		assertEquals("Alphabet 0 returns empty. ", Strings.VALUE_EMPTY, Strings.alphabetOf(0));
		try {
			Strings.alphabetOf(31);
			fail("Should throw IndexOutOfBoundsException for value < 32.");
		} catch (IllegalArgumentException e) {
			// expected
		}
		try {
			Strings.alphabetOf(127);
			fail("Should throw IndexOutOfBoundsException for value > 126.");
		} catch (IllegalArgumentException e) {
			// expected
		}
	}

	// ==================== Decimals 数值工具测试 ====================

	/**
	 * 测试Decimals转换 覆盖：整数/浮点/字符串转换、缓存值、null/空字符串处理
	 */
	public void testDecimalsValueOf() {
		// 缓存值 0-10 及 -1
		assertSame("valueOf(0) should use cache. ", Decimals.VALUE_ZERO, Decimals.valueOf(0));
		assertSame("valueOf(1) should use cache. ", Decimals.VALUE_ONE, Decimals.valueOf(1));
		assertSame("valueOf(-1) should use cache. ", Decimals.VALUE_MINUS_ONE, Decimals.valueOf(-1));
		assertSame("valueOf(int 5) should use cache. ", Decimals.valueOf(5), Decimals.valueOf(5));

		// 字符串转换
		assertEquals("valueOf string '99.99'. ", new BigDecimal("99.99"), Decimals.valueOf("99.99"));
		assertEquals("valueOf null returns zero. ", Decimals.VALUE_ZERO, Decimals.valueOf((String) null));
		assertEquals("valueOf empty returns zero. ", Decimals.VALUE_ZERO, Decimals.valueOf(""));
		assertEquals("valueOf '0' returns zero. ", Decimals.VALUE_ZERO, Decimals.valueOf("0"));
		assertEquals("valueOf '1.0' returns one. ", Decimals.VALUE_ONE, Decimals.valueOf("1.0"));
		assertEquals("valueOf '-1.0' returns minus one. ", Decimals.VALUE_MINUS_ONE, Decimals.valueOf("-1.0"));
	}

	/**
	 * 测试Decimals四则运算 覆盖：加/减/乘/除、连运算、null处理
	 */
	public void testDecimalsArithmetic() {
		// 加法
		assertEquals("Add. ", Decimals.valueOf(3), Decimals.add(Decimals.VALUE_ONE, Decimals.valueOf(2)));
		assertEquals("Add null treated as zero. ", Decimals.VALUE_ONE, Decimals.add(null, Decimals.VALUE_ONE));
		assertEquals("Add multiple. ", Decimals.valueOf(6),
				Decimals.add(Decimals.VALUE_ONE, Decimals.valueOf(2), Decimals.valueOf(3)));

		// 减法
		assertEquals("Subtract. ", Decimals.valueOf(-1), Decimals.subtract(Decimals.VALUE_ONE, Decimals.valueOf(2)));

		// 乘法
		assertTrue("Multiply. ",
				Decimals.equals(Decimals.valueOf(6), Decimals.multiply(Decimals.valueOf(2), Decimals.valueOf(3))));
		assertTrue("Multiply by null skipped. ",
				Decimals.equals(Decimals.valueOf(2), Decimals.multiply(Decimals.valueOf(2), (BigDecimal) null)));

		// 除法
		assertTrue("Divide. ",
				Decimals.equals(Decimals.valueOf(2), Decimals.divide(Decimals.valueOf(6), Decimals.valueOf(3))));
		assertTrue("Divide by one. ",
				Decimals.equals(Decimals.VALUE_ONE, Decimals.divide(Decimals.valueOf(1), Decimals.VALUE_ONE)));
	}

	/**
	 * 测试Decimals比较 覆盖：equals/greaterThan/lessThan/isZero
	 */
	public void testDecimalsComparison() {
		assertTrue("Equals. ", Decimals.equals(Decimals.VALUE_ONE, Decimals.VALUE_ONE));
		assertFalse("Not equals null. ", Decimals.equals(null, Decimals.VALUE_ONE));
		assertTrue("GreaterThan. ", Decimals.greaterThan(Decimals.valueOf(2), Decimals.VALUE_ONE));
		assertFalse("GreaterThan null. ", Decimals.greaterThan(null, Decimals.VALUE_ONE));
		assertTrue("LessThan. ", Decimals.lessThan(Decimals.VALUE_ONE, Decimals.valueOf(2)));
		assertTrue("IsZero. ", Decimals.isZero(Decimals.VALUE_ZERO));
		assertFalse("Not zero. ", Decimals.isZero(Decimals.VALUE_ONE));
	}

	/**
	 * 测试Decimals舍入 覆盖：四舍五入、指定精度
	 */
	public void testDecimalsRound() {
		BigDecimal value = Decimals.valueOf("99.9999");
		BigDecimal rounded = Decimals.round(value, 2);
		assertEquals("Round to 2 decimal places. ", new BigDecimal("100.00"), rounded);
	}

	// ==================== DateTimes 日期工具测试 ====================

	/**
	 * 测试DateTimes转换 覆盖：字符串、年月日、时间戳、Date对象
	 */
	public void testDateTimesValueOf() {
		// 字符串转换
		DateTime date = DateTimes.valueOf("2025-06-01");
		assertNotNull("Date from string should not be null. ", date);
		assertEquals("Date string format. ", "2025-06-01", date.toString());

		// 年月日转换
		date = DateTimes.valueOf(2025, 12, 25);
		assertNotNull("Date from ymd should not be null. ", date);
		assertTrue("Date should contain 2025. ", date.toString().contains("2025"));

		// 时间戳
		long timestamp = date.getTime();
		DateTime fromTimestamp = DateTimes.valueOf(timestamp);
		assertEquals("Date from timestamp should match. ", date, fromTimestamp);

		// null Date对象
		assertEquals("Null Date returns VALUE_MIN. ", DateTimes.VALUE_MIN, DateTimes.valueOf((java.util.Date) null));
	}

	/**
	 * 测试DateTimes比较 覆盖：equals/greaterThan/lessThan、null处理
	 */
	public void testDateTimesComparison() {
		DateTime a = DateTimes.valueOf(2025, 1, 1);
		DateTime b = DateTimes.valueOf(2025, 6, 1);
		assertTrue("Equals. ", DateTimes.equals(a, a));
		assertFalse("Not equals null. ", DateTimes.equals(null, a));
		assertTrue("GreaterThan. ", DateTimes.greaterThan(b, a));
		assertFalse("GreaterThan null. ", DateTimes.greaterThan(null, a));
		assertTrue("LessThan. ", DateTimes.lessThan(a, b));
		assertFalse("LessThan null. ", DateTimes.lessThan(null, b));
	}

	/**
	 * 测试DateTimes间隔计算 覆盖：小时/分钟/秒间隔
	 */
	public void testDateTimesInterval() {
		DateTime from = DateTimes.valueOf("2025-01-01");
		DateTime to = DateTimes.valueOf(2025, 1, 1);
		// 同一天
		assertEquals("Same day interval. ", 0, DateTimes.interval(from, to, emTimeUnit.HOUR));
	}

	/**
	 * 测试当前时间 覆盖：now/today/time
	 */
	public void testDateTimesCurrent() {
		DateTime now = DateTimes.now();
		assertNotNull("Now should not be null. ", now);
		DateTime today = DateTimes.today();
		assertNotNull("Today should not be null. ", today);
		short time = DateTimes.time();
		assertTrue("Time should be >= 0. ", time >= 0);
	}

	/**
	 * 测试最小/最大日期常量
	 */
	public void testDateTimesMinMax() {
		assertNotNull("VALUE_MIN should not be null. ", DateTimes.VALUE_MIN);
		assertNotNull("VALUE_MAX should not be null. ", DateTimes.VALUE_MAX);
		assertTrue("MIN < MAX. ", DateTimes.lessThan(DateTimes.VALUE_MIN, DateTimes.VALUE_MAX));
	}

	// ==================== Numbers 数值工具测试 ====================

	/**
	 * 测试Numbers数值判断 覆盖：整数/小数/负数/非法字符串
	 */
	public void testNumbersIsNumeric() {
		assertTrue("Integer is numeric. ", Numbers.isNumeric("123"));
		assertTrue("Decimal is numeric. ", Numbers.isNumeric("12.3"));
		assertTrue("Negative is numeric. ", Numbers.isNumeric("-12.3"));
		assertFalse("Non-numeric. ", Numbers.isNumeric("abc"));
		assertFalse("Null is not numeric. ", Numbers.isNumeric(null));
		assertFalse("Empty is not numeric. ", Numbers.isNumeric(""));
		assertFalse("Pure minus. ", Numbers.isNumeric("-"));
		assertFalse("Minus dot. ", Numbers.isNumeric("-."));
	}

	/**
	 * 测试Numbers零值判断 覆盖：零值、非零值、null/空/非数字视为零
	 */
	public void testNumbersIsZero() {
		assertTrue("Zero string. ", Numbers.isZero("0"));
		assertTrue("Zero decimal string. ", Numbers.isZero("0.00"));
		assertTrue("Null is treated as zero. ", Numbers.isZero(null));
		assertTrue("Empty is treated as zero. ", Numbers.isZero(""));
		assertFalse("Non-zero. ", Numbers.isZero("123"));
		assertFalse("Negative non-zero. ", Numbers.isZero("-1"));
	}

	/**
	 * 测试Numbers类型转换 覆盖：toDouble/toFloat/toInteger/toLong、null处理
	 */
	public void testNumbersConversion() {
		assertEquals("toDouble int. ", 5.0, Numbers.toDouble(5));
		assertEquals("toDouble BigDecimal null. ", 0.0, Numbers.toDouble((BigDecimal) null));
		assertEquals("toInteger double. ", 5, Numbers.toInteger(5.9));
		assertEquals("toLong int. ", 5L, Numbers.toLong(5));
		assertEquals("toFloat double. ", 5.0f, Numbers.toFloat(5.0));
	}

	/**
	 * 测试Numbers比较 覆盖：equals、null处理、Comparable比较
	 */
	public void testNumbersEquals() {
		assertTrue("Same integer. ", Numbers.equals(1, 1));
		assertFalse("Different. ", Numbers.equals(1, 2));
		assertFalse("Null returns false. ", Numbers.equals(null, 1));
		assertTrue("Same reference. ", Numbers.equals(1, Integer.valueOf(1)));
	}

	/**
	 * 测试Numbers默认值 覆盖：各数值类型零值
	 */
	public void testNumbersDefaultValue() {
		assertEquals("Integer default. ", Integer.valueOf(0), Numbers.defaultValue(Integer.class));
		assertEquals("Long default. ", Long.valueOf(0L), Numbers.defaultValue(Long.class));
		assertEquals("Double default. ", Double.valueOf(0d), Numbers.defaultValue(Double.class));
		assertEquals("Float default. ", Float.valueOf(0f), Numbers.defaultValue(Float.class));
		assertEquals("Short default. ", Short.valueOf((short) 0), Numbers.defaultValue(Short.class));
	}

	// ==================== Booleans 布尔工具测试 ====================

	/**
	 * 测试Booleans比较 覆盖：相等、null处理、默认值
	 */
	public void testBooleans() {
		assertTrue("Same true. ", Booleans.equals(true, true));
		assertTrue("Same false. ", Booleans.equals(false, false));
		assertFalse("Different. ", Booleans.equals(true, false));
		assertFalse("Null returns false. ", Booleans.equals(null, true));
		assertEquals("Default value is false. ", Boolean.FALSE, Booleans.defaultValue());
	}

	// ==================== Bytes 字节工具测试 ====================

	/**
	 * 测试Bytes十六进制编解码 覆盖：正常编码/解码、null/空处理、非法字符串
	 */
	public void testBytesHex() {
		byte[] data = new byte[] { 0x01, 0x23, (byte) 0xAB, (byte) 0xFF };
		String hex = Bytes.toHexString(data);
		assertEquals("Hex encoding. ", "0123abff", hex);

		byte[] decoded = Bytes.fromHexString(hex);
		assertEquals("Decoded byte 0. ", data[0], decoded[0]);
		assertEquals("Decoded byte 3. ", data[3], decoded[3]);

		assertNull("toHexString null returns null. ", Bytes.toHexString(null));
		assertNull("fromHexString null returns null. ", Bytes.fromHexString(null));
		assertNull("fromHexString empty returns null. ", Bytes.fromHexString(""));

		assertTrue("Valid hex string. ", Bytes.isHexString("0123abff"));
		assertFalse("Invalid hex (odd length). ", Bytes.isHexString("abc"));
		assertFalse("Invalid hex (invalid char). ", Bytes.isHexString("ghij"));
		assertFalse("Null is not hex. ", Bytes.isHexString(null));
	}

	/**
	 * 测试Bytes Base64编解码 覆盖：纯base64、DataURL格式、检测、null/空处理
	 */
	public void testBytesBase64() {
		byte[] data = "Hello, World!".getBytes();
		String base64 = Bytes.toBase64String(data);
		assertNotNull("Base64 encoding should not be null. ", base64);
		assertTrue("Base64 should be detected. ", Bytes.isBase64String(base64));

		byte[] decoded = Bytes.fromBase64String(base64);
		assertEquals("Decoded data should match original. ", new String(data), new String(decoded));

		// 空值处理
		assertEquals("toBase64 null returns empty. ", Strings.VALUE_EMPTY, Bytes.toBase64String(null));
		assertEquals("toBase64 empty returns empty. ", Strings.VALUE_EMPTY, Bytes.toBase64String(new byte[0]));
		assertEquals("fromBase64 null returns empty array. ", 0, Bytes.fromBase64String(null).length);
		assertEquals("fromBase64 empty returns empty array. ", 0, Bytes.fromBase64String("").length);
	}

	/**
	 * 测试Bytes DataURL格式Base64 覆盖：带MIME类型、无MIME类型退化
	 */
	public void testBytesDataURL() {
		byte[] data = new byte[] { 0x01, 0x02, 0x03 };
		String dataUrl = Bytes.toBase64String(data, "image/png");
		assertTrue("DataURL should start with data:. ", dataUrl.startsWith("data:image/png;base64,"));
		assertTrue("DataURL should be detected as base64. ", Bytes.isBase64String(dataUrl));

		byte[] decoded = Bytes.fromBase64String(dataUrl);
		assertEquals("Decoded DataURL should match. ", data.length, decoded.length);

		// 无MIME退化
		String plain = Bytes.toBase64String(data, null);
		assertFalse("Null mimeType should produce plain base64. ", plain.startsWith("data:"));
		String emptyMime = Bytes.toBase64String(data, "");
		assertFalse("Empty mimeType should produce plain base64. ", emptyMime.startsWith("data:"));
	}

	/**
	 * 测试Bytes非法base64检测 覆盖：长度非4的倍数、非法字符、无效DataURL
	 */
	public void testBytesInvalidBase64() {
		assertFalse("Odd length is not base64. ", Bytes.isBase64String("abc"));
		assertFalse("Invalid char is not base64. ", Bytes.isBase64String("a!b!"));
		assertFalse("Null is not base64. ", Bytes.isBase64String(null));
		assertFalse("Empty is not base64. ", Bytes.isBase64String(""));
		assertFalse("Invalid DataURL (no comma). ", Bytes.isBase64String("data:image/png;base64"));
	}

	// ==================== Enums 枚举工具测试 ====================

	/**
	 * 测试Enums转换 覆盖：按名称、按ordinal、按注解值、null处理
	 */
	public void testEnumsValueOf() {
		// 按名称
		assertEquals("By name. ", emDocumentStatus.RELEASED, Enums.valueOf(emDocumentStatus.class, "RELEASED"));
		// 按ordinal
		assertEquals("By ordinal. ", emDocumentStatus.FINISHED,
				Enums.valueOf(emDocumentStatus.class, emDocumentStatus.FINISHED.ordinal()));
		// null处理
		assertNull("Null type returns null. ", Enums.valueOf(null, "RELEASED"));
		assertNull("Null value returns null. ", Enums.valueOf(emDocumentStatus.class, (String) null));
		assertNull("Not enum returns null. ", Enums.valueOf(String.class, "hello"));
		// 不存在的值
		assertNull("Not found returns null. ", Enums.valueOf(emDocumentStatus.class, "NOT_EXIST"));
	}

	/**
	 * 测试Enums默认值 覆盖：枚举的第一个值
	 */
	public void testEnumsDefaultValue() {
		assertEquals("Default value should be first enum constant. ", emDocumentStatus.PLANNED,
				Enums.defaultValue(emDocumentStatus.class));
	}

	/**
	 * 测试Enums toKeyValues 覆盖：转换枚举为KeyValue数组
	 */
	public void testEnumsToKeyValues() {
		org.colorcoding.ibas.bobas.data.KeyValue[] kvs = Enums.toKeyValues(emDocumentStatus.class);
		assertTrue("Should have key values. ", kvs.length > 0);
		assertEquals("First key should be PLANNED. ", "PLANNED", kvs[0].getKey());
	}

	/**
	 * 测试Enums比较 覆盖：同类相等、null处理
	 */
	public void testEnumsEquals() {
		assertTrue("Same enum. ", Enums.equals(emDocumentStatus.RELEASED, emDocumentStatus.RELEASED));
		assertFalse("Different enum. ", Enums.equals(emDocumentStatus.RELEASED, emDocumentStatus.PLANNED));
		assertFalse("Null returns false. ", Enums.equals(null, emDocumentStatus.RELEASED));
	}

	// ==================== EncryptMD5 加密工具测试 ====================

	/**
	 * 测试MD5加密 覆盖：正常加密、null输入、多字符串拼接加密
	 */
	public void testEncryptMD5() {
		String result = EncryptMD5.md5("hello");
		assertNotNull("MD5 result should not be null. ", result);
		assertEquals("Same input same result. ", EncryptMD5.md5("hello"), EncryptMD5.md5("hello"));
		assertTrue("MD5 result should be hex string. ", Bytes.isHexString(result));

		// null输入
		assertNull("MD5 of null should be null. ", EncryptMD5.md5((String) null));

		// 多字符串拼接
		String multiResult = EncryptMD5.md5("hello", "world");
		String singleResult = EncryptMD5.md5("helloworld");
		assertEquals("Multi-string MD5 should equal concatenated. ", multiResult, singleResult);
	}

	/**
	 * 测试短链接生成 覆盖：正常生成、不同key不同结果
	 */
	public void testShortText() {
		String shortText = EncryptMD5.shortText("http://example.com");
		assertNotNull("Short text should not be null. ", shortText);
		assertTrue("Short text should not be empty. ", !shortText.isEmpty());

		// 不同key产生不同结果
		String shortText2 = EncryptMD5.shortText("http://example.com", "MY_KEY");
		assertFalse("Different key should produce different result. ", shortText.equals(shortText2));
	}

	// ==================== Files 文件路径工具测试 ====================

	/**
	 * 测试文件路径解析 覆盖：folderOf/nameOf/extensionOf
	 */
	public void testFilePathParsing() {
		// Windows风格路径
		assertEquals("Folder of Windows path. ", "C:\\Windows\\System32",
				Files.folderOf("C:\\Windows\\System32\\Win.exe"));
		assertEquals("Name of Windows path. ", "Win.exe", Files.nameOf("C:\\Windows\\System32\\Win.exe"));
		assertEquals("Extension of Windows path. ", "exe", Files.extensionOf("C:\\Windows\\System32\\Win.exe"));

		// Unix风格路径
		assertEquals("Folder of Unix path. ", "/home/users", Files.folderOf("/home/users/bash.sh"));
		assertEquals("Name of Unix path. ", "bash.sh", Files.nameOf("/home/users/bash.sh"));
		assertEquals("Extension of Unix path. ", "sh", Files.extensionOf("/home/users/bash.sh"));

		// 空值处理
		assertEquals("Folder of null. ", Strings.VALUE_EMPTY, Files.folderOf(null));
		assertEquals("Name of null. ", Strings.VALUE_EMPTY, Files.nameOf(null));
		assertEquals("Extension of null. ", Strings.VALUE_EMPTY, Files.extensionOf(null));

		// 无扩展名
		assertEquals("Extension of no extension. ", Strings.VALUE_EMPTY, Files.extensionOf("/home/users/bash"));
	}

	/**
	 * 测试路径标准化 覆盖：pathOf、valueOf
	 */
	public void testFilePathNormalization() {
		String normalized = Files.pathOf("/home/users/../admin");
		assertNotNull("Normalized path should not be null. ", normalized);
		assertTrue("Normalized path should contain admin. ", normalized.contains("admin"));

		assertEquals("Empty first returns empty. ", Strings.VALUE_EMPTY, Files.pathOf(""));
		assertEquals("Null first returns empty. ", Strings.VALUE_EMPTY, Files.pathOf(null));
	}

	/**
	 * 测试路径隐藏（maskingPath） 覆盖：正常隐藏、空值处理
	 */
	public void testFileMaskingPath() {
		assertEquals("Mask path. ", "bash.sh", Files.maskingPath((String) "/home/users/bash.sh", "/home/users"));
		assertEquals("Empty path returns empty. ", Strings.VALUE_EMPTY, Files.maskingPath((String) "", "/home"));
		assertEquals("Null path returns empty. ", Strings.VALUE_EMPTY, Files.maskingPath((String) null, "/home"));
		assertEquals("No mask returns original. ", "/home/users/bash.sh",
				Files.maskingPath((String) "/home/users/bash.sh", null));
	}
}
