package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.Serializable;

/**
 * 值及描述
 * 
 * @author Niuren.Zhu
 */
@XmlType(name = "KeyText", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
@XmlRootElement(name = "KeyText", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public class KeyText extends Serializable implements IKeyText {

	private static final long serialVersionUID = 4725190510330982065L;

	public KeyText() {
		this.key = Strings.VALUE_EMPTY;
		this.text = Strings.VALUE_EMPTY;
	}

	public KeyText(String key, String text) {
		this.key = key;
		this.text = text;
	}

	/**
	 * 值
	 */
	@XmlElement(name = "Key")
	private String key;

	public final String getKey() {
		if (this.key == null) {
			this.key = Strings.VALUE_EMPTY;
		}
		return key;
	}

	public final void setKey(String key) {
		this.key = key;
	}

	/**
	 * 描述
	 */
	@XmlElement(name = "Text")
	private String text;

	public final String getText() {
		return text;
	}

	public final void setText(String text) {
		this.text = text;
	}


	public String toString() {
		return String.format("{key text: %s %s}", this.getKey(), this.getText());
	}

}
