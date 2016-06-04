package org.colorcoding.ibas.bobas.test.configuration;

import org.colorcoding.ibas.bobas.configuration.Configuration;
import org.colorcoding.ibas.bobas.configuration.ConfigurationElement;
import org.colorcoding.ibas.bobas.configuration.ConfigurationElements;
import org.colorcoding.ibas.bobas.configuration.IConfigurationElement;
import org.colorcoding.ibas.bobas.core.ObjectCloner;

import junit.framework.TestCase;

/**
 * 测试 配置类 测试 范围：读取配置项，增加配置项，删除配置项，加载另外的配置文件，保存所有配置项到最后加载的配置文件路径
 * 
 * @author Eric Peng
 *
 */
public class testConfiguration extends TestCase {

	public void testGetConfigvalue() {
		Configuration configuration = Configuration.getConfiguration();
		String value_test = "";
		String value = Configuration.getConfigValue("I18nPath");
		System.out.println(value);
		ConfigurationElement appSetting = new ConfigurationElement();
		appSetting.setKey("test");
		appSetting.setValue("ceshi配置文件");
		configuration.addSetting(appSetting);// 添加配置项
		value_test = Configuration.getConfigValue("test");
		System.out.println(value_test);
		configuration.setSetting("test", "修改ceshi配置项");// 修改配置项
		value_test = Configuration.getConfigValue("test");
		System.out.println(value_test);
		configuration.deleteConfigElement("test");// 删除配置项
		value_test = Configuration.getConfigValue("test");
		System.out.println(value_test);
		configuration.putSettings(configuration.loadConfigFile("D:/app.xml"));// 加载新的配置文件
		value_test = Configuration.getConfigValue("MasterDbServer");
		System.out.println(value_test);
		configuration.saveSettings("D:/app_T.xml");// 保存配置项
	}

	public void testConfigurationSerialize() {
		ConfigurationElements elements = new ConfigurationElements();
		IConfigurationElement element = elements.create();
		element.setKey("name");
		element.setValue("XXOO");
		IConfigurationElement element1 = elements.create();
		element1.setKey("name1");
		element1.setValue("XXOO1");
		System.out.println(ObjectCloner.toXmlString(elements, true));
	}

}
