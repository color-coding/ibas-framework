package org.colorcoding.bobas.common;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.bobas.MyConsts;
import org.colorcoding.bobas.data.IEnumItem;
import org.colorcoding.bobas.mapping.db.DbValue;

/**
 * 对比条件
 * 
 */
@XmlType(name = "ConditionOperation", namespace = MyConsts.NAMESPACE_BOBAS_COMMON)
public enum ConditionOperation implements IEnumItem {
	/** 无 */
	@DbValue(value = "NONE") co_NONE(0),
	/** 等于(=) */
	@DbValue(value = "EQUAL") co_EQUAL(1),
	/** 大于(>) */
	@DbValue(value = "GRATER_THAN") co_GRATER_THAN(2),
	/** 小于(<) */
	@DbValue(value = "LESS_THAN") co_LESS_THAN(3),
	/** 大于等于(>=) */
	@DbValue(value = "GRATER_EQUAL") co_GRATER_EQUAL(4),
	/** 小于等于(<=) */
	@DbValue(value = "LESS_EQUAL") co_LESS_EQUAL(5),
	/** 不等于(<>) */
	@DbValue(value = "NOT_EQUAL") co_NOT_EQUAL(6),
	/** 包含Like (%) */
	@DbValue(value = "CONTAIN") co_CONTAIN(7),
	/** 不包含Not like (%) */
	@DbValue(value = "NOT_CONTAIN") co_NOT_CONTAIN(8),
	/** 开始为(...%) */
	@DbValue(value = "START") co_START(9),
	/** 结束为(%...) */
	@DbValue(value = "END") co_END(10),
	// 包含于((A1...An))
	// @DbValue(value = "BETWEEN") co_BETWEEN(11),
	// // 不包含((A1...An))
	// @DbValue(value = "NOT_BETWEEN") co_NOT_BETWEEN(12),
	/** 是空 */
	@DbValue(value = "IS_NULL") co_IS_NULL(13),
	/** 非空 */
	@DbValue(value = "NOT_NULL") co_NOT_NULL(14);

	private int intValue;
	private static java.util.HashMap<Integer, ConditionOperation> mappings;

	private synchronized static java.util.HashMap<Integer, ConditionOperation> getMappings() {
		if (mappings == null) {
			mappings = new java.util.HashMap<Integer, ConditionOperation>();
		}
		return mappings;
	}

	private ConditionOperation(int value) {
		intValue = value;
		ConditionOperation.getMappings().put(value, this);
	}

	public int getValue() {
		return intValue;
	}

	public static ConditionOperation forValue(int value) {
		return getMappings().get(value);
	}
}