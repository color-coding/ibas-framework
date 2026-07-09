package org.colorcoding.ibas.bobas.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Iterator;

import org.colorcoding.ibas.bobas.serialization.ISerializer;
import org.colorcoding.ibas.bobas.serialization.SerializationException;
import org.colorcoding.ibas.bobas.serialization.SerializationFactory;

public class Strings {

	private Strings() {
	}

	public static final String[] ALPHABETS = new String[] {
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
	 * @param value ASCII码（0返回空字符串；范围[32,126]，越界抛IllegalArgumentException）
	 * @return 对应字符
	 */
	public static String alphabetOf(short value) {
		if (value == 0) {
			return VALUE_EMPTY;
		}
		if (value < 32 || value > 126) {
			throw new IllegalArgumentException(Strings.format("ascii value [%d] is out of range [32, 126].", value));
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
	 * 字符：*
	 */
	public static final String VALUE_ASTERISK = alphabetOf(42);

	/**
	 * 类型默认值
	 *
	 * @return 空字符串
	 */
	public static String defaultValue() {
		return VALUE_EMPTY;
	}

	/**
	 * 判断字符串是否为空值
	 *
	 * @param value 字符串
	 * @return null或空字符串返回true
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
	 * 索引越界时返回空字符串，不抛异常
	 *
	 * @param value      字符串；null返回空字符串
	 * @param beginIndex 开始索引
	 * @param endIndex   结束索引
	 * @return 截取结果；无效输入返回空字符串
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
	 * 截取字符串（从起始位置）
	 *
	 * count必须大于1；超过字符串长度时自动调整
	 *
	 * @param value 字符串；null返回空字符串
	 * @param count 截取长度；不大于1时返回空字符串
	 * @return 截取结果
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
		return value.substring(0, count);
	}

	/**
	 * 格式化字符串
	 *
	 * null模板返回空字符串；无参数返回模板本身
	 *
	 * @param value 模板；null返回空字符串
	 * @param args  格式参数
	 * @return 格式化结果
	 */
	public static String format(String value, Object... args) {
		if (value == null) {
			return VALUE_EMPTY;
		}
		if (args != null && args.length > 0) {
			return String.format(value, args);
		} else {
			return value;
		}
	}

	/**
	 * 转为字符串
	 *
	 * 数组/集合以逗号拼接；null返回空字符串
	 * <p>
	 * 字符串归一：空串归一到{@link #VALUE_EMPTY}，单字符归一到{@link #ALPHABETS}缓存
	 *
	 * @param value 对象；null返回空字符串
	 * @return 字符串表示
	 */
	public static String valueOf(Object value) {
		if (value == null) {
			return VALUE_EMPTY;
		}
		if (value.getClass() == String.class) {
			String nValue = (String) value;
			// 空字符串归一
			if (nValue.isEmpty()) {
				return VALUE_EMPTY;
			}
			// 单字符归一到字母表缓存
			if (nValue.length() == 1) {
				char vChar = nValue.charAt(0);
				if (vChar >= 32 && vChar <= 127) {
					return alphabetOf(vChar);
				}
			}
			return nValue;
		} else if (value.getClass().isArray()) {
			Object tmpVlue = null;
			StringBuilder builder = new StringBuilder();
			int length = Array.getLength(value);
			for (int i = 0; i < length; i++) {
				if (builder.length() > 0) {
					builder.append(VALUE_COMMA);
				}
				tmpVlue = Array.get(value, i);
				if (tmpVlue == null) {
					continue;
				}
				builder.append(tmpVlue);
			}
			return builder.toString();
		} else if (Iterable.class.isInstance(value)) {
			Object tmpVlue = null;
			StringBuilder builder = new StringBuilder();
			Iterator<?> iterator = ((Iterable<?>) value).iterator();
			while (iterator.hasNext()) {
				if (builder.length() > 0) {
					builder.append(VALUE_COMMA);
				}
				tmpVlue = iterator.next();
				if (tmpVlue == null) {
					continue;
				}
				builder.append(tmpVlue);
			}
			return builder.toString();
		}
		return String.valueOf(value);
	}

	/**
	 * 是否相等
	 *
	 * @param a 对象a
	 * @param b 对象b
	 * @return 相等返回true；任一为null返回false
	 */
	public static boolean equals(Object a, Object b) {
		return equals(a, b, false);
	}

	/**
	 * 是否相等（忽略大小写）
	 *
	 * @param a 对象a
	 * @param b 对象b
	 * @return 相等返回true；任一为null返回false
	 */
	public static boolean equalsIgnoreCase(Object a, Object b) {
		return equals(a, b, true);
	}

	/**
	 * 是否相等
	 *
	 * 任一参数为null返回false
	 *
	 * @param a          对象a
	 * @param b          对象b
	 * @param ignoreCase 忽略大小写
	 * @return 相等返回true；任一为null返回false
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
	 * null项自动跳过
	 *
	 * @param pars 待拼接内容
	 * @return 拼接结果；无有效内容返回空字符串
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
	 * @param content 字符串；null返回-1
	 * @param value   待查询内容；null返回-1
	 * @return 索引位置；未找到或输入null返回-1
	 */
	public static int indexOf(String content, String value) {
		if (content == null) {
			return -1;
		}
		if (value == null) {
			return -1;
		}
		return content.indexOf(value);
	}

	/**
	 * 判断字符串开头结尾
	 *
	 * @param content 字符串；空返回false
	 * @param prefix  开头字符；空值跳过比较
	 * @param suffix  结尾字符；空值跳过比较
	 * @return 同时满足返回true
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
	 * @param content    字符串；空返回false
	 * @param prefix     开头字符
	 * @param ignoreCase 忽略大小写
	 * @return 匹配返回true
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
	 * @param content 字符串；空返回false
	 * @param prefix  开头字符
	 * @return 匹配返回true
	 */
	public static boolean startsWith(String content, String prefix) {
		return isWith(content, prefix, null);
	}

	/**
	 * 判断字符串结尾
	 *
	 * @param content    字符串；空返回false
	 * @param suffix     结尾字符
	 * @param ignoreCase 忽略大小写
	 * @return 匹配返回true
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
	 * @param content 字符串；空返回false
	 * @param suffix  结尾字符
	 * @return 匹配返回true
	 */
	public static boolean endsWith(String content, String suffix) {
		return isWith(content, null, suffix);
	}

	/**
	 * 确保字符串以指定前缀开头；已有则原样返回，否则拼接前缀
	 *
	 * @param content 字符串
	 * @param prefix  期望前缀；空值返回原字符串
	 * @return 处理后的字符串
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
	 * 确保字符串以指定后缀结尾；已有则原样返回，否则拼接后缀
	 *
	 * @param content 字符串
	 * @param suffix  期望后缀；空值返回原字符串
	 * @return 处理后的字符串
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
	 * content或oldString为空时返回原字符串
	 *
	 * @param content   原字符串
	 * @param oldString 搜索内容；空值不替换
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
	 * @param value 字符串；null或空返回false
	 * @return 有效数字格式返回true
	 */
	public static boolean isNumeric(String value) {
		return Numbers.isNumeric(value);
	}

	/**
	 * 转为大写字符
	 *
	 * @param value 字符串；null返回null
	 * @return 大写字符串
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
	 * @param value 字符串；null返回null
	 * @return 去空格结果
	 */
	public static String trim(String value) {
		if (value == null) {
			return null;
		}
		return value.trim();
	}

	/**
	 * 转为小写字符
	 *
	 * @param value 字符串；null返回null
	 * @return 小写字符串
	 */
	public static String toLowerCase(String value) {
		if (value == null) {
			return null;
		}
		return value.toLowerCase();
	}

	/**
	 * 分割字符串
	 * 
	 * @param content 内容
	 * @param regex   分割字符
	 * @return
	 */
	public static String[] split(String content, String regex) {
		return isNullOrEmpty(content) ? new String[] {} : content.split(regex);
	}

	/**
	 * 分割字符串（,）
	 * 
	 * @param content 内容
	 * @return
	 */
	public static String[] split(String content) {
		return split(content, VALUE_COMMA);
	}

	/**
	 * 转换字符串为16进制字符串
	 *
	 * @param value 字符串；null返回空字符串
	 * @return 小写16进制字符串
	 */
	public static String toHexString(String value) {
		if (value == null) {
			return VALUE_EMPTY;
		}
		return Bytes.toHexString(Bytes.valueOf(value));
	}

	/**
	 * 转换字符串为base64字符串（UTF-8编码）
	 *
	 * @param value 字符串；null返回空字符串
	 * @return base64编码字符串
	 */
	public static String toBase64String(String value) {
		if (value == null) {
			return VALUE_EMPTY;
		}
		return Bytes.toBase64String(Bytes.valueOf(value));
	}

	/**
	 * 选择非空白值
	 *
	 * @param values 待选值；null返回空字符串
	 * @return 第一个非空白值；全部空白返回空字符串
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
	 * 选择非空值
	 *
	 * @param values 待选值；null返回空字符串
	 * @return 第一个非null值；全部为null返回空字符串
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
	 * @param data 数据对象
	 * @return JSON字符串；序列化失败抛出SerializationException
	 */
	public static String toJsonString(Object data) {
		try (ByteArrayOutputStream writer = new ByteArrayOutputStream(256)) {
			ISerializer serializer = SerializationFactory.createManager().create(SerializationFactory.TYPE_JSON);
			serializer.serialize(data, writer);
			return writer.toString("UTF-8");
		} catch (IOException e) {
			throw new SerializationException(e.getMessage(), e);
		}
	}

	/**
	 * 转为xml字符串
	 *
	 * @param data 数据对象
	 * @return XML字符串；序列化失败抛出SerializationException
	 */
	public static String toXmlString(Object data) {
		try (ByteArrayOutputStream writer = new ByteArrayOutputStream(512)) {
			ISerializer serializer = SerializationFactory.createManager().create(SerializationFactory.TYPE_XML);
			serializer.serialize(data, writer);
			return writer.toString("UTF-8");
		} catch (IOException e) {
			throw new SerializationException(e.getMessage(), e);
		}
	}

	// ==================== 判断 ====================

	/**
	 * 判断字符串是否为空白（null、空串或仅含空白字符）
	 *
	 * @param value 字符串
	 * @return 为空白返回true
	 */
	public static boolean isBlank(String value) {
		if (value == null || value.isEmpty()) {
			return true;
		}
		for (int i = 0; i < value.length(); i++) {
			if (!Character.isWhitespace(value.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断字符串是否非空白
	 *
	 * @param value 字符串
	 * @return 非空白返回true
	 */
	public static boolean isNotBlank(String value) {
		return !isBlank(value);
	}

	/**
	 * 判断字符串是否包含指定子串
	 *
	 * @param content 字符串；null返回false
	 * @param value   子串；null返回false
	 * @return 包含返回true
	 */
	public static boolean contains(String content, String value) {
		if (content == null || value == null) {
			return false;
		}
		return content.contains(value);
	}

	/**
	 * 判断字符串是否包含指定子串（忽略大小写）
	 *
	 * @param content 字符串；null返回false
	 * @param value   子串；null返回false
	 * @return 包含返回true
	 */
	public static boolean containsIgnoreCase(String content, String value) {
		if (content == null || value == null) {
			return false;
		}
		return content.toUpperCase().contains(value.toUpperCase());
	}

	// ==================== 处理 ====================

	/**
	 * 去除两端空白；null安全版
	 *
	 * @param value 字符串
	 * @return 去空白结果；null返回空字符串
	 */
	public static String trimToEmpty(String value) {
		if (value == null) {
			return VALUE_EMPTY;
		}
		return value.trim();
	}

	/**
	 * 去除两端空白；null返回null
	 *
	 * @param value 字符串
	 * @return 去空白结果；空白返回null
	 */
	public static String trimToNull(String value) {
		String result = trim(value);
		if (result == null || result.isEmpty()) {
			return null;
		}
		return result;
	}

	/**
	 * 去除前缀；存在则去除，否则原样返回
	 *
	 * @param content 字符串；null返回null
	 * @param prefix  前缀；空值不处理
	 * @return 去除前缀后的字符串
	 */
	public static String removeStart(String content, String prefix) {
		if (isNullOrEmpty(content) || isNullOrEmpty(prefix)) {
			return content;
		}
		if (content.startsWith(prefix)) {
			return content.substring(prefix.length());
		}
		return content;
	}

	/**
	 * 去除后缀；存在则去除，否则原样返回
	 *
	 * @param content 字符串；null返回null
	 * @param suffix  后缀；空值不处理
	 * @return 去除后缀后的字符串
	 */
	public static String removeEnd(String content, String suffix) {
		if (isNullOrEmpty(content) || isNullOrEmpty(suffix)) {
			return content;
		}
		if (content.endsWith(suffix)) {
			return content.substring(0, content.length() - suffix.length());
		}
		return content;
	}

	/**
	 * 重复字符串
	 *
	 * @param value  字符串；null或空返回空字符串
	 * @param repeat 重复次数；小于1返回空字符串
	 * @return 重复后的字符串
	 */
	public static String repeat(String value, int repeat) {
		if (isNullOrEmpty(value) || repeat < 1) {
			return VALUE_EMPTY;
		}
		StringBuilder builder = new StringBuilder(value.length() * repeat);
		for (int i = 0; i < repeat; i++) {
			builder.append(value);
		}
		return builder.toString();
	}

	/**
	 * 左填充到指定长度
	 *
	 * @param value   字符串；null视为空字符串
	 * @param size    目标长度；小于原长度时不填充
	 * @param padChar 填充字符
	 * @return 填充后的字符串
	 */
	public static String padLeft(String value, int size, char padChar) {
		if (value == null) {
			value = VALUE_EMPTY;
		}
		if (value.length() >= size) {
			return value;
		}
		StringBuilder builder = new StringBuilder(size);
		for (int i = value.length(); i < size; i++) {
			builder.append(padChar);
		}
		builder.append(value);
		return builder.toString();
	}

	/**
	 * 右填充到指定长度
	 *
	 * @param value   字符串；null视为空字符串
	 * @param size    目标长度；小于原长度时不填充
	 * @param padChar 填充字符
	 * @return 填充后的字符串
	 */
	public static String padRight(String value, int size, char padChar) {
		if (value == null) {
			value = VALUE_EMPTY;
		}
		if (value.length() >= size) {
			return value;
		}
		StringBuilder builder = new StringBuilder(size);
		builder.append(value);
		for (int i = value.length(); i < size; i++) {
			builder.append(padChar);
		}
		return builder.toString();
	}

	/**
	 * 截取字符串最大长度；超过时追加省略标记
	 *
	 * @param value  字符串；null返回空字符串
	 * @param maxLen 最大长度；小于1返回空字符串
	 * @return 截取结果
	 */
	public static String ellipsis(String value, int maxLen) {
		return ellipsis(value, maxLen, "...");
	}

	/**
	 * 截取字符串最大长度；超过时追加省略标记
	 *
	 * @param value  字符串；null返回空字符串
	 * @param maxLen 最大长度（含省略标记）；小于1返回空字符串
	 * @param suffix 省略标记
	 * @return 截取结果
	 */
	public static String ellipsis(String value, int maxLen, String suffix) {
		if (isNullOrEmpty(value) || maxLen < 1) {
			return VALUE_EMPTY;
		}
		if (value.length() <= maxLen) {
			return value;
		}
		if (isNullOrEmpty(suffix) || maxLen <= suffix.length()) {
			return value.substring(0, maxLen);
		}
		return value.substring(0, maxLen - suffix.length()) + suffix;
	}

	/**
	 * 首字母大写
	 *
	 * @param value 字符串；null或空返回原值
	 * @return 首字母大写的字符串
	 */
	public static String capitalize(String value) {
		if (isNullOrEmpty(value)) {
			return value;
		}
		char first = value.charAt(0);
		if (Character.isUpperCase(first)) {
			return value;
		}
		return Character.toUpperCase(first) + value.substring(1);
	}

	/**
	 * 首字母小写
	 *
	 * @param value 字符串；null或空返回原值
	 * @return 首字母小写的字符串
	 */
	public static String uncapitalize(String value) {
		if (isNullOrEmpty(value)) {
			return value;
		}
		char first = value.charAt(0);
		if (Character.isLowerCase(first)) {
			return value;
		}
		return Character.toLowerCase(first) + value.substring(1);
	}

	/**
	 * 驼峰转下划线（myName → my_name）
	 *
	 * @param value 字符串；null返回null
	 * @return 下划线格式字符串
	 */
	public static String toUnderscoreCase(String value) {
		if (isNullOrEmpty(value)) {
			return value;
		}
		StringBuilder builder = new StringBuilder(value.length() * 2);
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (Character.isUpperCase(c)) {
				if (i > 0) {
					builder.append('_');
				}
				builder.append(Character.toLowerCase(c));
			} else {
				builder.append(c);
			}
		}
		return builder.toString();
	}

	/**
	 * 下划线转驼峰（my_name → myName）
	 *
	 * @param value 字符串；null返回null
	 * @return 驼峰格式字符串
	 */
	public static String toCamelCase(String value) {
		if (isNullOrEmpty(value)) {
			return value;
		}
		StringBuilder builder = new StringBuilder(value.length());
		boolean nextUpper = false;
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (c == '_') {
				nextUpper = true;
			} else {
				if (nextUpper) {
					builder.append(Character.toUpperCase(c));
					nextUpper = false;
				} else {
					builder.append(c);
				}
			}
		}
		return builder.toString();
	}

	/**
	 * 用分隔符拼接数组/集合
	 *
	 * @param separator 分隔符；null视为空字符串
	 * @param values    待拼接内容；null返回空字符串
	 * @return 拼接结果
	 */
	public static String join(String separator, Object... values) {
		if (values == null || values.length == 0) {
			return VALUE_EMPTY;
		}
		String sep = separator != null ? separator : VALUE_EMPTY;
		StringBuilder builder = new StringBuilder();
		for (Object item : values) {
			if (item == null) {
				continue;
			}
			if (builder.length() > 0) {
				builder.append(sep);
			}
			builder.append(item);
		}
		return builder.toString();
	}

	/**
	 * 限制字符串最大长度；超过时截取，不追加省略标记
	 *
	 * @param value  字符串；null返回空字符串
	 * @param maxLen 最大长度；小于1返回空字符串
	 * @return 截取结果
	 */
	public static String truncate(String value, int maxLen) {
		if (isNullOrEmpty(value) || maxLen < 1) {
			return VALUE_EMPTY;
		}
		if (value.length() <= maxLen) {
			return value;
		}
		return value.substring(0, maxLen);
	}

	/**
	 * 计算子串出现次数
	 *
	 * @param content 字符串；null返回0
	 * @param value   子串；null或空返回0
	 * @return 出现次数
	 */
	public static int countOf(String content, String value) {
		if (isNullOrEmpty(content) || isNullOrEmpty(value)) {
			return 0;
		}
		int count = 0;
		int index = 0;
		while ((index = content.indexOf(value, index)) != -1) {
			count++;
			index += value.length();
		}
		return count;
	}

	/**
	 * 反转字符串
	 *
	 * @param value 字符串；null返回null
	 * @return 反转后的字符串
	 */
	public static String reverse(String value) {
		if (value == null) {
			return null;
		}
		return new StringBuilder(value).reverse().toString();
	}
}
