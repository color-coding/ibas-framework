package org.colorcoding.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.bobas.MyConsts;
import org.colorcoding.bobas.mapping.db.DbValue;

@XmlType(name = "emApprovalStepStatus", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public enum emApprovalStepStatus {
	// 挂起的
	@DbValue(value = "D") Pending,
	// 审批中
	@DbValue(value = "P") Processing,
	// 已批准
	@DbValue(value = "A") Approved,
	// 已拒绝
	@DbValue(value = "R") Rejected,
	// 已跳过
	@DbValue(value = "S") Skipped;

	public int getValue() {
		return this.ordinal();
	}

	public static emApprovalStepStatus forValue(int value) {
		return values()[value];
	}
}
