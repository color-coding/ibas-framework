package org.colorcoding.ibas.bobas.expressions;

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
public enum JudmentOperations {
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
	 * 与（仅布尔比较有效）
	 */
	AND,
	/**
	 * 或（仅布尔比较有效）
	 */
	OR;

	public static JudmentOperations valueOf(ConditionOperation value) {
		if (value == null) {
			return null;
		}
		if (value == ConditionOperation.co_CONTAIN) {
			return JudmentOperations.CONTAIN;
		} else if (value == ConditionOperation.co_NOT_CONTAIN) {
			return JudmentOperations.NOT_CONTAIN;
		} else if (value == ConditionOperation.co_EQUAL) {
			return JudmentOperations.EQUAL;
		} else if (value == ConditionOperation.co_NOT_EQUAL) {
			return JudmentOperations.NOT_EQUAL;
		} else if (value == ConditionOperation.co_GRATER_EQUAL) {
			return JudmentOperations.GRATER_EQUAL;
		} else if (value == ConditionOperation.co_GRATER_THAN) {
			return JudmentOperations.GRATER_THAN;
		} else if (value == ConditionOperation.co_LESS_EQUAL) {
			return JudmentOperations.LESS_EQUAL;
		} else if (value == ConditionOperation.co_LESS_THAN) {
			return JudmentOperations.LESS_THAN;
		} else if (value == ConditionOperation.co_START) {
			return JudmentOperations.BEGIN_WITH;
		} else if (value == ConditionOperation.co_END) {
			return JudmentOperations.END_WITH;
		}
		return null;
	}

	public static JudmentOperations valueOf(ConditionRelationship value) {
		if (value == null) {
			return null;
		}
		if (value == ConditionRelationship.cr_AND) {
			return JudmentOperations.AND;
		} else if (value == ConditionRelationship.cr_OR) {
			return JudmentOperations.OR;
		}
		return null;
	}

	public static JudmentOperations valueOf(emConditionOperation value) {
		if (value == null) {
			return null;
		}
		if (value == emConditionOperation.CONTAIN) {
			return JudmentOperations.CONTAIN;
		} else if (value == emConditionOperation.NOT_CONTAIN) {
			return JudmentOperations.NOT_CONTAIN;
		} else if (value == emConditionOperation.EQUAL) {
			return JudmentOperations.EQUAL;
		} else if (value == emConditionOperation.NOT_EQUAL) {
			return JudmentOperations.NOT_EQUAL;
		} else if (value == emConditionOperation.GRATER_EQUAL) {
			return JudmentOperations.GRATER_EQUAL;
		} else if (value == emConditionOperation.GRATER_THAN) {
			return JudmentOperations.GRATER_THAN;
		} else if (value == emConditionOperation.LESS_EQUAL) {
			return JudmentOperations.LESS_EQUAL;
		} else if (value == emConditionOperation.LESS_THAN) {
			return JudmentOperations.LESS_THAN;
		} else if (value == emConditionOperation.BEGIN_WITH) {
			return JudmentOperations.BEGIN_WITH;
		} else if (value == emConditionOperation.END_WITH) {
			return JudmentOperations.END_WITH;
		}
		return null;
	}

	public static JudmentOperations valueOf(emConditionRelationship value) {
		if (value == null) {
			return null;
		}
		if (value == emConditionRelationship.AND) {
			return JudmentOperations.AND;
		} else if (value == emConditionRelationship.OR) {
			return JudmentOperations.OR;
		}
		return null;
	}
}
