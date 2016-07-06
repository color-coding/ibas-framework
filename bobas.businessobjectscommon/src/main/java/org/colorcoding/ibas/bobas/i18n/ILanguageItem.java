package org.colorcoding.ibas.bobas.i18n;

/**
 * 语言项目
 * 
 * @author Niuren.Zhu
 *
 */
public interface ILanguageItem extends Iterable<ILanguageItemContent> {

	/**
	 * 获取-项目键
	 * 
	 * @return
	 */
	String getKey();

	/**
	 * 设置-项目键
	 * 
	 * @param key
	 * 
	 */
	void setKey(String key);

	/**
	 * 获取-语言内容
	 * 
	 * @param langcode
	 *            语言编码
	 * @return
	 */
	String getContent(String langcode);

	/**
	 * 添加语言内容
	 * 
	 * @param langcode
	 *            语言类型
	 * @param content
	 *            内容
	 */
	void addContent(String langcode, String content);
}
