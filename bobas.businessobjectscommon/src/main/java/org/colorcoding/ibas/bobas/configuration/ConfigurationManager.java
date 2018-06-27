package org.colorcoding.ibas.bobas.configuration;

import java.util.Collection;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.colorcoding.ibas.bobas.data.DataConvert;
import org.colorcoding.ibas.bobas.message.Logger;

/**
 * 配置项管理员
 * 
 */

@XmlAccessorType(XmlAccessType.NONE)
public abstract class ConfigurationManager implements IConfigurationManager {

	private HashMap<String, IConfigurationElement> elementsMap = new HashMap<String, IConfigurationElement>();

	@Override
	public Collection<IConfigurationElement> getElements() {
		return this.elementsMap.values();
	}

	@Override
	public String getConfigValue(String key) {
		IConfigurationElement element = this.elementsMap.get(key);
		if (element != null) {
			return element.getValue();
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P> P getConfigValue(String key, P defaultValue) {
		String valueString = this.getConfigValue(key);
		if (valueString == null || valueString.isEmpty()) {
			return defaultValue;
		} else {
			try {
				// 强行转换配置值为P类型
				if (defaultValue != null) {
					return (P) DataConvert.convert(defaultValue.getClass(), valueString);
				}
				return (P) valueString;
			} catch (Exception e) {
				Logger.log(e);
				return defaultValue;
			}
		}
	}

	@Override
	public void addConfigValue(String key, String value) {
		IConfigurationElement element = this.elementsMap.get(key);
		if (element == null) {
			element = new ConfigurationElement();
			element.setKey(key);
			this.elementsMap.put(element.getKey(), element);
		}
		element.setValue(value);
	}

	private String configSign;

	public String getConfigSign() {
		return configSign;
	}

	public void setConfigSign(String configSign) {
		this.configSign = configSign;
	}

}
