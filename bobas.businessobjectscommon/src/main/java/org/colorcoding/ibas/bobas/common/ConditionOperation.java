package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.mapping.Value;

/**
 * 对比条件
 * 
 */
@XmlType(name = "ConditionOperation", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
public enum ConditionOperation {
	/** 无 */
	@Value("NONE")
	NONE,
	/** 等于(=) */
	@Value("EQUAL")
	EQUAL,
	/** 大于(>) */
	@Value("GRATER_THAN")
	GRATER_THAN,
	/** 小于(<) */
	@Value("LESS_THAN")
	LESS_THAN,
	/** 大于等于(>=) */
	@Value("GRATER_EQUAL")
	GRATER_EQUAL,
	/** 小于等于(<=) */
	@Value("LESS_EQUAL")
	LESS_EQUAL,
	/** 不等于(<>) */
	@Value("NOT_EQUAL")
	NOT_EQUAL,
	/** 包含Like (%) */
	@Value("CONTAIN")
	CONTAIN,
	/** 不包含Not like (%) */
	@Value("NOT_CONTAIN")
	NOT_CONTAIN,
	/** 开始为(...%) */
	@Value("START")
	START,
	/** 结束为(%...) */
	@Value("END")
	END,
	// 包含于((A1...An))
	// @Value("BETWEEN") BETWEEN,
	// // 不包含((A1...An))
	// @Value("NOT_BETWEEN") NOT_BETWEEN,
	/** 是空 */
	@Value("IS_NULL")
	IS_NULL,
	/** 非空 */
	@Value("NOT_NULL")
	NOT_NULL,
	/** 在 */
	@Value("IN")
	IN,
	/** 不在 */
	@Value("NOT_IN")
	NOT_IN;

	public static ConditionOperation valueOf(int value) {
		return values()[value];
	}

	public static ConditionOperation valueOf(String value, boolean ignoreCase) {
		if (ignoreCase) {
			for (Object item : ConditionOperation.class.getEnumConstants()) {
				if (item.toString().equalsIgnoreCase(value)) {
					return (ConditionOperation) item;
				}
			}
		}
		return ConditionOperation.valueOf(value);
	}

}