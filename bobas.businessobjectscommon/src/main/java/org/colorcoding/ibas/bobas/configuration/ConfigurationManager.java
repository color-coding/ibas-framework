package org.colorcoding.ibas.bobas.configuration;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.DataConvert;
import org.colorcoding.ibas.bobas.data.IKeyText;
import org.colorcoding.ibas.bobas.data.KeyText;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;

/**
 * 配置项管理员
 * 
 */

@XmlAccessorType(XmlAccessType.NONE)
public abstract class ConfigurationManager {

	public ConfigurationManager() {
		this(64);
	}

	public ConfigurationManager(int initialCapacity) {
		this.elementsMap = new ConcurrentHashMap<>(initialCapacity, 1);
	}

	private Map<String, String> elementsMap;

	public Collection<IKeyText> getElements() {
		ArrayList<IKeyText> elements = new ArrayList<>(this.elementsMap.size());
		for (Entry<String, String> item : this.elementsMap.entrySet()) {
			elements.add(new KeyText(item.getKey(), item.getValue()));
		}
		return elements;
	}

	public String getConfigValue(String key) {
		String value = this.elementsMap.get(key);
		if (value != null) {
			return value;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <P> P getConfigValue(String key, P defaultValue) {
		String value = this.getConfigValue(key);
		if (value == null) {
			return defaultValue;
		}
		if (value.getClass() == String.class) {
			if (Strings.isNullOrEmpty(value.toString())) {
				return defaultValue;
			}
		}
		if (defaultValue == null) {
			return (P) value;
		}
		try {
			return (P) DataConvert.convert(defaultValue.getClass(), value);
		} catch (Exception e) {
			Logger.log(MessageLevel.WARN, e);
			return defaultValue;
		}
	}

	public void addConfigValue(String key, String value) {
		if (key == null) {
			return;
		}
		if (value == null) {
			return;
		}
		this.elementsMap.put(key, value);
	}

	private String configSign;

	public String getConfigSign() {
		return configSign;
	}

	public void setConfigSign(String configSign) {
		this.configSign = configSign;
	}

	public abstract void update();
}
