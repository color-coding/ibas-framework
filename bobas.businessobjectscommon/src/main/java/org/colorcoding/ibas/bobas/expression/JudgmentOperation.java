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
public enum JudgmentOperation {
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
	GREATER_THAN,
	/**
	 * 小于
	 */
	LESS_THAN,
	/**
	 * 大于等于
	 */
	GREATER_EQUAL,
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

	public static JudgmentOperation valueOf(int value) {
		JudgmentOperation[] ops = values();
		if (value < 0 || value >= ops.length) {
			throw new ExpressionException(
					String.format("invalid operation value: %d, valid range [0, %d]", value, ops.length - 1));
		}
		return ops[value];
	}

	public static JudgmentOperation valueOf(String value, boolean ignoreCase) {
		if (ignoreCase) {
			for (Object item : JudgmentOperation.class.getEnumConstants()) {
				if (item.toString().equalsIgnoreCase(value)) {
					return (JudgmentOperation) item;
				}
			}
		}
		return JudgmentOperation.valueOf(value);
	}

	public static JudgmentOperation valueOf(ConditionOperation value) {
		if (value == null) {
			return null;
		}
		if (value == ConditionOperation.CONTAIN) {
			return JudgmentOperation.CONTAIN;
		} else if (value == ConditionOperation.NOT_CONTAIN) {
			return JudgmentOperation.NOT_CONTAIN;
		} else if (value == ConditionOperation.EQUAL) {
			return JudgmentOperation.EQUAL;
		} else if (value == ConditionOperation.NOT_EQUAL) {
			return JudgmentOperation.NOT_EQUAL;
		} else if (value == ConditionOperation.GREATER_EQUAL) {
			return JudgmentOperation.GREATER_EQUAL;
		} else if (value == ConditionOperation.GREATER_THAN) {
			return JudgmentOperation.GREATER_THAN;
		} else if (value == ConditionOperation.LESS_EQUAL) {
			return JudgmentOperation.LESS_EQUAL;
		} else if (value == ConditionOperation.LESS_THAN) {
			return JudgmentOperation.LESS_THAN;
		} else if (value == ConditionOperation.START) {
			return JudgmentOperation.BEGIN_WITH;
		} else if (value == ConditionOperation.END) {
			return JudgmentOperation.END_WITH;
		} else if (value == ConditionOperation.IS_NULL) {
			return JudgmentOperation.IS_NULL;
		} else if (value == ConditionOperation.NOT_NULL) {
			return JudgmentOperation.NOT_NULL;
		} else if (value == ConditionOperation.IN) {
			return JudgmentOperation.IN;
		} else if (value == ConditionOperation.NOT_IN) {
			return JudgmentOperation.NOT_IN;
		}
		return null;
	}

	public static JudgmentOperation valueOf(ConditionRelationship value) {
		if (value == null) {
			return null;
		}
		if (value == ConditionRelationship.AND) {
			return JudgmentOperation.AND;
		} else if (value == ConditionRelationship.OR) {
			return JudgmentOperation.OR;
		}
		return null;
	}

	public static JudgmentOperation valueOf(emConditionOperation value) {
		if (value == null) {
			return null;
		}
		if (value == emConditionOperation.CONTAIN) {
			return JudgmentOperation.CONTAIN;
		} else if (value == emConditionOperation.NOT_CONTAIN) {
			return JudgmentOperation.NOT_CONTAIN;
		} else if (value == emConditionOperation.EQUAL) {
			return JudgmentOperation.EQUAL;
		} else if (value == emConditionOperation.NOT_EQUAL) {
			return JudgmentOperation.NOT_EQUAL;
		} else if (value == emConditionOperation.GREATER_EQUAL) {
			return JudgmentOperation.GREATER_EQUAL;
		} else if (value == emConditionOperation.GREATER_THAN) {
			return JudgmentOperation.GREATER_THAN;
		} else if (value == emConditionOperation.LESS_EQUAL) {
			return JudgmentOperation.LESS_EQUAL;
		} else if (value == emConditionOperation.LESS_THAN) {
			return JudgmentOperation.LESS_THAN;
		} else if (value == emConditionOperation.BEGIN_WITH) {
			return JudgmentOperation.BEGIN_WITH;
		} else if (value == emConditionOperation.END_WITH) {
			return JudgmentOperation.END_WITH;
		} else if (value == emConditionOperation.NOT_BEGIN_WITH) {
			return JudgmentOperation.NOT_BEGIN_WITH;
		} else if (value == emConditionOperation.NOT_END_WITH) {
			return JudgmentOperation.NOT_END_WITH;
		} else if (value == emConditionOperation.IN) {
			return JudgmentOperation.IN;
		} else if (value == emConditionOperation.NOT_IN) {
			return JudgmentOperation.NOT_IN;
		}
		return null;
	}

	public static JudgmentOperation valueOf(emConditionRelationship value) {
		if (value == null) {
			return null;
		}
		if (value == emConditionRelationship.AND) {
			return JudgmentOperation.AND;
		} else if (value == emConditionRelationship.OR) {
			return JudgmentOperation.OR;
		}
		return null;
	}

}
