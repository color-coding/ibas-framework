package org.colorcoding.ibas.bobas.core.fields;

import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.IBusinessObjectListBase;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;

//以下均为package内类型
/**
 * 字段，整数型
 */
class FieldDataInteger extends FieldDataBase<Integer> {

    public FieldDataInteger(Class<?> valueType) {
        this.setValueType(valueType);
    }

    private int value = 0;

    @Override
    public Integer getValue() {
        return this.value;
    }

    public boolean setValue(Integer value) {
        if (value == null) {
            value = 0;
        }
        if (this.value == value) {
            return false;
        }
        this.value = value;
        this.setDirty(true);
        return true;
    }

    @Override
    public boolean setValue(Object value) {
        return this.setValue((Integer) value);
    }

}

/**
 * 字段，短整数型
 */
class FieldDataShort extends FieldDataBase<Short> {

    public FieldDataShort(Class<?> valueType) {
        this.setValueType(valueType);
    }

    private short value = 0;

    @Override
    public Short getValue() {
        return this.value;
    }

    public boolean setValue(Short value) {
        if (value == null) {
            value = 0;
        }
        if (this.value == value) {
            return false;
        }
        this.value = value;
        this.setDirty(true);
        return true;
    }

    @Override
    public boolean setValue(Object value) {
        return this.setValue((Short) value);
    }

}

/**
 * 字段，字符型
 */
class FieldDataChar extends FieldDataBase<Character> {
    public FieldDataChar(Class<?> valueType) {
        this.setValueType(valueType);
    }

    @Override
    public Class<?> getValueType() {
        return Character.class;
    }

    private Character value = null;

    @Override
    public Character getValue() {
        return this.value;
    }

    public boolean setValue(Character value) {
        if (this.value != null && this.value.equals(value)) {
            return false;
        }
        this.value = value;
        this.setDirty(true);
        return true;
    }

    @Override
    public boolean setValue(Object value) {
        return this.setValue((Character) value);
    }

}

/**
 * 字段，字符串
 */
class FieldDataString extends FieldDataBase<String> {

    public FieldDataString(Class<?> valueType) {
        this.setValueType(valueType);
    }

    private String value = null;

    @Override
    public String getValue() {
        return this.value;
    }

    public boolean setValue(String value) {
        if (this.value != null && this.value.equals(value)) {
            return false;
        }
        this.value = value;
        this.setDirty(true);
        return true;
    }

    @Override
    public boolean setValue(Object value) {
        return this.setValue((String) value);
    }
}

/**
 * 字段，十进制数值
 */
class FieldDataDecimal extends FieldDataBase<Decimal> {

    public FieldDataDecimal(Class<?> valueType) {
        this.setValueType(valueType);
    }

    private Decimal value = Decimal.ZERO;

    @Override
    public Decimal getValue() {
        return this.value;
    }

    public boolean setValue(Decimal value) {
        if (this.value != null && this.value.equals(value)) {
            return false;
        }
        this.value = value;
        this.setDirty(true);
        return true;
    }

    @Override
    public boolean setValue(Object value) {
        return this.setValue((Decimal) value);
    }
}

/**
 * 字段，日期
 */
class FieldDataDateTime extends FieldDataBase<DateTime> {

    public FieldDataDateTime(Class<?> valueType) {
        this.setValueType(valueType);
    }

    private DateTime value = DateTime.minValue;

    @Override
    public DateTime getValue() {
        return this.value;
    }

    public boolean setValue(DateTime value) {
        if (this.value != null && this.value.equals(value)) {
            return false;
        }
        this.value = value;
        this.setDirty(true);
        return true;
    }

    @Override
    public boolean setValue(Object value) {
        return this.setValue((DateTime) value);
    }
}

/**
 * 字段，单精度小数
 */
class FieldDataFloat extends FieldDataBase<Float> {

    public FieldDataFloat(Class<?> valueType) {
        this.setValueType(valueType);
    }

    private float value = 0.0f;

    @Override
    public Float getValue() {
        return this.value;
    }

    public boolean setValue(Float value) {
        if (value == null) {
            value = 0.0f;
        }
        if (this.value == value) {
            return false;
        }
        this.value = value;
        this.setDirty(true);
        return true;
    }

    @Override
    public boolean setValue(Object value) {
        return this.setValue((Float) value);
    }
}

/**
 * 字段，双精度小数
 */
class FieldDataDouble extends FieldDataBase<Double> {

    public FieldDataDouble(Class<?> valueType) {
        this.setValueType(valueType);
    }

    private double value = 0.0;

    @Override
    public Double getValue() {
        return this.value;
    }

    public boolean setValue(Double value) {
        if (value == null) {
            value = 0.0;
        }
        if (this.value == value) {
            return false;
        }
        this.value = value;
        this.setDirty(true);
        return true;
    }

    @Override
    public boolean setValue(Object value) {
        return this.setValue((Double) value);
    }
}

/**
 * 字段，布尔
 */
class FieldDataBoolean extends FieldDataBase<Boolean> {

    public FieldDataBoolean(Class<?> valueType) {
        this.setValueType(valueType);
    }

    private boolean value = false;

    @Override
    public Boolean getValue() {
        return this.value;
    }

    public boolean setValue(Boolean value) {
        if (this.value == value) {
            return false;
        }
        this.value = value;
        this.setDirty(true);
        return true;
    }

    @Override
    public boolean setValue(Object value) {
        return this.setValue((Boolean) value);
    }
}

/**
 * 字段，枚举
 */
class FieldDataEnum extends FieldDataBase<Enum<?>> {

    public FieldDataEnum(Class<?> valueType) {
        this.setValueType(valueType);
        this.setValue(valueType.getEnumConstants()[0]);// 获取枚举的第一个值
    }

    private Enum<?> value = null;

    @Override
    public Enum<?> getValue() {
        return this.value;
    }

    public boolean setValue(Enum<?> value) {
        if (this.value != null && this.value.equals(value)) {
            return false;
        }
        this.value = value;
        this.setDirty(true);
        return true;
    }

    @Override
    public boolean setValue(Object value) {
        return this.setValue((Enum<?>) value);
    }
}

/**
 * 字段，业务对象
 */
class FieldDataBO extends FieldDataBase<IBusinessObjectBase> {

    public FieldDataBO(Class<?> valueType) {
        this.setValueType(valueType);
        this.setSavable(true);
    }

    private IBusinessObjectBase value = null;

    @Override
    public IBusinessObjectBase getValue() {
        return this.value;
    }

    public boolean setValue(IBusinessObjectBase value) {
        if (this.value != null && this.value.equals(value)) {
            return false;
        }
        this.value = value;
        this.setDirty(true);
        return true;
    }

    @Override
    public boolean setValue(Object value) {
        return this.setValue((IBusinessObjectBase) value);
    }
}

/**
 * 字段，业务对象集合
 */
class FieldDataBOs extends FieldDataBase<IBusinessObjectListBase<?>> {

    public FieldDataBOs(Class<?> valueType) {
        this.setValueType(valueType);
        this.setSavable(true);
    }

    private IBusinessObjectListBase<?> value = null;

    @Override
    public IBusinessObjectListBase<?> getValue() {
        return this.value;
    }

    public boolean setValue(IBusinessObjectListBase<?> value) {
        if (this.value != null && this.value.equals(value)) {
            return false;
        }
        this.value = value;
        this.setDirty(true);
        return true;
    }

    @Override
    public boolean setValue(Object value) {
        return this.setValue((IBusinessObjectListBase<?>) value);
    }
}