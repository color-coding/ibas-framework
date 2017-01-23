package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.mapping.DbValue;

/**
 * 对比条件
 * 
 */
@XmlType(name = "ConditionOperation", namespace = MyConsts.NAMESPACE_BOBAS_COMMON)
public enum ConditionOperation {
	/** 无 */
	@DbValue(value = "NONE")
	NONE,
	/** 等于(=) */
	@DbValue(value = "EQUAL")
	EQUAL,
	/** 大于(>) */
	@DbValue(value = "GRATER_THAN")
	GRATER_THAN,
	/** 小于(<) */
	@DbValue(value = "LESS_THAN")
	LESS_THAN,
	/** 大于等于(>=) */
	@DbValue(value = "GRATER_EQUAL")
	GRATER_EQUAL,
	/** 小于等于(<=) */
	@DbValue(value = "LESS_EQUAL")
	LESS_EQUAL,
	/** 不等于(<>) */
	@DbValue(value = "NOT_EQUAL")
	NOT_EQUAL,
	/** 包含Like (%) */
	@DbValue(value = "CONTAIN")
	CONTAIN,
	/** 不包含Not like (%) */
	@DbValue(value = "NOT_CONTAIN")
	NOT_CONTAIN,
	/** 开始为(...%) */
	@DbValue(value = "START")
	START,
	/** 结束为(%...) */
	@DbValue(value = "END")
	END,
	// 包含于((A1...An))
	// @DbValue(value = "BETWEEN") BETWEEN,
	// // 不包含((A1...An))
	// @DbValue(value = "NOT_BETWEEN") NOT_BETWEEN,
	/** 是空 */
	@DbValue(value = "IS_NULL")
	IS_NULL,
	/** 非空 */
	@DbValue(value = "NOT_NULL")
	NOT_NULL;

	public int getValue() {
		return this.ordinal();
	}

	public static ConditionOperation forValue(int value) {
		return values()[value];
	}

}