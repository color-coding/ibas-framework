package org.colorcoding.ibas.bobas.core.fields;

import java.math.BigDecimal;

import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;

//以下均为package内类型
/**
 * 字段，整数型
 */
final class DbFieldDataInteger extends FieldDataDbBase<Integer> {

	public static final Integer DEFAULT_VALUE = Integer.valueOf("0");

	public DbFieldDataInteger(Class<?> valueType) {
		this.setValueType(valueType);
	}

	private Integer value = DEFAULT_VALUE;

	@Override
	public final Integer getValue() {
		return this.value;
	}

	public final boolean setValue(Integer value) {
		if (value == null) {
			value = DEFAULT_VALUE;
		}
		if (this.value == value) {
			return false;
		}
		this.value = value;
		return true;
	}

	@Override
	public final boolean setValue(Object value) {
		return this.setValue((Integer) value);
	}

	@Override
	public final DbFieldType getFieldType() {
		return DbFieldType.NUMERIC;
	}

}

/**
 * 字段，短整数型
 */
final class DbFieldDataShort extends FieldDataDbBase<Short> {

	public static final Short DEFAULT_VALUE = Short.valueOf("0");

	public DbFieldDataShort(Class<?> valueType) {
		this.setValueType(valueType);
	}

	private Short value = DEFAULT_VALUE;

	@Override
	public final Short getValue() {
		return this.value;
	}

	public final boolean setValue(Short value) {
		if (value == null) {
			value = DEFAULT_VALUE;
		}
		if (this.value == value) {
			return false;
		}
		this.value = value;
		return true;
	}

	@Override
	public final boolean setValue(Object value) {
		return this.setValue((Short) value);
	}

	@Override
	public final DbFieldType getFieldType() {
		return DbFieldType.NUMERIC;
	}

}

/**
 * 字段，长整数型
 */
final class DbFieldDataLong extends FieldDataDbBase<Long> {

	public static final Long DEFAULT_VALUE = Long.valueOf("0");

	public DbFieldDataLong(Class<?> valueType) {
		this.setValueType(valueType);
	}

	private Long value = DEFAULT_VALUE;

	@Override
	public final Long getValue() {
		return this.value;
	}

	public final boolean setValue(Long value) {
		if (value == null) {
			value = DEFAULT_VALUE;
		}
		if (this.value == value) {
			return false;
		}
		this.value = value;
		return true;
	}

	@Override
	public final boolean setValue(Object value) {
		return this.setValue((Long) value);
	}

	@Override
	public final DbFieldType getFieldType() {
		return DbFieldType.NUMERIC;
	}

}

/**
 * 字段，字符型
 */
final class DbFieldDataChar extends FieldDataDbBase<Character> {

	public DbFieldDataChar(Class<?> valueType) {
		this.setValueType(valueType);
	}

	private Character value = null;

	@Override
	public final Character getValue() {
		return this.value;
	}

	public final boolean setValue(Character value) {
		if (this.value != null && this.value.equals(value)) {
			return false;
		}
		this.value = value;
		return true;
	}

	@Override
	public final boolean setValue(Object value) {
		return this.setValue((Character) value);
	}

	@Override
	public final DbFieldType getFieldType() {
		return DbFieldType.ALPHANUMERIC;
	}

}

/**
 * 字段，字符串
 */
final class DbFieldDataString extends FieldDataDbBase<String> {

	public DbFieldDataString(Class<?> valueType) {
		this.setValueType(valueType);
	}

	private String value = null;

	@Override
	public final String getValue() {
		return this.value;
	}

	public final boolean setValue(String value) {
		if (this.value != null && this.value.equals(value)) {
			return false;
		}
		this.value = value;
		return true;
	}

	@Override
	public final boolean setValue(Object value) {
		return this.setValue((String) value);
	}

	@Override
	public final DbFieldType getFieldType() {
		return DbFieldType.ALPHANUMERIC;
	}

}

/**
 * 字段，十进制数值
 */
final class DbFieldDataDecimal extends FieldDataDbBase<BigDecimal> {

	public static final BigDecimal DEFAULT_VALUE = Decimal.ZERO;

	public DbFieldDataDecimal(Class<?> valueType) {
		this.setValueType(valueType);
	}

	private BigDecimal value = DEFAULT_VALUE;

	@Override
	public final BigDecimal getValue() {
		return this.value;
	}

