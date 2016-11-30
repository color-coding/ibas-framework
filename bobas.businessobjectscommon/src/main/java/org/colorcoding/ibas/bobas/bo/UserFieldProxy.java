package org.colorcoding.ibas.bobas.bo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.data.DataConvert;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "UserFieldProxy", namespace = MyConsts.NAMESPACE_BOBAS_BO)
public class UserFieldProxy implements IUserField {
    /**
     * 
     */
    private static final long serialVersionUID = -7522271348157162894L;

    private String name;

    @Override
    @XmlElement(name = "Name")
    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    private DbFieldType valueType;

    @Override
    @XmlElement(name = "ValueType")
    public final DbFieldType getValueType() {
        return valueType;
    }

    public final void setValueType(DbFieldType valueType) {
        this.valueType = valueType;
    }

    private String value;

    @Override
    public final Object getValue() {
        return value;
    }

    @Override
    public final boolean setValue(Object value) {
        this.value = DataConvert.toString(value);
        return true;
    }

    @XmlElement(name = "Value")
    public final String getValueString() {
        return this.value;
    }

    public final void setValueString(String value) {
        this.value = value;
    }

    final Object convertValue(DbFieldType type) {
        if (type == DbFieldType.db_Numeric) {
            if (this.getValueString() == null || this.getValueString().isEmpty()) {
                return 0;
            }
            return Integer.valueOf(this.getValueString());
        } else if (type == DbFieldType.db_Date) {
            if (this.getValueString() == null || this.getValueString().isEmpty()) {
                return DateTime.minValue;
            }
            return DateTime.valueOf(this.getValueString());
        } else if (type == DbFieldType.db_Decimal) {
            if (this.getValueString() == null || this.getValueString().isEmpty()) {
                return Decimal.ZERO;
            }
            return new Decimal(this.getValueString());
        }
        return this.getValue();
    }

    @Override
    public String toString() {
        return String.format("{user field proxy: %s %s %s}", this.getName(), this.getValue(), this.getValueType());
    }

}
