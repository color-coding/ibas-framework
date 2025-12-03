package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Value;

/**
 * 条件-操作
 * 
 * @author Niuren.Zhu
 *
 */
@XmlType(namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public enum emConditionOperation {
	/**
	 * 等于(=)
	 */
	@Value("EQ")
	EQUAL,
	/**
	 * 大于(>)
	 */
	@Value("GT")
	GRATER_THAN,
	/**
	 * 小于(<)
	 */
	@Value("LT")
	LESS_THAN,
	/**
	 * 大于等于(>=)
	 */
	@Value("GE")
	GRATER_EQUAL,
	/**
	 * 小于等于(<=)
	 */
	@Value("LE")
	LESS_EQUAL,
	/**
	 * 不等于(<>)
	 */
	@Value("NE")
	NOT_EQUAL,
	/**
	 * 开始于
	 */
	@Value("BW")
	BEGIN_WITH,
	/**
	 * 不是开始于
	 */
	@Value("NB")
	NOT_BEGIN_WITH,
	/**
	 * 结束于
	 */
	@Value("EW")
	END_WITH,
	/**
	 * 不是结束于
	 */
	@Value("NW")
	NOT_END_WITH,
	/**
	 * 包括
	 */
	@Value("CN")
	CONTAIN,
	/**
	 * 不包含
	 */
	@Value("NC")
	NOT_CONTAIN,
	/** 在 */
	@Value("IN")
	IN,
	/** 不在 */
	@Value("NI")
	NOT_IN;
}
