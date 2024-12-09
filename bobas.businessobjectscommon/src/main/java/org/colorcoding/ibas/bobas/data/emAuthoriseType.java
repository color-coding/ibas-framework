package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Value;

/**
 * 权限
 * 
 * @author Niuren.Zhu
 *
 */
@XmlType(namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public enum emAuthoriseType {
	/**
	 * 完全
	 */
	@Value("A")
	ALL,
	/**
	 * 只读
	 */
	@Value("R")
	READ,
	/**
	 * 没有
	 */
	@Value("N")
	NONE;
}
