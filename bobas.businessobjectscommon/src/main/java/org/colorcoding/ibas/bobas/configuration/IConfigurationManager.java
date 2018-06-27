package org.colorcoding.ibas.bobas.configuration;

import java.util.Collection;

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
	Collection<IConfigurationElement> getElements();

	/**
	 * 获取值
	 * 
	 * @param key
	 * @return
	 */
	String getConfigValue(String key);

	/**
	 * 添加或更新值
	 * 
	 * @param key
	 * @param value
	 */
	void addConfigValue(String key, String value);

	/**
	 * 保存配置项目
	 */
	void save() throws Exception;

	/**
	 * 更新配置项目
	 */
	void update() throws Exception;
}
