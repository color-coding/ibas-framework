package org.colorcoding.ibas.bobas.i18n;

/**
 * 多语言
 * 
 * @author Niuren.Zhu
 *
 */
public class I18N {

	private I18N() {
	}

	private volatile static LanguageItemManager instance;

	public static LanguageItemManager getInstance() {
		if (instance == null) {
			synchronized (I18N.class) {
				if (instance == null) {
					instance = new LanguageItemManager();
					instance.readResources();
				}
			}
		}
		return instance;
	}

	/**
	 * 获取key所对应的值
	 * 
	 * @param key  需要翻译的文本
	 * @param args 替换值
	 * @return 返回key所对应的值
	 */
	public static String prop(String key, Object... args) {
		return getInstance().getContent(key, args);
	}

}
