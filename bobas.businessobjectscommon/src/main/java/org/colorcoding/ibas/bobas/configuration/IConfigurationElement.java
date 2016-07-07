package org.colorcoding.ibas.bobas.configuration;

/**
 * 配置项
 * 
 */
public interface IConfigurationElement {
	/**
	 * 获取key的值
	 * 
	 * @return 返回数据
	 */
	String getKey();

	/**
	 * 设置key的值
	 * 
	 * @param key
	 *            key值
	 */
	void setKey(String key);

	/**
	 * 获取value的值
	 * 
	 * @return 返回数据
	 */
	String getValue();

	/**
	 * 设置key的值
	 * 
	 * @param value
	 *            key值
	 */
	void setValue(String value);

}
