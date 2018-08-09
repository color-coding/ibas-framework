package org.colorcoding.ibas.bobas.test.util;

import org.colorcoding.ibas.bobas.util.Encrypt;

import junit.framework.TestCase;

public class TestEncrypt extends TestCase {

	public void testHexString() {
		String key = "cc!iBAS==>*7788~";
		String hKey = Encrypt.toHexString(key);
		System.out.println(String.format("%s > %s", key, hKey));
		String cKey = new String(Encrypt.toBytes(hKey));
		assertEquals("Hex faild.", key, cKey);
		System.out.println(String.format("~ %s", cKey));

		String data = "I'm niuren.";
		String hData = Encrypt.toHexString(data);
		String cData = new String(Encrypt.toBytes(hData));
		System.out.println(String.format("%s > %s", data, hData));
		assertEquals("Hex faild.", data, cData);
		System.out.println(String.format("~ %s", cData));

	}
}
