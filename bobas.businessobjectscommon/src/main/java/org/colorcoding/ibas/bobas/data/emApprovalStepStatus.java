package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.mapping.DbValue;

/**
 * 审批步骤状态
 * 
 * @author Niuren.Zhu
 *
 */
@XmlType(name = "emApprovalStepStatus", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public enum emApprovalStepStatus {

	/**
	 * 挂起的
	 */
	@DbValue(value = "D")
	PENDING,

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

	/**
	 * 已跳过
	 */
	@DbValue(value = "S")
	SKIPPED;

	public int getValue() {
		return this.ordinal();
	}

	public static emApprovalStepStatus forValue(int value) {
		return values()[value];
	}
}
