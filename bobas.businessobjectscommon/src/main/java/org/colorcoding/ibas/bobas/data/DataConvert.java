package org.colorcoding.ibas.bobas.data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.i18n.I18N;

public class DataConvert {

	/**
	 * 转换类型
	 * 
	 * @param type
	 *            目标类型
	 * @param value
	 *            值
	 * @return 目标类型值
	 */
	public static Object convert(Class<?> type, Object value) {
		if (type == null || value == null) {
			return value;
		}
		if (type == value.getClass()) {
			return value;
		}
		if (type == String.class) {
			return toString(value);
		} else if (type == Short.class) {
			return Short.valueOf(toString(value));
		} else if (type == Integer.class) {
			return Integer.valueOf(toString(value));
		} else if (type == Long.class) {
			return Long.valueOf(toString(value));
		} else if (type == BigInteger.class) {
			return new BigInteger(toString(value));
		} else if (type == Double.class) {
			return Double.valueOf(toString(value));
		} else if (type == Float.class) {
			return Float.valueOf(toString(value));
		} else if (type == Decimal.class) {
			return new Decimal(toString(value));
		} else if (type == BigDecimal.class) {
			return new BigDecimal(toString(value));
		} else if (type == Boolean.class) {
			return Boolean.valueOf(toString(value));
		} else if (type == DateTime.class) {
			if (value.getClass() == Long.class) {
				return DateTime.valueOf((long) value);
			} else if (value.getClass() == Date.class) {
				return DateTime.valueOf((Date) value);
			} else {
				return DateTime.valueOf(toString(value));
			}
		} else if (type.isEnum()) {
			String tmp = toString(value);
			for (Object item : type.getEnumConstants()) {
				if (tmp.equals(item.toString())) {
					return item;
				}
			}
		}
		throw new DataConvertException(I18N.prop("msg_bobas_not_support_convert_to_type", type.getName()));
	}

	public static String toString(Object value) {
		if (value == null) {
			return null;
		}
		return value.toString();
	}

	public static String toString(DateTime value) {
		if (value == null) {
			return null;
		}
		return value.toString();
	}

	public static String toString(DateTime value, String format) {
		if (value == null) {
			return null;
		}
		return value.toString(format);
	}

	public static double toDouble(float value) {
		return value;
	}

	public static double toDouble(int value) {
		return value;
	}

	public static double toDouble(long value) {
		return value;
	}

	public static double toDouble(short value) {
		return value;
	}

	public static double toDouble(BigDecimal value) {
		return value.doubleValue();
	}

	public static float toFloat(double value) {
		return (float) value;
	}

	public static float toFloat(int value) {
		return value;
	}

	public static float toFloat(long value) {
		return value;
	}

	public static float toFloat(short value) {
		return value;
	}

	public static float toFloat(BigDecimal value) {
		return value.floatValue();
	}

	public static int toInt(double value) {
		return (int) value;
	}

	public static int toInt(float value) {
		return (int) value;
	}

	public static int toInt(long value) {
		return (int) value;
	}

	public static int toInt(short value) {
		return value;
	}

	public static int toInt(BigDecimal value) {
		return value.intValue();
	}

	public static long toLong(double value) {
		return (long) value;
	}

	public static long toLong(float value) {
		return (long) value;
	}

	public static long toLong(int value) {
		return value;
	}

	public static long toLong(short value) {
		return value;
	}

	public static long toLong(BigDecimal value) {
		return value.longValue();
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
		throw new DataConvertException(I18N.prop("msg_bobas_not_support_convert_to_type", value));
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
		throw new DataConvertException(I18N.prop("msg_bobas_not_support_convert_to_type", value));
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
		throw new DataConvertException(I18N.prop("msg_bobas_not_support_convert_to_type", value));
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
		throw new DataConvertException(I18N.prop("msg_bobas_not_support_convert_to_type", value));
	}

	/**
	 * 转换类型为KeyValue
	 * 
	 * @param type
	 *            目前可识别的类型：枚举类型
	 * @return
	 */
	public static KeyValue[] toKeyValues(Class<?> type) {
		if (type.isEnum()) {
			Object[] constants = type.getEnumConstants();
			if (constants.length > 0) {
				KeyValue[] values = new KeyValue[constants.length];
				for (int i = 0; i < values.length; i++) {
					Enum<?> item = (Enum<?>) constants[i];
					values[i] = new KeyValue(item.name(), item.ordinal());
				}
				return values;
			}
		}
		return new KeyValue[] {};
	}
}
