package org.colorcoding.ibas.bobas.core.fields;

import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.IBusinessObjectListBase;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.core.PropertyInfo;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.i18n.i18n;
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
		} else if (type == Decimal.class || type == java.math.BigDecimal.class) {
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
		} else if (IBusinessObjectListBase.class.isAssignableFrom(type)) {
			return new FieldDataBOs(type);
		} else {
			throw new NotSupportTypeException(i18n.prop("msg_bobas_data_type_not_support", type));
		}
	}

	public AssociatedFieldDataBase<?> createAssociatedField(AssociationMode mode, int assoCount)
			throws NotSupportTypeException {
		if (mode == AssociationMode.OneToOne || mode == AssociationMode.OneToZero) {
			return new AssociatedFieldDataBO(assoCount);
		} else if (mode == AssociationMode.OneToMany) {
			return new AssociatedFieldDataArray(assoCount);
		} else {
			throw new NotSupportTypeException(i18n.prop("msg_bobas_association_mode_not_support", mode));
		}
	}

	public ComplexFieldDataBase<?> createComplexField(ComplexFieldType type) throws NotSupportTypeException {
		if (type == ComplexFieldType.cp_Measurement) {
			return new ComplexFieldDataMeasurement();
		} else {
			throw new NotSupportTypeException(i18n.prop("msg_bobas_association_mode_not_support", type));
		}
	}

	public FieldDataDbBase<?> createDbField(Class<?> type) throws NotSupportTypeException {
		if (type == Integer.class) {
			return new DbFieldDataInteger(type);
		} else if (type == String.class) {
			return new DbFieldDataString(type);
		} else if (type == Decimal.class || type == java.math.BigDecimal.class) {
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
			throw new NotSupportTypeException(i18n.prop("msg_bobas_data_type_not_support", type));
		}
	}

	public IFieldData create(IPropertyInfo<?> property) throws NotSupportTypeException {
		IFieldData fieldData = null;
		PropertyInfo<?> cProperty = (PropertyInfo<?>) property;
		Object dbAnnotation = cProperty.getAnnotation(DbField.class);
		Object assoAnnotation = cProperty.getAnnotation(Associations.class);
		Object cmplAnnotation = cProperty.getAnnotation(ComplexField.class);
		if (dbAnnotation != null) {
			// 数据库字段
			DbField dbField = (DbField) dbAnnotation;
			FieldDataDbBase<?> tmpFieldData = this.createDbField(property.getValueType());
			tmpFieldData.mapping(cProperty);
			tmpFieldData.mapping(dbField);
			fieldData = tmpFieldData;
		} else if (assoAnnotation != null) {
			// 关联字段
			Associations associations = (Associations) assoAnnotation;
			AssociatedFieldDataBase<?> tmpFieldData = this.createAssociatedField(associations.mode(),
					associations.value().length);
			tmpFieldData.mapping(cProperty);
			tmpFieldData.mapping(associations);
			fieldData = tmpFieldData;
		} else if (cmplAnnotation != null) {
			// 复合字段
			ComplexField complexField = (ComplexField) cmplAnnotation;
			ComplexFieldDataBase<?> tmpFieldData = this.createComplexField(complexField.type());
			tmpFieldData.mapping(cProperty);
			tmpFieldData.mapping(complexField);
			fieldData = tmpFieldData;
		} else {
			// 普通字段
			FieldDataBase<?> tmpFieldData = this.createField(cProperty.getValueType());
			tmpFieldData.mapping(cProperty);
			fieldData = tmpFieldData;
		}
		return fieldData;
	}
}
