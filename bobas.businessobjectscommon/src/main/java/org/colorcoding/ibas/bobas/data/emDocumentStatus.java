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
	PLANNED,
	/**
	 * 下达
	 */
	@DbValue(value = "R")
	RELEASED,
	/**
	 * 完成
	 */
	@DbValue(value = "F")
	FINISHED,
	/**
	 * 结算
	 */
	@DbValue(value = "C")
	CLOSED;

	public int getValue() {
		return this.ordinal();
	}

	public static emDocumentStatus forValue(int value) {
		return values()[value];
	}
}
