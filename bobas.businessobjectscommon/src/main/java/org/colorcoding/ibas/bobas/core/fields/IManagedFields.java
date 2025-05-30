package org.colorcoding.ibas.bobas.core.fields;

import java.util.Objects;
import java.util.function.Predicate;

import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.IFieldedObject;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.List;
import org.colorcoding.ibas.bobas.db.DbField;
import org.colorcoding.ibas.bobas.db.DbFieldType;

/**
 * 管理数据字段
 * 
 * @author Niuren.Zhu
 *
 */
public interface IManagedFields {
	/**
	 * 获取字段
	 * 
	 * @return 字段
	 */
	default IFieldData[] getFields() {
		if (this instanceof IFieldedObject) {
			IFieldedObject fieldedObject = (IFieldedObject) this;
			List<IPropertyInfo<?>> properties = fieldedObject.properties();
			IFieldData[] fields = new IFieldData[properties.size()];
			for (int i = 0; i < properties.size(); i++) {
				IPropertyInfo<?> property = properties.get(i);
				DbField dbField = property.getAnnotation(DbField.class);
				if (dbField == null) {
					fields[i] = new IFieldData() {

						@Override
						public boolean setValue(Object value) {
							fieldedObject.setProperty(property, value);
							return true;
						}

						@Override
						public boolean isUniqueKey() {
							return property.isUniqueKey();
						}

						@Override
						public boolean isSavable() {
							return false;
						}

						@Override
						public boolean isDirty() {
							return fieldedObject.isModified(property);
						}

						@Override
						public boolean isPrimaryKey() {
							return property.isPrimaryKey();
						}

						@Override
						public boolean isOriginal() {
							return true;
						}

						@Override
						public Class<?> getValueType() {
							return property.getValueType();
						}

						@Override
						public Object getValue() {
							return fieldedObject.getProperty(property);
						}

						@Override
						public String getName() {
							return property.getName();
						}

						@Override
						public String toString() {
							return String.format("{field: %s, %s}", this.getName(), this.getValue());
						}

					};
				} else {
					fields[i] = new IFieldDataDb() {

						@Override
						public boolean setValue(Object value) {
							fieldedObject.setProperty(property, value);
							return true;
						}

						@Override
						public boolean isUniqueKey() {
							return property.isUniqueKey();
						}

						@Override
						public boolean isSavable() {
							return true;
						}

						@Override
						public boolean isDirty() {
							return fieldedObject.isModified(property);
						}

						@Override
						public boolean isPrimaryKey() {
							return property.isPrimaryKey();
						}

						@Override
						public boolean isOriginal() {
							return true;
						}

						@Override
						public Class<?> getValueType() {
							return property.getValueType();
						}

						@Override
						public Object getValue() {
							return fieldedObject.getProperty(property);
						}

						@Override
						public String getName() {
							return property.getName();
						}

						@Override
						public String getDbField() {
							return dbField.name();
						}

						@Override
						public String getDbTable() {
							return dbField.table();
						}

						@Override
						public DbFieldType getFieldType() {
							return dbField.type();
						}

						@Override
						public String toString() {
							return String.format("{field: %s, %s}", this.getName(), this.getValue());
						}
					};
				}
			}
			return fields;
		}
		return new IFieldData[] {};
	}

	/**
	 * 获取字段
	 * 
	 * @return 字段
	 */
	default IFieldData[] getFields(Predicate<? super IFieldData> filter) {
		Objects.requireNonNull(filter);
		List<IFieldData> fields = new ArrayList<>();
		for (IFieldData item : this.getFields()) {
			if (filter.test(item)) {
				fields.add(item);
			}
		}
		return fields.toArray(new IFieldData[] {});
	}

	/**
	 * 获取字段
	 * 
	 * @return 字段
	 */
	default IFieldData getField(String name) {
		if (this instanceof IFieldedObject) {
			IFieldedObject fieldedObject = (IFieldedObject) this;
			IPropertyInfo<?> property = fieldedObject.properties(c -> Strings.equalsIgnoreCase(c.getName(), name))
					.firstOrDefault();
			if (property != null) {
				DbField dbField = property.getAnnotation(DbField.class);
				if (dbField == null) {
					return new IFieldData() {

						@Override
						public boolean setValue(Object value) {
							fieldedObject.setProperty(property, value);
							return true;
						}

						@Override
						public boolean isUniqueKey() {
							return property.isUniqueKey();
						}

						@Override
						public boolean isSavable() {
							return false;
						}

						@Override
						public boolean isDirty() {
							return fieldedObject.isModified(property);
						}

						@Override
						public boolean isPrimaryKey() {
							return property.isPrimaryKey();
						}

						@Override
						public boolean isOriginal() {
							return true;
						}

						@Override
						public Class<?> getValueType() {
							return property.getValueType();
						}

						@Override
						public Object getValue() {
							return fieldedObject.getProperty(property);
						}

						@Override
						public String getName() {
							return property.getName();
						}

						@Override
						public String toString() {
							return String.format("{field: %s, %s}", this.getName(), this.getValue());
						}
					};
				} else {
					return new IFieldDataDb() {

						@Override
						public boolean setValue(Object value) {
							fieldedObject.setProperty(property, value);
							return true;
						}

						@Override
						public boolean isUniqueKey() {
							return property.isUniqueKey();
						}

						@Override
						public boolean isSavable() {
							return true;
						}

						@Override
						public boolean isDirty() {
							return fieldedObject.isModified(property);
						}

						@Override
						public boolean isPrimaryKey() {
							return property.isPrimaryKey();
						}

						@Override
						public boolean isOriginal() {
							return true;
						}

						@Override
						public Class<?> getValueType() {
							return property.getValueType();
						}

						@Override
						public Object getValue() {
							return fieldedObject.getProperty(property);
						}

						@Override
						public String getName() {
							return property.getName();
						}

						@Override
						public String getDbField() {
							return dbField.name();
						}

						@Override
						public String getDbTable() {
							return dbField.table();
						}

						@Override
						public DbFieldType getFieldType() {
							return dbField.type();
						}

						@Override
						public String toString() {
							return String.format("{field: %s, %s}", this.getName(), this.getValue());
						}
					};
				}
			}
		}
		return null;
	}
}
