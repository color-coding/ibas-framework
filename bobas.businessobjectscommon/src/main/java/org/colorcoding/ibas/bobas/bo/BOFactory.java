package org.colorcoding.ibas.bobas.bo;

import java.util.HashMap;
import java.util.Map;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.FieldedObject;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.IArrayList;
import org.colorcoding.ibas.bobas.i18n.I18N;

/**
 * 业务对象工厂
 */
public class BOFactory {

	private BOFactory() {
	}

	/**
	 * 业务对象的编码字典
	 */
	private volatile static Map<Class<?>, String> MAP_BO2CODE = new HashMap<Class<?>, String>(256);
	/**
	 * 业务编码的对象字典
	 */
	private volatile static Map<String, Class<?>> MAP_CODE2BO = new HashMap<String, Class<?>>(256);

	/**
	 * 注册对象
	 * 
	 * @param type 对象类型
	 * @return
	 */
	public static boolean register(Class<?> type) {
		String boCode = codeOf(type);
		if (!Strings.isNullOrEmpty(boCode)) {
			MAP_BO2CODE.put(type, boCode);
			MAP_CODE2BO.put(boCode, type);
			return true;
		}
		return false;
	}

	/**
	 * 注册对象
	 * 
	 * @param boCode 对象编码
	 * @param type   对象类型
	 * @return
	 */
	public static boolean register(String boCode, Class<?> type) {
		if (type == null) {
			return false;
		}
		MAP_BO2CODE.put(type, boCode);
		MAP_CODE2BO.put(boCode, type);
		return true;
	}

	/**
	 * 获取对象编码
	 * 
	 * @param type 对象类型
	 * @return
	 */
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

	/**
	 * 创建对象实例
	 * 
	 * @param <P>
	 * @param type
	 * @return
	 */
	public static <P> P newInstance(Class<P> type) {
		try {
			return type.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static Map<Class<?>, IArrayList<IPropertyInfo<?>>> boProperties = new HashMap<>();

	/**
	 * 获取对象的所有属性
	 * 
	 * @param type 对象
	 * @return
	 */
	public static IArrayList<IPropertyInfo<?>> propertyInfos(Class<?> type) {
		if (boProperties.containsKey(type)) {
			return boProperties.get(type);
		}
		Object data = newInstance(type);
		if (data instanceof FieldedObject) {
			boProperties.put(type, ((FieldedObject) data).properties());
			return propertyInfos(type);
		}
		throw new RuntimeException(I18N.prop("msg_bobas_value_can_not_be_resolved", type.toString()));
	}

}
