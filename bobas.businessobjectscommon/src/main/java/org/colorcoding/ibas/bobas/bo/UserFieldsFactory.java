package org.colorcoding.ibas.bobas.bo;

import java.util.HashMap;

import org.colorcoding.ibas.bobas.core.fields.FieldDataDbBase;
import org.colorcoding.ibas.bobas.core.fields.FieldsFactory;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;
import org.colorcoding.ibas.bobas.messages.MessageLevel;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;

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
		RuntimeLog.log(MessageLevel.DEBUG, RuntimeLog.MSG_USER_FIELDS_REGISTER, boType.getName(), fields.length);
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
		// dfieldData.setValue(this.getDefaultValue(type));
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
		if (type == DbFieldType.ALPHANUMERIC) {
			return String.class;
		} else if (type == DbFieldType.DATE) {
			return DateTime.class;
		} else if (type == DbFieldType.DECIMAL) {
			return Decimal.class;
		} else if (type == DbFieldType.NUMERIC) {
			return Integer.class;
		}
		return null;
	}
	/*
	 * private Object getDefaultValue(DbFieldType type) { if (type ==
	 * DbFieldType.ALPHANUMERIC) { return ""; } else if (type ==
	 * DbFieldType.DATE) { return DateTime.minValue; } else if (type ==
	 * DbFieldType.DECIMAL) { return Decimal.ZERO; } else if (type ==
	 * DbFieldType.NUMERIC) { return 0; } return null; }
	 */
}
