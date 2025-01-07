package org.colorcoding.ibas.bobas.core;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.List;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class FieldedObject extends Trackable implements IFieldedObject {

	private static final long serialVersionUID = 1L;

	public FieldedObject() {
		this.setLoading(true);
		this.fields = PropertyInfoManager.initFields(this.getClass());
		// 初始化被修改字段数组（默认一半长度）
		this.modifiedFields = new ArrayList<IPropertyInfo<?>>(this.fields.size() / 2);
		this.setLoading(false);
	}

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
	private transient Map<IPropertyInfo<?>, Object> fields = null;
	/**
	 * 被修改的字段
	 */
	protected transient List<IPropertyInfo<?>> modifiedFields = null;

	/**
	 * 属性
	 * 
	 * @return
	 */
	@Override
	public List<IPropertyInfo<?>> properties() {
		ArrayList<IPropertyInfo<?>> propertyInfos = new ArrayList<>();
		for (IPropertyInfo<?> item : this.fields.keySet()) {
			propertyInfos.add(item);
		}
		// 属性排序
		propertyInfos.sort(new Comparator<IPropertyInfo<?>>() {

			@Override
			public int compare(IPropertyInfo<?> o1, IPropertyInfo<?> o2) {
				return Integer.compare(o1.getIndex(), o2.getIndex());
			}
		});
		return propertyInfos;
	}

	/**
	 * 获取属性的值
	 * 
	 * @param property 依赖属性
	 * 
	 * @return 属性的值
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final <P> P getProperty(IPropertyInfo<?> property) {
		Objects.requireNonNull(property);
		if (this.fields.containsKey(property)) {
			P value = (P) this.fields.get(property);
			// 值是空，则使用默认值（减少内存占用）
			if (value == null) {
				return (P) property.getDefaultValue();
			}
			return value;
		}
		throw new IllegalArgumentException(
				Strings.format("[%s] not exists property [%s].", this.getClass().getName(), property.getName()));
	}

	/**
	 * 设置属性的值
	 * 
	 * @param property 依赖属性
	 * 
	 * @param value    新的值
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final <P> void setProperty(IPropertyInfo<?> property, P value) {
		Objects.requireNonNull(property);
		if (!this.fields.containsKey(property)) {
			throw new IllegalArgumentException(
					Strings.format("[%s] not exists property [%s].", this.getClass().getName(), property.getName()));
		}
		if (this.isLoading()) {
			this.fields.put(property, value);
		} else {
			P oldValue = (P) this.fields.get(property);
			if (oldValue != value) {
				this.fields.put(property, value);
				if (this.modifiedFields != null && !this.modifiedFields.contains(property)) {
					this.modifiedFields.add(property);
				}
				this.markDirty();
				this.firePropertyChange(property.getName(), oldValue, value);
			}
		}
	}

	@Override
	protected void firePropertyChange(String name, Object oldValue, Object newValue) {
		super.firePropertyChange(name, oldValue, newValue);
	}

	@Override
	public void markOld() {
		super.markOld();
		if (this.modifiedFields != null) {
			this.modifiedFields.clear();
		}
	}

	@Override
	public boolean isDirty(IPropertyInfo<?> propertyInfo) {
		if (this.modifiedFields != null) {
			for (IPropertyInfo<?> item : this.modifiedFields) {
				if (item.equals(propertyInfo)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("{%s: %s}", this.getClass().getSimpleName(), this.hashCode());
	}

}
