package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.mapping.Value;

/**
 * 审批步骤状态
 * 
 * @author Niuren.Zhu
 *
 */
@XmlType(name = "emApprovalStepStatus", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public enum emApprovalStepStatus {

	/**
	 * 挂起的
	 */
	@Value("D")
	PENDING,

	/**
	 * 审批中
	 */
	@Value("P")
	PROCESSING,

	/**
	 * 已批准
	 */
	@Value("A")
	APPROVED,

	/**
	 * 已拒绝
	 */
	@Value("R")
	REJECTED,

	/**
	 * 已跳过
	 */
	@Value("S")
	SKIPPED;

	public int getValue() {
		return this.ordinal();
	}

	public static emApprovalStepStatus forValue(int value) {
		return values()[value];
	}
}
