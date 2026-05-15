package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.Serializable;

/**
 * 键值
 * 
 * @author Niuren.Zhu
 *
 */
@XmlType(name = "KeyValue", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
@XmlRootElement(name = "KeyValue", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public class KeyValue extends Serializable implements IKeyValue {

	private static final long serialVersionUID = 5102847294361392265L;

	public KeyValue() {
		this.key = Strings.VALUE_EMPTY;
	}

	public KeyValue(String key, Object value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * 键
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
	 * 值
	 */
	@XmlElement(name = "Value")
	private Object value;

	public final Object getValue() {
		return value;
	}

	public final void setValue(Object value) {
		this.value = value;
	}

	/**
	 * 获取值并按目标类型转换，类型不匹配时抛出ClassCastException
	 *
	 * @param <T> 目标类型
	 * @return 转换后的值，可能为null
	 */
	@SuppressWarnings("unchecked")
	public final <T> T asValue() {
		return (T) value;
	}

	public String toString() {
		return String.format("{keyValue: %s %s}", this.getKey(), this.getValue());
	}
}
