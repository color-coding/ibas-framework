package org.colorcoding.ibas.bobas.serialization.structure;

import java.lang.reflect.Method;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;

/**
 * 属性元素
 * 
 * @author Niuren.Zhu
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "ElementMethod", namespace = MyConfiguration.NAMESPACE_BOBAS_SERIALIZATION)
@XmlRootElement(name = "ElementMethod", namespace = MyConfiguration.NAMESPACE_BOBAS_SERIALIZATION)
public class ElementMethod extends Element {

	private static final long serialVersionUID = 6834396939699534564L;

	public static final String METHOD_GET_TEMPALTE = "get%s";
	public static final String METHOD_IS_TEMPALTE = "is%s";
	public static final String METHOD_SET_TEMPALTE = "set%s";

	public ElementMethod() {
	}

	public ElementMethod(String name) {
		this();
		this.setName(name);
	}

	public ElementMethod(String name, Class<?> type) {
		this(name);
		this.setType(type);
	}

	public ElementMethod(String name, Class<?> type, String wrapper) {
		this(name, type);
		this.setWrapper(wrapper);
	}

	@Override
	public Object getValue() {
		try {
			if (this.getParent() == null) {
				return null;
			}
			if (this.getParent().getType() == null) {
				return null;
			}
			Object parentValue = this.getParent().getValue();
			if (parentValue == null) {
				return null;
			}
			Method method = null;
			try {
				method = this.getParent().getType().getMethod(String.format(METHOD_GET_TEMPALTE, this.getName()));
			} catch (NoSuchMethodException e) {
				method = this.getParent().getType().getMethod(String.format(METHOD_IS_TEMPALTE, this.getName()));
			}
			if (method == null) {
				return null;
			}
			return method.invoke(parentValue);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setValue(Object value) {
		try {
			if (value == null) {
				return;
			}
			if (this.getParent() == null) {
				return;
			}
			if (this.getParent().getType() == null) {
				return;
			}
			Object parentValue = this.getParent().getValue();
			if (parentValue == null) {
				return;
			}
			Method method = this.getParent().getType().getMethod(String.format(METHOD_SET_TEMPALTE, this.getName()),
					this.getType());
			method.invoke(parentValue, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
