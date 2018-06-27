package org.colorcoding.ibas.bobas.configuration;

import java.util.Collection;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

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
	public void addConfigValue(String key, String value) {
		IConfigurationElement element = this.elementsMap.get(key);
		if (element == null) {
			element = new ConfigurationElement();
			element.setKey(key);
			this.elementsMap.put(element.getKey(), element);
		}
		element.setValue(value);
	}

}
