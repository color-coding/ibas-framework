package org.colorcoding.ibas.bobas.i18n;

/**
 * 多语言
 * 
 * @author Niuren.Zhu
 *
 */
public class i18n {
	private i18n() {
	}

	private volatile static ILanguageItemManager instance;

	public static ILanguageItemManager getInstance() {
		if (instance == null) {
			synchronized (i18n.class) {
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
	 * @param key
	 *            需要翻译的文本
	 * @param args
	 *            有效值
	 * @return 返回key所对应的值
	 */
	public static String prop(String key, Object... args) {
		return getInstance().getContent(key, args);
	}

}
