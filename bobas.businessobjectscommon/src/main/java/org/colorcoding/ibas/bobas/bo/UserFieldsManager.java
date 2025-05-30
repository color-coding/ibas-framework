package org.colorcoding.ibas.bobas.bo;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.List;
import org.colorcoding.ibas.bobas.db.DbFieldType;
import org.colorcoding.ibas.bobas.i18n.I18N;

public abstract class UserFieldsManager {

	private volatile Map<Class<?>, UserFieldInfoList> USER_FIELDS = new ConcurrentHashMap<Class<?>, UserFieldInfoList>(
			64);

	public static List<IPropertyInfo<?>> EMPTY_USER_FIELDS = new UserFieldInfoList(0);

	/**
	 * 注册属性
	 * 
	 * @param objectType 所属类型
	 * @param property   属性
	 */
	@SuppressWarnings("unchecked")
	<P> IPropertyInfo<P> registerUserField(Class<?> objectType, UserFieldInfo<P> property) {
		synchronized (USER_FIELDS) {
			UserFieldInfoList propertys = USER_FIELDS.get(objectType);
			if (propertys != null) {
				synchronized (propertys) {
					if (propertys.add(property)) {
						property.setIndex(propertys.size() - 1);
					} else {
						return (UserFieldInfo<P>) propertys.get(property.getName());
					}
				}
			} else {
				USER_FIELDS.put(objectType, propertys = new UserFieldInfoList(8));
				propertys.add(property);
				property.setIndex(propertys.size() - 1);
			}
		}
		return property;
	}

	public List<IPropertyInfo<?>> getUserFieldInfoList(Class<?> objectType) {
		synchronized (USER_FIELDS) {
			UserFieldInfoList propertyInfoList = USER_FIELDS.get(objectType);
			if (propertyInfoList != null) {
				return propertyInfoList;
			} else {
				// 未注册，检索父项是否注册
				Class<?> superClass = objectType.getSuperclass();
				while (superClass != null) {
					if (superClass == BusinessObject.class) {
						return EMPTY_USER_FIELDS;
					}
					if (USER_FIELDS.containsKey(superClass)) {
						return USER_FIELDS.get(superClass);
					}
					superClass = superClass.getSuperclass();
				}
			}
			return EMPTY_USER_FIELDS;
		}
	}

	/**
	 * 设置类型无用户字段
	 * 
	 * @param objectType 所属类型
	 */
	public List<IPropertyInfo<?>> setNoUserFields(Class<?> objectType) {
		this.USER_FIELDS.put(objectType, new UserFieldInfoList(0));
		return this.USER_FIELDS.get(objectType);
	}

	/**
	 * 注册属性
	 * 
	 * @param objectType 所属类型
	 * @param name       名称
	 * @param valueType  值类型
	 * @return
	 */
	public IPropertyInfo<?> registerUserField(Class<?> objectType, String name, Class<?> valueType) {
		return this.registerUserField(objectType, UserFieldsFactory.createUserField(name, valueType));
	}

	/**
	 * 注册属性
	 * 
	 * @param objectType 所属类型
	 * @param name       名称
	 * @param valueType  值类型
	 * @return
	 */
	public IPropertyInfo<?> registerUserField(Class<?> objectType, String name, DbFieldType valueType) {
		return this.registerUserField(objectType, name, this.classOf(valueType));
	}

	/**
	 * 获取数据类型
	 * 
	 * @param type 数据库字段类型
	 * @return
	 */
	public Class<?> classOf(DbFieldType type) {
		if (type == DbFieldType.ALPHANUMERIC) {
			return String.class;
		} else if (type == DbFieldType.DATE) {
			return DateTime.class;
		} else if (type == DbFieldType.DECIMAL) {
			return BigDecimal.class;
		} else if (type == DbFieldType.NUMERIC) {
			return Integer.class;
		} else if (type == DbFieldType.MEMO) {
			return String.class;
		}
		throw new RuntimeException(I18N.prop("msg_bobas_value_can_not_be_resolved", type.toString()));
	}

	/**
	 * 初始化方法
	 */
	public abstract void initialize();

}
