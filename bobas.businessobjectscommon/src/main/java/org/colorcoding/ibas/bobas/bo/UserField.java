package org.colorcoding.ibas.bobas.bo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.colorcoding.ibas.bobas.core.BindableBase;
import org.colorcoding.ibas.bobas.core.fields.IFieldDataDb;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;

/**
 * 用户字段元素
 * 
 * @author Niuren.Zhu
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class UserField extends BindableBase implements IUserField {

	private static final long serialVersionUID = -4092373163622194831L;

	/**
	 * 用户字段前缀标记
	 */
	public static final String USER_FIELD_PREFIX_SIGN = "U_";

	public UserField(IFieldDataDb fieldData) {
		this.setFieldData(fieldData);
	}

	private IFieldDataDb fieldData = null;

	protected IFieldDataDb getFieldData() {
		return fieldData;
	}

	protected void setFieldData(IFieldDataDb value) {
		this.fieldData = value;
	}

	@Override
	@XmlElement(name = "Name")
	public String getName() {
		return this.fieldData.getName();
	}

	@Override
	@XmlElement(name = "ValueType")
	public DbFieldType getValueType() {
		return this.fieldData.getFieldType();
	}

	@Override
	@XmlElement(name = "Value")
	public Object getValue() {
		return this.fieldData.getValue();
	}

	@Override
	public void setValue(Object value) {
		Object oldValue = this.fieldData.getValue();
		this.fieldData.setValue(value);
		this.firePropertyChange(this.getName(), oldValue, value);
	}

	@Override
	public String toString() {
		return String.format("{user field: %s %s}", this.getName(), this.getValue());
	}
}
