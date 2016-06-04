package org.colorcoding.bobas.bo;

import java.util.HashMap;

import org.colorcoding.bobas.core.fields.FieldDataDbBase;
import org.colorcoding.bobas.core.fields.FieldsFactory;
import org.colorcoding.bobas.data.DateTime;
import org.colorcoding.bobas.data.Decimal;
import org.colorcoding.bobas.mapping.db.DbFieldType;
import org.colorcoding.bobas.messages.RuntimeLog;

public class UserFieldsFactory {
	volatile private static UserFieldsFactory instance = null;

	public static UserFieldsFactory create() {
		if (instance == null) {
			synchronized (UserFieldsFactory.class) {
				if (instance == null) {
					instance = new UserFieldsFactory();
				}
			}
		}
		return instance;
	}

	private volatile static HashMap<Class<?>, UserFieldInfoList> userFieldsCache = new HashMap<Class<?>, UserFieldInfoList>();

	public synchronized boolean register(Class<?> boType, UserFieldInfo[] fields) {
		if (boType == null || fields == null) {
			return false;
		}
		RuntimeLog.log(RuntimeLog.MSG_USER_FIELDS_REGISTER, boType.getName(), fields.length);
		if (userFieldsCache.containsKey(boType)) {
			UserFieldInfoList infoList = userFieldsCache.get(boType);
			synchronized (infoList) {
				// 锁
				for (UserFieldInfo item : fields) {
					infoList.add(item);
				}
			}
		} else {
			synchronized (userFieldsCache) {
				// 锁
				UserFieldInfoList infoList = new UserFieldInfoList();
				for (UserFieldInfo item : fields) {
					infoList.add(item);
				}
				userFieldsCache.put(boType, infoList);
			}
		}
		return true;
	}

	public UserFieldInfoList getUserFieldInfoList(Class<?> boType) {
		if (userFieldsCache.containsKey(boType)) {
			return userFieldsCache.get(boType);
		}
		return null;
	}

	public UserField create(String name, DbFieldType type) {
		if (name == null || type == null) {
			return null;
		}
		FieldDataDbBase<?> dfieldData = FieldsFactory.create().createDbField(this.getFieldType(type));
		dfieldData.setName(name);
		dfieldData.setPrimaryKey(false);
		dfieldData.setSavable(true);
		dfieldData.setOriginal(true);
		dfieldData.setDbField(name);
		dfieldData.setFieldType(type);
		return new UserField(dfieldData);
	}

	public UserField[] create(Class<?> boType) {
		if (userFieldsCache.containsKey(boType)) {
			UserFieldInfoList infoList = userFieldsCache.get(boType);
			UserField[] userFields = new UserField[infoList.size()];
			for (int i = 0; i < infoList.size(); i++) {
				UserFieldInfo userFieldInfo = infoList.get(i);
				userFields[i] = this.create(userFieldInfo.getName(), userFieldInfo.getValueType());
			}
			return userFields;
		}
		return null;
	}

	private Class<?> getFieldType(DbFieldType type) {
		if (type == DbFieldType.db_Alphanumeric) {
			return String.class;
		} else if (type == DbFieldType.db_Date) {
			return DateTime.class;
		} else if (type == DbFieldType.db_Decimal) {
			return Decimal.class;
		} else if (type == DbFieldType.db_Numeric) {
			return Integer.class;
		}
		return null;
	}
}
