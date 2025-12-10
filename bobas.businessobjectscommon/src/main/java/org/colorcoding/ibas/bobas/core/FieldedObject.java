package org.colorcoding.ibas.bobas.core;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.fields.IManagedFields;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.data.List;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class FieldedObject extends Trackable implements IFieldedObject, IManagedFields {

	private static final long serialVersionUID = 1L;

	public FieldedObject() {
		this.setLoading(true);
		// 初始化对象属性
		this.fields = PropertyInfoManager.initFields(this.getClass());
		// 初始化被修改属性
		this.modifiedProperties = new HashSet<IPropertyInfo<?>>();
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
	protected transient Set<IPropertyInfo<?>> modifiedProperties = null;

	/**
	 * 对象的属性（全部）
	 * 
	 * @return
	 */
	@Override
	public synchronized List<IPropertyInfo<?>> properties() {
		return this.properties(null);
	}

	/**
	 * 对象的属性
	 * 
	 * @param filter 过滤条件
	 * @return
	 */
	@Override
	public synchronized List<IPropertyInfo<?>> properties(Predicate<IPropertyInfo<?>> filter) {
		ArrayList<IPropertyInfo<?>> propertyInfos = new ArrayList<>(this.fields.size());
		for (IPropertyInfo<?> propertyInfo : this.fields.keySet()) {
			if (filter == null || filter.test(propertyInfo)) {
				propertyInfos.add(propertyInfo);
			}
		}
		// 符合条件的小于总量，则回收空间
		if (propertyInfos.size() < this.fields.size()) {
			propertyInfos.trimToSize();
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
	public synchronized <P> P getProperty(IPropertyInfo<?> property) {
		Objects.requireNonNull(property);
		if (this.fields.containsKey(property)) {
			P value = (P) this.fields.get(property);
			// 值是空，则使用默认值（减少内存占用）
			if (value == null) {
				return (P) property.getDefaultValue();
			}
			return value;
		}
		if (MyConfiguration.isDebugMode()) {
			for (IPropertyInfo<?> item : this.fields.keySet()) {
				// 存在同名属性，但实例不同
				if (Strings.equalsIgnoreCase(item.getName(), property.getName())) {
					Logger.log(MessageLevel.WARN,
							"has same property [%s], but instances are different. this:[%s] and caller:[%s].",
							property.getName(), item.hashCode(), property.hashCode());
					break;
				}
			}
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
	public synchronized <P> void setProperty(IPropertyInfo<?> property, P value) {
		Objects.requireNonNull(property);
		if (!this.fields.containsKey(property)) {
			throw new IllegalArgumentException(
					Strings.format("[%s] not exists property [%s].", this.getClass().getName(), property.getName()));
		}
		// 加载状态时，非主键、唯一键不触发属性改变事件
		if (this.isLoading() && !(property.isPrimaryKey() || property.isUniqueKey())) {
			if (property.getDefaultValue() == value) {
				this.fields.put(property, null);
			} else {
				this.fields.put(property, value);
			}
		} else {
			P oldValue = (P) this.fields.get(property);
			if (oldValue != value) {
				this.fields.put(property, value);
				if (this.modifiedProperties != null && !this.modifiedProperties.contains(property)) {
					this.modifiedProperties.add(property);
				}
				this.firePropertyChange(property.getName(), oldValue, value);
				this.markDirty();
			}
		}
	}

	@Override
	public void markOld() {
		super.markOld();
		if (this.modifiedProperties != null) {
			this.modifiedProperties.clear();
		}
	}

	@Override
	public boolean isModified(IPropertyInfo<?> propertyInfo) {
		if (this.modifiedProperties != null) {
			return this.modifiedProperties.contains(propertyInfo);
		}
		return false;
	}

	@Override
	protected void beforeUnmarshal(Object parent) {
		this.setLoading(true);
	}

	@Override
	protected void afterUnmarshal(Object parent) {
		this.setLoading(false);
	}

	@Override
	public String toString() {
		return String.format("{%s: %s}", this.getClass().getSimpleName(), this.hashCode());
	}

}
