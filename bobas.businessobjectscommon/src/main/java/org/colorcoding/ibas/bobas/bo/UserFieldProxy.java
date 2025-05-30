package org.colorcoding.ibas.bobas.bo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.db.DbFieldType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "UserFieldProxy", namespace = MyConfiguration.NAMESPACE_BOBAS_BO)
public final class UserFieldProxy {

	@XmlElement(name = "Name")
	private String name;

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "ValueType")
	private String valueType;

	public final String getValueType() {
		return valueType;
	}

	public final void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public final void setValueType(Class<?> valueType) {
		this.setValueType(DbFieldType.valueOf(valueType).toString());
	}

	@XmlElement(name = "Value")
	private String value;

	public final String getValue() {
		return value;
	}

	public final void setValue(Object value) {
		if (value == null) {
			this.value = null;
		} else if (value == DateTimes.VALUE_MIN) {
			this.value = null;
		} else {
			this.value = Strings.valueOf(value);
		}
	}

	@Override
	public String toString() {
		return String.format("{proxy field: %s %s}", this.getName(), this.getValue());
	}

}
