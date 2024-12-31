package org.colorcoding.ibas.bobas.i18n;

import org.colorcoding.ibas.bobas.common.Strings;

/**
 * 多语言
 * 
 * @author Niuren.Zhu
 *
 */
public class I18N {
	private I18N() {
	}

	/**
	 * 获取key所对应的值
	 * 
	 * @param key  需要翻译的文本
	 * @param args 有效值
	 * @return 返回key所对应的值
	 */
	public static String prop(String key, Object... args) {
		return Strings.format(key, args);
	}

}
