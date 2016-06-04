package org.colorcoding.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.bobas.MyConsts;
import org.colorcoding.bobas.mapping.db.DbValue;

@XmlType(name = "emYesNo", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public enum emYesNo {
	// 否
	@DbValue(value = "N") No,
	// 是
	@DbValue(value = "Y") Yes;

	public int getValue() {
		return this.ordinal();
	}

	public static emYesNo forValue(int value) {
		return values()[value];
	}
}
