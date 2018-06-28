package org.colorcoding.ibas.bobas.bo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "UserFieldProxy", namespace = MyConfiguration.NAMESPACE_BOBAS_BO)
public class UserFieldProxy implements IUserField {

	private static final long serialVersionUID = -7522271348157162894L;

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
	private DbFieldType valueType;

	@Override
	public final DbFieldType getValueType() {
		return valueType;
	}

	public final void setValueType(DbFieldType valueType) {
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
		} else if (value == DateTime.MIN_VALUE) {
			this.value = null;
		} else {
			this.value = value.toString();
		}
	}

	@Override
	public String toString() {
		return String.format("{proxy field: %s %s}", this.getName(), this.getValue());
	}

	public Object convertValue() {
		if (this.getValueType() == DbFieldType.NUMERIC) {
			if (this.value == null || this.value.isEmpty()) {
				return 0;
			}
			return Integer.valueOf(this.value);
		} else if (this.getValueType() == DbFieldType.DATE) {
			if (this.value == null || this.value.isEmpty()) {
				return DateTime.MIN_VALUE;
			}
			return DateTime.valueOf(this.value);
		} else if (this.getValueType() == DbFieldType.DECIMAL) {
			if (this.value == null || this.value.isEmpty()) {
				return Decimal.ZERO;
			}
			return Decimal.valueOf(this.value);
		}
		return this.value;
	}
}
