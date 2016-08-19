package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.mapping.DbValue;

/**
 * 单据状态
 * 
 * @author Niuren.Zhu
 *
 */
@XmlType(name = "emDocumentStatus", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public enum emDocumentStatus {
	/**
	 * 计划
	 */
	@DbValue(value = "P")
	Planned,
	/**
	 * 下达
	 */
	@DbValue(value = "R")
	Released,
	/**
	 * 完成
	 */
	@DbValue(value = "F")
	Finished,
	/**
	 * 结算
	 */
	@DbValue(value = "C")
	Closed;

	public int getValue() {
		return this.ordinal();
	}

	public static emDocumentStatus forValue(int value) {
		return values()[value];
	}
}
