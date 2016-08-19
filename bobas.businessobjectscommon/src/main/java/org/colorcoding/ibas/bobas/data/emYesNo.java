package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.mapping.DbValue;

/**
 * 是否
 * 
 * @author Niuren.Zhu
 *
 */
@XmlType(name = "emYesNo", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public enum emYesNo {
	/**
	 * 否
	 */
	@DbValue(value = "N")
	No,
	/**
	 * 是
	 */
	@DbValue(value = "Y")
	Yes;

	public int getValue() {
		return this.ordinal();
	}

	public static emYesNo forValue(int value) {
		return values()[value];
	}
}
