package org.colorcoding.ibas.bobas.core;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.colorcoding.ibas.bobas.bo.IBusinessObjects;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;

/**
 * 属性管理员
 * 
 * @author Niuren.Zhu
 *
 */
public final class PropertyInfoManager {

	private PropertyInfoManager() {
	}

	static final String BO_PROPERTY_NAMING_RULES_UPPER = "PROPERTY_%s";

	static final String BO_PROPERTY_NAMING_RULES_CAMEL = "%sProperty";

	private final static class PropertyInfoList extends ArrayList<PropertyInfo<?>> {

		public static final PropertyInfoList EMPTY = new PropertyInfoList(0);

		private static final long serialVersionUID = -6320914335175298868L;

		public PropertyInfoList(int initialCapacity) {
			super(initialCapacity);
			this.keys = new HashSet<String>(initialCapacity);
		}

		private Set<String> keys;

		public synchronized boolean add(PropertyInfo<?> e) {
			if (this.keys == null) {
				// 被清理，则重建
				this.keys = new HashSet<String>(this.size());
				for (IPropertyInfo<?> item : this) {
					this.keys.add(item.getName());
				}
			}
			if (this.keys.contains(e.getName())) {
				return false;
			}
			this.keys.add(e.getName());
			return super.add(e);
		}

		public synchronized void recycling() {
			if (this.keys == null) {
				return;
			}
			this.keys = null;
			this.trimToSize();
		}

		private boolean resolved = false;

		public synchronized void resolving(Class<?> belong) {
			if (belong == null) {
				return;
			}
			if (this.resolved) {
				return;
			}
			Field field;
			Field[] fields = belong.getFields();
			if (fields != null && fields.length > 0) {
				for (PropertyInfo<?> property : this) {
					for (int i = 0; i < fields.length; i++) {
						field = fields[i];
						if (field == null) {
							continue;
						}
						if (!Modifier.isStatic(field.getModifiers())) {
							continue;
						}
						if (!Modifier.isPublic(field.getModifiers())) {
							continue;
						}
						if (!Modifier.isFinal(field.getModifiers())) {
							continue;
						}
						if (Strings.equalsIgnoreCase(field.getName(),
								Strings.format(BO_PROPERTY_NAMING_RULES_UPPER, property.getName().toUpperCase()))
								|| Strings.equalsIgnoreCase(field.getName(),
										Strings.format(BO_PROPERTY_NAMING_RULES_CAMEL, property.getName()))) {
							property.setAnnotations(field.getAnnotations());
						}
					}
				}

			}
		}

	}

	private final static class PropertyMap extends java.util.HashMap<IPropertyInfo<?>, Object> {

		private static final long serialVersionUID = 1L;

		public PropertyMap(int initialCapacity, float loadFactor) {
			super(initialCapacity, loadFactor);
		}

		@Override
		public String toString() {
			Iterator<Entry<IPropertyInfo<?>, Object>> i = entrySet().iterator();
			if (!i.hasNext())
				return "{}";
			int index = 0;
			StringBuilder sb = new StringBuilder();
			sb.append('{');
			for (;;) {
				Entry<IPropertyInfo<?>, Object> e = i.next();
				IPropertyInfo<?> key = e.getKey();
				Object value = e.getValue();
				sb.append('[');
				sb.append(index);
				sb.append(']');
				sb.append('.');
				sb.append(key.getName());
				// sb.append(' ');
				sb.append('=');
				// sb.append(' ');
				if (value == this) {
					sb.append("(this Map)");
				} else if (value instanceof IBusinessObjects) {
					IBusinessObjects<?, ?> items = (IBusinessObjects<?, ?>) value;
					if (items.getElementType() != null) {
						sb.append('{');
						sb.append(items.getElementType().getSimpleName());
						sb.append('}');
						sb.append('(');
						sb.append(items.size());
						sb.append(')');
					} else {
						sb.append('{');
						sb.append(items.getClass().getSimpleName());
						sb.append('(');
						sb.append(items.size());
						sb.append(')');
						sb.append('}');
					}
				} else {
					sb.append(value);
				}
				if (!i.hasNext())
					return sb.append('}').toString();
				sb.append(',').append(' ');
				index++;
			}
		}
	}

