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

	private int _value = 0;

	@Override
	public Integer getValue() {
		return this._value;
	}

	public boolean setValue(Integer value) {
		if (value == null) {
			value = 0;
		}
		if (this._value == value) {
			return false;
		}
		this._value = value;
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

	private short _value = 0;

	@Override
	public Short getValue() {
		return this._value;
	}

	public boolean setValue(Short value) {
		if (value == null) {
			value = 0;
		}
		if (this._value == value) {
			return false;
		}
		this._value = value;
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

	private Character _value = null;

	@Override
	public Character getValue() {
		return this._value;
	}

	public boolean setValue(Character value) {
		if (this._value != null && this._value.equals(value)) {
			return false;
		}
		this._value = value;
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

	private String _value = null;

	@Override
	public String getValue() {
		return this._value;
	}

	public boolean setValue(String value) {
		if (this._value != null && this._value.equals(value)) {
			return false;
		}
		this._value = value;
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

	private Decimal _value = new Decimal("0.0");

	@Override
	public Decimal getValue() {
		return this._value;
	}

	public boolean setValue(Decimal value) {
		if (this._value != null && this._value.equals(value)) {
			return false;
		}
		this._value = value;
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

	private DateTime _value = DateTime.getMinValue();

	@Override
	public DateTime getValue() {
		return this._value;
	}

	public boolean setValue(DateTime value) {
		if (this._value != null && this._value.equals(value)) {
			return false;
		}
		this._value = value;
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

	private float _value = 0.0f;

	@Override
	public Float getValue() {
		return this._value;
	}

	public boolean setValue(Float value) {
		if (value == null) {
			value = 0.0f;
		}
		if (this._value == value) {
			return false;
		}
		this._value = value;
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

	private double _value = 0.0;

	@Override
	public Double getValue() {
		return this._value;
	}

	public boolean setValue(Double value) {
		if (value == null) {
			value = 0.0;
		}
		if (this._value == value) {
			return false;
		}
		this._value = value;
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

	private Boolean _value = false;

	@Override
	public Boolean getValue() {
		return this._value;
	}

	public boolean setValue(Boolean value) {
		if (this._value == value) {
			return false;
		}
		this._value = value;
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

	private Enum<?> _value = null;

	@Override
	public Enum<?> getValue() {
		return this._value;
	}

	public boolean setValue(Enum<?> value) {
		if (this._value != null && this._value.equals(value)) {
			return false;
		}
		this._value = value;
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

	private IBusinessObjectBase _value = null;

	@Override
	public IBusinessObjectBase getValue() {
		return this._value;
	}

	public boolean setValue(IBusinessObjectBase value) {
		if (this._value != null && this._value.equals(value)) {
			return false;
		}
		this._value = value;
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

	private IBusinessObjectListBase<?> _value = null;

	@Override
	public IBusinessObjectListBase<?> getValue() {
		return this._value;
	}

	public boolean setValue(IBusinessObjectListBase<?> value) {
		if (this._value != null && this._value.equals(value)) {
			return false;
		}
		this._value = value;
		this.setDirty(true);
		return true;
	}

	@Override
	public boolean setValue(Object value) {
		return this.setValue((IBusinessObjectListBase<?>) value);
	}
}