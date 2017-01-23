package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.mapping.DbValue;

/**
 * 审批状态
 * 
 * @author Niuren.Zhu
 *
 */
@XmlType(name = "emApprovalStatus", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public enum emApprovalStatus {

	/**
	 * 不影响
	 */
	@DbValue(value = "U")
	UNAFFECTED,

	/**
	 * 审批中
	 */
	@DbValue(value = "P")
	PROCESSING,

	/**
	 * 已批准
	 */
	@DbValue(value = "A")
	APPROVED,

	/**
	 * 已拒绝
	 */
	@DbValue(value = "R")
	REJECTED,
	// /**
	// * 已退回
	// */
	// @DbValue(value = "T") RETURNED,
	/**
	 * 已取消
	 */
	@DbValue(value = "C")
	CANCELLED;

	public int getValue() {
		return this.ordinal();
	}

	public static emApprovalStatus forValue(int value) {
		return values()[value];
	}
}