	private volatile static Map<Class<?>, PropertyInfoList> PROPERTY_INFOS = new ConcurrentHashMap<Class<?>, PropertyInfoList>(
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
			if (PROPERTY_INFOS.containsKey(objectType)) {
				PropertyInfoList propertys = PROPERTY_INFOS.get(objectType);
				propertys.add(property);
				property.setIndex(propertys.size() - 1);
				if (property.getIndex() > 0) {
					// 优化内存消耗，使用上一个
					property.resolver = propertys.get(property.getIndex() - 1).resolver;
				} else {
					property.resolver = () -> {
						propertys.resolving(objectType);
					};
				}
			} else {
				// 获取父类的属性定义
				PropertyInfoList propertys = recursePropertyInfos(objectType.getSuperclass());
				PROPERTY_INFOS.put(objectType, propertys);
				// 添加当前属性
				propertys.add(property);
				property.setIndex(propertys.size() - 1);
				property.resolver = () -> {
					propertys.resolving(objectType);
				};
				// 触发对象注册通知
				firePropertyInfoRegistered(objectType);
			}
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
	static PropertyInfoList recursePropertyInfos(Class<?> objectType) {
		PropertyInfoList propertys = new PropertyInfoList(32);
		if (objectType == null) {
			return propertys;
		}
		if (objectType == FieldedObject.class) {
			return propertys;
		}
		if (PROPERTY_INFOS.containsKey(objectType)) {
			propertys.addAll(PROPERTY_INFOS.get(objectType));
		}
		return propertys;
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
						return PropertyInfoList.EMPTY;
					}
					if (PROPERTY_INFOS.containsKey(superClass)) {
						return PROPERTY_INFOS.get(superClass);
					}
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
		synchronized (propertyInfoList) {
			propertyInfoList.resolving(objectType);
			propertyInfoList.recycling();

			Map<IPropertyInfo<?>, Object> fieldsMap = new PropertyMap(propertyInfoList.size(), 1);
			for (IPropertyInfo<?> propertyInfo : propertyInfoList) {
				fieldsMap.put(propertyInfo, null);
			}
			return fieldsMap;
		}
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
		}
	}

	private static void firePropertyInfoRegistered(Class<?> clazz) {
		for (PropertyInfoRegisterListener listener : listeners) {
			if (listener != null) {
				listener.onRegistered(clazz);
			}
		}
	}

	public static Function<Class<?>, Collection<IPropertyInfo<?>>> createFetcher() {
		return new Function<Class<?>, Collection<IPropertyInfo<?>>>() {
			@Override
			public List<IPropertyInfo<?>> apply(Class<?> objectType) {
				if (!PROPERTY_INFOS.containsKey(objectType)) {
					// 不包含，可能类还未加载
					if (FieldedObject.class.isAssignableFrom(objectType)) {
						try {
							// 使用加载业务对象的类加载器
							Class.forName(objectType.getName(), true, objectType.getClassLoader());
						} catch (ClassNotFoundException e) {
							Logger.log(MessageLevel.FATAL, e);
							StringBuilder builder = new StringBuilder();
							builder.append("Class not found. Classpath:");
							ClassLoader cl = ClassLoader.getSystemClassLoader();
							URL[] urls = ((URLClassLoader) cl).getURLs();
							for (URL url : urls) {
								builder.append(url.getFile());
							}
							Logger.log(MessageLevel.FATAL, builder.toString());
						}
					}
				}
				PropertyInfoList propertyInfoList = PROPERTY_INFOS.get(objectType);
				if (propertyInfoList == null) {
					return new ArrayList<IPropertyInfo<?>>(0);
				}
				synchronized (propertyInfoList) {
					propertyInfoList.recycling();
					return new ArrayList<IPropertyInfo<?>>(propertyInfoList);
				}
			}
		};
	}
}
