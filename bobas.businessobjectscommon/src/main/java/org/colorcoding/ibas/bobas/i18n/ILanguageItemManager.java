package org.colorcoding.ibas.bobas.i18n;

/**
 * 语言项目管理员
 * 
 * @author Niuren.Zhu
 *
 */
public interface ILanguageItemManager {

	/**
	 * 获取-语言编码
	 * 
	 * @return
	 */
	String getLanguageCode();

	/**
	 * 设置-语言编码
	 * 
	 * @param value
	 */
	void setLanguageCode(String value);

	/**
	 * 获取内容
	 * 
	 * @param key
	 *            键值
	 * @param args
	 *            参数
	 * @return 格式化的内容
	 */
	String getContent(String key, Object... args);

	/**
	 * 读取资源文件（配置的默认路径）
	 */
	void readResources();
}
