package org.colorcoding.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.bobas.MyConsts;
import org.colorcoding.bobas.mapping.db.DbValue;

@XmlType(name = "emAuthoriseType", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public enum emAuthoriseType {
	// 仅可见
	@DbValue(value = "V") OnlyView,
	// 完全
	@DbValue(value = "A") All,
	// 没有
	@DbValue(value = "N") None;

	public int getValue() {
		return this.ordinal();
	}

	public static emAuthoriseType forValue(int value) {
		return values()[value];
	}
}
