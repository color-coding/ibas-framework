package org.colorcoding.ibas.bobas.configuration;

import java.io.IOException;
import java.util.Collection;

import javax.xml.bind.JAXBException;

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
	 * 保存配置项到指定路径
	 * 
	 * @param filePath
	 *            指定路径
	 * @throws JAXBException
	 * @throws IOException
	 */
	void saveSettings(String filePath) throws JAXBException, IOException;

	/**
	 * 获取值
	 * 
	 * @param key
	 * @return
	 */
	String getValue(String key);

	/**
	 * 添加或更新值
	 * 
	 * @param key
	 * @param value
	 */
	void addSetting(String key, String value);

	/**
	 * 更新配置项目
	 * 
	 * @param filePath
	 *            配置文件路径
	 * @return 是否成功
	 */
	boolean update(String filePath);
}
