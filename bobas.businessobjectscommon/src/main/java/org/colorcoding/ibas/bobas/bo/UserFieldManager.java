package org.colorcoding.ibas.bobas.bo;

import java.util.HashMap;

import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.core.PropertyInfoList;
import org.colorcoding.ibas.bobas.core.fields.FieldDataDbBase;
import org.colorcoding.ibas.bobas.core.fields.FieldsFactory;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;

public class UserFieldManager {

	protected static final String MSG_USER_FIELDS_REGISTER = "user fields: register type [%s]'s user fields, count [%s].";

	private volatile static HashMap<Class<?>, PropertyInfoList> userFieldsCache = new HashMap<Class<?>, PropertyInfoList>();

	public static synchronized boolean register(Class<?> boType, PropertyInfoList fields) {
		if (boType == null || fields == null) {
			return false;
		}
		Logger.log(MessageLevel.DEBUG, MSG_USER_FIELDS_REGISTER, boType.getName(), fields.size());
		if (userFieldsCache.containsKey(boType)) {
			PropertyInfoList infoList = userFieldsCache.get(boType);
			synchronized (infoList) {
				// 锁
				for (IPropertyInfo<?> item : fields) {
					infoList.add(item);
				}
			}
		} else {
			synchronized (userFieldsCache) {
				// 锁
				PropertyInfoList infoList = new PropertyInfoList();
				for (IPropertyInfo<?> item : fields) {
					infoList.add(item);
				}
				userFieldsCache.put(boType, infoList);
			}
		}
		return true;
	}

	public static synchronized PropertyInfoList getUserFieldInfoList(Class<?> boType) {
		if (userFieldsCache.containsKey(boType)) {
			return userFieldsCache.get(boType);
		}
		return null;
	}

	public static UserField create(IPropertyInfo<?> propertyInfo) {
		FieldDataDbBase<?> dfieldData = FieldsFactory.create().createDbField(propertyInfo.getValueType());
		dfieldData.setOriginal(false);
		dfieldData.setDbField(propertyInfo.getName());
		return new UserField(dfieldData);
	}

	public static UserField create(String name, DbFieldType type) {
		if (name == null || type == null) {
			return null;
		}
		FieldDataDbBase<?> dfieldData = FieldsFactory.create().createDbField(getFieldType(type));
		dfieldData.setName(name);
		dfieldData.setPrimaryKey(false);
		dfieldData.setOriginal(false);
		dfieldData.setSavable(true);
		dfieldData.setDbField(name);
		dfieldData.setFieldType(type);
		return new UserField(dfieldData);
	}

	private static Class<?> getFieldType(DbFieldType type) {
		if (type == DbFieldType.ALPHANUMERIC) {
			return String.class;
		} else if (type == DbFieldType.DATE) {
			return DateTime.class;
		} else if (type == DbFieldType.DECIMAL) {
			return Decimal.class;
		} else if (type == DbFieldType.NUMERIC) {
			return Integer.class;
		}
		throw new RuntimeException(I18N.prop("msg_bobas_value_can_not_be_resolved", type.toString()));
	}

	public static UserField[] create(Class<?> boType) {
		if (userFieldsCache.containsKey(boType)) {
			PropertyInfoList infoList = userFieldsCache.get(boType);
			UserField[] userFields = new UserField[infoList.size()];
			for (int i = 0; i < infoList.size(); i++) {
				userFields[i] = create(infoList.get(i));
			}
			return userFields;
		}
		return null;
	}

}
