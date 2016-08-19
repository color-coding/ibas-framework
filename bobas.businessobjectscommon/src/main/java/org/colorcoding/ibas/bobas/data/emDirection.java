package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.mapping.DbValue;

/**
 * 方向
 * 
 * @author Niuren.Zhu
 *
 */
@XmlType(name = "emDirection", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public enum emDirection {
	/**
	 * 入
	 */
	@DbValue(value = "I")
	In,
	/**
	 * 出
	 */
	@DbValue(value = "O")
	Out;

	public int getValue() {
		return this.ordinal();
	}

	public static emDirection forValue(int value) {
		return values()[value];
	}
}
