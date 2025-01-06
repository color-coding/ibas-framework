package org.colorcoding.ibas.bobas.serialization.structure;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.colorcoding.ibas.bobas.bo.IBusinessObjects;
import org.colorcoding.ibas.bobas.logging.Logger;

/**
 * 结构分析者，通过Get方法
 * 
 * @author Niuren.Zhu
 *
 */
public class AnalyzerGetter extends Analyzer {

	public static final String METHOD_GET_PREFIX = "get";
	public static final String METHOD_IS_PREFIX = "is";

	public AnalyzerGetter() {
		super();
		this.skipMethods = new ArrayList<>();
		this.skipMethods.add("get");
		this.skipMethods.add("getFields");
		this.skipMethods.add("getProperty");
		this.skipMethods.add("getCriteria");
		this.skipMethods.add("getIdentifiers");
	}

	protected List<String> skipMethods;

	@Override
	protected Element createElement(Method method) {
		if (!method.getName().startsWith(METHOD_GET_PREFIX) && !method.getName().startsWith(METHOD_IS_PREFIX)) {
			return null;
		}
		if (method.getParameterCount() != 0) {
			return null;
		}
		int modifiers = method.getModifiers();
		if (Modifier.isStatic(modifiers)) {
			// 静态方法，跳过
			return null;
		}
		if (!Modifier.isPublic(modifiers)) {
			// 非公共，跳过
			return null;
		}
		if (this.skipMethods != null) {
			if (this.skipMethods.contains(method.getName())) {
				return null;
			}
		}
		Class<?> type = method.getReturnType();
		if (type.isArray()) {
			type = type.getComponentType();
		}
		if (IBusinessObjects.class.isAssignableFrom(type)) {
			if (type.getName().endsWith("s")) {
				String name = type.getName().substring(0, type.getName().length() - 1);
				try {
					type = Class.forName(name);
				} catch (ClassNotFoundException e) {
					Logger.log(e);
				}
			}
		}
		return new ElementMethod(this.namedElement(method), type);
	}

}
