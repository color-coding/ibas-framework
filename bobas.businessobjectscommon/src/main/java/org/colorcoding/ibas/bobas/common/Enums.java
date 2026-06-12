package org.colorcoding.ibas.bobas.common;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.KeyValue;

public class Enums {

	private Enums() {
	}

	private static Map<Class<?>, Enum<?>> DEFAULT_VALUES = new ConcurrentHashMap<>(128);

	/**
	 * 类型默认值
	 *
	 * @param enumType 枚举类型；不允许为null，非枚举抛IllegalArgumentException
	 * @return 枚举的第一个值（缓存）
	 */
	public static Enum<?> defaultValue(Class<?> enumType) {
		Objects.requireNonNull(enumType);
		if (!enumType.isEnum()) {
			throw new IllegalArgumentException(Strings.format("type [%s] is not Enum.", enumType.getName()));
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
	 * 获取枚举值的@Value注解标记值
	 *
	 * @param value 枚举实例；null返回null
	 * @return 注解值；非枚举或无注解返回null
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
	 * @param type  枚举类型；null返回null
	 * @param value 字符串（支持枚举名称、@Value注解值、ordinal索引）；null返回null
	 * @return 匹配的枚举值；未匹配返回null
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
		Enum<?> itemValue;
		for (Object enumItem : type.getEnumConstants()) {
			if (value.equalsIgnoreCase(enumItem.toString())) {
				// 枚举的字符串
				return (T) enumItem;
			}
			if (value.equalsIgnoreCase(annotationValue(enumItem))) {
				// 枚举的dbValue
				return (T) enumItem;
			}
			if (Numbers.isNumeric(value) && enumItem instanceof Enum<?>) {
				itemValue = (Enum<?>) enumItem;
				if (itemValue.ordinal() == Integer.valueOf(value)) {
					return (T) enumItem;
				}
			}
		}
		return null;
	}

	/**
	 * 获取枚举值
	 *
	 * @param type  枚举类型；null返回null
	 * @param value 枚举的ordinal索引
	 * @return 匹配的枚举值；未匹配返回null
	 */
	@SuppressWarnings("unchecked")
	public static <T> T valueOf(Class<?> type, int value) {
		// 无效值
		if (type == null || !type.isEnum()) {
			return null;
		}
		Enum<?> itemValue;
		for (Object enumItem : type.getEnumConstants()) {
			if (enumItem instanceof Enum<?>) {
				// 枚举值（比对枚举索引）
				itemValue = (Enum<?>) enumItem;
				if (itemValue.ordinal() == value) {
					return (T) enumItem;
				}
			}
		}
		return null;
	}

	/**
	 * 转换枚举类型为KeyValue数组
	 *
	 * @param enumType 枚举类型；不允许为null，非枚举抛IllegalArgumentException
	 * @return KeyValue数组（key=枚举名，value=ordinal）
	 */
	public static KeyValue[] toKeyValues(Class<?> enumType) {
		Objects.requireNonNull(enumType);
		if (!enumType.isEnum()) {
			throw new IllegalArgumentException(Strings.format("type [%s] is not Enum.", enumType.getName()));
		}
		Object[] constants = enumType.getEnumConstants();
		ArrayList<KeyValue> values = new ArrayList<>();
		if (constants.length > 0) {
			for (int i = 0; i < constants.length; i++) {
				Enum<?> item = (Enum<?>) constants[i];
				values.add(new KeyValue(item.name(), item.ordinal()));
			}
		}
		return values.toArray(new KeyValue[values.size()]);
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
