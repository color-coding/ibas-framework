package org.colorcoding.ibas.bobas.bo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.DateTimes;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "UserFieldProxy", namespace = MyConfiguration.NAMESPACE_BOBAS_BO)
public class UserFieldProxy implements IUserField<Object> {

	@XmlElement(name = "Name")
	private String name;

	@Override
	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "ValueType")
	private Class<?> valueType;

	@Override
	public final Class<?> getValueType() {
		return valueType;
	}

	public final void setValueType(Class<?> valueType) {
		this.valueType = valueType;
	}

	@XmlElement(name = "Value")
	private String value;

	@Override
	public final Object getValue() {
		return value;
	}

	@Override
	public final void setValue(Object value) {
		if (value == null) {
			this.value = null;
		} else if (value == DateTimes.VALUE_MIN) {
			this.value = null;
		} else {
			this.value = value.toString();
		}
	}

	@Override
	public String toString() {
		return String.format("{proxy field: %s %s}", this.getName(), this.getValue());
	}

}
