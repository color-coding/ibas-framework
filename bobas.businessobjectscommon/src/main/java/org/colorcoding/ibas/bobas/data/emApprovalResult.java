package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Value;

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
}
