package org.colorcoding.ibas.bobas.common;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Bytes {

	private Bytes() {
	}

	/**
	 * 字符串转字节数组（UTF-8编码）
	 *
	 * @param value 输入字符串，为null时返回空数组
	 * @return UTF-8编码的字节数组；null输入返回空数组而非null
	 */
	public static byte[] valueOf(String value) {
		if (value == null) {
			return new byte[] {};
		}
		return value.getBytes(StandardCharsets.UTF_8);
	}

	private static byte charToByte(char c) {
		int index = "0123456789ABCDEF".indexOf(c);
		if (index < 0) {
			throw new IllegalArgumentException(Strings.format("invalid hex character [%s].", c));
		}
		return (byte) index;
	}

	/**
	 * 判断字符串是否为有效的16进制字符串
	 *
	 * @param value 待检测字符串
	 * @return true表示字符串非空、长度为偶数、仅包含0-9/A-F/a-f字符
	 */
	public static boolean isHexString(String value) {
		if (value == null || value.isEmpty() || value.length() % 2 != 0) {
			return false;
		}
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (!((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f'))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 16进制字符串转为字节数组
	 *
	 * @param hexString 16进制字符串，每两个字符表示一个字节
	 * @return 字节数组；null或空字符串输入返回null；非法格式抛出IllegalArgumentException
	 */
	public static byte[] fromHexString(String hexString) {
		if (hexString == null || hexString.isEmpty()) {
			return null;
		}
		if (!isHexString(hexString)) {
			throw new IllegalArgumentException("invalid hex string.");
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * 字节数组转为16进制字符串（小写）
	 *
	 * @param bytes 字节数组
	 * @return 小写16进制字符串；null或空数组输入返回null
	 */
	public static String toHexString(byte[] bytes) {
		StringBuilder stringBuilder = new StringBuilder();
		if (bytes == null || bytes.length <= 0) {
			return null;
		}
		for (int i = 0; i < bytes.length; i++) {
			int v = bytes[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * 判断字符串是否为有效的base64字符串（含DataURL格式）
	 *
	 * @param value 待检测字符串，支持纯base64及DataURL（data:xxx;base64,...）格式
	 * @return true表示字符串非空、剥离DataURL前缀后仅包含base64合法字符且长度为4的倍数
	 */
	public static boolean isBase64String(String value) {
		if (Strings.isNullOrEmpty(value)) {
			return false;
		}
		// 剥离 DataURL 前缀
		String pureBase64 = value;
		if (pureBase64.startsWith("data:")) {
			int idx = pureBase64.indexOf(',');
			if (idx < 0) {
				return false;
			}
			pureBase64 = pureBase64.substring(idx + 1);
		}
		if (pureBase64.isEmpty()) {
			return false;
		}
		// 长度必须是4的倍数
		if (pureBase64.length() % 4 != 0) {
			return false;
		}
		// 末尾的 '=' 填充最多2个
		int padStart = pureBase64.length();
		if (pureBase64.charAt(pureBase64.length() - 1) == '=') {
			padStart = pureBase64.length() - 1;
			if (pureBase64.charAt(pureBase64.length() - 2) == '=') {
				padStart = pureBase64.length() - 2;
			}
		}
		// 有效字符部分只能包含 A-Z, a-z, 0-9, +, /
		for (int i = 0; i < padStart; i++) {
			char c = pureBase64.charAt(i);
			if (!((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '+' || c == '/')) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 字节数组转为base64字符串
	 *
	 * @param bytes 字节数组
	 * @return base64编码字符串；null或空数组输入返回空字符串
	 */
	public static String toBase64String(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return Strings.VALUE_EMPTY;
		}
		return Base64.getEncoder().encodeToString(bytes);
	}

	/**
	 * 字节数组转为DataURL格式的base64字符串
	 *
	 * @param bytes    字节数组
	 * @param mimeType MIME类型，如image/png；为null或空时退化为纯base64输出
	 * @return DataURL格式字符串（data:{mimeType};base64,{data}），或纯base64字符串
	 */
	public static String toBase64String(byte[] bytes, String mimeType) {
		if (Strings.isNullOrEmpty(mimeType)) {
			return toBase64String(bytes);
		}
		StringBuilder builder = new StringBuilder();
		builder.append("data:");
		builder.append(mimeType);
		builder.append(";base64");
		builder.append(",");
		builder.append(toBase64String(bytes));
		return builder.toString();
	}

	/**
	 * base64字符串转为字节数组（支持DataURL格式）
	 *
	 * @param base64String 纯base64字符串或DataURL格式字符串
	 * @return 解码后的字节数组；null或空字符串输入返回空数组；非法格式抛出IllegalArgumentException
	 */
	public static byte[] fromBase64String(String base64String) {
		if (Strings.isNullOrEmpty(base64String)) {
			return new byte[0];
		}
		if (!isBase64String(base64String)) {
			throw new IllegalArgumentException("invalid base64 string.");
		}
		// 剥离 DataURL 前缀
		String pureBase64 = base64String;
		if (pureBase64.startsWith("data:")) {
			int idx = pureBase64.indexOf(',');
			if (idx > 0) {
				pureBase64 = pureBase64.substring(idx + 1);
			}
		}
		return Base64.getDecoder().decode(pureBase64);
	}
}