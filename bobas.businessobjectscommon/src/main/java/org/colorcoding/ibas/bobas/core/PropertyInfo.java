package org.colorcoding.ibas.bobas.core;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;

import org.colorcoding.ibas.bobas.common.Booleans;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Decimals;
import org.colorcoding.ibas.bobas.common.Enums;
import org.colorcoding.ibas.bobas.common.Field;
import org.colorcoding.ibas.bobas.common.Numbers;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.db.DbField;

final class PropertyInfo<P> implements IPropertyInfo<P> {

	public PropertyInfo(String name, Class<P> type) {
		this.setName(name);
		this.setValueType(type);
	}

	private String name = Strings.VALUE_EMPTY;

	public final String getName() {
		return this.name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	private int index = -1;

	public final int getIndex() {
		return this.index;
	}

	public final void setIndex(int value) {
		this.index = value;
	}

	private Class<P> valueType = null;

	public final Class<P> getValueType() {
		return this.valueType;
	}

	public final void setValueType(Class<P> value) {
		this.valueType = value;
	}

	private P defaultValue;

	@SuppressWarnings("unchecked")
	public final P getDefaultValue() {
		if (this.defaultValue == null) {
			if (this.getValueType() == String.class) {
				return (P) Strings.defaultValue();
			} else if (this.getValueType() == BigDecimal.class) {
				return (P) Decimals.defaultValue();
			} else if (this.getValueType() == Integer.class) {
				return (P) Numbers.defaultValue(this.getValueType());
			} else if (this.getValueType() == Short.class) {
				return (P) Numbers.defaultValue(this.getValueType());
			} else if (this.getValueType() == Long.class) {
				return (P) Numbers.defaultValue(this.getValueType());
			} else if (this.getValueType() == Double.class) {
				return (P) Numbers.defaultValue(this.getValueType());
			} else if (this.getValueType() == Float.class) {
				return (P) Numbers.defaultValue(this.getValueType());
			} else if (this.getValueType().isEnum()) {
				return (P) Enums.defaultValue(this.getValueType());
			} else if (this.getValueType() == Boolean.class) {
				return (P) Booleans.defaultValue();
			} else if (this.getValueType() == DateTime.class) {
				return (P) DateTimes.defaultValue();
			}
		}
		return this.defaultValue;
	}

	public final void setDefaultValue(P value) {
		this.defaultValue = value;
	}

	private boolean primaryKey = Booleans.VALUE_FALSE;

	public boolean isPrimaryKey() {
		return this.primaryKey;
	}

	public void setPrimaryKey(boolean value) {
		this.primaryKey = value;
	}

	private boolean uniqueKey = Booleans.VALUE_FALSE;

	public boolean isUniqueKey() {
		return this.uniqueKey;
	}

	public void setUniqueKey(boolean value) {
		this.uniqueKey = value;
	}

	volatile Runnable resolver = null;

	private Annotation[] annotations = null;

	protected synchronized Annotation[] getAnnotations() {
		if (this.resolver != null) {
			this.resolver.run();
			this.resolver = null;
		}
		return annotations;
	}

	synchronized void setAnnotations(Annotation[] annotations) {
		if (this.resolver != null && annotations != null) {
			this.resolver = null;
		}
		this.annotations = annotations;
		if (this.annotations != null) {
			for (Annotation annotation : this.annotations) {
				if (annotation.annotationType().equals(Field.class)) {
					Field field = (Field) annotation;
					if (field.primaryKey() == true) {
						this.primaryKey = true;
					}
					if (field.uniqueKey() == true) {
						this.uniqueKey = true;
					}
					break;
				} else if (annotation.annotationType().equals(DbField.class)) {
					DbField field = (DbField) annotation;
					if (field.primaryKey() == true) {
						this.primaryKey = true;
					}
					if (field.uniqueKey() == true) {
						this.uniqueKey = true;
					}
					break;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public synchronized final <A extends Annotation> A getAnnotation(Class<A> type) {
		if (this.resolver != null) {
			this.resolver.run();
			this.resolver = null;
		}
		if (this.annotations == null) {
			return null;
		}
		for (Annotation annotation : this.annotations) {
			if (annotation.annotationType().equals(type)) {
				return (A) annotation;
			}
		}
		return null;
	}

	public final String toString() {
		return String.format("{property: %s}", this.getName());
	}

}
