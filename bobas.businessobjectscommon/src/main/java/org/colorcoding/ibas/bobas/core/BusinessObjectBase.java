package org.colorcoding.ibas.bobas.core;

import java.lang.reflect.Array;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.core.fields.FieldManager;
import org.colorcoding.ibas.bobas.core.fields.IFieldData;
import org.colorcoding.ibas.bobas.core.fields.IManageFields;
import org.colorcoding.ibas.bobas.core.fields.NotRegisterTypeException;
import org.colorcoding.ibas.bobas.messages.MessageLevel;
import org.colorcoding.ibas.bobas.messages.RuntimeLog;
import org.colorcoding.ibas.bobas.util.ArrayList;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "BusinessObjectBase", namespace = MyConsts.NAMESPACE_BOBAS_CORE)
public abstract class BusinessObjectBase<T extends IBusinessObjectBase> extends TrackableBase
        implements IBusinessObjectBase, IManageProperties, IManageFields {

    /**
     * 
     */
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
     * 获取主键字段
     * 
     * @return 主键字段
     */
    @Override
    public final IFieldData[] getKeyFields() {
        ArrayList<IFieldData> keyFields = new ArrayList<IFieldData>();
        for (IFieldData iFieldData : this.fieldManager) {
            if (iFieldData.isPrimaryKey()) {
                keyFields.add(iFieldData);
            }
        }
        return keyFields.toArray(new IFieldData[] {});
    }

    /**
     * 是否存在管理的属性
     * 
     * @return
     */
    @Override
    public final boolean hasManagedProperties() {
        try {
            PropertyInfoList propertyInfoList = PropertyInfoManager.getPropertyInfoList(this.getClass());
            if (propertyInfoList != null && propertyInfoList.size() > 0) {
                return true;
            }
        } catch (NotRegisterTypeException e) {
            RuntimeLog.log(e);
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
    public final boolean existProperty(IPropertyInfo<?> property) {
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
            RuntimeLog.log(e);
        }
        return false;
    }

    /**
     * 获取管理的所有属性
     * 
     * @return 属性数组
     */
    @Override
    public final IPropertyInfo<?>[] getManagedProperties() {
        IPropertyInfo<?>[] properties = new IPropertyInfo<?>[] {};
        try {
            PropertyInfoList propertyInfoList = PropertyInfoManager.getPropertyInfoList(this.getClass());
            if (propertyInfoList != null && propertyInfoList.size() > 0) {
                propertyInfoList.toArray(properties);
            }
        } catch (NotRegisterTypeException e) {
            RuntimeLog.log(e);
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
            throw null;
        }
        IFieldData fieldData = this.fieldManager.getFieldData(property);
        if (fieldData == null) {
            // 没有定义的属性
            RuntimeLog.log(RuntimeLog.MSG_PROPERTIES_NOT_FOUND_PROPERTIES, this.getClass().getName(),
                    property.getName());
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
            RuntimeLog.log(MessageLevel.ERROR, RuntimeLog.MSG_PROPERTIES_NOT_FOUND_PROPERTIES,
                    this.getClass().getName(), property.getName());
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
     * 标记为未修改
     * 
     * @param forced
     *            包括子项及属性
     */
    @Override
    public final void markOld(boolean forced) {
        super.markOld();
        if (forced) {
            for (IFieldData item : this.fieldManager) {
                item.markOld();// 数据字段标记未修改
                Object data = item.getValue();
                if (data == null) {
                    continue;
                }
                if (data instanceof ITrackStatusOperator) {
                    // 值是业务对象
                    ((ITrackStatusOperator) data).markOld(true);
                } else if (data instanceof IBusinessObjectListBase<?>) {
                    // 值是业务对象列表
                    IBusinessObjectListBase<?> boList = (IBusinessObjectListBase<?>) data;
                    for (IBusinessObjectBase childItem : boList) {
                        if (childItem instanceof ITrackStatusOperator) {
                            ((ITrackStatusOperator) childItem).markOld(true);
                        }
                    }
                } else if (data.getClass().isArray()) {
                    // 值是数组
                    int length = Array.getLength(data);
                    for (int i = 0; i < length; i++) {
                        Object childItem = Array.get(data, i);
                        if (childItem instanceof ITrackStatusOperator) {
                            ((ITrackStatusOperator) childItem).markOld(true);
                        }
                    }
                }
            }
        }
    }

    /**
     * 标记为删除
     * 
     * @param forced
     *            包括子项及属性
     */
    @Override
    public final void markDeleted(boolean forced) {
        super.markDeleted();
        if (forced) {
            for (IFieldData item : this.fieldManager) {
                Object data = item.getValue();
                if (data == null) {
                    continue;
                }
                if (data instanceof ITrackStatusOperator) {
                    // 值是业务对象
                    ((ITrackStatusOperator) data).markDeleted(true);
                } else if (data instanceof IBusinessObjectListBase<?>) {
                    // 值是业务对象列表
                    IBusinessObjectListBase<?> boList = (IBusinessObjectListBase<?>) data;
                    for (IBusinessObjectBase childItem : boList) {
                        if (childItem instanceof ITrackStatusOperator) {
                            ((ITrackStatusOperator) childItem).markDeleted(true);
                        }
                    }
                } else if (data.getClass().isArray()) {
                    // 值是数组
                    int length = Array.getLength(data);
                    for (int i = 0; i < length; i++) {
                        Object childItem = Array.get(data, i);
                        if (childItem instanceof ITrackStatusOperator) {
                            ((ITrackStatusOperator) childItem).markDeleted(true);
                        }
                    }
                }
            }
        }
    }

    /**
     * 清理标记删除的数据
     * 
     * @param forced
     *            包括子项及属性
     */
    @Override
    public final void clearDeleted() {
        for (IFieldData item : this.fieldManager) {
            Object data = item.getValue();
            if (data == null) {
                continue;
            }
            if (data instanceof ITrackStatus) {
                // 值是业务对象
                if (((ITrackStatus) data).isDeleted()) {
                    item.setValue(null);
                }
            } else if (data instanceof IBusinessObjectListBase<?>) {
                // 值是业务对象列表
                IBusinessObjectListBase<?> boList = (IBusinessObjectListBase<?>) data;
                for (int i = boList.size() - 1; i >= 0; i--) {
                    IBusinessObjectBase childItem = boList.get(i);
                    if (childItem.isDeleted()) {
                        boList.remove(childItem);
                    }
                }
            } else if (data.getClass().isArray()) {
                // 值是数组
                int length = Array.getLength(data);
                for (int i = 0; i < length; i++) {
                    Object childItem = Array.get(data, i);
                    if (childItem instanceof ITrackStatus) {
                        if (((ITrackStatus) childItem).isDeleted()) {
                            Array.set(data, i, null);
                        }
                    }
                }
            }
        }
    }

    /**
     * 克隆
     * 
     * @return 对象副本
     */
    @SuppressWarnings("unchecked")
    @Override
    public T clone() {
        return (T) Serializer.clone(this);
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

}
