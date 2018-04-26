package org.colorcoding.ibas.bobas.serialization.structure;

import java.lang.reflect.Field;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;

/**
 * 字段元素
 * 
 * @author Niuren.Zhu
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "ElementField", namespace = MyConfiguration.NAMESPACE_BOBAS_SERIALIZATION)
@XmlRootElement(name = "ElementField", namespace = MyConfiguration.NAMESPACE_BOBAS_SERIALIZATION)
public class ElementField extends Element {

	private static final long serialVersionUID = 6834396939699534564L;

	public ElementField() {
	}

	public ElementField(String name) {
		this();
		this.setName(name);
	}

	public ElementField(String name, Class<?> type) {
		this(name);
		this.setType(type);
	}

	public ElementField(String name, Class<?> type, String wrapper) {
		this(name, type);
		this.setWrapper(wrapper);
	}

	private Object source;

	public final Object getSource() {
		return source;
	}

	public final void setSource(Object source) {
		this.source = source;
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
			Field field = this.getParent().getType().getField(this.getName());
			return field.get(parentValue);
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
			Field field = this.getParent().getType().getField(this.getName());
			field.set(parentValue, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
