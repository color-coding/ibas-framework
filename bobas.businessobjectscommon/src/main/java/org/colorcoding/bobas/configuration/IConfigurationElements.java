package org.colorcoding.bobas.configuration;

/**
 * 配置项集合接口
 * 
 */
public interface IConfigurationElements extends Iterable<IConfigurationElement> {
	/**
	 * 创建配置项实例
	 * 
	 * @return 返回结果
	 */
	IConfigurationElement create();

}
