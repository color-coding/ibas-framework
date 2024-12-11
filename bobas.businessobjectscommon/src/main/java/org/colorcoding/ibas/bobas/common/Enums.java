package org.colorcoding.ibas.bobas.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
	 * 转换类型为KeyValue
	 * 
	 * @param type 目前可识别的类型：枚举类型
	 * @return
	 */
	public static KeyValue[] toKeyValues(Class<?> enumType) {
		Objects.requireNonNull(enumType);
		if (!enumType.isEnum()) {
			throw new ClassCastException("is not Enum.");
		}
		Object[] constants = enumType.getEnumConstants();
		if (constants.length > 0) {
			KeyValue[] values = new KeyValue[constants.length];
			for (int i = 0; i < values.length; i++) {
				Enum<?> item = (Enum<?>) constants[i];
				values[i] = new KeyValue(item.name(), item.ordinal());
			}
			return values;
		}
		return new KeyValue[] {};
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
