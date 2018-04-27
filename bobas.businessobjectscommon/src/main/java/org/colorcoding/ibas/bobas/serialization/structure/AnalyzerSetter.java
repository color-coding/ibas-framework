package org.colorcoding.ibas.bobas.serialization.structure;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.colorcoding.ibas.bobas.bo.IBusinessObjects;
import org.colorcoding.ibas.bobas.message.Logger;

/**
 * 结构分析者，通过Set方法
 * 
 * @author Niuren.Zhu
 *
 */
public class AnalyzerSetter extends Analyzer {

	public AnalyzerSetter() {
		super();
		this.skipMethods = new ArrayList<>();
		this.skipMethods.add("set");
		// this.skipMethods.add("setDeleted");
		// this.skipMethods.add("setSavable");
		// this.skipMethods.add("setLoading");
		// this.skipMethods.add("setBusy");
		// this.skipMethods.add("setDirty");
		// this.skipMethods.add("setNew");
		// this.skipMethods.add("setValid");
	}

	protected List<String> skipMethods;

	@Override
	protected Element createElement(Method method) {
		if (!method.getName().startsWith("set")) {
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
		if (method.getParameterTypes().length != 1) {
			return null;
		}
		if (!method.getDeclaringClass().isInterface() && this.skipMethods != null) {
			if (this.skipMethods.contains(method.getName())) {
				return null;
			}
		}
		Class<?> type = method.getParameterTypes()[0];
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
