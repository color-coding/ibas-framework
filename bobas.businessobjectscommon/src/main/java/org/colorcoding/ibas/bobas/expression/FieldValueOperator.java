package org.colorcoding.ibas.bobas.expression;

import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.core.FieldedObject;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;

/**
 * 对象属性值操作
 * 
 * @author Niuren.Zhu
 *
 */
public class FieldValueOperator implements IPropertyValueOperator {

	protected FieldedObject dataObject;
	protected IPropertyInfo<?> property;

	public IPropertyInfo<?> getProperty() {
		if (this.property == null) {
			this.property = this.dataObject.properties()
					.firstOrDefault(c -> c.getName().equalsIgnoreCase(this.getPropertyName()));
		}
		if (this.property == null) {
			throw new ExpressionException("not found property.");
		}
		return this.property;
	}

	@Override
	public void setValue(Object value) {
		if (!BOUtilities.isBusinessObject(value)) {
			throw new ExpressionException("unrecognized object.");
		}
		this.dataObject = (FieldedObject) value;
		this.property = null;
	}

	@Override
	public Object getValue() {
		if (this.dataObject == null) {
			return null;
		}
		return this.dataObject.getProperty(this.getProperty());
	}

	@Override
	public Class<?> getValueClass() {
		return this.getProperty().getValueType();
	}

	private String propertyName;

	@Override
	public void setPropertyName(String value) {
		this.propertyName = value;
		this.property = null;
	}

	@Override
	public String getPropertyName() {
		return this.propertyName;
	}

	@Override
	public String toString() {
		return String.format("{field operator: %s}", this.getPropertyName());
	}
}