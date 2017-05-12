package org.colorcoding.ibas.bobas.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.colorcoding.ibas.bobas.messages.RuntimeLog;

public class EncryptAES extends Encrypt {

	/**
	 * 加密
	 * 
	 * @param content
	 *            被加密的字符串
	 * @param key
	 *            密钥
	 * @return 密文
	 */
	public static String encrypt(String content, String key) {
		String rs = null;
		try {
			byte[] inBytes = toBytes(content);
			byte[] keyBytes = toBytes(key);
			SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");// "算法/模式/补码方式"
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(inBytes);
			rs = toHexString(encrypted);
		} catch (Exception e) {
			RuntimeLog.log(e);
		}
		return rs;
	}

	/**
	 * 解密
	 * 
	 * @param content
	 *            加密的内容
	 * @param key
	 *            密钥
	 * @return 明文
	 */
	public static String decrypt(String content, String key) {
		String rs = null;
		try {
			byte[] outBytes = toBytes(content);
			byte[] keyBytes = toBytes(key);
			SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");// "算法/模式/补码方式"
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] decBytes = cipher.doFinal(outBytes);
			rs = toHexString(decBytes);
		} catch (Exception e) {
			RuntimeLog.log(e);
		}
		return rs;
	}

}
