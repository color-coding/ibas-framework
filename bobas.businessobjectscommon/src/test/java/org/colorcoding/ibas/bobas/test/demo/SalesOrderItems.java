package org.colorcoding.ibas.bobas.test.demo;

import javax.xml.bind.annotation.XmlSeeAlso;

import org.colorcoding.ibas.bobas.bo.BusinessObjects;

/**
 * 销售订单-行 集合
 */
@XmlSeeAlso({ SalesOrderItem.class })
public class SalesOrderItems extends BusinessObjects<SalesOrderItem, SalesOrder> {

	private static final long serialVersionUID = 5296887996825885427L;

	public SalesOrderItems() {
		super();
	}

	public SalesOrderItems(SalesOrder parent) {
		super(parent);
	}

	public SalesOrderItem create() {
		SalesOrderItem item = new SalesOrderItem();
		this.add(item);
		return item;
	}

	@Override
	public Class<SalesOrderItem> getElementType() {
		return SalesOrderItem.class;
	}

}
