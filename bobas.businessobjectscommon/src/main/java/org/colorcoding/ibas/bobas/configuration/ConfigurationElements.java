package org.colorcoding.ibas.bobas.configuration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;

/**
 * 封装配置项信息，便于调用和序列化
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "ConfigurationElements", namespace = MyConsts.NAMESPACE_BOBAS_CONFIGURATION)
@XmlRootElement(name = "configuration", namespace = MyConsts.NAMESPACE_BOBAS_CONFIGURATION)
public class ConfigurationElements implements IConfigurationElements {

	@XmlElementWrapper(name = "appSettings")
	@XmlElement(name = "add", type = ConfigurationElement.class)
	private List<IConfigurationElement> elements;

	/**
	 * 获取所有配置项
	 * 
	 * @return 配置项集合
	 */
	private List<IConfigurationElement> getElements() {
		if (elements == null) {
			elements = new ArrayList<IConfigurationElement>();
		}
		return elements;
	}

	/**
	 * 创建一个新配置项
	 * 
	 * @return 配置项
	 */
	@Override
	public IConfigurationElement create() {
		ConfigurationElement configurationElement = new ConfigurationElement();
		getElements().add(configurationElement);
		return configurationElement;
	}

	/**
	 * 重写 迭代
	 * 
	 * @return 迭代集合
	 */
	@Override
	public Iterator<IConfigurationElement> iterator() {
		return getElements().iterator();
	}

}
