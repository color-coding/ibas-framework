package org.colorcoding.bobas.common;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.bobas.MyConsts;
import org.colorcoding.bobas.mapping.db.DbValue;

/**
 * 排序
 */
@XmlType(name = "SortType", namespace = MyConsts.NAMESPACE_BOBAS_COMMON)
public enum SortType {

	/** 降序 */
	@DbValue(value = "D") st_Descending,
	/** 升序 */
	@DbValue(value = "A") st_Ascending;

	public int getValue() {
		return this.ordinal();
	}

	public static SortType forValue(int value) {
		return values()[value];
	}
}
