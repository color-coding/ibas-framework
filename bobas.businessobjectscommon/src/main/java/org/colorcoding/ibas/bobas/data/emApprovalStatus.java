package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.mapping.Value;

/**
 * 审批状态
 * 
 * @author Niuren.Zhu
 *
 */
@XmlType(name = "emApprovalStatus", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public enum emApprovalStatus {

	/**
	 * 不影响
	 */
	@Value("U")
	UNAFFECTED,

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
	// /**
	// * 已退回
	// */
	// @Value("T") RETURNED,
	/**
	 * 已取消
	 */
	@Value("C")
	CANCELLED;

	public static emApprovalStatus valueOf(int value) {
		return values()[value];
	}

	public static emApprovalStatus valueOf(String value, boolean ignoreCase) {
		if (ignoreCase) {
			for (Object item : emApprovalStatus.class.getEnumConstants()) {
				if (item.toString().equalsIgnoreCase(value)) {
					return (emApprovalStatus) item;
				}
			}
		}
		return emApprovalStatus.valueOf(value);
	}
}
