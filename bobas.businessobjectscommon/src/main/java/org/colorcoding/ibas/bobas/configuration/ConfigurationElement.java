package org.colorcoding.ibas.bobas.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * 配置项类
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
class ConfigurationElement {

	public ConfigurationElement() {

	}

	public ConfigurationElement(String key, String value) {
		this.key = key;
		this.value = value;
	}

	private String key = "";

	@XmlAttribute(name = "key")
	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	private String value = "";

	@XmlAttribute(name = "value")
	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.format("{element: %s %s}", this.getKey(), this.getValue());
	}

}
