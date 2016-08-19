package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.mapping.DbValue;

/**
 * 审批结果
 * 
 * @author Niuren.Zhu
 *
 */
@XmlType(name = "emApprovalResult", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public enum emApprovalResult {
	/**
	 * 已批准
	 */
	@DbValue(value = "A")
	Approved,
	/**
	 * 拒绝的
	 */
	@DbValue(value = "R")
	Rejected,
	/**
	 * 重置为进行中
	 */
	@DbValue(value = "P")
	Processing;
	// /**
	// * 退回
	// */
	// @DbValue(value = "P") Returned,
	// /**
	// * 取消
	// */
	// @DbValue(value = "P") Cancelled;

	public int getValue() {
		return this.ordinal();
	}

	public static emApprovalResult forValue(int value) {
		return values()[value];
	}
}
