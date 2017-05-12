package org.colorcoding.ibas.bobas.test.util;

import org.colorcoding.ibas.bobas.util.EncryptAES;

import junit.framework.TestCase;

public class testEncrypt extends TestCase {

	public void testEncryptAES() {
		String key = EncryptAES.toHexString("cc!iBAS==>*7788~");
		String data = EncryptAES.toHexString("i'm niuren.");
		String eData = EncryptAES.encrypt(data, key);
		System.out.println(eData);
		String dData = EncryptAES.decrypt(eData, key);
		System.out.println(dData);

	}
}
