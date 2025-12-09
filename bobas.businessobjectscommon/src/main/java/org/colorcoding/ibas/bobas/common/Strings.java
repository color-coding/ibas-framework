package org.colorcoding.ibas.bobas.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Iterator;

import org.colorcoding.ibas.bobas.data.DataConvert;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;
import org.colorcoding.ibas.bobas.serialization.ISerializer;
import org.colorcoding.ibas.bobas.serialization.SerializationException;
import org.colorcoding.ibas.bobas.serialization.SerializationFactory;

public class Strings {

	private Strings() {
	}

	static final String[] ALPHABETS = new String[] {
			// 符号（32 - 47）
			" ", "!", "\"", "#", "$", "%", "&", "'", "(", ")", "*", "+", ",", "-", ".", "/",
			// 数字（48 - 57）
			"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
			// 符号（58 - 54）
			":", ";", "<", "=", ">", "?", "@",
			// 大写字母（65 - 90）
			"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
			"V", "W", "X", "Y", "Z",
			// 符号（91 - 96）
			"[", "\\", "]", "^", "_", "`",
			// 小写字母（97 - 122）
			"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
			"v", "w", "x", "y", "z",
			// 符号（123 - 126）
			"{", "|", "}", "~",

	};

	/**
	 * 根据字母表返回字符
	 * 
	 * @param value 位置（[32, 126]）
	 * @return
	 */
	public static String alphabetOf(short value) {
		if (value == 0) {
			return VALUE_EMPTY;
		}
		if (value < 32 || value > 126) {
			throw new IndexOutOfBoundsException();
		}
		return ALPHABETS[value - 32];
	}

	public static String alphabetOf(int value) {
		return alphabetOf((short) value);
	}

	/**
	 * 字符：白值
	 */
	public static final String VALUE_EMPTY = "";
	/**
	 * 字符：0
	 */
	public static final String VALUE_ZERO = alphabetOf(48);
	/**
	 * 字符：1
	 */
	public static final String VALUE_ONE = alphabetOf(49);
	/**
	 * 字符：空格
	 */
	public static final String VALUE_SPACE = alphabetOf(32);
	/**
	 * 字符：,
	 */
	public static final String VALUE_COMMA = alphabetOf(44);
	/**
	 * 字符：;
	 */
	public static final String VALUE_SEMICOLON = alphabetOf(59);
	/**
	 * 字符：.
	 */
	public static final String VALUE_DOT = alphabetOf(46);
	/**
	 * 字符：/
	 */
	public static final String VALUE_SLASH = alphabetOf(47);
	/**
	 * 字符：\
	 */
	public static final String VALUE_BACKSLASH = alphabetOf(92);

	/**
	 * 类型默认值
	 * 
	 * @return
	 */
	public static String defaultValue() {
		return VALUE_EMPTY;
	}

	/**
	 * 判断字符串是否为空值
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isNullOrEmpty(String value) {
		if (value == null) {
			return true;
		}
		if (value.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * 字符串长度
	 * 
	 * @param value 字符串
	 * @return 长度; 空值为 -1
	 */
	public static int length(String value) {
		if (value == null) {
			return -1;
		}
		return value.length();
	}

	/**
	 * 截取字符串
	 * 
	 * @param value      字符串
	 * @param beginIndex 开始索引
	 * @param endIndex   结束索引
	 * @return
	 */
	public static String substring(String value, int beginIndex, int endIndex) {
		if (value == null) {
			return VALUE_EMPTY;
		}
		if (beginIndex < 0) {
			return VALUE_EMPTY;
		}
		if (endIndex > value.length()) {
			return VALUE_EMPTY;
		}
		return value.substring(beginIndex, endIndex);
	}

	/**
	 * 截取字符串
	 * 
	 * @param value 字符串
	 * @param count 截取长度
	 * @return
	 */
	public static String substring(String value, int count) {
		if (value == null) {
			return VALUE_EMPTY;
		}
		if (!(count > 1)) {
			return VALUE_EMPTY;
		}
		if (count > value.length()) {
			count = value.length();
		}
		return value.substring(0, count - 1);
	}

	/**
	 * 格式化字符串
	 * 
	 * @param value 模板
	 * @param args  参数
	 * @return
	 */
	public static String format(String value, Object... args) {
		if (value == null) {
			return VALUE_EMPTY;
		}
		return String.format(value, args);
	}

