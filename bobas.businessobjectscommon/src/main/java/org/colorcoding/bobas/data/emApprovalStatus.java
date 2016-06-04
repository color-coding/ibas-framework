package org.colorcoding.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.bobas.MyConsts;
import org.colorcoding.bobas.mapping.db.DbValue;

@XmlType(name = "emApprovalStatus", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public enum emApprovalStatus {
	// 不影响
	@DbValue(value = "U") Unaffected,
	// 审批中
	@DbValue(value = "P") Processing,
	// 已批准
	@DbValue(value = "A") Approved,
	// 已拒绝
	@DbValue(value = "R") Rejected,
	// 已取消
	@DbValue(value = "C") Cancelled,
	// 已删除
	@DbValue(value = "D") Deleted;

	public int getValue() {
		return this.ordinal();
	}

	public static emApprovalStatus forValue(int value) {
		return values()[value];
	}
}
