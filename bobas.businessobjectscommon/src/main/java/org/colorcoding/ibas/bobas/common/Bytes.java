package org.colorcoding.ibas.bobas.common;

import java.nio.charset.StandardCharsets;

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

	/**
	 * 字符转字节
	 * 
	 * @param c
	 * @return
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
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
	 * 转移字节数组为16进制字符串
	 * 
	 * @param src
	 * @return
	 */
	public static String toHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}
}