	/**
	 * 转为字符串
	 * 
	 * @param value
	 * @return
	 */
	public static String valueOf(Object value) {
		if (value == null) {
			return VALUE_EMPTY;
		}
		if (value.getClass() == String.class) {
			String nValue = String.valueOf(value);
			if (nValue.length() == 1) {
				char vChar = nValue.charAt(0);
				if (vChar >= 32 && vChar <= 127) {
					return alphabetOf(vChar);
				}
			}
			return nValue;
		} else if (value.getClass().isArray()) {
			StringBuilder builder = new StringBuilder();
			int length = Array.getLength(value);
			for (int i = 0; i < length; i++) {
				if (builder.length() > 0) {
					builder.append(DataConvert.DATA_SEPARATOR);
				}
				builder.append(Array.get(value, i));
			}
			return builder.toString();
		} else if (Iterable.class.isInstance(value)) {
			StringBuilder builder = new StringBuilder();
			Iterator<?> iterator = ((Iterable<?>) value).iterator();
			while (iterator.hasNext()) {
				if (builder.length() > 0) {
					builder.append(DataConvert.DATA_SEPARATOR);
				}
				builder.append(iterator.next());
			}
			return builder.toString();
		}
		return String.valueOf(value);
	}

	/**
	 * 是否相等
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean equals(Object a, Object b) {
		return equals(a, b, false);
	}

	/**
	 * 是否相等（忽略大小写）
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean equalsIgnoreCase(Object a, Object b) {
		return equals(a, b, true);
	}

	/**
	 * 是否相等
	 * 
	 * @param a
	 * @param b
	 * @param ignoreCase 忽略大小写
	 * @return
	 */
	public static boolean equals(Object a, Object b, boolean ignoreCase) {
		if (a == null || b == null) {
			return false;
		}
		if (a == b) {
			return true;
		}
		if (ignoreCase) {
			if (valueOf(a).equalsIgnoreCase(valueOf(b))) {
				return true;
			}
		} else {
			if (valueOf(a).equals(valueOf(b))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 字符串拼接
	 * 
	 * @param pars 待拼接内容
	 * @return
	 */
	public static String concat(String... pars) {
		if (pars.length > 0) {
			StringBuilder builder = new StringBuilder();
			for (String item : pars) {
				if (item == null) {
					continue;
				}
				builder.append(item);
			}
			return builder.toString();
		}
		return Strings.VALUE_EMPTY;
	}

	/**
	 * 字符串位置
	 * 
	 * @param content 字符串
	 * @param value   待查询内容
	 * @return
	 */
	public static int indexOf(String content, String value) {
		if (content == null) {
			return -1;
		}
		return content.indexOf(value);
	}

	/**
	 * 判断字符串开头结尾
	 * 
	 * @param content 字符串
	 * @param prefix  开头字符（空跳过）
	 * @param suffix  结尾字符（空跳过）
	 * @return
	 */
	public static boolean isWith(String content, String prefix, String suffix) {
		if (isNullOrEmpty(content)) {
			return false;
		}
		if (!isNullOrEmpty(prefix)) {
			if (!content.startsWith(prefix)) {
				return false;
			}
		}
		if (!isNullOrEmpty(suffix)) {
			if (!content.endsWith(suffix)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断字符串开头
	 * 
	 * @param content    字符串
	 * @param prefix     开头字符
	 * @param ignoreCase 忽略大小写
	 * @return
	 */
	public static boolean startsWith(String content, String prefix, boolean ignoreCase) {
		if (ignoreCase) {
			return startsWith(toUpperCase(content), toUpperCase(prefix));
		} else {
			return startsWith(content, prefix);
		}
	}

	/**
	 * 判断字符串开头
	 * 
	 * @param content 字符串
	 * @param prefix  开头字符
	 * @return
	 */
	public static boolean startsWith(String content, String prefix) {
		return isWith(content, prefix, null);
	}

	/**
	 * 判断字符串结尾
	 * 
	 * @param content    字符串
	 * @param suffix     结尾字符
	 * @param ignoreCase 忽略大小写
	 * @return
	 */
	public static boolean endsWith(String content, String suffix, boolean ignoreCase) {
		if (ignoreCase) {
			return endsWith(toUpperCase(content), toUpperCase(suffix));
		} else {
			return endsWith(content, suffix);
		}
	}

	/**
	 * 判断字符串结尾
	 * 
	 * @param content 字符串
	 * @param suffix  结尾字符
	 * @return
	 */
	public static boolean endsWith(String content, String suffix) {
		return isWith(content, null, suffix);
	}

	/**
	 * 使字符串开头为
	 * 
	 * @param content 字符串
	 * @param prefix  开头字符
	 * @return
	 */
	public static String makeStartsWith(String content, String prefix) {
		if (isNullOrEmpty(prefix)) {
			return content;
		}
		if (isNullOrEmpty(content)) {
			return prefix;
		}
		if (startsWith(content, prefix)) {
			return content;
		}
		return prefix + content;
	}

	/**
	 * 使字符串结尾为
	 * 
	 * @param content 字符串
	 * @param suffix  结尾字符
	 * @return
	 */
	public static String makeEndsWith(String content, String suffix) {
		if (isNullOrEmpty(suffix)) {
			return content;
		}
		if (isNullOrEmpty(content)) {
			return suffix;
		}
		if (endsWith(content, suffix)) {
			return content;
		}
		return content + suffix;
	}

	/**
	 * 替换字符
	 * 
	 * @param content   原字符串
	 * @param oldString 搜索内容
	 * @param newString 替换内容
	 */
	public static String replace(String content, String oldString, String newString) {
		if (isNullOrEmpty(content)) {
			return content;
		}
		if (isNullOrEmpty(oldString)) {
			return content;
		}
		StringBuilder builder = new StringBuilder(content);
		int index = builder.indexOf(oldString);
		while (index != -1) {
			builder.replace(index, index + oldString.length(), newString);
			index = builder.indexOf(oldString, index + newString.length());
		}
		return builder.toString();
	}

	/**
	 * 判断字符串是否为数字
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isNumeric(String value) {
		return Numbers.isNumeric(value);
	}

	/**
	 * 转为大字符
	 * 
	 * @param value
	 * @return
	 */
	public static String toUpperCase(String value) {
		if (value == null) {
			return null;
		}
		return value.toUpperCase();
	}

	/**
	 * 去除空格
	 * 
	 * @param value
	 * @return
	 */
	public static String trim(String value) {
		if (value == null) {
			return null;
		}
		return value.trim();
	}

	/**
	 * 转为小字符
	 * 
	 * @param value
	 * @return
	 */
	public static String toLowerCase(String value) {
		if (value == null) {
			return null;
		}
		return value.toLowerCase();
	}

	/**
	 * 转移字符串为16进制字符串
	 * 
	 * @param value
	 * @return
	 */
	public static String toHexString(String value) {
		if (value == null) {
			return VALUE_EMPTY;
		}
		return Bytes.toHexString(value.getBytes());
	}

	/**
	 * 选择非空白值（默认空白）
	 * 
	 * @param values 待选值
	 * @return
	 */
	public static String optionNonEmpty(String... values) {
		if (values == null) {
			return Strings.VALUE_EMPTY;
		}
		for (String value : values) {
			if (!isNullOrEmpty(value)) {
				return value;
			}
		}
		return Strings.VALUE_EMPTY;
	}

	/**
	 * 选择非空值（默认空白）
	 * 
	 * @param values 待选值
	 * @return
	 */
	public static String optionNonNull(String... values) {
		if (values == null) {
			return Strings.VALUE_EMPTY;
		}
		for (String value : values) {
			if (value != null) {
				return value;
			}
		}
		return Strings.VALUE_EMPTY;
	}

	/**
	 * 转为json字符串
	 * 
	 * @param data 数据
	 * @return
	 */
	public static String toJsonString(Object data) {
		// 首先使用内置序列化方式
		try (ByteArrayOutputStream writer = new ByteArrayOutputStream()) {
			ISerializer serializer = new SerializerJson();
			serializer.serialize(data, writer);
			return writer.toString();
		} catch (IOException e) {
			throw new SerializationException(e);
		} catch (SerializationException e) {
			Logger.log(MessageLevel.WARN, e);
			// 内置发生错误，则使用标准方式
			try (ByteArrayOutputStream writer = new ByteArrayOutputStream()) {
				ISerializer serializer = SerializationFactory.createManager().create(SerializationFactory.TYPE_JSON);
				serializer.serialize(data, writer);
				return writer.toString();
			} catch (IOException e1) {
				throw new SerializationException(e1);
			}
		}
	}

	/**
	 * 转为xml字符串
	 * 
	 * @param data
	 * @return
	 */
	public static String toXmlString(Object data) {
		// 首先使用内置序列化方式
		try (ByteArrayOutputStream writer = new ByteArrayOutputStream()) {
			ISerializer serializer = new SerializerXml();
			serializer.serialize(data, writer);
			return writer.toString();
		} catch (IOException e) {
			throw new SerializationException(e);
		} catch (SerializationException e) {
			Logger.log(MessageLevel.WARN, e);
			// 内置发生错误，则使用标准方式
			try (ByteArrayOutputStream writer = new ByteArrayOutputStream()) {
				ISerializer serializer = SerializationFactory.createManager().create(SerializationFactory.TYPE_XML);
				serializer.serialize(data, writer);
				return writer.toString();
			} catch (IOException e1) {
				throw new SerializationException(e1);
			}
		}
	}
}
