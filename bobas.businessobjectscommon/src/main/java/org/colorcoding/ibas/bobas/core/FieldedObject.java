package org.colorcoding.ibas.bobas.core;

import java.util.Map;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.IArrayList;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class FieldedObject<T extends IFieldedObject> extends Trackable implements IFieldedObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 注册属性
	 * 
	 * @param name       属性名称
	 * @param dataType   属性的值类型
	 * @param objectType 对象类型
	 * 
	 * @return 依赖属性
	 */
	public final static <P> IPropertyInfo<P> registerProperty(String name, Class<P> dataType, Class<?> objectType) {
		return PropertyInfoManager.registerProperty(objectType, name, dataType);
	}

	/**
	 * 注册属性
	 * 
	 * @param name         属性名称
	 * @param dataType     属性的值类型
	 * @param objectType   对象类型
	 * @param defaultValue 默认值
	 * 
	 * @return 依赖属性
	 */
	public final static <P> IPropertyInfo<P> registerProperty(String name, Class<P> dataType, Class<?> objectType,
			P defaultValue) {
		return PropertyInfoManager.registerProperty(objectType, name, dataType, defaultValue);
	}

	/**
	 * 字段值
	 */
	private Map<IPropertyInfo<?>, Object> fields = PropertyInfoManager.initFields(this.getClass());

	/**
	 * 属性
	 * 
	 * @return
	 */
	public final IArrayList<IPropertyInfo<?>> properties() {
		ArrayList<IPropertyInfo<?>> propertyInfos = new ArrayList<>();
		for (IPropertyInfo<?> item : this.fields.keySet()) {
			propertyInfos.add(item);
		}
		return propertyInfos;
	}

	/**
	 * 获取属性的值
	 * 
	 * @param property 依赖属性
	 * 
	 * @return 属性的值
	 */
	@SuppressWarnings("unchecked")
	public final <P> P getProperty(IPropertyInfo<P> property) {
		Objects.requireNonNull(property);
		if (this.fields.containsKey(property)) {
			P value = (P) this.fields.get(property);
			// 值是空，则使用默认值（减少内存占用）
			if (value == null) {
				return property.getDefaultValue();
			}
			return value;
		}
		throw new IllegalArgumentException("not found property.");
	}

	/**
	 * 设置属性的值
	 * 
	 * @param property 依赖属性
	 * 
	 * @param value    新的值
	 */
	public final <P> void setProperty(IPropertyInfo<P> property, P value) {
		Objects.requireNonNull(property);
		if (!this.fields.containsKey(property)) {
			throw new IllegalArgumentException("not found property.");
		}
		this.fields.put(property, value);
	}


	public String toString() {
		return String.format("{%s: %s}", this.getClass().getSimpleName(), this.hashCode());
	}

}
