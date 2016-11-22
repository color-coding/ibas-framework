package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;

@XmlType(name = "SingleValue", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
@XmlRootElement(name = "SingleValue", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public class SingleValue implements ISingleValue, IBusinessObjectBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8757890513425535280L;

	public SingleValue() {
	}

	public SingleValue(Object object) {
		this.setValue(object);
	}

	public boolean isValid() {
		return false;
	}

	public boolean isDirty() {
		return false;
	}

	public boolean isDeleted() {
		return false;
	}

	public boolean isNew() {
		return false;
	}

	public boolean isSavable() {
		return false;
	}

	public boolean isBusy() {
		return false;
	}

	@Override
	public boolean isLoading() {
		return false;
	}

	public ICriteria getCriteria() {
		return null;
	}

	@XmlElement(name = "Value")
	private Object _value = null;

	public void setValue(Object value) {
		this._value = value;
	}

	public Object getValue() {
		return this._value;
	}

	public String toString(String type) {
		return this.toString();
	}

	public String getSchema(String type) {
		return null;
	}

}
