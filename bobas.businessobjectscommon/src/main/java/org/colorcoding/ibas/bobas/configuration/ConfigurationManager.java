package org.colorcoding.ibas.bobas.configuration;

import java.util.Collection;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.colorcoding.ibas.bobas.common.DataConvert;
import org.colorcoding.ibas.bobas.data.KeyText;
import org.colorcoding.ibas.bobas.logging.Logger;

/**
 * 配置项管理员
 * 
 */

@XmlAccessorType(XmlAccessType.NONE)
public abstract class ConfigurationManager implements IConfigurationManager {

	private HashMap<String, KeyText> elementsMap = new HashMap<String, KeyText>(128);

	public Collection<KeyText> getElements() {
		return this.elementsMap.values();
	}

	public String getConfigValue(String key) {
		KeyText element = this.elementsMap.get(key);
		if (element != null) {
			return element.getText();
		}
		return null;
	}

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

	public void addConfigValue(String key, String value) {
		KeyText element = this.elementsMap.get(key);
		// 值是空是删除配置项
		if (key == null && element != null) {
			this.elementsMap.remove(key);
			return;
		}
		if (element == null) {
			element = new KeyText();
			element.setKey(key);
			this.elementsMap.put(element.getKey(), element);
		}
		element.setText(value);
	}

	private String configSign;

	public String getConfigSign() {
		return configSign;
	}

	public void setConfigSign(String configSign) {
		this.configSign = configSign;
	}

}
