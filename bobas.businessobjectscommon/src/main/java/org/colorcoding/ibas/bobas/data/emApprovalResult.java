package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.mapping.Value;

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
	@Value("A")
	APPROVED,
	/**
	 * 拒绝的
	 */
	@Value("R")
	REJECTED,
	/**
	 * 重置为进行中
	 */
	@Value("P")
	PROCESSING;
	// /**
	// * 退回
	// */
	// @Value("P") RETURNED,
	// /**
	// * 取消
	// */
	// @Value("P") CANCELLED;

	public int getValue() {
		return this.ordinal();
	}

	public static emApprovalResult forValue(int value) {
		return values()[value];
	}
}
