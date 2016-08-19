package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.data.IEnumItem;
import org.colorcoding.ibas.bobas.mapping.DbValue;

/**
 * 对比条件
 * 
 */
@XmlType(name = "ConditionOperation", namespace = MyConsts.NAMESPACE_BOBAS_COMMON)
public enum ConditionOperation implements IEnumItem {
	/** 无 */
	@DbValue(value = "NONE")
	co_NONE,
	/** 等于(=) */
	@DbValue(value = "EQUAL")
	co_EQUAL,
	/** 大于(>) */
	@DbValue(value = "GRATER_THAN")
	co_GRATER_THAN,
	/** 小于(<) */
	@DbValue(value = "LESS_THAN")
	co_LESS_THAN,
	/** 大于等于(>=) */
	@DbValue(value = "GRATER_EQUAL")
	co_GRATER_EQUAL,
	/** 小于等于(<=) */
	@DbValue(value = "LESS_EQUAL")
	co_LESS_EQUAL,
	/** 不等于(<>) */
	@DbValue(value = "NOT_EQUAL")
	co_NOT_EQUAL,
	/** 包含Like (%) */
	@DbValue(value = "CONTAIN")
	co_CONTAIN,
	/** 不包含Not like (%) */
	@DbValue(value = "NOT_CONTAIN")
	co_NOT_CONTAIN,
	/** 开始为(...%) */
	@DbValue(value = "START")
	co_START,
	/** 结束为(%...) */
	@DbValue(value = "END")
	co_END,
	// 包含于((A1...An))
	// @DbValue(value = "BETWEEN") co_BETWEEN,
	// // 不包含((A1...An))
	// @DbValue(value = "NOT_BETWEEN") co_NOT_BETWEEN,
	/** 是空 */
	@DbValue(value = "IS_NULL")
	co_IS_NULL,
	/** 非空 */
	@DbValue(value = "NOT_NULL")
	co_NOT_NULL;

	public int getValue() {
		return this.ordinal();
	}

	public static ConditionOperation forValue(int value) {
		return values()[value];
	}

}