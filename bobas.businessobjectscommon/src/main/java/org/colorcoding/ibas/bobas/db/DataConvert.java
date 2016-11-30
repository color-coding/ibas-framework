package org.colorcoding.ibas.bobas.db;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.IEnumItem;
import org.colorcoding.ibas.bobas.mapping.DbValue;

public class DataConvert extends org.colorcoding.ibas.bobas.data.DataConvert {

	/**
	 * 获取值的数据库值
	 * 
	 * @param value
	 * @return
	 */
	public static String toDbValue(Object value) {
		if (value == null) {
			return null;
		}
		Class<?> valueType = value.getClass();
		if (valueType.isEnum()) {
			for (Field field : valueType.getDeclaredFields()) {
				Annotation annotation = field.getAnnotation(DbValue.class);
				if (annotation != null) {
					DbValue dbValue = (DbValue) annotation;
					if (field.getName().equals(value.toString())) {
						return dbValue.value();
					}
				}
			}
		} else if (valueType == DateTime.class) {
			// 日期类型的，最小值认为是空
			DateTime dateTime = (DateTime) value;
			if (dateTime.equals(DateTime.minValue)) {
				return null;
			}
		} else if (valueType == Decimal.class) {
			Decimal decimal = (Decimal) value;
			return Decimal.round(decimal, Decimal.RESERVED_DECIMAL_PLACES_STORAGE).toString();
		}
		return value.toString();
	}

	/**
	 * 获取枚举值
	 * 
	 * @param type
	 *            枚举类型
	 * @param value
	 *            字符串（包括dbValue）
	 * @return 字符串对应的枚举值
	 */
	public static Object toEnumValue(Class<?> type, String value) {
		// 无效值
		if (type == null || value == null) {
			return null;
		}
		// 不是枚举
		if (!type.isEnum()) {
			return null;
		}
		for (Object enumItem : type.getEnumConstants()) {
			if (value.equals(enumItem.toString())) {
				// 枚举的字符串
				return enumItem;
			}
			String dbValue = toDbValue(enumItem);
			if (value.equals(dbValue)) {
				// 枚举的dbValue
				return enumItem;
			}
		}
		return null;
	}

	/**
	 * 获取枚举值
	 * 
	 * @param type
	 *            枚举类型
	 * @param value
	 *            枚举的数值（枚举值或索引）
	 * @return 数值对应的枚举值
	 */
	public static Object toEnumValue(Class<?> type, int value) {
		// 无效值
		if (type == null || !type.isEnum()) {
			return null;
		}
		for (Object enumItem : type.getEnumConstants()) {
			if (enumItem instanceof IEnumItem) {
				// 枚举条目（比对枚举值）
				IEnumItem itemValue = (IEnumItem) enumItem;
				if (itemValue.getValue() == value) {
					return enumItem;
				}
			} else if (enumItem instanceof Enum<?>) {
				// 枚举值（比对枚举索引）
				Enum<?> itemValue = (Enum<?>) enumItem;
				if (itemValue.ordinal() == value) {
					return enumItem;
				}
			}
		}
		return null;
	}
}
