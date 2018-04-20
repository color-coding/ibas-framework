package org.colorcoding.ibas.bobas.core;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.colorcoding.ibas.bobas.bo.BusinessObject;
import org.colorcoding.ibas.bobas.core.fields.NotRegisterTypeException;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;

/**
 * 属性管理员
 * 
 * @author Niuren.Zhu
 *
 */
public class PropertyInfoManager {

	protected static final String MSG_PROPERTIES_GET_TYPE_PROPERTIES = "properties: get type [%s]'s properties.";
	protected static final String MSG_PROPERTIES_REGISTER_PROPERTIES = "properties: register type [%s]'s property [%s].";

	public static final String BO_PROPERTY_NAMING_RULES_UPPER = "PROPERTY_%s";
	public static final String BO_PROPERTY_NAMING_RULES_CAMEL = "%sProperty";

	volatile private static HashMap<Class<?>, PropertyInfoList> PROPERTY_INFOS = new HashMap<Class<?>, PropertyInfoList>();

	/**
	 * 注册属性
	 * 
	 * @param boType
	 *            所属类型
	 * @param property
	 *            属性
	 */
	public static void registerProperty(Class<?> boType, PropertyInfo<?> property) {
		// Logger.log(MessageLevel.DEBUG, MSG_PROPERTIES_REGISTER_PROPERTIES,
		// boType.getName(), property.getName());
		synchronized (PROPERTY_INFOS) {
			PropertyInfoList propertys = PROPERTY_INFOS.get(boType);
			if (propertys != null) {
				propertys.add(property);
				property.setIndex(propertys.size() - 1);// 没排序，坑
			} else {
				propertys = new PropertyInfoList();
				// 获取父类的属性定义
				for (IPropertyInfo<?> item : recursePropertyInfos(boType.getSuperclass())) {
					propertys.add(item);
				}
				// 添加当前属性
				propertys.add(property);
				property.setIndex(propertys.size() - 1);
				PROPERTY_INFOS.put(boType, propertys);
			}
		}
		// 获取属性的注释
		try {
			Field pField = boType
					.getField(String.format(BO_PROPERTY_NAMING_RULES_UPPER, property.getName().toUpperCase()));
			if (pField != null) {
				property.addAnnotation(pField.getAnnotations());
			}
		} catch (NoSuchFieldException e) {
			try {
				Field pField = boType.getField(String.format(BO_PROPERTY_NAMING_RULES_CAMEL, property.getName()));
				if (pField != null) {
					property.addAnnotation(pField.getAnnotations());
				}
			} catch (Exception e2) {
				Logger.log(e);
			}
		} catch (Exception e) {
			Logger.log(e);
		}
	}

	/**
	 * 注册属性
	 * 
	 * @param boType
	 *            所属类型
	 * @param name
	 *            属性名称
	 * @param type
	 *            属性类型
	 * @return
	 */
	public static <P> IPropertyInfo<P> registerProperty(Class<?> boType, String name, Class<P> type) {
		PropertyInfo<P> property = new PropertyInfo<P>(name, type);
		registerProperty(boType, property);
		return property;
	}

	/**
	 * 注册属性
	 * 
	 * @param boType
	 *            所属类型
	 * @param name
	 *            属性名称
	 * @param type
	 *            属性类型
	 * @param defaultValue
	 *            属性默认值
	 * @return
	 */
	public static <P> IPropertyInfo<P> registerProperty(Class<?> boType, String name, Class<P> type, P defaultValue) {
		PropertyInfo<P> property = new PropertyInfo<P>(name, type);
		property.setDefaultValue(defaultValue);
		registerProperty(boType, property);
		return property;
	}

	/**
	 * 获取属性列表（包含父项类型）
	 * 
	 * @param boType
	 *            获取的对象类型
	 * @return
	 */
	public static IPropertyInfo<?>[] recursePropertyInfos(Class<?> boType) {
		if (boType == null) {
			return new IPropertyInfo<?>[] {};
		}
		if (boType == BusinessObjectBase.class || boType == BusinessObject.class) {
			return new IPropertyInfo<?>[] {};
		}
		PropertyInfoList propertys = new PropertyInfoList();
		// 获取当前类的属性
		try {
			for (IPropertyInfo<?> item : getPropertyInfoList(boType)) {
				propertys.add(item);
			}
		} catch (Exception e) {
			// 此项没有注册
		}
		// 获取父类的属性
		Class<?> parent = boType.getSuperclass();
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
	 * @param boType
	 *            类型
	 * @return 类型的属性列表
	 * @throws NotRegisterTypeException
	 */
	public static PropertyInfoList getPropertyInfoList(Class<?> boType) throws NotRegisterTypeException {
		// Logger.log(MessageLevel.DEBUG, MSG_PROPERTIES_GET_TYPE_PROPERTIES,
		// boType.getName());
		synchronized (PROPERTY_INFOS) {
			PropertyInfoList propertyInfoList = PROPERTY_INFOS.get(boType);
			if (propertyInfoList != null) {
				return propertyInfoList;
			} else {
				if (registerClass(boType)) {
					propertyInfoList = PROPERTY_INFOS.get(boType);
					if (propertyInfoList != null) {
						return propertyInfoList;
					} else {
						// 未注册，检索父项是否注册
						Class<?> superClass = boType.getSuperclass();
						while (superClass != null) {
							if (PROPERTY_INFOS.containsKey(superClass))
								return PROPERTY_INFOS.get(superClass);
							superClass = boType.getSuperclass();
						}
					}
				}
			}
			throw new NotRegisterTypeException(I18N.prop("msg_bobas_not_register_bo_type", boType.getName()));
		}
	}

	/**
	 * 注册类型的属性
	 * 
	 * @param boType
	 *            类型
	 * @return 是否注册成功
	 */
	public static boolean registerClass(Class<?> boType) {
		return registerClass(boType.getName());
	}

	/**
	 * 注册类型的属性
	 * 
	 * @param className
	 *            类型名称
	 * @return 是否注册成功
	 */
	public static boolean registerClass(String className) {
		try {
			BOFactory.create().loadClass(className);
			return true;
		} catch (ClassNotFoundException e) {
			Logger.log(e);
		}
		return false;
	}
}
