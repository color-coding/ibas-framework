package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.mapping.db.DbValue;

@XmlType(name = "emConditionRelationship", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public enum emConditionRelationship {
	// 无
	@DbValue(value = "NON") NONE,
	// 并且
	@DbValue(value = "AND") AND,
	// 或者
	@DbValue(value = "OR") OR;

	public int getValue() {
		return this.ordinal();
	}

	public static emConditionRelationship forValue(int value) {
		return values()[value];
	}
}
