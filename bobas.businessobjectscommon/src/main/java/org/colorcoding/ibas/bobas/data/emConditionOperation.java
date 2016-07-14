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
	@DbValue(value = "EQ") EQUAL(0),

	/**
	 * 大于(>)
	 */
	@DbValue(value = "GT") GRATER_THAN(1),

	/**
	 * 小于(<)
	 */
	@DbValue(value = "LT") LESS_THAN(2),

	/**
	 * 大于等于(>=)
	 */
	@DbValue(value = "GE") GRATER_EQUAL(3),

	/**
	 * 小于等于(<=)
	 */
	@DbValue(value = "LE") LESS_EQUAL(4),

	/**
	 * 不等于(<>)
	 */
	@DbValue(value = "NE") NOT_EQUAL(5),

	/**
	 * 开始于
	 */
	@DbValue(value = "BW") BEGIN_WITH(6),

	/**
	 * 不是开始于
	 */
	@DbValue(value = "NB") NOT_BEGIN_WITH(7),

	/**
	 * 结束于
	 */
	@DbValue(value = "EW") END_WITH(8),

	/**
	 * 不是结束于
	 */
	@DbValue(value = "NW") NOT_END_WITH(9),

	/**
	 * 包括
	 */
	@DbValue(value = "CN") CONTAIN(10),

	/**
	 * 不包含
	 */
	@DbValue(value = "NC") NOT_CONTAIN(11);

	private int intValue;
	private static java.util.HashMap<Integer, emConditionOperation> mappings;

	private synchronized static java.util.HashMap<Integer, emConditionOperation> getMappings() {
		if (mappings == null) {
			mappings = new java.util.HashMap<Integer, emConditionOperation>();
		}
		return mappings;
	}

	private emConditionOperation(int value) {
		intValue = value;
		emConditionOperation.getMappings().put(value, this);
	}

	public int getValue() {
		return intValue;
	}

	public static emConditionOperation forValue(int value) {
		return getMappings().get(value);
	}
}
