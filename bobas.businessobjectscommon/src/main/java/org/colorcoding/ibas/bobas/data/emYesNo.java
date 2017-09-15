package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.mapping.Value;

/**
 * 是否
 * 
 * @author Niuren.Zhu
 *
 */
@XmlType(name = "emYesNo", namespace = MyConfiguration.NAMESPACE_BOBAS_DATA)
public enum emYesNo {
	/**
	 * 否
	 */
	@Value("N")
	NO,
	/**
	 * 是
	 */
	@Value("Y")
	YES;

	public int getValue() {
		return this.ordinal();
	}

	public static emYesNo forValue(int value) {
		return values()[value];
	}
}
