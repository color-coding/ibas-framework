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
	 * 仅可见
	 */
	@DbValue(value = "V")
	OnlyView,
	/**
	 * 完全
	 */
	@DbValue(value = "A")
	All,
	/**
	 * 没有
	 */
	@DbValue(value = "N")
	None;

	public int getValue() {
		return this.ordinal();
	}

	public static emAuthoriseType forValue(int value) {
		return values()[value];
	}
}
