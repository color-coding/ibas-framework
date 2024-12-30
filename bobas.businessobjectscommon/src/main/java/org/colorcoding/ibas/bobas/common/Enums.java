package org.colorcoding.ibas.bobas.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.KeyValue;

public class Enums {

	private Enums() {
	}

	private static Map<Class<?>, Enum<?>> DEFAULT_VALUES = new HashMap<>(32);

	/**
	 * 类型默认值
	 * 
	 * @return
	 */
	public static Enum<?> defaultValue(Class<?> enumType) {
		Objects.requireNonNull(enumType);
		if (!enumType.isEnum()) {
			throw new ClassCastException("is not Enum.");
		}
		if (!DEFAULT_VALUES.containsKey(enumType)) {
			// 获取枚举的第一个值
			for (Object item : enumType.getEnumConstants()) {
				DEFAULT_VALUES.put(enumType, (Enum<?>) item);
				break;
			}
		}
		return DEFAULT_VALUES.get(enumType);
	}

	/**
	 * 属性标记值
	 * 
	 * @param value
	 * @return
	 */
	public static String annotationValue(Object value) {
		if (value != null) {
			Class<?> valueType = value.getClass();
			if (valueType.isEnum()) {
				for (java.lang.reflect.Field field : valueType.getDeclaredFields()) {
					Value annotation = field.getAnnotation(Value.class);
					if (annotation != null) {
						if (field.getName().equals(value.toString())) {
							return annotation.value();
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * 获取枚举值
	 * 
	 * @param type  枚举类型
	 * @param value 字符串（包括dbValue）
	 * @return 字符串对应的枚举值
	 */
	@SuppressWarnings("unchecked")
	public static <T> T valueOf(Class<?> type, String value) {
		// 无效值
		if (type == null || value == null) {
			return null;
		}
		// 不是枚举
		if (!type.isEnum()) {
			return null;
		}
		for (Object enumItem : type.getEnumConstants()) {
			if (value.equalsIgnoreCase(enumItem.toString())) {
				// 枚举的字符串
				return (T) enumItem;
			}
			String dbValue = annotationValue(enumItem);
			if (value.equalsIgnoreCase(dbValue)) {
				// 枚举的dbValue
				return (T) enumItem;
			}
		}
		return null;
	}

	/**
	 * 获取枚举值
	 * 
	 * @param type  枚举类型
	 * @param value 枚举的数值（枚举值或索引）
	 * @return 数值对应的枚举值
	 */
	@SuppressWarnings("unchecked")
	public static <T> T valueOf(Class<?> type, int value) {
		// 无效值
		if (type == null || !type.isEnum()) {
			return null;
		}
		for (Object enumItem : type.getEnumConstants()) {
			if (enumItem instanceof Enum<?>) {
				// 枚举值（比对枚举索引）
				Enum<?> itemValue = (Enum<?>) enumItem;
				if (itemValue.ordinal() == value) {
					return (T) enumItem;
				}
			}
		}
		return null;
	}

	/**
	 * 转换类型为KeyValue
	 * 
	 * @param type 目前可识别的类型：枚举类型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static KeyValue<Integer>[] toKeyValues(Class<?> enumType) {
		Objects.requireNonNull(enumType);
		if (!enumType.isEnum()) {
			throw new ClassCastException("is not Enum.");
		}
		Object[] constants = enumType.getEnumConstants();
		ArrayList<KeyValue<Integer>> values = new ArrayList<>();
		if (constants.length > 0) {
			for (int i = 0; i < constants.length; i++) {
				Enum<?> item = (Enum<?>) constants[i];
				values.add(new KeyValue<Integer>(item.name(), item.ordinal()));
			}
		}
		return (KeyValue<Integer>[]) values.toArray(new KeyValue<?>[] {});
	}

	public static boolean equals(Object a, Object b) {
		if (a == null || b == null) {
			return false;
		}
		if (a == b) {
			return true;
		}
		if (a.getClass() == b.getClass()) {
			if (Numbers.equals(a, b)) {
				return true;
			}
		}
		return false;
	}
}
