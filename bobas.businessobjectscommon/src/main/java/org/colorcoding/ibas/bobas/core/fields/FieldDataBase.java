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
        this.setName(property.getName());

    }

    private Class<?> valueType = null;

    @Override
    public Class<?> getValueType() {
        return this.valueType;
    }

    protected void setValueType(Class<?> valueType) {
        this.valueType = valueType;
    }

    private String name = "";

    @Override
    public final String getName() {
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

    private boolean isSavable = false;

    @Override
    public final boolean isSavable() {
        return this.isSavable;
    }

    public final void setSavable(boolean value) {
        this.isSavable = value;
    }

    private boolean isPrimaryKey = false;

    @Override
    public final boolean isPrimaryKey() {
        return this.isPrimaryKey;
    }

    public final void setPrimaryKey(boolean value) {
        this.isPrimaryKey = value;
    }

    private boolean isOriginal = false;

    @Override
    public final boolean isOriginal() {
        return this.isOriginal;
    }

    public final void setOriginal(boolean value) {
        this.isOriginal = value;
    }

    private boolean isLinkage = true;

    @Override
    public final boolean isLinkage() {
        return this.isLinkage;
    }

    public final void setLinkage(boolean value) {
        this.isLinkage = value;
    }

    private boolean isDirty = false;

    @Override
    public final boolean isDirty() {
        return this.isDirty;
    }

    @Override
    public void markOld() {
        this.setDirty(false);
    }

    protected void setDirty(boolean value) {
        this.isDirty = value;
    }

    @Override
    public abstract T getValue();

    @Override
    public String toString() {
        return String.format("{field data: %s %s %s}", this.getName(), this.getValue(), this.getValueType().getName());
    }

    public void mapping(IPropertyInfo<?> mapping) {
        this.setName(mapping.getName());
    }
}
