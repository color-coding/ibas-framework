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
	 * 获取国际化文本
	 *
	 * @param key  文本键名；未找到时返回[key]格式
	 * @param args 格式化参数
	 * @return 国际化文本
	 */
	public static String prop(String key, Object... args) {
		return getInstance().getContent(key, args);
	}

}
