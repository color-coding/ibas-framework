package org.colorcoding.ibas.bobas.core.fields;

import java.math.BigDecimal;

import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.IBusinessObjectsBase;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.core.PropertyInfo;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.mapping.AssociationMode;
import org.colorcoding.ibas.bobas.mapping.Associations;
import org.colorcoding.ibas.bobas.mapping.ComplexField;
import org.colorcoding.ibas.bobas.mapping.ComplexFieldType;
import org.colorcoding.ibas.bobas.mapping.DbField;

public class FieldsFactory {
	private FieldsFactory() {

	}

	volatile private static FieldsFactory _Instance;

	public static FieldsFactory create() {
		if (_Instance == null) {
			synchronized (FieldsFactory.class) {
				if (_Instance == null) {
					_Instance = new FieldsFactory();
				}
			}
		}
		return _Instance;
	}

	public FieldDataBase<?> createField(Class<?> type) throws NotSupportTypeException {
		if (type == Integer.class) {
			return new FieldDataInteger(type);
		} else if (type == String.class) {
			return new FieldDataString(type);
		} else if (type == BigDecimal.class) {
			return new FieldDataDecimal(type);
		} else if (type == Double.class) {
			return new FieldDataDouble(type);
		} else if (type == DateTime.class || type == java.util.Date.class) {
			return new FieldDataDateTime(type);
		} else if (type == Short.class) {
			return new FieldDataShort(type);
		} else if (type == Character.class) {
			return new FieldDataChar(type);
		} else if (type == Float.class) {
			return new FieldDataFloat(type);
		} else if (type == Boolean.class) {
			return new FieldDataBoolean(type);
		} else if (type.isEnum()) {// 判断是否为枚举
			return new FieldDataEnum(type);
		} else if (IBusinessObjectBase.class.isAssignableFrom(type)) {
			return new FieldDataBO(type);
		} else if (IBusinessObjectsBase.class.isAssignableFrom(type)) {
			return new FieldDataBOs(type);
		} else {
			throw new NotSupportTypeException(I18N.prop("msg_bobas_data_type_not_support", type));
		}
	}

	public AssociatedFieldDataBase<?> createAssociatedField(AssociationMode mode, int assoCount)
			throws NotSupportTypeException {
		if (mode == AssociationMode.ONE_TO_ONE || mode == AssociationMode.ONE_TO_ZERO) {
			return new AssociatedFieldDataBO(assoCount);
		} else if (mode == AssociationMode.ONE_TO_MANY) {
			return new AssociatedFieldDataArray(assoCount);
		} else {
			throw new NotSupportTypeException(I18N.prop("msg_bobas_association_mode_not_support", mode));
		}
	}

	public ComplexFieldDataBase<?> createComplexField(ComplexFieldType type) throws NotSupportTypeException {
		if (type == ComplexFieldType.MEASUREMENT) {
			return new ComplexFieldDataMeasurement();
		} else {
			throw new NotSupportTypeException(I18N.prop("msg_bobas_association_mode_not_support", type));
		}
	}

	public FieldDataDbBase<?> createDbField(Class<?> type) throws NotSupportTypeException {
		if (type == Integer.class) {
			return new DbFieldDataInteger(type);
		} else if (type == String.class) {
			return new DbFieldDataString(type);
		} else if (type == BigDecimal.class) {
			return new DbFieldDataDecimal(type);
		} else if (type == Double.class) {
			return new DbFieldDataDouble(type);
		} else if (type == DateTime.class || type == java.util.Date.class) {
			return new DbFieldDataDateTime(type);
		} else if (type == Short.class) {
			return new DbFieldDataShort(type);
		} else if (type == Character.class) {
			return new DbFieldDataChar(type);
		} else if (type == Float.class) {
			return new DbFieldDataFloat(type);
		} else if (type == Boolean.class) {
			return new DbFieldDataBoolean(type);
		} else if (type.isEnum()) {// 判断是否为枚举
			return new DbFieldDataEnum(type);
		} else {
			throw new NotSupportTypeException(I18N.prop("msg_bobas_data_type_not_support", type));
		}
	}

	public IFieldData create(IPropertyInfo<?> property) throws NotSupportTypeException {
		PropertyInfo<?> cProperty = (PropertyInfo<?>) property;
		DbField dbField = cProperty.getAnnotation(DbField.class);
		if (dbField != null) {
			// 数据库字段
			FieldDataDbBase<?> fieldData = this.createDbField(property.getValueType());
			fieldData.mapping(cProperty);
			fieldData.mapping(dbField);
			return fieldData;
		}
		Associations associations = cProperty.getAnnotation(Associations.class);
		if (associations != null) {
			// 关联字段
			AssociatedFieldDataBase<?> fieldData = this.createAssociatedField(associations.mode(),
					associations.value().length);
			fieldData.mapping(cProperty);
			fieldData.mapping(associations);
			return fieldData;
		}
		ComplexField complexField = cProperty.getAnnotation(ComplexField.class);
		if (complexField != null) {
			// 复合字段
			ComplexFieldDataBase<?> fieldData = this.createComplexField(complexField.type());
			fieldData.mapping(cProperty);
			fieldData.mapping(complexField);
			return fieldData;
		}
		// 普通字段
		FieldDataBase<?> fieldData = this.createField(cProperty.getValueType());
		fieldData.mapping(cProperty);
		return fieldData;
	}
}
