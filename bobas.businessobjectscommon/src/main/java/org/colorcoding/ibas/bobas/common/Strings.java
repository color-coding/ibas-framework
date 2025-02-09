package org.colorcoding.ibas.bobas.common;

import java.lang.reflect.Array;
import java.util.Iterator;

public class Strings {

	private Strings() {
	}

	public static final String VALUE_ZERO = "0";
	public static final String VALUE_ONE = "1";
	public static final String VALUE_EMPTY = "";
	public static final String VALUE_SPACE = " ";
	public static final String VALUE_DATA_SEPARATOR = ",";

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
		if (value.getClass().isArray()) {
			StringBuilder builder = new StringBuilder();
			int length = Array.getLength(value);
			for (int i = 0; i < length; i++) {
				if (builder.length() > 0) {
					builder.append(VALUE_DATA_SEPARATOR);
				}
				builder.append(Array.get(value, i));
			}
			return builder.toString();
		} else if (Iterable.class.isInstance(value)) {
			StringBuilder builder = new StringBuilder();
			Iterator<?> iterator = ((Iterable<?>) value).iterator();
			while (iterator.hasNext()) {
				if (builder.length() > 0) {
					builder.append(VALUE_DATA_SEPARATOR);
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
		if (a == null || b == null) {
			return false;
		}
		if (a == b) {
			return true;
		}
		if (valueOf(a).equals(valueOf(b))) {
			return true;
		}
		return false;
	}

	/**
	 * 是否相等（忽略大小写）
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean equalsIgnoreCase(Object a, Object b) {
		if (a == null || b == null) {
			return false;
		}
		if (a == b) {
			return true;
		}
		if (valueOf(a).equalsIgnoreCase(valueOf(b))) {
			return true;
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
	 * 转移字符串为16进制字符串
	 * 
	 * @param src
	 * @return
	 */
	public static String toHexString(String src) {
		return Bytes.toHexString(src.getBytes());
	}

}
