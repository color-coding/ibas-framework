package org.colorcoding.ibas.bobas.bo;

import java.util.HashMap;
import java.util.Map;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Strings;

/**
 * 业务对象工厂
 */
public class BOFactory {

	private BOFactory() {
	}

	/**
	 * 业务对象的编码字典
	 */
	private volatile static Map<Class<?>, String> MAP_BO2CODE = new HashMap<Class<?>, String>();
	/**
	 * 业务编码的对象字典
	 */
	private volatile static Map<String, Class<?>> MAP_CODE2BO = new HashMap<String, Class<?>>();

	public static String codeOf(Class<?> type) {
		if (type == null) {
			return Strings.VALUE_EMPTY;
		}
		BusinessObjectUnit businessObjectUnit = type.getAnnotation(BusinessObjectUnit.class);
		if (businessObjectUnit != null) {
			return MyConfiguration.applyVariables(businessObjectUnit.code());
		}
		return Strings.VALUE_EMPTY;
	}

	public static boolean register(Class<?> type) {
		String boCode = codeOf(type);
		if (boCode != null && !boCode.isEmpty()) {
			MAP_BO2CODE.put(type, boCode);
			MAP_CODE2BO.put(boCode, type);
			return true;
		}
		return false;
	}

	public static boolean register(String boCode, Class<?> type) {
		if (type == null) {
			return false;
		}
		MAP_BO2CODE.put(type, boCode);
		MAP_CODE2BO.put(boCode, type);
		return true;
	}
}
