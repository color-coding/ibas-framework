package org.colorcoding.ibas.bobas.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.colorcoding.ibas.bobas.MyConfiguration;

public class Encrypt {
	// 十六进制下数字到字符的映射数组
	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D",
			"E", "F" };
	// 要使用生成URL的字符
	private final static String[] chars = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l",
			"m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6",
			"7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
			"S", "T", "U", "V", "W", "X", "Y", "Z" };

	/**
	 * 把inputString加密
	 * 
	 * @throws Exception
	 */
	public static String md5(String inputStr) throws Exception {
		return encodeByMD5(inputStr);
	}

	/**
	 * 把inputString组合并加密
	 * 
	 * @param inputStrs
	 *            字符串数组
	 * @return
	 * @throws Exception
	 */
	public static String md5(String... inputStrs) throws Exception {
		StringBuilder stringBuilder = new StringBuilder();
		for (String string : inputStrs) {
			stringBuilder.append(string);
		}
		return md5(stringBuilder.toString());
	}

	/**
	 * 对字符串进行MD5编码
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	private static String encodeByMD5(String originString) throws Exception {
		if (originString != null) {
			try {
				// 创建具有指定算法名称的信息摘要
				MessageDigest md5 = MessageDigest.getInstance("MD5");
				// 使用指定的字节数组对摘要进行最后更新，然后完成摘要计算
				byte[] results = md5.digest(originString.getBytes());
				// 将得到的字节数组变成字符串返回
				String result = byteArrayToHexString(results);
				return result;
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 轮换字节数组为十六进制字符串
	 * 
	 * @param b
	 *            字节数组
	 * @return 十六进制字符串
	 */
	private static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	// 将一个字节转化成十六进制形式的字符串
	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	/**
	 * 加密字符串
	 * 
	 * @param text
	 *            字符串
	 * @param chars
	 *            密文内容
	 * @param key
	 *            混淆字符
	 * @return
	 * @throws Exception
	 */
	public static String shortText(String text, String[] chars, String key) throws Exception {
		String hex = Encrypt.md5(key + text);
		int hexLen = hex.length();
		int subHexLen = hexLen / 8;
		String[] shortStr = new String[4];

		for (int i = 0; i < subHexLen; i++) {
			String outChars = "";
			int j = i + 1;
			String subHex = hex.substring(i * 8, j * 8);
			long idx = Long.valueOf("3FFFFFFF", 16) & Long.valueOf(subHex, 16);

			for (int k = 0; k < 2; k++) {
				int index = (int) (Long.valueOf("0000003D", 16) & idx);
				outChars += chars[index];
				idx = idx >> 5;
			}
			shortStr[i] = outChars;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < shortStr.length; i++) {
			if (shortStr[i] == null) {
				continue;
			}
			sb.append(shortStr[i]);
		}
		return sb.toString();
	}

	/**
	 * 加密字符串
	 * 
	 * @param text
	 *            字符串
	 * @param key
	 *            混淆字符
	 * @return
	 * @throws Exception
	 */
	public static String shortText(String text, String key) throws Exception {
		return shortText(text, chars, key);
	}

	/**
	 * 加密字符串
	 * 
	 * @param text
	 *            字符串
	 * @param chars
	 *            密文字符
	 * @return
	 * @throws Exception
	 */
	public static String shortText(String text, String[] chars) throws Exception {
		String key = MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_COMPANY_ID, "CC");
		return shortText(text, chars, key);
	}

	/**
	 * 加密字符串
	 * 
	 * @param text
	 *            字符串
	 * @return
	 * @throws Exception
	 */
	public static String shortText(String text) throws Exception {
		// 自定义生成MD5加密字符串前的混合KEY
		return shortText(text, chars);
	}

}
