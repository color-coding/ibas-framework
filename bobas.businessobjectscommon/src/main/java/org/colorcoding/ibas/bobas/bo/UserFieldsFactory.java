package org.colorcoding.ibas.bobas.bo;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Booleans;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Decimals;
import org.colorcoding.ibas.bobas.common.Enums;
import org.colorcoding.ibas.bobas.common.Numbers;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.configuration.ConfigurableFactory;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.List;
import org.colorcoding.ibas.bobas.db.DbField;
import org.colorcoding.ibas.bobas.db.DbFieldType;

public class UserFieldsFactory {

	private UserFieldsFactory() {
	}

	/**
	 * 初始化对象字段
	 * 
	 * @param objectType 对象类型
	 * @return
	 */
	static Map<IPropertyInfo<?>, Object> initFields(Class<?> objectType) {
		List<IPropertyInfo<?>> infoList = createManager().getUserFieldInfoList(objectType);
		if (infoList == null || infoList.isEmpty()) {
			return null;
		}
		Map<IPropertyInfo<?>, Object> fieldsMap = new HashMap<>(infoList.size(), 1);
		for (IPropertyInfo<?> propertyInfo : infoList) {
			fieldsMap.put(propertyInfo, null);
		}
		return fieldsMap;
	}

	static <P> UserFieldInfo<P> createUserField(String name, Class<P> type) {
		UserFieldInfo<P> property = new UserFieldInfo<P>(name, type);
		return property;
	}

	private volatile static UserFieldsManager instance;

	public synchronized static UserFieldsManager createManager() {
		if (instance == null) {
			synchronized (UserFieldsFactory.class) {
				if (instance == null) {
					instance = new Factory().create();
					instance.initialize();
				}
			}
		}
		return instance;
	}

	private static class Factory extends ConfigurableFactory<UserFieldsManager> {

		public synchronized UserFieldsManager create() {
			return this.create(MyConfiguration.CONFIG_ITEM_USER_FIELDS_WAY, UserFieldsManager.class.getSimpleName());
		}

		@Override
		protected UserFieldsManager createDefault(String typeName) {
			return new UserFieldsManager() {

				@Override
				public void initialize() {
				}

			};
		}
	}
}

class UserFieldInfoList extends ArrayList<IPropertyInfo<?>> {

	private static final long serialVersionUID = -6320914335175298868L;

	public UserFieldInfoList(int initialCapacity) {
		super(initialCapacity);
		this.keys = new HashSet<String>(initialCapacity);
	}

	private Set<String> keys;

	public synchronized boolean add(IPropertyInfo<?> e) {
		if (this.keys.contains(e.getName())) {
			return false;
		}
		this.keys.add(e.getName());
		return super.add(e);
	}

	public synchronized IPropertyInfo<?> get(String name) {
		for (IPropertyInfo<?> item : this) {
			if (Strings.equals(item.getName(), name)) {
				return item;
			}
		}
		throw new ArrayIndexOutOfBoundsException(Strings.format("not found %s.", name));
	}
}

class UserFieldInfo<P> implements IPropertyInfo<P> {

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

	private P defaultValue;

	@Override
	@SuppressWarnings("unchecked")
	public P getDefaultValue() {
		if (this.defaultValue == null) {
			if (this.getValueType() == String.class) {
				return (P) Strings.defaultValue();
			} else if (this.getValueType() == BigDecimal.class) {
				return (P) Decimals.defaultValue();
			} else if (this.getValueType() == Integer.class) {
				return (P) Numbers.defaultValue(this.getValueType());
			} else if (this.getValueType() == Short.class) {
				return (P) Numbers.defaultValue(this.getValueType());
			} else if (this.getValueType() == Long.class) {
				return (P) Numbers.defaultValue(this.getValueType());
			} else if (this.getValueType() == Double.class) {
				return (P) Numbers.defaultValue(this.getValueType());
			} else if (this.getValueType() == Float.class) {
				return (P) Numbers.defaultValue(this.getValueType());
			} else if (this.getValueType().isEnum()) {
				return (P) Enums.defaultValue(this.getValueType());
			} else if (this.getValueType() == Boolean.class) {
				return (P) Booleans.defaultValue();
			} else if (this.getValueType() == DateTime.class) {
				return (P) DateTimes.defaultValue();
			}
		}
		return this.defaultValue;
	}

	@Override
	public boolean isPrimaryKey() {
		return false;
	}

	@Override
	public boolean isUniqueKey() {
		return false;
	}

	private DbField dbField = null;

	@Override
	@SuppressWarnings("unchecked")
	public <A extends Annotation> A getAnnotation(Class<A> type) {
		if (type == DbField.class) {
			if (this.dbField == null) {
				this.dbField = new DbField() {

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
			return (A) this.dbField;
		}
		return null;
	}

	@Override
	public final String toString() {
		return String.format("{userField: %s}", this.getName());
	}
}
