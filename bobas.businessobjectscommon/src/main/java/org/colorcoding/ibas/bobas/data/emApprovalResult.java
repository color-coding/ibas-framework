package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.mapping.Value;

/**
 * 审批结果
 * 
 * @author Niuren.Zhu
 *
 */
@XmlType(name = "emApprovalResult", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
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
	PROCESSING,
	/**
	 * 退回
	 */
	@Value("T")
	RETURNED;
	// /**
	// * 取消
	// */
	// @Value("C") CANCELLED;

	public static emApprovalResult valueOf(int value) {
		return values()[value];
	}

	public static emApprovalResult valueOf(String value, boolean ignoreCase) {
		if (ignoreCase) {
			for (Object item : emApprovalResult.class.getEnumConstants()) {
				if (item.toString().equalsIgnoreCase(value)) {
					return (emApprovalResult) item;
				}
			}
		}
		return emApprovalResult.valueOf(value);
	}
}
