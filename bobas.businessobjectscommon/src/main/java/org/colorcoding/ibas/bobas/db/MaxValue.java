package org.colorcoding.ibas.bobas.db;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.IFieldedObject;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.core.Trackable;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.List;

public class MaxValue extends Trackable implements IFieldedObject {

	private static final long serialVersionUID = 1L;

	private Class<?> boType;

	public MaxValue(Class<?> boType) {
		this.boType = boType;
		this.fields = new HashMap<>();
	}

	public Class<?> getType() {
		return this.boType;
	}

	private IPropertyInfo<?> keyField;

	public IPropertyInfo<?> getKeyField() {
		return keyField;
	}

	public void setKeyField(IPropertyInfo<?> keyField) {
		if (this.keyField != null) {
			this.fields.remove(this.keyField);
		}
		this.keyField = keyField;
		this.fields.put(keyField, null);
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
		this.fields.put(conditionField, null);
	}

	@Override
	public List<IPropertyInfo<?>> properties() {
		ArrayList<IPropertyInfo<?>> propertyInfos = new ArrayList<>();
		for (IPropertyInfo<?> item : this.fields.keySet()) {
			propertyInfos.add(item);
		}
		return propertyInfos;
	}

	private Map<IPropertyInfo<?>, Object> fields = null;

	@Override
	@SuppressWarnings("unchecked")
	public <P> P getProperty(IPropertyInfo<?> property) {
		Objects.requireNonNull(property);
		if (this.fields.containsKey(property)) {
			P value = (P) this.fields.get(property);
			// 值是空，则使用默认值（减少内存占用）
			if (value == null) {
				return (P) property.getDefaultValue();
			}
			return value;
		}
		throw new IllegalArgumentException(
				Strings.format("[%s] not exists property [%s].", this.getClass().getName(), property.getName()));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P> void setProperty(IPropertyInfo<?> property, P value) {
		Objects.requireNonNull(property);
		if (!this.fields.containsKey(property)) {
			throw new IllegalArgumentException(
					Strings.format("[%s] not exists property [%s].", this.getClass().getName(), property.getName()));
		}
		if (this.isLoading()) {
			this.fields.put(property, value);
		} else {
			P oldValue = (P) this.fields.get(property);
			if (oldValue != value) {
				this.fields.put(property, value);
				this.markDirty();
				this.firePropertyChange(property.getName(), oldValue, value);
			}
		}
	}

	public Object getValue() {
		return this.getProperty(this.getKeyField());
	}
}
