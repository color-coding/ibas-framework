package org.colorcoding.ibas.bobas.test.logics;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.bo.BusinessObjects;

/**
 * 采购订单-行 集合
 */
@XmlType(name = "PurchaseOrderItems")
@XmlSeeAlso({ PurchaseOrderItem.class })
public class PurchaseOrderItems extends BusinessObjects<PurchaseOrderItem, PurchaseOrder> {

	/**
	 * 序列化版本标记
	 */
	private static final long serialVersionUID = 5296887996825885427L;

	/**
	 * 构造方法
	 */
	public PurchaseOrderItems() {
		super();
	}

	/**
	 * 构造方法
	 * 
	 * @param parent
	 *            父项对象
	 */
	public PurchaseOrderItems(PurchaseOrder parent) {
		super(parent);
	}

	/**
	 * 元素类型
	 */
	public Class<?> getElementType() {
		return PurchaseOrderItem.class;
	}

	/**
	 * 创建销售订单-行
	 * 
	 * @return 销售订单-行
	 */
	public PurchaseOrderItem create() {
		PurchaseOrderItem item = new PurchaseOrderItem();
		this.add(item);
		return item;
	}

}
