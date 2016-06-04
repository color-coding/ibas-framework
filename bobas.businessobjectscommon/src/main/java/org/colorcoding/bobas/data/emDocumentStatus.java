package org.colorcoding.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.bobas.MyConsts;
import org.colorcoding.bobas.mapping.db.DbValue;

@XmlType(name = "emDocumentStatus", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public enum emDocumentStatus {
	// 计划
	@DbValue(value = "P") Planned,
	// 下达
	@DbValue(value = "R") Released,
	// 完成
	@DbValue(value = "F") Finished,
	// 结算
	@DbValue(value = "C") Closed;

	public int getValue() {
		return this.ordinal();
	}

	public static emDocumentStatus forValue(int value) {
		return values()[value];
	}
}
