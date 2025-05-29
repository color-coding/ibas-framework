package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Value;

/**
 * 单据状态
 * 
 * @author Niuren.Zhu
 *
 */
@XmlType(namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public enum emDocumentStatus {
	/**
	 * 计划
	 */
	@Value("P")
	PLANNED,
	/**
	 * 下达
	 */
	@Value("R")
	RELEASED,
	/**
	 * 完成
	 */
	@Value("F")
	FINISHED,
	/**
	 * 结算
	 */
	@Value("C")
	CLOSED;
}
