package org.colorcoding.ibas.bobas.expression;

import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.data.emConditionOperation;
import org.colorcoding.ibas.bobas.data.emConditionRelationship;

/**
 * 判断操作
 * 
 * @author Niuren.Zhu
 *
 */
public enum JudmentOperation {
	/**
	 * 等于
	 */
	EQUAL,
	/**
	 * 不等于
	 */
	NOT_EQUAL,
	/**
	 * 大于
	 */
	GRATER_THAN,
	/**
	 * 小于
	 */
	LESS_THAN,
	/**
	 * 大于等于
	 */
	GRATER_EQUAL,
	/**
	 * 小于等于
	 */
	LESS_EQUAL,
	/**
	 * 开始于（仅字符比较有效）
	 */
	BEGIN_WITH,
	/**
	 * 不是开始于（仅字符比较有效）
	 */
	NOT_BEGIN_WITH,
	/**
	 * 结束于（仅字符比较有效）
	 */
	END_WITH,
	/**
	 * 不是结束于（仅字符比较有效）
	 */
	NOT_END_WITH,
	/**
	 * 包括（仅字符比较有效）
	 */
	CONTAIN,
	/**
	 * 不包含（仅字符比较有效）
	 */
	NOT_CONTAIN,
	/**
	 * 是空
	 */
	IS_NULL,
	/**
	 * 非空
	 */
	NOT_NULL,
	/**
	 * 在
	 */
	IN,
	/**
	 * 不在
	 */
	NOT_IN,
	/**
	 * 与（仅布尔比较有效）
	 */
	AND,
	/**
	 * 或（仅布尔比较有效）
	 */
	OR;

	public static JudmentOperation valueOf(int value) {
		return values()[value];
	}

	public static JudmentOperation valueOf(String value, boolean ignoreCase) {
		if (ignoreCase) {
			for (Object item : JudmentOperation.class.getEnumConstants()) {
				if (item.toString().equalsIgnoreCase(value)) {
					return (JudmentOperation) item;
				}
			}
		}
		return JudmentOperation.valueOf(value);
	}

	public static JudmentOperation valueOf(ConditionOperation value) {
		if (value == null) {
			return null;
		}
		if (value == ConditionOperation.CONTAIN) {
			return JudmentOperation.CONTAIN;
		} else if (value == ConditionOperation.NOT_CONTAIN) {
			return JudmentOperation.NOT_CONTAIN;
		} else if (value == ConditionOperation.EQUAL) {
			return JudmentOperation.EQUAL;
		} else if (value == ConditionOperation.NOT_EQUAL) {
			return JudmentOperation.NOT_EQUAL;
		} else if (value == ConditionOperation.GRATER_EQUAL) {
			return JudmentOperation.GRATER_EQUAL;
		} else if (value == ConditionOperation.GRATER_THAN) {
			return JudmentOperation.GRATER_THAN;
		} else if (value == ConditionOperation.LESS_EQUAL) {
			return JudmentOperation.LESS_EQUAL;
		} else if (value == ConditionOperation.LESS_THAN) {
			return JudmentOperation.LESS_THAN;
		} else if (value == ConditionOperation.START) {
			return JudmentOperation.BEGIN_WITH;
		} else if (value == ConditionOperation.END) {
			return JudmentOperation.END_WITH;
		} else if (value == ConditionOperation.IS_NULL) {
			return JudmentOperation.IS_NULL;
		} else if (value == ConditionOperation.NOT_NULL) {
			return JudmentOperation.NOT_NULL;
		} else if (value == ConditionOperation.IN) {
			return JudmentOperation.IN;
		} else if (value == ConditionOperation.NOT_IN) {
			return JudmentOperation.NOT_IN;
		}
		return null;
	}

	public static JudmentOperation valueOf(ConditionRelationship value) {
		if (value == null) {
			return null;
		}
		if (value == ConditionRelationship.AND) {
			return JudmentOperation.AND;
		} else if (value == ConditionRelationship.OR) {
			return JudmentOperation.OR;
		}
		return null;
	}

	public static JudmentOperation valueOf(emConditionOperation value) {
		if (value == null) {
			return null;
		}
		if (value == emConditionOperation.CONTAIN) {
			return JudmentOperation.CONTAIN;
		} else if (value == emConditionOperation.NOT_CONTAIN) {
			return JudmentOperation.NOT_CONTAIN;
		} else if (value == emConditionOperation.EQUAL) {
			return JudmentOperation.EQUAL;
		} else if (value == emConditionOperation.NOT_EQUAL) {
			return JudmentOperation.NOT_EQUAL;
		} else if (value == emConditionOperation.GRATER_EQUAL) {
			return JudmentOperation.GRATER_EQUAL;
		} else if (value == emConditionOperation.GRATER_THAN) {
			return JudmentOperation.GRATER_THAN;
		} else if (value == emConditionOperation.LESS_EQUAL) {
			return JudmentOperation.LESS_EQUAL;
		} else if (value == emConditionOperation.LESS_THAN) {
			return JudmentOperation.LESS_THAN;
		} else if (value == emConditionOperation.BEGIN_WITH) {
			return JudmentOperation.BEGIN_WITH;
		} else if (value == emConditionOperation.END_WITH) {
			return JudmentOperation.END_WITH;
		} else if (value == emConditionOperation.NOT_BEGIN_WITH) {
			return JudmentOperation.NOT_BEGIN_WITH;
		} else if (value == emConditionOperation.NOT_END_WITH) {
			return JudmentOperation.NOT_END_WITH;
		} else if (value == emConditionOperation.IN) {
			return JudmentOperation.IN;
		} else if (value == emConditionOperation.NOT_IN) {
			return JudmentOperation.NOT_IN;
		}
		return null;
	}

	public static JudmentOperation valueOf(emConditionRelationship value) {
		if (value == null) {
			return null;
		}
		if (value == emConditionRelationship.AND) {
			return JudmentOperation.AND;
		} else if (value == emConditionRelationship.OR) {
			return JudmentOperation.OR;
		}
		return null;
	}

}
