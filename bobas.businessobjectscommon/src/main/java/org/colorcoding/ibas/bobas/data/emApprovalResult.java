package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.mapping.db.DbValue;

@XmlType(name = "emApprovalResult", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public enum emApprovalResult {
	// 已批准
	@DbValue(value = "A") Approved,
	// 拒绝的
	@DbValue(value = "R") Rejected,
	// 重置为挂起
	@DbValue(value = "P") Processing;

	public int getValue() {
		return this.ordinal();
	}

	public static emApprovalResult forValue(int value) {
		return values()[value];
	}
}
