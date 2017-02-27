package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.mapping.DbValue;

/**
 * 权限
 * 
 * @author Niuren.Zhu
 *
 */
@XmlType(name = "emAuthoriseType", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public enum emAuthoriseType {
	/**
	 * 只读
	 */
	@DbValue(value = "R")
	READ,
	/**
	 * 完全
	 */
	@DbValue(value = "A")
	ALL,
	/**
	 * 没有
	 */
	@DbValue(value = "N")
	NONE;

	public int getValue() {
		return this.ordinal();
	}

	public static emAuthoriseType forValue(int value) {
		return values()[value];
	}
}
