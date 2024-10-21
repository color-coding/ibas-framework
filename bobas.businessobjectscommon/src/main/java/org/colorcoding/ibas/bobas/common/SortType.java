package org.colorcoding.ibas.bobas.common;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;

/**
 * 排序
 */
@XmlType(name = "SortType", namespace = MyConfiguration.NAMESPACE_BOBAS_COMMON)
public enum SortType {
	/** 降序 */
	DESCENDING,
	/** 升序 */
	ASCENDING;
}
