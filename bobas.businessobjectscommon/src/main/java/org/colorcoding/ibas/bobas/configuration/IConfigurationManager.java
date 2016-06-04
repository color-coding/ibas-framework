package org.colorcoding.ibas.bobas.configuration;

import java.io.InputStream;

/**
 * 配置项操作接口
 * 
 */
public interface IConfigurationManager {

	/**
	 * 读取配置项
	 * 
	 * @param stream
	 *            配置项流
	 * @return 返回key value 集合
	 */
	IConfigurationElements readSettings(InputStream stream);

	/**
	 * 通过文件路径读取配置项
	 * 
	 * @param filePath
	 *            完整的文件路径
	 * @return 返回配置项集合
	 */
	IConfigurationElements readSettings(String filePath);

	/**
	 * 保存配置项到指定路径
	 * 
	 * @param elements
	 *            配置项集合
	 * @param filePath
	 *            指定路径
	 */
	void saveSettings(IConfigurationElements elements, String filePath);

}
