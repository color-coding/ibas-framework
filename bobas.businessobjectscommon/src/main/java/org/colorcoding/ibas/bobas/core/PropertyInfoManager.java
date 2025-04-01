package org.colorcoding.ibas.bobas.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.colorcoding.ibas.bobas.i18n.I18N;

/**
 * 属性管理员
 * 
 * @author Niuren.Zhu
 *
 */
public class PropertyInfoManager {

	private PropertyInfoManager() {
	}

	static final String BO_PROPERTY_NAMING_RULES_UPPER = "PROPERTY_%s";

	static final String BO_PROPERTY_NAMING_RULES_CAMEL = "%sProperty";

	private static class PropertyInfoList extends ArrayList<IPropertyInfo<?>> {

		private static final long serialVersionUID = -6320914335175298868L;

		public PropertyInfoList(int initialCapacity) {
			super(initialCapacity);
		}

		public synchronized boolean add(IPropertyInfo<?> e) {
			for (IPropertyInfo<?> item : this) {
				if (item.getName().equals(e.getName())) {
					return false;
				}
			}
			return super.add(e);
		}
	}

	private volatile static Map<Class<?>, PropertyInfoList> PROPERTY_INFOS = new HashMap<Class<?>, PropertyInfoList>(
			256);

	static <P> PropertyInfo<P> createProperty(String name, Class<P> type) {
		PropertyInfo<P> property = new PropertyInfo<P>(name, type);
		return property;
	}

	/**
	 * 注册属性
	 * 
	 * @param objectType 所属类型
	 * @param property   属性
	 */
	static <P> IPropertyInfo<P> registerProperty(Class<?> objectType, PropertyInfo<P> property) {
		synchronized (PROPERTY_INFOS) {
			PropertyInfoList propertys = PROPERTY_INFOS.get(objectType);
			if (propertys != null) {
				propertys.add(property);
				property.setIndex(propertys.size() - 1);
			} else {
				propertys = new PropertyInfoList(32);
				// 获取父类的属性定义
				for (IPropertyInfo<?> item : recursePropertyInfos(objectType.getSuperclass())) {
					propertys.add(item);
				}
				// 添加当前属性
				propertys.add(property);
				property.setIndex(propertys.size() - 1);
				PROPERTY_INFOS.put(objectType, propertys);
				// 触发对象注册通知
				firePropertyInfoRegistered(objectType);
			}
			// 触发属性注册通知
			firePropertyInfoRegistered(objectType, property);
		}
		// 获取属性的注释
		try {
			Field pField = objectType
					.getField(String.format(BO_PROPERTY_NAMING_RULES_UPPER, property.getName().toUpperCase()));
			if (pField != null) {
				property.setAnnotations(pField.getAnnotations());
			}
		} catch (NoSuchFieldException e) {
			try {
				Field pField = objectType.getField(String.format(BO_PROPERTY_NAMING_RULES_CAMEL, property.getName()));
				if (pField != null) {
					property.setAnnotations(pField.getAnnotations());
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return property;
	}

	/**
	 * 注册属性
	 * 
	 * @param objectType 所属类型
	 * @param name       属性名称
	 * @param type       属性类型
	 * @return
	 */
	static <P> IPropertyInfo<P> registerProperty(Class<?> objectType, String name, Class<P> type) {
		return registerProperty(objectType, createProperty(name, type));
	}

	/**
	 * 注册属性
	 * 
	 * @param objectType   所属类型
	 * @param name         属性名称
	 * @param type         属性类型
	 * @param defaultValue 属性默认值
	 * @return
	 */
	static <P> IPropertyInfo<P> registerProperty(Class<?> objectType, String name, Class<P> type, P defaultValue) {
		PropertyInfo<P> property = createProperty(name, type);
		property.setDefaultValue(defaultValue);
		return registerProperty(objectType, property);
	}

	/**
	 * 获取属性列表（包含父项类型）
	 * 
	 * @param objectType 获取的对象类型
	 * @return
	 */
	static IPropertyInfo<?>[] recursePropertyInfos(Class<?> objectType) {
		if (objectType == null) {
			return new IPropertyInfo<?>[] {};
		}
		if (objectType == FieldedObject.class) {
			return new IPropertyInfo<?>[] {};
		}
		PropertyInfoList propertys = new PropertyInfoList(32);
		// 获取当前类的属性
		try {
			for (IPropertyInfo<?> item : getPropertyInfoList(objectType)) {
				propertys.add(item);
			}
		} catch (Exception e) {
			// 此项没有注册
		}
		// 获取父类的属性
		Class<?> parent = objectType.getSuperclass();
		if (parent != null) {
			for (IPropertyInfo<?> item : recursePropertyInfos(parent)) {
				propertys.add(item);
			}
		}
		return propertys.toArray(new IPropertyInfo<?>[] {});
	}

	/**
	 * 获取属性列表
	 * 
	 * @param objectType 类型
	 * @return 类型的属性列表
	 * @throws IllegalArgumentException
	 */
	static PropertyInfoList getPropertyInfoList(Class<?> objectType) {
		synchronized (PROPERTY_INFOS) {
			PropertyInfoList propertyInfoList = PROPERTY_INFOS.get(objectType);
			if (propertyInfoList != null) {
				return propertyInfoList;
			} else {
				// 未注册，检索父项是否注册
				Class<?> superClass = objectType.getSuperclass();
				while (superClass != null) {
					if (superClass == FieldedObject.class) {
						return new PropertyInfoList(0);
					}
					if (PROPERTY_INFOS.containsKey(superClass))
						return PROPERTY_INFOS.get(superClass);
					superClass = superClass.getSuperclass();
				}
			}
			throw new IllegalArgumentException(I18N.prop("msg_bobas_not_register_bo_type", objectType.getName()));
		}
	}

	/**
	 * 初始化对象字段
	 * 
	 * @param objectType 对象类型
	 * @return
	 */
	static Map<IPropertyInfo<?>, Object> initFields(Class<?> objectType) {
		PropertyInfoList propertyInfoList = getPropertyInfoList(objectType);
		Map<IPropertyInfo<?>, Object> fieldsMap = new HashMap<>(propertyInfoList.size(), 1);
		for (IPropertyInfo<?> propertyInfo : propertyInfoList) {
			fieldsMap.put(propertyInfo, null);
		}
		return fieldsMap;
	}

	private static ArrayList<PropertyInfoRegisterListener> listeners = new ArrayList<>(4);

	public static void registerListener(PropertyInfoRegisterListener listener) {
		// 要求有值
		Objects.requireNonNull(listener);
		// 监听
		listeners.add(listener);
		// 通知已创建的
		for (Entry<Class<?>, PropertyInfoList> infoEntry : PROPERTY_INFOS.entrySet()) {
			listener.onRegistered(infoEntry.getKey());
			for (IPropertyInfo<?> propertyInfo : infoEntry.getValue()) {
				listener.onRegistered(infoEntry.getKey(), propertyInfo);
			}
		}
	}

	private static void firePropertyInfoRegistered(Class<?> clazz) {
		for (PropertyInfoRegisterListener listener : listeners) {
			if (listener != null) {
				listener.onRegistered(clazz);
			}
		}
	}

	private static void firePropertyInfoRegistered(Class<?> clazz, IPropertyInfo<?> propertyInfo) {
		for (PropertyInfoRegisterListener listener : listeners) {
			if (listener != null) {
				listener.onRegistered(clazz, propertyInfo);
			}
		}
	}

}
