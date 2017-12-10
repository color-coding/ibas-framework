package org.colorcoding.ibas.bobas.core.fields;

import java.util.UUID;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.util.EncryptMD5;

/**
 * 字段基础类
 */
public abstract class FieldDataBase<T> implements IFieldData {

	public FieldDataBase() {
	}

	public FieldDataBase(String name) {
		this.setName(name);
	}

	public FieldDataBase(Class<?> valueType) {
		this.setValueType(valueType);
	}

	public FieldDataBase(String name, Class<?> valueType) {
		this.setName(name);
		this.setValueType(valueType);
	}

	public FieldDataBase(IPropertyInfo<?> property) {
		this.mapping(property);
	}

	private Class<?> valueType;

	@Override
	public Class<?> getValueType() {
		return this.valueType;
	}

	protected void setValueType(Class<?> valueType) {
		this.valueType = valueType;
	}

	private String name;

	@Override
	public final String getName() {
		if (this.propertyMapping != null) {
			// 映射属性，则使用属性名称
			return this.propertyMapping.getName();
		}
		if (this.name == null || this.name.isEmpty()) {
			try {
				this.setName(EncryptMD5.shortText(UUID.randomUUID().toString()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this.name;
	}

	public final void setName(String value) {
		this.name = value;
	}

	private boolean savable = false;

	@Override
	public final boolean isSavable() {
		return this.savable;
	}

	public final void setSavable(boolean value) {
		this.savable = value;
	}

	private boolean primaryKey = false;

	@Override
	public final boolean isPrimaryKey() {
		return this.primaryKey;
	}

	public final void setPrimaryKey(boolean value) {
		this.primaryKey = value;
	}

	private boolean uniqueKey = false;

	public final boolean isUniqueKey() {
		return uniqueKey;
	}

	public final void setUniqueKey(boolean uniqueKey) {
		this.uniqueKey = uniqueKey;
	}

	private boolean original = false;

	@Override
	public final boolean isOriginal() {
		return this.original;
	}

	public final void setOriginal(boolean value) {
		this.original = value;
	}

	private boolean linkage = true;

	@Override
	public final boolean isLinkage() {
		return this.linkage;
	}

	public final void setLinkage(boolean value) {
		this.linkage = value;
	}

	@Override
	public abstract T getValue();

	@Override
	public String toString() {
		return String.format("{field data: %s %s %s}", this.getName(), this.getValue(), this.getValueType().getName());
	}

	private IPropertyInfo<?> propertyMapping;

	public void mapping(IPropertyInfo<?> mapping) {
		this.propertyMapping = mapping;
	}
}
