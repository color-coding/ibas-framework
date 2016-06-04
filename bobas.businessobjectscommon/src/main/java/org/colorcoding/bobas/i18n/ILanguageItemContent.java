package org.colorcoding.bobas.i18n;

public interface ILanguageItemContent {
	/**
	 * 获取语言版本
	 * 
	 * @return 返回语言信息
	 */
	String getLanguageCode();

	/**
	 * 设置语言版本
	 * 
	 * @param languageCode
	 *            语言类型
	 */

	void setLanguageCode(String languageCode);

	/**
	 * 得到相应内容
	 * 
	 * @return 返回内容
	 */
	String getContent();

	/**
	 * 设置相应内容
	 * 
	 * @param content
	 *            资源内容
	 */
	void setContent(String content);

}
