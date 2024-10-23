package org.colorcoding.ibas.bobas.bo;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.FieldedObject;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.db.DbField;
import org.colorcoding.ibas.bobas.db.DbFieldType;

public class UserFieldsManager {

	private UserFieldsManager() {
	}

	private volatile static Map<Class<?>, UserFieldInfoList> USER_FIELDS = new HashMap<Class<?>, UserFieldInfoList>();

	public static class UserFieldInfoList extends ArrayList<IPropertyInfo<?>> {

		private static final long serialVersionUID = -6320914335175298868L;

		public UserFieldInfoList() {
			super();
		}

		public UserFieldInfoList(int initialCapacity) {
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

	public static class UserFieldInfo<P> implements IPropertyInfo<P> {

		public UserFieldInfo(String name, Class<P> type) {
			this.setName(name);
			this.setValueType(type);
		}

		private String name = Strings.VALUE_EMPTY;

		public final String getName() {
			return this.name;
		}

		public final void setName(String name) {
			this.name = name;
		}

		private int index = -1;

		public final int getIndex() {
			return this.index;
		}

		public final void setIndex(int value) {
			this.index = value;
		}

		private Class<P> valueType = null;

		public final Class<P> getValueType() {
			return this.valueType;
		}

		public final void setValueType(Class<P> value) {
			this.valueType = value;
		}

		@Override
		public P getDefaultValue() {
			return null;
		}

		@Override
		public boolean isPrimaryKey() {
			return false;
		}

		@Override
		public boolean isUniqueKey() {
			return false;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <A extends Annotation> A getAnnotation(Class<A> type) {
			if (type == DbField.class) {
				return (A) new DbField() {

					@Override
					public Class<? extends Annotation> annotationType() {
						return DbField.class;
					}

					@Override
					public String name() {
						return UserFieldInfo.this.getName();
					}

					@Override
					public DbFieldType type() {
						return DbFieldType.valueOf(UserFieldInfo.this.getValueType());
					}

					@Override
					public String table() {
						return Strings.VALUE_EMPTY;
					}

					@Override
					public boolean primaryKey() {
						return false;
					}

					@Override
					public boolean uniqueKey() {
						return false;
					}
				};
			}
			return null;
		}

		@Override
		public final String toString() {
			return String.format("{userField: %s}", this.getName());
		}
	}

	protected static <P> UserFieldInfo<P> createUserField(String name, Class<P> type) {
		UserFieldInfo<P> property = new UserFieldInfo<P>(name, type);
		return property;
	}

	public static UserFieldInfoList getUserFieldInfoList(Class<?> objectType) {
		synchronized (USER_FIELDS) {
			UserFieldInfoList propertyInfoList = USER_FIELDS.get(objectType);
			if (propertyInfoList != null) {
				return propertyInfoList;
			} else {
				// 未注册，检索父项是否注册
				Class<?> superClass = objectType.getSuperclass();
				while (superClass != null) {
					if (superClass == FieldedObject.class) {
						return new UserFieldInfoList(0);
					}
					if (USER_FIELDS.containsKey(superClass))
						return USER_FIELDS.get(superClass);
					superClass = superClass.getSuperclass();
				}
			}
			return new UserFieldInfoList(0);
		}
	}

	/**
	 * 注册属性
	 * 
	 * @param objectType 所属类型
	 * @param property   属性
	 */
	public static <P> IPropertyInfo<P> registerUserField(Class<?> objectType, UserFieldInfo<P> property) {
		synchronized (USER_FIELDS) {
			UserFieldInfoList propertys = USER_FIELDS.get(objectType);
			if (propertys != null) {
				propertys.add(property);
				property.setIndex(propertys.size() - 1);
			} else {
				propertys = new UserFieldInfoList();
				propertys.add(property);
				property.setIndex(propertys.size() - 1);
				USER_FIELDS.put(objectType, propertys);
			}
		}
		return property;
	}

	/**
	 * 初始化对象字段
	 * 
	 * @param objectType 对象类型
	 * @return
	 */
	public static Map<IPropertyInfo<?>, Object> initFields(Class<?> objectType) {
		UserFieldInfoList infoList = getUserFieldInfoList(objectType);
		Map<IPropertyInfo<?>, Object> fieldsMap = new HashMap<>(infoList.size());
		for (IPropertyInfo<?> propertyInfo : infoList) {
			fieldsMap.put(propertyInfo, null);
		}
		return fieldsMap;
	}
}
