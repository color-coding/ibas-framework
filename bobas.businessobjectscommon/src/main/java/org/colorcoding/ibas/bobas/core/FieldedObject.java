package org.colorcoding.ibas.bobas.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
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
		// 初始化对象属性
		this.fields = PropertyInfoManager.initFields(this.getClass());
		// 被修改属性延迟初始化，首次写入时创建以节省内存
	}

	/**
	 * 注册属性
	 * 
	 * @param name       属性名称
	 * @param dataType   属性的值类型
	 * @param objectType 对象类型（MY_CLASS，必须指向当前类）
	 *
	 * @return 属性信息
	 */
	public final static <P> IPropertyInfo<P> registerProperty(String name, Class<P> dataType, Class<?> objectType) {
		return PropertyInfoManager.registerProperty(objectType, name, dataType);
	}

	/**
	 * 注册属性
	 * 
	 * @param name         属性名称
	 * @param dataType     属性的值类型
	 * @param objectType   对象类型（MY_CLASS，必须指向当前类）
	 * @param defaultValue 默认值
	 *
	 * @return 属性信息
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
	 * @return 属性列表
	 */
	@Override
	public List<IPropertyInfo<?>> properties() {
		return this.properties(null);
	}

	/**
	 * 对象的属性
	 *
	 * @param filter 过滤条件；null表示不过滤
	 * @return 符合条件的属性列表
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
		propertyInfos.sort((c1, c2) -> Integer.compare(c1.getIndex(), c2.getIndex()));
		return propertyInfos;
	}

	/**
	 * 获取属性的值
	 *
	 * 值为null时返回属性的默认值（减少内存占用）。
	 * 加载中时跳过 synchronized 以避免锁开销（对象单线程访问）。
	 *
	 * @param property 属性信息（不允许为null）
	 *
	 * @return 属性的值；存储null时返回默认值
	 */
	@Override
	public <P> P getProperty(IPropertyInfo<?> property) {
		if (this.isLoading()) {
			return this.getPropertyValue(property);
		}
		synchronized (this) {
			return this.getPropertyValue(property);
		}
	}

	/**
	 * 读取属性值的实际逻辑（无锁）
	 *
	 * @param property 属性信息（不允许为null）
	 * @return 属性的值；存储null时返回默认值
	 */
	@SuppressWarnings("unchecked")
	private <P> P getPropertyValue(IPropertyInfo<?> property) {
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
	 * 加载中时非主键/唯一键不触发属性改变事件；值等于默认值时存储null以节省内存。
	 * 加载中时跳过 synchronized 以避免数据批量加载阶段的锁开销（对象刚创建，单线程访问）。
	 *
	 * @param property 属性信息（不允许为null）
	 *
	 * @param value    新的值
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <P> void setProperty(IPropertyInfo<?> property, P value) {
		Objects.requireNonNull(property);
		if (!this.fields.containsKey(property)) {
			throw new IllegalArgumentException(
					Strings.format("[%s] not exists property [%s].", this.getClass().getName(), property.getName()));
		}
		// 加载状态时，非主键、唯一键不触发属性改变事件
		if (this.isLoading() && !(property.isPrimaryKey() || property.isUniqueKey())) {
			if (Objects.equals(property.getDefaultValue(), value)) {
				this.fields.put(property, null);
			} else {
				this.fields.put(property, value);
			}
		} else {
			synchronized (this) {
				P oldValue = (P) this.fields.get(property);
				if (oldValue == null) {
					oldValue = (P) property.getDefaultValue();
				}
				if (oldValue == null || value == null || !oldValue.equals(value)) {
					this.fields.put(property, value);
					this.getModifiedProperties().add(property);
					this.firePropertyChange(property.getName(), oldValue, value);
					this.markDirty();
				}
			}
		}
	}

	@Override
	public synchronized void markOld() {
		super.markOld();
		if (this.modifiedProperties != null) {
			this.modifiedProperties.clear();
		}
	}

	@Override
	public synchronized boolean isModified(IPropertyInfo<?> propertyInfo) {
		if (this.modifiedProperties != null) {
			return this.modifiedProperties.contains(propertyInfo);
		}
		return false;
	}

	/**
	 * 获取被修改的属性集合（延迟初始化，首次写入时创建以节省内存）
	 *
	 * 对于只读对象（查询结果等），不会触发创建，节省每个实例约48B的HashSet开销
	 *
	 * @return 被修改的属性集合
	 */
	protected final synchronized Set<IPropertyInfo<?>> getModifiedProperties() {
		if (this.modifiedProperties == null) {
			this.modifiedProperties = new HashSet<>();
		}
		return this.modifiedProperties;
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
	public synchronized Object clone() {
		FieldedObject nData = (FieldedObject) super.clone();
		// 被修改属性延迟初始化
		nData.modifiedProperties = null;
		// 初始化对象属性
		nData.fields = new HashMap<>(this.fields);
		// 替换可克隆的值
		for (Entry<?, Object> entry : nData.fields.entrySet()) {
			if (entry.getValue() instanceof java.util.ArrayList) {
				@SuppressWarnings("unchecked")
				java.util.ArrayList<Object> values = (java.util.ArrayList<Object>) ((java.util.ArrayList<?>) entry
						.getValue()).clone();
				Object value = null;
				for (int i = 0; i < values.size(); i++) {
					value = values.get(i);
					if (value instanceof ICloneable) {
						values.set(i, ((ICloneable) value).clone());
					}
				}
				entry.setValue(values);
			} else if (entry.getValue() instanceof java.util.HashMap) {
				@SuppressWarnings("unchecked")
				java.util.HashMap<Object, Object> values = (java.util.HashMap<Object, Object>) ((java.util.HashMap<?, ?>) entry
						.getValue()).clone();
				for (Entry<Object, Object> vEntry : values.entrySet()) {
					if (vEntry.getValue() instanceof ICloneable) {
						vEntry.setValue(((ICloneable) vEntry.getValue()).clone());
					}
				}
				entry.setValue(values);
			} else if (entry.getValue() instanceof ICloneable) {
				entry.setValue(((ICloneable) entry.getValue()).clone());
			} else if (entry.getValue() != null && entry.getValue().getClass().isArray()) {
				int length = java.lang.reflect.Array.getLength(entry.getValue());
				Class<?> componentType = entry.getValue().getClass().getComponentType();
				Object values = java.lang.reflect.Array.newInstance(componentType, length);
				Object value = null;
				for (int i = 0; i < length; i++) {
					value = java.lang.reflect.Array.get(entry.getValue(), i);
					if (value instanceof ICloneable) {
						java.lang.reflect.Array.set(values, i, ((ICloneable) value).clone());
					} else {
						java.lang.reflect.Array.set(values, i, value);
					}
				}
				entry.setValue(values);
			}
		}
		return nData;
	}

	@Override
	public String toString() {
		return String.format("{%s: %s}", this.getClass().getSimpleName(), this.hashCode());
	}

}
