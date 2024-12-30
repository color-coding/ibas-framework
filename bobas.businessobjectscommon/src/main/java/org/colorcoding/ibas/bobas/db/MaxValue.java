package org.colorcoding.ibas.bobas.db;

import org.colorcoding.ibas.bobas.core.FieldedObject;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.List;

public class MaxValue extends FieldedObject {

	private static final long serialVersionUID = 1L;

	private Class<?> boType;

	public MaxValue(Class<?> boType) {
		this.boType = boType;
	}

	public Class<?> getType() {
		return this.boType;
	}

	private IPropertyInfo<?> keyField;

	public IPropertyInfo<?> getKeyField() {
		return keyField;
	}

	public void setKeyField(IPropertyInfo<?> keyField) {
		this.keyField = keyField;
	}

	private List<IPropertyInfo<?>> conditionFields;

	public List<IPropertyInfo<?>> getConditionFields() {
		if (this.conditionFields == null) {
			this.conditionFields = new ArrayList<>();
		}
		return conditionFields;
	}

	public void addConditionField(IPropertyInfo<?> conditionField) {
		this.getConditionFields().add(conditionField);
	}

	@Override
	public final List<IPropertyInfo<?>> properties() {
		List<IPropertyInfo<?>> propertyInfos = new ArrayList<IPropertyInfo<?>>();
		propertyInfos.add(this.getKeyField());
		propertyInfos.addAll(this.getConditionFields());
		return propertyInfos;
	}

	public Object getValue() {
		return this.getProperty(this.getKeyField());
	}

}
