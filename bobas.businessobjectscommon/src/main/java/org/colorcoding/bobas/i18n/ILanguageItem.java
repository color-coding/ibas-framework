package org.colorcoding.bobas.i18n;

public interface ILanguageItem extends Iterable<ILanguageItemContent> {
	/**
	 * 根据语言类型 获取翻译文本内容
	 * 
	 * @param langcode
	 *            语言类型
	 * @return 若存在返回结果，不存在返回空
	 */
	String getContent(String langcode);

	/**
	 * 获取默认的翻译文本
	 * 
	 * @return 若存在返回结果，不存在返回空
	 */
	String getContent();

	/**
	 * 创建内容实例
	 * 
	 * @return 返回实例
	 */
	ILanguageItemContent create();

	/**
	 * 获取翻译文本的key 值
	 * 
	 * @return 返回key值
	 */
	String getKey();

	/**
	 * 设置多语言文本的key 值
	 * 
	 * @param key
	 *            key值
	 */
	void setKey(String key);

	/**
	 * 添加多语言值到内容集合中
	 * 
	 * @param langcode
	 *            语言类型
	 * @param content
	 *            对应的值
	 */
	void addContent(String langcode, String content);
}
