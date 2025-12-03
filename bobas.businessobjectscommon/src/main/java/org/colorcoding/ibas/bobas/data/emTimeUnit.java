package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.common.Value;

/**
 * 时间单位
 */
@XmlType(namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public enum emTimeUnit {
	/**
	 * 秒
	 */
	@Value("S")
	SECOND,
	/**
	 * 分钟
	 */
	@Value("M")
	MINUTE,
	/**
	 * 小时
	 */
	@Value("H")
	HOUR;
}
