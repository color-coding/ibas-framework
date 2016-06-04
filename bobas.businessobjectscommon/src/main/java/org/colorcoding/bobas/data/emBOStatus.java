package org.colorcoding.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.bobas.MyConsts;
import org.colorcoding.bobas.mapping.db.DbValue;

@XmlType(name = "emBOStatus", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public enum emBOStatus {
	// 未清
	@DbValue(value = "O") Open,
	// 已清
	@DbValue(value = "C") Closed;

	public int getValue() {
		return this.ordinal();
	}

	public static emBOStatus forValue(int value) {
		return values()[value];
	}
}
