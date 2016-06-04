package org.colorcoding.ibas.bobas.data;

import java.math.BigDecimal;

import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.i18n.i18n;

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
		if (type == String.class) {
			return toString(value);
		} else if (type == Integer.class) {
			String tmp = toString(value);
			return Integer.valueOf(tmp);
		} else if (type == Double.class) {
			String tmp = toString(value);
			return Double.valueOf(tmp);
		} else if (type == Long.class) {
			String tmp = toString(value);
			return Long.valueOf(tmp);
		} else if (type == Short.class) {
			String tmp = toString(value);
			return Short.valueOf(tmp);
		} else if (type == Boolean.class) {
			String tmp = toString(value);
			return Boolean.valueOf(tmp);
		} else if (type == DateTime.class) {
			String tmp = toString(value);
			return DateTime.valueOf(tmp);
		} else if (type == Decimal.class) {
			String tmp = toString(value);
			return new Decimal(tmp);
		} else if (type.isEnum()) {
			String tmp = toString(value);
			for (Object item : type.getEnumConstants()) {
				if (tmp.equals(item.toString())) {
					return item;
				}
			}
		}
		throw new DataConvertException(i18n.prop("msg_bobas_not_support_convert_to_type", type.getName()));
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
			return ConditionRelationship.cr_AND;
		} else if (value == emConditionRelationship.OR) {
			return ConditionRelationship.cr_OR;
		} else if (value == emConditionRelationship.NONE) {
			return ConditionRelationship.cr_NONE;
		}
		throw new DataConvertException(i18n.prop("msg_bobas_not_support_convert_to_type", value));
	}

	public static ConditionOperation toOperation(emConditionOperation value) {
		if (value == null) {
			throw new NullPointerException();
		}
		if (value == emConditionOperation.CONTAIN) {
			return ConditionOperation.co_CONTAIN;
		} else if (value == emConditionOperation.NOT_CONTAIN) {
			return ConditionOperation.co_NOT_CONTAIN;
		} else if (value == emConditionOperation.EQUAL) {
			return ConditionOperation.co_EQUAL;
		} else if (value == emConditionOperation.NOT_EQUAL) {
			return ConditionOperation.co_NOT_EQUAL;
		} else if (value == emConditionOperation.GRATER_EQUAL) {
			return ConditionOperation.co_GRATER_EQUAL;
		} else if (value == emConditionOperation.GRATER_THAN) {
			return ConditionOperation.co_GRATER_THAN;
		} else if (value == emConditionOperation.LESS_EQUAL) {
			return ConditionOperation.co_LESS_EQUAL;
		} else if (value == emConditionOperation.LESS_THAN) {
			return ConditionOperation.co_LESS_THAN;
		} else if (value == emConditionOperation.BEGIN_WITH) {
			return ConditionOperation.co_START;
		} else if (value == emConditionOperation.END_WITH) {
			return ConditionOperation.co_END;
		}
		throw new DataConvertException(i18n.prop("msg_bobas_not_support_convert_to_type", value));
	}

	public static emConditionRelationship toRelationship(ConditionRelationship value) {
		if (value == null) {
			throw new NullPointerException();
		}
		if (value == ConditionRelationship.cr_AND) {
			return emConditionRelationship.AND;
		} else if (value == ConditionRelationship.cr_OR) {
			return emConditionRelationship.OR;
		} else if (value == ConditionRelationship.cr_NONE) {
			return emConditionRelationship.NONE;
		}
		throw new DataConvertException(i18n.prop("msg_bobas_not_support_convert_to_type", value));
	}

	public static emConditionOperation toOperation(ConditionOperation value) {
		if (value == null) {
			throw new NullPointerException();
		}
		if (value == ConditionOperation.co_CONTAIN) {
			return emConditionOperation.CONTAIN;
		} else if (value == ConditionOperation.co_NOT_CONTAIN) {
			return emConditionOperation.NOT_CONTAIN;
		} else if (value == ConditionOperation.co_EQUAL) {
			return emConditionOperation.EQUAL;
		} else if (value == ConditionOperation.co_NOT_EQUAL) {
			return emConditionOperation.NOT_EQUAL;
		} else if (value == ConditionOperation.co_GRATER_EQUAL) {
			return emConditionOperation.GRATER_EQUAL;
		} else if (value == ConditionOperation.co_GRATER_THAN) {
			return emConditionOperation.GRATER_THAN;
		} else if (value == ConditionOperation.co_LESS_EQUAL) {
			return emConditionOperation.LESS_EQUAL;
		} else if (value == ConditionOperation.co_LESS_THAN) {
			return emConditionOperation.LESS_THAN;
		} else if (value == ConditionOperation.co_START) {
			return emConditionOperation.BEGIN_WITH;
		} else if (value == ConditionOperation.co_END) {
			return emConditionOperation.END_WITH;
		}
		throw new DataConvertException(i18n.prop("msg_bobas_not_support_convert_to_type", value));
	}
}
