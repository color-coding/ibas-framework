package org.colorcoding.ibas.bobas.common;

import java.math.BigDecimal;
import java.util.Date;

import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.i18n.I18N;

public class DataConvert {

	/**
	 * 转换类型
	 * 
	 * @param type  目标类型
	 * @param value 值
	 * @return 目标类型值
	 */
	@SuppressWarnings("unchecked")
	public static <P> P convert(Class<P> type, Object value) {
		if (type == null) {
			return null;
		}
		if (value == null) {
			return null;
		}
		if (type == value.getClass()) {
			return (P) value;
		}
		String sValue = Strings.toString(value);
		if (type == String.class) {
			return (P) sValue;
		} else if (type == Short.class) {
			return (P) (Strings.isNullOrEmpty(sValue) ? Numbers.defaultValue(type) : Short.valueOf(sValue));
		} else if (type == Integer.class) {
			return (P) (Strings.isNullOrEmpty(sValue) ? Numbers.defaultValue(type) : Integer.valueOf(sValue));
		} else if (type == Long.class) {
			return (P) (Strings.isNullOrEmpty(sValue) ? Numbers.defaultValue(type) : Long.valueOf(sValue));
		} else if (type == Double.class) {
			return (P) (Strings.isNullOrEmpty(sValue) ? Numbers.defaultValue(type) : Double.valueOf(sValue));
		} else if (type == Float.class) {
			return (P) (Strings.isNullOrEmpty(sValue) ? Numbers.defaultValue(type) : Float.valueOf(sValue));
		} else if (type == BigDecimal.class) {
			return (P) (Strings.isNullOrEmpty(sValue) ? Decimals.VALUE_ZERO : Decimals.valueOf(sValue));
		} else if (type == Boolean.class) {
			return (P) Boolean.valueOf(sValue);
		} else if (type == DateTime.class) {
			if (value.getClass() == Long.class) {
				return (P) DateTimes.valueOf((long) value);
			} else if (value.getClass() == Date.class) {
				return (P) DateTimes.valueOf((Date) value);
			} else {
				return (P) DateTimes.valueOf(sValue);
			}
		} else if (type.isEnum()) {
			for (Object item : type.getEnumConstants()) {
				if (sValue.equals(item.toString())) {
					return (P) item;
				}
			}
		}
		throw new ClassCastException(I18N.prop("msg_bobas_not_support_convert_to_type", type.getName()));
	}

}
