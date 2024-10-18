package org.colorcoding.ibas.bobas.configuration;

import java.util.Collection;

import org.colorcoding.ibas.bobas.data.KeyText;

/**
 * 配置项操作接口
 * 
 */
public interface IConfigurationManager {
	/**
	 * 获取配置元素
	 * 
	 * @return
	 */
	Collection<KeyText> getElements();

	/**
	 * 获取值
	 * 
	 * @param key
	 * @return
	 */
	String getConfigValue(String key);

	/**
	 * 获取值(不存在使用默认值)
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	<P> P getConfigValue(String key, P defaultValue);

	/**
	 * 添加或更新值
	 * 
	 * @param key
	 * @param value
	 */
	void addConfigValue(String key, String value);

	/**
	 * 获取-配置标记
	 * 
	 * @return
	 */
	String getConfigSign();

	/**
	 * 设置-配置标记
	 * 
	 * @param configSign
	 */
	void setConfigSign(String configSign);

	/**
	 * 保存配置项目
	 */
	void save();

	/**
	 * 更新配置项目
	 */
	void update();
}
