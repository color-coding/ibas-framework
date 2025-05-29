package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Value;

/**
 * 业务对象状态
 * 
 * @author Niuren.Zhu
 *
 */
@XmlType(namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public enum emBOStatus {

	/**
	 * 未清
	 */
	@Value("O")
	OPEN,

	/**
	 * 已清
	 */
	@Value("C")
	CLOSED;
}