	public final boolean setValue(BigDecimal value) {
		if (value == null) {
			value = DEFAULT_VALUE;
		}
		if (this.value != null && (this.value.equals(value) || this.value.compareTo(value) == 0)) {
			return false;
		}
		this.value = value;
		return true;
	}

	@Override
	public final boolean setValue(Object value) {
		return this.setValue((BigDecimal) value);
	}

	@Override
	public final DbFieldType getFieldType() {
		return DbFieldType.DECIMAL;
	}

}

/**
 * 字段，日期
 */
final class DbFieldDataDateTime extends FieldDataDbBase<DateTime> {

	public static final DateTime DEFAULT_VALUE = DateTime.MIN_VALUE;

	public DbFieldDataDateTime(Class<?> valueType) {
		this.setValueType(valueType);
	}

	private DateTime value = DEFAULT_VALUE;

	@Override
	public final DateTime getValue() {
		return this.value;
	}

	public final boolean setValue(DateTime value) {
		if (value == null) {
			value = DEFAULT_VALUE;
		}
		if (this.value != null && (this.value.equals(value) || this.value.compareTo(value) == 0)) {
			return false;
		}
		this.value = value;
		return true;
	}

	@Override
	public final boolean setValue(Object value) {
		return this.setValue((DateTime) value);
	}

	@Override
	public final DbFieldType getFieldType() {
		return DbFieldType.DATE;
	}

}

/**
 * 字段，单精度小数
 */
final class DbFieldDataFloat extends FieldDataDbBase<Float> {

	public static final Float DEFAULT_VALUE = Float.valueOf("0");

	public DbFieldDataFloat(Class<?> valueType) {
		this.setValueType(valueType);
	}

	private Float value = DEFAULT_VALUE;

	@Override
	public final Float getValue() {
		return this.value;
	}

	public final boolean setValue(Float value) {
		if (value == null) {
			value = DEFAULT_VALUE;
		}
		if (this.value == value) {
			return false;
		}
		this.value = value;
		return true;
	}

	@Override
	public final boolean setValue(Object value) {
		return this.setValue((Float) value);
	}

	@Override
	public final DbFieldType getFieldType() {
		return DbFieldType.DECIMAL;
	}

}

/**
 * 字段，双精度小数
 */
final class DbFieldDataDouble extends FieldDataDbBase<Double> {

	public static final Double DEFAULT_VALUE = Double.valueOf("0");

	public DbFieldDataDouble(Class<?> valueType) {
		this.setValueType(valueType);
	}

	private Double value = DEFAULT_VALUE;

	@Override
	public final Double getValue() {
		return this.value;
	}

	public final boolean setValue(Double value) {
		if (value == null) {
			value = DEFAULT_VALUE;
		}
		if (this.value == value) {
			return false;
		}
		this.value = value;
		return true;
	}

	@Override
	public final boolean setValue(Object value) {
		return this.setValue((Double) value);
	}

	@Override
	public final DbFieldType getFieldType() {
		return DbFieldType.DECIMAL;
	}

}

/**
 * 字段，布尔
 */
final class DbFieldDataBoolean extends FieldDataDbBase<Boolean> {

	public DbFieldDataBoolean(Class<?> valueType) {
		this.setValueType(valueType);
	}

	private boolean value = false;

	@Override
	public final Boolean getValue() {
		return this.value;
	}

	public final boolean setValue(Boolean value) {
		if (this.value == value) {
			return false;
		}
		this.value = value;
		return true;
	}

	@Override
	public final boolean setValue(Object value) {
		return this.setValue((Boolean) value);
	}

	@Override
	public final DbFieldType getFieldType() {
		return DbFieldType.NUMERIC;
	}

}

/**
 * 字段，枚举
 */
final class DbFieldDataEnum extends FieldDataDbBase<Enum<?>> {

	public DbFieldDataEnum(Class<?> valueType) {
		this.setValueType(valueType);
		for (Object item : valueType.getEnumConstants()) {
			this.setValue(item);// 获取枚举的第一个值
			break;
		}
	}

	private Enum<?> value = null;

	@Override
	public final Enum<?> getValue() {
		return this.value;
	}

	public final boolean setValue(Enum<?> value) {
		if (this.value != null && this.value.equals(value)) {
			return false;
		}
		this.value = value;
		return true;
	}

	@Override
	public final boolean setValue(Object value) {
		return this.setValue((Enum<?>) value);
	}

	@Override
	public final DbFieldType getFieldType() {
		return DbFieldType.ALPHANUMERIC;
	}

}
