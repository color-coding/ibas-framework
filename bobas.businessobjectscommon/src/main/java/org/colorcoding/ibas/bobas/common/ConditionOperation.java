package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;

/**
 * 对比条件
 * 
 */
@XmlType(name = "ConditionOperation", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
public enum ConditionOperation {
	/** 无 */
	NONE,
	/** 等于(=) */
	EQUAL,
	/** 大于(>) */
	GRATER_THAN,
	/** 小于(<) */
	LESS_THAN,
	/** 大于等于(>=) */
	GRATER_EQUAL,
	/** 小于等于(<=) */
	LESS_EQUAL,
	/** 不等于(<>) */
	NOT_EQUAL,
	/** 包含Like (%) */
	CONTAIN,
	/** 不包含Not like (%) */
	NOT_CONTAIN,
	/** 开始为(...%) */
	START,
	/** 结束为(%...) */
	END,
	/** 是空 */
	IS_NULL,
	/** 非空 */
	NOT_NULL;

}