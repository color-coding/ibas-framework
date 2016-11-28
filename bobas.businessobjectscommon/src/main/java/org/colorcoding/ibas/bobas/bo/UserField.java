package org.colorcoding.ibas.bobas.bo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.core.fields.IFieldDataDb;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;

/**
 * 自定义字段元素
 * 
 * @author Niuren.Zhu
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "UserField", namespace = MyConsts.NAMESPACE_BOBAS_BO)
public class UserField implements IUserField {
    /**
     * 
     */
    private static final long serialVersionUID = -4092373163622194831L;

    /**
     * 用户自定义字段前缀标记
     */
    public static final String USER_FIELD_PREFIX_SIGN = "U_";

    public UserField(IFieldDataDb fieldData) {
        this.setFieldData(fieldData);
    }

    private IFieldDataDb fieldData = null;

    IFieldDataDb getFieldData() {
        return fieldData;
    }

    void setFieldData(IFieldDataDb value) {
        this.fieldData = value;
    }

    @Override
    @XmlElement(name = "Name")
    public String getName() {
        return this.fieldData.getName();
    }

    @Override
    @XmlElement(name = "ValueType")
    public DbFieldType getValueType() {
        return this.fieldData.getFieldType();
    }

    public void setValueType(DbFieldType value) {

    }

    @Override
    @XmlElement(name = "Value")
    public Object getValue() {
        return this.fieldData.getValue();
    }

    @Override
    public boolean setValue(Object value) {
        return this.fieldData.setValue(value);
    }

    @Override
    public String toString() {
        return String.format("{user field: %s %s %s}", this.getName(), this.getValue(), this.getValueType());
    }
}
