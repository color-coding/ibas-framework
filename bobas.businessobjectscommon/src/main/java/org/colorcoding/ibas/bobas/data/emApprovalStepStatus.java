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
	Pending,

	/**
	 * 审批中
	 */
	@DbValue(value = "P")
	Processing,

	/**
	 * 已批准
	 */
	@DbValue(value = "A")
	Approved,

	/**
	 * 已拒绝
	 */
	@DbValue(value = "R")
	Rejected,

	/**
	 * 已跳过
	 */
	@DbValue(value = "S")
	Skipped;

	public int getValue() {
		return this.ordinal();
	}

	public static emApprovalStepStatus forValue(int value) {
		return values()[value];
	}
}
