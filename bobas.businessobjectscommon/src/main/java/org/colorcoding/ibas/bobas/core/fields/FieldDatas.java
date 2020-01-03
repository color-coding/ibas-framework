package org.colorcoding.ibas.bobas.core.fields;

import java.math.BigDecimal;

import org.colorcoding.ibas.bobas.core.IBusinessObjectBase;
import org.colorcoding.ibas.bobas.core.IBusinessObjectsBase;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;

//以下均为package内类型
/**
 * 字段，整数型
 */
final class FieldDataInteger extends FieldDataBase<Integer> {

	public final static Integer DEFAULT_VALUE = Integer.valueOf("0");

	public FieldDataInteger(Class<?> valueType) {
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

}

/**
 * 字段，短整数型
 */
final class FieldDataShort extends FieldDataBase<Short> {

	public final static Short DEFAULT_VALUE = Short.valueOf("0");

	public FieldDataShort(Class<?> valueType) {
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

}

/**
 * 字段，字符型
 */
final class FieldDataChar extends FieldDataBase<Character> {

	public FieldDataChar(Class<?> valueType) {
		this.setValueType(valueType);
	}

	@Override
	public final Class<?> getValueType() {
		return Character.class;
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

}

/**
 * 字段，字符串
 */
final class FieldDataString extends FieldDataBase<String> {

	public FieldDataString(Class<?> valueType) {
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
}

/**
 * 字段，十进制数值
 */
final class FieldDataDecimal extends FieldDataBase<BigDecimal> {

	public final static BigDecimal DEFAULT_VALUE = Decimal.ZERO;

	public FieldDataDecimal(Class<?> valueType) {
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
}

/**
 * 字段，日期
 */
final class FieldDataDateTime extends FieldDataBase<DateTime> {

	public final static DateTime DEFAULT_VALUE = DateTime.MIN_VALUE;

	public FieldDataDateTime(Class<?> valueType) {
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
}

/**
 * 字段，单精度小数
 */
final class FieldDataFloat extends FieldDataBase<Float> {

	public final static Float DEFAULT_VALUE = Float.valueOf("0");

	public FieldDataFloat(Class<?> valueType) {
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
}

/**
 * 字段，双精度小数
 */
final class FieldDataDouble extends FieldDataBase<Double> {

	public final static Double DEFAULT_VALUE = Double.valueOf("0");

	public FieldDataDouble(Class<?> valueType) {
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
}

/**
 * 字段，布尔
 */
final class FieldDataBoolean extends FieldDataBase<Boolean> {

	public FieldDataBoolean(Class<?> valueType) {
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
}

/**
 * 字段，枚举
 */
final class FieldDataEnum extends FieldDataBase<Enum<?>> {

	public FieldDataEnum(Class<?> valueType) {
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
}

/**
 * 字段，业务对象
 */
final class FieldDataBO extends FieldDataBase<IBusinessObjectBase> {

	public FieldDataBO(Class<?> valueType) {
		this.setValueType(valueType);
		this.setSavable(true);
	}

	private IBusinessObjectBase value = null;

	@Override
	public final IBusinessObjectBase getValue() {
		return this.value;
	}

	public final boolean setValue(IBusinessObjectBase value) {
		if (this.value != null && this.value.equals(value)) {
			return false;
		}
		this.value = value;
		return true;
	}

	@Override
	public final boolean setValue(Object value) {
		return this.setValue((IBusinessObjectBase) value);
	}
}

/**
 * 字段，业务对象集合
 */
final class FieldDataBOs extends FieldDataBase<IBusinessObjectsBase<?>> {

	public FieldDataBOs(Class<?> valueType) {
		this.setValueType(valueType);
		this.setSavable(true);
	}

	private IBusinessObjectsBase<?> value = null;

	@Override
	public final IBusinessObjectsBase<?> getValue() {
		return this.value;
	}

	public final boolean setValue(IBusinessObjectsBase<?> value) {
		if (this.value != null && this.value.equals(value)) {
			return false;
		}
		this.value = value;
		return true;
	}

	@Override
	public final boolean setValue(Object value) {
		return this.setValue((IBusinessObjectsBase<?>) value);
	}
}