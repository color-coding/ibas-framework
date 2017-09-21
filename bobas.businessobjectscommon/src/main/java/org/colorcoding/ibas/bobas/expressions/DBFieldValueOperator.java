package org.colorcoding.ibas.bobas.expressions;

import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.core.fields.IFieldDataDb;
import org.colorcoding.ibas.bobas.core.fields.IManageFields;
import org.colorcoding.ibas.bobas.expressions.IPropertyValueOperator;
import org.colorcoding.ibas.bobas.expressions.JudgmentLinkException;
import org.colorcoding.ibas.bobas.i18n.I18N;

/**
 * 数据库字段值操作
 * 
 * @author Niuren.Zhu
 *
 */
public class DBFieldValueOperator implements IPropertyValueOperator {
	private IManageFields value;
	private IFieldData field = null;

	private IFieldData getField() {
		if (this.field == null) {
			for (IFieldData item : value.getFields()) {
				if (item instanceof IFieldDataDb) {
					IFieldDataDb dbField = (IFieldDataDb) item;
					if (dbField.getDbField().equalsIgnoreCase(this.getPropertyName())) {
						this.field = dbField;
						break;
					}
				}
			}
		}
		if (this.field == null) {
			throw new JudgmentLinkException(I18N.prop("msg_bobas_not_found_bo_field", this.getPropertyName()));
		}
		return this.field;
	}

	@Override
	public void setValue(Object value) {
		if (value != null && !(value instanceof IManageFields)) {
			throw new JudgmentLinkException(I18N.prop("msg_bobas_invaild_bo_type"));
		}
		this.value = (IManageFields) value;
		this.field = null;
	}

	@Override
	public Object getValue() {
		if (this.value == null) {
			return null;
		}
		return this.getField().getValue();
	}

	@Override
	public Class<?> getValueClass() {
		if (this.value == null) {
			return null;
		}
		return this.getField().getValueType();
	}

	private String propertyName;

	@Override
	public void setPropertyName(String value) {
		this.propertyName = value;
	}

	@Override
	public String getPropertyName() {
		return this.propertyName;
	}

	@Override
	public String toString() {
		return String.format("{property's value: %s}", this.getPropertyName());
	}
}
