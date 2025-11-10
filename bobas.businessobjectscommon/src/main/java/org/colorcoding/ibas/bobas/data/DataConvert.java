package org.colorcoding.ibas.bobas.data;

import java.math.BigDecimal;
import java.util.Date;

import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Decimals;
import org.colorcoding.ibas.bobas.common.Numbers;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.i18n.I18N;

public class DataConvert {

	protected DataConvert() {
	}

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
		String sValue = Strings.valueOf(value);
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

	public static String toString(Object value) {
		return convert(String.class, value);
	}

	public static ConditionRelationship toRelationship(emConditionRelationship value) {
		if (value == null) {
			throw new NullPointerException();
		}
		if (value == emConditionRelationship.AND) {
			return ConditionRelationship.AND;
		} else if (value == emConditionRelationship.OR) {
			return ConditionRelationship.OR;
		} else if (value == emConditionRelationship.NONE) {
			return ConditionRelationship.NONE;
		}
		throw new ClassCastException(I18N.prop("msg_bobas_not_support_convert_to_type", value));
	}

	public static ConditionOperation toOperation(emConditionOperation value) {
		if (value == null) {
			throw new NullPointerException();
		}
		if (value == emConditionOperation.CONTAIN) {
			return ConditionOperation.CONTAIN;
		} else if (value == emConditionOperation.NOT_CONTAIN) {
			return ConditionOperation.NOT_CONTAIN;
		} else if (value == emConditionOperation.EQUAL) {
			return ConditionOperation.EQUAL;
		} else if (value == emConditionOperation.NOT_EQUAL) {
			return ConditionOperation.NOT_EQUAL;
		} else if (value == emConditionOperation.GRATER_EQUAL) {
			return ConditionOperation.GRATER_EQUAL;
		} else if (value == emConditionOperation.GRATER_THAN) {
			return ConditionOperation.GRATER_THAN;
		} else if (value == emConditionOperation.LESS_EQUAL) {
			return ConditionOperation.LESS_EQUAL;
		} else if (value == emConditionOperation.LESS_THAN) {
			return ConditionOperation.LESS_THAN;
		} else if (value == emConditionOperation.BEGIN_WITH) {
			return ConditionOperation.START;
		} else if (value == emConditionOperation.END_WITH) {
			return ConditionOperation.END;
		}
		throw new ClassCastException(I18N.prop("msg_bobas_not_support_convert_to_type", value));
	}

	public static emConditionRelationship toRelationship(ConditionRelationship value) {
		if (value == null) {
			throw new NullPointerException();
		}
		if (value == ConditionRelationship.AND) {
			return emConditionRelationship.AND;
		} else if (value == ConditionRelationship.OR) {
			return emConditionRelationship.OR;
		} else if (value == ConditionRelationship.NONE) {
			return emConditionRelationship.NONE;
		}
		throw new ClassCastException(I18N.prop("msg_bobas_not_support_convert_to_type", value));
	}

	public static emConditionOperation toOperation(ConditionOperation value) {
		if (value == null) {
			throw new NullPointerException();
		}
		if (value == ConditionOperation.CONTAIN) {
			return emConditionOperation.CONTAIN;
		} else if (value == ConditionOperation.NOT_CONTAIN) {
			return emConditionOperation.NOT_CONTAIN;
		} else if (value == ConditionOperation.EQUAL) {
			return emConditionOperation.EQUAL;
		} else if (value == ConditionOperation.NOT_EQUAL) {
			return emConditionOperation.NOT_EQUAL;
		} else if (value == ConditionOperation.GRATER_EQUAL) {
			return emConditionOperation.GRATER_EQUAL;
		} else if (value == ConditionOperation.GRATER_THAN) {
			return emConditionOperation.GRATER_THAN;
		} else if (value == ConditionOperation.LESS_EQUAL) {
			return emConditionOperation.LESS_EQUAL;
		} else if (value == ConditionOperation.LESS_THAN) {
			return emConditionOperation.LESS_THAN;
		} else if (value == ConditionOperation.START) {
			return emConditionOperation.BEGIN_WITH;
		} else if (value == ConditionOperation.END) {
			return emConditionOperation.END_WITH;
		}
		throw new ClassCastException(I18N.prop("msg_bobas_not_support_convert_to_type", value));
	}

	@Deprecated
	public static final String STRING_VALUE_ZERO = Strings.VALUE_ZERO;
	@Deprecated
	public static final String STRING_VALUE_EMPTY = Strings.VALUE_EMPTY;

	@Deprecated
	public static boolean isNumeric(String value) {
		return Numbers.isNumeric(value);
	}

	@Deprecated
	public static boolean isNullOrEmpty(String value) {
		return Strings.isNullOrEmpty(value);
	}
}
