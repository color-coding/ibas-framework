package org.colorcoding.ibas.bobas.serialization.structure;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElement.DEFAULT;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.colorcoding.ibas.bobas.data.DateTime;

/**
 * 结构分析者
 * 
 * @author Niuren.Zhu
 *
 */
public class Analyzer {

	public static final String XML_ELEMENT_DEFAULT_VALUE = "##default";

	public Analyzer() {
		this.setRecursion(true);
		this.getPrimitiveTypes().add(BigDecimal.class);
		this.getPrimitiveTypes().add(BigInteger.class);
		this.getPrimitiveTypes().add(Date.class);
		this.getPrimitiveTypes().add(DateTime.class);
	}

	private boolean recursion;

	/**
	 * 是否递归分析
	 * 
	 * @return
	 */
	public final boolean isRecursion() {
		return recursion;
	}

	public final void setRecursion(boolean recursion) {
		this.recursion = recursion;
	}

	/**
	 * 命名元素
	 * 
	 * @param method
	 * @return
	 */
	protected String namedElement(Method method) {
		String name = method.getName();
		if (name.startsWith("get") || name.startsWith("set")) {
			return name.substring(3);
		}
		if (name.startsWith("is")) {
			return name.substring(2);
		}
		return name;
	}

	/**
	 * 分析类型
	 * 
	 * @param type
	 * @return
	 */
	public ElementRoot analyse(Class<?> type) {
		ElementRoot element = new ElementRoot(type.getSimpleName(), type);
		XmlRootElement annotation = type.getAnnotation(XmlRootElement.class);
		if (annotation != null && annotation.namespace() != null && !annotation.namespace().isEmpty()
				&& !annotation.namespace().equals(XML_ELEMENT_DEFAULT_VALUE)) {
			element.setNamespace(annotation.namespace());
		}
		this.analyse(element, type);
		return element;
	}

	private List<Class<?>> primitiveTypes;

	public final List<Class<?>> getPrimitiveTypes() {
		if (this.primitiveTypes == null) {
			this.primitiveTypes = new ArrayList<>();
		}
		return primitiveTypes;
	}

	protected void analyse(Element parent, Class<?> type) {
		// 避免嵌套，发现父项的父项有一样类型的则不再处理
		Element grandpa = parent;
		while (grandpa.getParent() != null) {
			grandpa = grandpa.getParent();
			if (grandpa.getType() == type) {
				parent = null;
				break;
			}
		}
		if (parent == null) {
			return;
		}
		// 开始本层分析
		if (type.isPrimitive()) {
			// 基本类型不做处理
			return;
		} else if (type.isEnum()) {
			// 基本类型不做处理
			return;
		} else if (type.getName().startsWith("java.lang.")) {
			// 基本类型不做处理
			return;
		} else if (this.getPrimitiveTypes().contains(type)) {
			// 基本类型不做处理
			return;
		} else if (type.isInterface()) {
			// 类型为接口时
			if (this.isRecursion()) {
				// 递归，先获取基类
				for (Class<?> item : type.getInterfaces()) {
					this.analyse(parent, item);
				}
			}
			List<Element> elements = new ArrayList<>();
			for (Method method : type.getMethods()) {
				Element element = this.createElement(method);
				if (element != null && type != element.getType()) {
					elements.add(element);
					this.analyse(element, element.getType());
				}
			}
			elements.sort(null);
			parent.getChilds().addAll(elements);
		} else {
			// 类型是类
			if (this.isRecursion()) {
				// 递归，先获取基类
				Class<?> superClass = type.getSuperclass();
				if (superClass != null && !superClass.equals(Object.class)) {
					this.analyse(parent, superClass);
				}
			}
			// 被标记的字段
			for (Field field : type.getDeclaredFields()) {
				Element element = this.createElement(field);
				if (element != null && type != element.getType()) {
					parent.getChilds().add(element);
					this.analyse(element, element.getType());
				}
			}
			// 被标记的属性
			List<Element> elements = new ArrayList<>();
			for (Method method : type.getDeclaredMethods()) {
				Element element = this.createElement(method);
				if (element != null && type != element.getType()) {
					elements.add(element);
					this.analyse(element, element.getType());
				}
			}
			elements.sort(null);
			parent.getChilds().addAll(elements);
		}
	}

	protected Element createElement(Method method) {
		XmlElement xmlElement = method.getAnnotation(XmlElement.class);
		if (xmlElement == null) {
			return null;
		}
		ElementField element = new ElementField(this.namedElement(method));
		if (xmlElement.name() != null && !xmlElement.name().isEmpty()
				&& !xmlElement.name().equals(XML_ELEMENT_DEFAULT_VALUE)) {
			element.setName(xmlElement.name());
		}
		if (xmlElement.type() != null && xmlElement.type() != DEFAULT.class) {
			element.setType(xmlElement.type());
		}
		if (element.getType() == null) {
			element.setType(method.getReturnType());
		}
		if (element.getType() == null && method.getParameterCount() > 0) {
			element.setType(method.getParameterTypes()[0]);
		}
		XmlElementWrapper xmlWrapper = method.getAnnotation(XmlElementWrapper.class);
		if (xmlWrapper != null && xmlWrapper.name() != null && !xmlWrapper.name().isEmpty()
				&& !xmlWrapper.name().equals(XML_ELEMENT_DEFAULT_VALUE)) {
			// 首先判断是否为数组元素
			element.setWrapper(xmlWrapper.name());
		}
		return element;
	}

	protected Element createElement(Field field) {
		XmlElement xmlElement = field.getAnnotation(XmlElement.class);
		if (xmlElement == null) {
			return null;
		}
		ElementField element = new ElementField(field.getName(), field.getType());
		if (xmlElement.name() != null && !xmlElement.name().isEmpty()
				&& !xmlElement.name().equals(XML_ELEMENT_DEFAULT_VALUE)) {
			element.setName(xmlElement.name());
		}
		if (xmlElement.type() != null && xmlElement.type() != DEFAULT.class) {
			element.setType(xmlElement.type());
		}
		XmlElementWrapper xmlWrapper = field.getAnnotation(XmlElementWrapper.class);
		if (xmlWrapper != null && xmlWrapper.name() != null && !xmlWrapper.name().isEmpty()
				&& !xmlWrapper.name().equals(XML_ELEMENT_DEFAULT_VALUE)) {
			// 首先判断是否为数组元素
			element.setWrapper(xmlWrapper.name());
		}
		return element;
	}
}
