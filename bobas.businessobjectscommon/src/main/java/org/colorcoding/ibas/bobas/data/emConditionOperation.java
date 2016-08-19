package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.mapping.DbValue;

/**
 * 条件-操作
 * 
 * @author Niuren.Zhu
 *
 */
@XmlType(name = "emConditionOperation", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public enum emConditionOperation implements IEnumItem {

	/**
	 * 等于(=)
	 */
	@DbValue(value = "EQ")
	EQUAL,

	/**
	 * 大于(>)
	 */
	@DbValue(value = "GT")
	GRATER_THAN,

	/**
	 * 小于(<)
	 */
	@DbValue(value = "LT")
	LESS_THAN,

	/**
	 * 大于等于(>=)
	 */
	@DbValue(value = "GE")
	GRATER_EQUAL,

	/**
	 * 小于等于(<=)
	 */
	@DbValue(value = "LE")
	LESS_EQUAL,

	/**
	 * 不等于(<>)
	 */
	@DbValue(value = "NE")
	NOT_EQUAL,

	/**
	 * 开始于
	 */
	@DbValue(value = "BW")
	BEGIN_WITH,

	/**
	 * 不是开始于
	 */
	@DbValue(value = "NB")
	NOT_BEGIN_WITH,

	/**
	 * 结束于
	 */
	@DbValue(value = "EW")
	END_WITH,

	/**
	 * 不是结束于
	 */
	@DbValue(value = "NW")
	NOT_END_WITH,

	/**
	 * 包括
	 */
	@DbValue(value = "CN")
	CONTAIN,

	/**
	 * 不包含
	 */
	@DbValue(value = "NC")
	NOT_CONTAIN;

	public int getValue() {
		return this.ordinal();
	}

	public static emConditionOperation forValue(int value) {
		return values()[value];
	}

}
