package org.colorcoding.ibas.bobas.core;

import java.util.Objects;
import java.util.function.Predicate;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.core.fields.FieldManager;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.core.fields.IManageFields;
import org.colorcoding.ibas.bobas.core.fields.NotRegisterTypeException;
import org.colorcoding.ibas.bobas.data.ArrayList;
import org.colorcoding.ibas.bobas.message.Logger;
import org.colorcoding.ibas.bobas.message.MessageLevel;
import org.colorcoding.ibas.bobas.serialization.ISerializer;
import org.colorcoding.ibas.bobas.serialization.SerializerFactory;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "BusinessObjectBase", namespace = MyConfiguration.NAMESPACE_BOBAS_CORE)
public abstract class BusinessObjectBase<T extends IBusinessObjectBase> extends TrackableBase
		implements IBusinessObjectBase, IManageProperties, IManageFields {

	protected static final String MSG_PROPERTIES_NOT_FOUND_PROPERTIES = "properties: not found type [%s]'s property [%s].";

	private static final long serialVersionUID = -9005802565228088068L;

	/**
	 * 注册属性
	 * 
	 * @param name
	 *            属性名称
	 * 
	 * @param dataType
	 *            属性的值类型
	 * 
	 * @param boType
	 *            业务对象类型
	 * 
	 * @return 依赖属性
	 */
	public final static <P> IPropertyInfo<P> registerProperty(String name, Class<P> dataType, Class<?> boType) {
		return PropertyInfoManager.registerProperty(boType, name, dataType);
	}

	/**
	 * 注册属性
	 * 
	 * @param name
	 *            属性名称
	 * 
	 * @param dataType
	 *            属性的值类型
	 * 
	 * @param boType
	 *            业务对象类型
	 * 
	 * @param defaultValue
	 *            默认值
	 * 
	 * @return 依赖属性
	 */
	public final static <P> IPropertyInfo<P> registerProperty(String name, Class<P> dataType, P defaultValue,
			Class<?> boType) {
		return PropertyInfoManager.registerProperty(boType, name, dataType, defaultValue);
	}

	/**
	 * 字段管理员
	 */
	transient FieldManager fieldManager = new FieldManager(this.getClass());

	/**
	 * 获取字段
	 * 
	 * @return 字段
	 */
	@Override
	public IFieldData[] getFields() {
		return this.fieldManager.toArray();
	}

	@Override
	public IFieldData[] getFields(Predicate<? super IFieldData> filter) {
		Objects.requireNonNull(filter);
		ArrayList<IFieldData> fieldDatas = new ArrayList<>();
		for (IFieldData item : this.fieldManager) {
			if (filter.test(item)) {
				fieldDatas.add(item);
			}
		}
		return fieldDatas.toArray(new IFieldData[] {});
	}

	/**
	 * 获取主键字段
	 * 
	 * @param name
	 *            属性名称（Property;BO.Property）
	 * @return 主键字段
	 */
	@Override
	public IFieldData getField(String name) {
		if (name.indexOf(".") > 0) {
			// 包括属性路径的，BO.Property
			String cName = name.split("\\.")[0];
			IFieldData cFieldData = this.getField(cName);
			if (cFieldData.getValue() instanceof IManageFields) {
				IManageFields sBO = (IManageFields) cFieldData.getValue();
				return sBO.getField(name.substring(cName.length() + 1, name.length()));
			}
		} else {
			// 没有属性路径的，Property
			for (IFieldData iFieldData : this.fieldManager) {
				if (iFieldData.getName().equals(name)) {
					return iFieldData;
				}
			}
		}
		return null;
	}

	/**
	 * 是否存在管理的属性
	 * 
	 * @return
	 */
	@Override
	public final boolean hasProperty() {
		try {
			PropertyInfoList propertyInfoList = PropertyInfoManager.getPropertyInfoList(this.getClass());
			if (propertyInfoList != null && propertyInfoList.size() > 0) {
				return true;
			}
		} catch (NotRegisterTypeException e) {
			Logger.log(e);
		}
		return false;
	}

	/**
	 * 属性是否存在
	 * 
	 * @param property
	 *            属性
	 * @return true;false
	 */
	@Override
	public final boolean hasProperty(IPropertyInfo<?> property) {
		try {
			PropertyInfoList propertyInfoList = PropertyInfoManager.getPropertyInfoList(this.getClass());
			if (propertyInfoList != null && propertyInfoList.size() > 0) {
				for (IPropertyInfo<?> iPropertyInfo : propertyInfoList) {
					if (iPropertyInfo == property) {
						return true;
					}
				}
			}
		} catch (NotRegisterTypeException e) {
			Logger.log(e);
		}
		return false;
	}

	/**
	 * 获取管理的所有属性
	 * 
	 * @return 属性数组
	 */
	@Override
	public final IPropertyInfo<?>[] getProperty() {
		IPropertyInfo<?>[] properties = new IPropertyInfo<?>[] {};
		try {
			PropertyInfoList propertyInfoList = PropertyInfoManager.getPropertyInfoList(this.getClass());
			if (propertyInfoList != null && propertyInfoList.size() > 0) {
				propertyInfoList.toArray(properties);
			}
		} catch (NotRegisterTypeException e) {
			Logger.log(e);
		}
		return properties;
	}

	/**
	 * 获取属性的值
	 * 
	 * @param property
	 *            依赖属性
	 * 
	 * @return 属性的值
	 */
	@Override
	@SuppressWarnings("unchecked")
	public final <P> P getProperty(IPropertyInfo<P> property) {
		if (property == null) {
			return null;
		}
		IFieldData fieldData = this.fieldManager.getFieldData(property);
		if (fieldData == null) {
			// 没有定义的属性
			Logger.log(MSG_PROPERTIES_NOT_FOUND_PROPERTIES, this.getClass().getName(), property.getName());
			if (property.getDefaultValue() != null) {
				return property.getDefaultValue();
			}
			return null;
		} else {
			return (P) fieldData.getValue();
		}
	}

	/**
	 * 设置属性的值
	 * 
	 * @param property
	 *            依赖属性
	 * 
	 * @param value
	 *            新的值
	 */
	@Override
	public final <P> void setProperty(IPropertyInfo<P> property, P value) {
		if (property == null) {
			return;
		}
		IFieldData fieldData = this.fieldManager.getFieldData(property);
		if (fieldData == null) {
			// 没有定义的属性
			Logger.log(MessageLevel.ERROR, MSG_PROPERTIES_NOT_FOUND_PROPERTIES, this.getClass().getName(),
					property.getName());
		} else {
			Object oldValue = fieldData.getValue();// 旧值
			boolean changed = fieldData.setValue(value);
			if (changed) {
				// 成功修改值
				// 改变对象状态
				this.markDirty();
				// 触发属性改变事件
				this.firePropertyChange(fieldData.getName(), oldValue, fieldData.getValue());
				// 调用设置值方法
				this.afterSetProperty(property);
			}
		}
	}

	/**
	 * 设置值之后
	 * 
	 * @param property
	 *            被赋值的属性
	 */
	protected <P> void afterSetProperty(IPropertyInfo<P> property) {

	}

	/**
	 * 克隆
	 * 
	 * @return 对象副本
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T clone() {
		ISerializer<?> serializer = SerializerFactory.create().createManager().create();
		return (T) serializer.clone(this);
	}

	/**
	 * 获取查询
	 * 
	 * @return
	 */
	@Override
	public ICriteria getCriteria() {
		return null;
	}

	/**
	 * 反序列化之前调用
	 * 
	 * @param parent
	 *            所属父项
	 */
	protected void beforeUnmarshal(Object parent) {
		this.setLoading(true);
	}

	/**
	 * 反序列化之后调用
	 * 
	 * @param parent
	 *            所属父项
	 */
	protected void afterUnmarshal(Object parent) {
		this.setLoading(false);
	}

	/**
	 * 序列化之前调用
	 */
	protected void beforeMarshal() {

	}

	/**
	 * 序列化之后调用
	 */
	protected void afterMarshal() {

	}

	/**
	 * （系统）回掉方法-反序列化之前
	 * 
	 * @param target
	 * @param parent
	 */
	final void beforeUnmarshal(Unmarshaller target, Object parent) {
		this.beforeUnmarshal(parent);
	}

	/**
	 * （系统）回掉方法-反序列化之后
	 * 
	 * @param target
	 * @param parent
	 */
	final void afterUnmarshal(Unmarshaller target, Object parent) {
		this.afterUnmarshal(parent);
	}

	/**
	 * （系统）回掉方法-序列化之前
	 * 
	 * @param target
	 * @param parent
	 */
	final void beforeMarshal(Marshaller marshaller) {
		this.beforeMarshal();
	}

	/**
	 * （系统）回掉方法-序列化之后
	 * 
	 * @param target
	 * @param parent
	 */
	final void afterMarshal(Marshaller marshaller) {
		this.afterMarshal();
	}

}
