package org.colorcoding.ibas.bobas.core.fields;

import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;

//以下均为package内类型
/**
 * 字段，整数型
 */
class DbFieldDataInteger extends FieldDataDbBase<Integer> {

	public DbFieldDataInteger(Class<?> valueType) {
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
		return true;
	}

	@Override
	public boolean setValue(Object value) {
		return this.setValue((Integer) value);
	}

	@Override
	public DbFieldType getFieldType() {
		return DbFieldType.NUMERIC;
	}

}

/**
 * 字段，短整数型
 */
class DbFieldDataShort extends FieldDataDbBase<Short> {

	public DbFieldDataShort(Class<?> valueType) {
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
		return true;
	}

	@Override
	public boolean setValue(Object value) {
		return this.setValue((Short) value);
	}

	@Override
	public DbFieldType getFieldType() {
		return DbFieldType.NUMERIC;
	}

}

/**
 * 字段，字符型
 */
class DbFieldDataChar extends FieldDataDbBase<Character> {

	public DbFieldDataChar(Class<?> valueType) {
		this.setValueType(valueType);
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
		return true;
	}

	@Override
	public boolean setValue(Object value) {
		return this.setValue((Character) value);
	}

	@Override
	public DbFieldType getFieldType() {
		return DbFieldType.ALPHANUMERIC;
	}

}

/**
 * 字段，字符串
 */
class DbFieldDataString extends FieldDataDbBase<String> {

	public DbFieldDataString(Class<?> valueType) {
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
		return true;
	}

	@Override
	public boolean setValue(Object value) {
		return this.setValue((String) value);
	}

	@Override
	public DbFieldType getFieldType() {
		return DbFieldType.ALPHANUMERIC;
	}

}

/**
 * 字段，十进制数值
 */
class DbFieldDataDecimal extends FieldDataDbBase<Decimal> {

	public DbFieldDataDecimal(Class<?> valueType) {
		this.setValueType(valueType);
	}

	private Decimal value = Decimal.ZERO;

	@Override
	public Decimal getValue() {
		return this.value;
	}

	public boolean setValue(Decimal value) {
		if (this.value != null && (this.value.equals(value) || this.value.compareTo(value) == 0)) {
			return false;
		}
		this.value = value;
		return true;
	}

	@Override
	public boolean setValue(Object value) {
		return this.setValue((Decimal) value);
	}

	@Override
	public DbFieldType getFieldType() {
		return DbFieldType.DECIMAL;
	}

}

/**
 * 字段，日期
 */
class DbFieldDataDateTime extends FieldDataDbBase<DateTime> {

	public DbFieldDataDateTime(Class<?> valueType) {
		this.setValueType(valueType);
	}

	private DateTime value = DateTime.MIN_VALUE;

	@Override
	public DateTime getValue() {
		return this.value;
	}

	public boolean setValue(DateTime value) {
		if (this.value != null && (this.value.equals(value) || this.value.compareTo(value) == 0)) {
			return false;
		}
		this.value = value;
		return true;
	}

	@Override
	public boolean setValue(Object value) {
		return this.setValue((DateTime) value);
	}

	@Override
	public DbFieldType getFieldType() {
		return DbFieldType.DATE;
	}

}

/**
 * 字段，单精度小数
 */
class DbFieldDataFloat extends FieldDataDbBase<Float> {

	public DbFieldDataFloat(Class<?> valueType) {
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
		return true;
	}

	@Override
	public boolean setValue(Object value) {
		return this.setValue((Float) value);
	}

	@Override
	public DbFieldType getFieldType() {
		return DbFieldType.DECIMAL;
	}

}

/**
 * 字段，双精度小数
 */
class DbFieldDataDouble extends FieldDataDbBase<Double> {

	public DbFieldDataDouble(Class<?> valueType) {
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
		return true;
	}

	@Override
	public boolean setValue(Object value) {
		return this.setValue((Double) value);
	}

	@Override
	public DbFieldType getFieldType() {
		return DbFieldType.DECIMAL;
	}

}

/**
 * 字段，布尔
 */
class DbFieldDataBoolean extends FieldDataDbBase<Boolean> {

	public DbFieldDataBoolean(Class<?> valueType) {
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
		return true;
	}

	@Override
	public boolean setValue(Object value) {
		return this.setValue((Boolean) value);
	}

	@Override
	public DbFieldType getFieldType() {
		return DbFieldType.NUMERIC;
	}

}

/**
 * 字段，枚举
 */
class DbFieldDataEnum extends FieldDataDbBase<Enum<?>> {

	public DbFieldDataEnum(Class<?> valueType) {
		this.setValueType(valueType);
		for (Object item : valueType.getEnumConstants()) {
			this.setValue(item);// 获取枚举的第一个值
			break;
		}
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
		return true;
	}

	@Override
	public boolean setValue(Object value) {
		return this.setValue((Enum<?>) value);
	}

	@Override
	public DbFieldType getFieldType() {
		return DbFieldType.ALPHANUMERIC;
	}

}