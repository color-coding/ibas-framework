package org.colorcoding.ibas.bobas.common;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Bytes {

	private Bytes() {
	}

	/**
	 * 字符串转字节（UTF-8）
	 * 
	 * @param value
	 * @return
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
	 * 16进制字符串转为字节数组
	 *
	 * @param hexString
	 * @return
	 */
	public static byte[] fromHexString(String hexString) {
		if (hexString == null || hexString.isEmpty()) {
			return null;
		}
		if (hexString.length() % 2 != 0) {
			throw new IllegalArgumentException("hex string must have even length.");
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
	 * 字节数组转为16进制字符串
	 * 
	 * @param bytes
	 * @return
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
	 * 字节数组转为base64字符串
	 * 
	 * @param bytes
	 * @return
	 */
	public static String toBase64String(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return Strings.VALUE_EMPTY;
		}
		return Base64.getEncoder().encodeToString(bytes);
	}

	/**
	 * 字节数组转为base64字符串
	 * 
	 * @param bytes
	 * @param mimeType (未知：application/octet-stream)
	 * @return
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
	 * base64字符串转为字节数组
	 * 
	 * @param base64String
	 * @return
	 */
	public static byte[] fromBase64String(String base64String) {
		if (Strings.isNullOrEmpty(base64String)) {
			return new byte[0];
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
