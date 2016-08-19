package org.colorcoding.ibas.bobas.data;

import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.MyConsts;
import org.colorcoding.ibas.bobas.mapping.DbValue;

/**
 * 存储操作类型
 * 
 * @author Niuren.Zhu
 *
 */
@XmlType(name = "emStoreOperationType", namespace = MyConsts.NAMESPACE_BOBAS_DATA)
public enum emStoreOperationType {
	/**
	 * 添加
	 */
	@DbValue(value = "A")
	Add,
	/**
	 * 更新
	 */
	@DbValue(value = "U")
	Update,

	/**
	 * 删除
	 */
	@DbValue(value = "D")
	Delete,
	/**
	 * 删除前
	 */
	@DbValue(value = "X")
	BeforeDelete,
	/**
	 * 添加前
	 */
	@DbValue(value = "Y")
	BeforeAdd,
	/**
	 * 更新前
	 */
	@DbValue(value = "Z")
	BeforeUpdate;

	public int getValue() {
		return this.ordinal();
	}

	public static emStoreOperationType forValue(int value) {
		return values()[value];
	}
}
