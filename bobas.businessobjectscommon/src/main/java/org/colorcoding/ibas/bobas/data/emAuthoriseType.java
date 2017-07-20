package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.mapping.Value;

/**
 * 权限
 * 
 * @author Niuren.Zhu
 *
 */
@XmlType(name = "emAuthoriseType", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
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

	public int getValue() {
		return this.ordinal();
	}

	public static emAuthoriseType forValue(int value) {
		return values()[value];
	}
}
