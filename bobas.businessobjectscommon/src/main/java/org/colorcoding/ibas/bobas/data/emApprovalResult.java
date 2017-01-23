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
	APPROVED,
	/**
	 * 拒绝的
	 */
	@DbValue(value = "R")
	REJECTED,
	/**
	 * 重置为进行中
	 */
	@DbValue(value = "P")
	PROCESSING;
	// /**
	// * 退回
	// */
	// @DbValue(value = "P") RETURNED,
	// /**
	// * 取消
	// */
	// @DbValue(value = "P") CANCELLED;

	public int getValue() {
		return this.ordinal();
	}

	public static emApprovalResult forValue(int value) {
		return values()[value];
	}
}
