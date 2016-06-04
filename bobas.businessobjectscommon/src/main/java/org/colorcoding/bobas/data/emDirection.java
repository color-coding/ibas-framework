package org.colorcoding.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.bobas.MyConsts;
import org.colorcoding.bobas.mapping.db.DbValue;

@XmlType(name = "emDirection", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public enum emDirection {
	// 入
	@DbValue(value = "I") In,
	// 出
	@DbValue(value = "O") Out;

	public int getValue() {
		return this.ordinal();
	}

	public static emDirection forValue(int value) {
		return values()[value];
	}
}
