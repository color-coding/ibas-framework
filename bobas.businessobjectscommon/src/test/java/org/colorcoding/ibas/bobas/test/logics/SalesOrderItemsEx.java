package org.colorcoding.ibas.bobas.test.logics;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.test.bo.ISalesOrder;
import org.colorcoding.ibas.bobas.test.bo.ISalesOrderItem;
import org.colorcoding.ibas.bobas.test.bo.SalesOrderItems;

@XmlType(name = "SalesOrderItemsEx")
@XmlSeeAlso({ SalesOrderItemEx.class })
public class SalesOrderItemsEx extends SalesOrderItems {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4062784903911615013L;

	public SalesOrderItemsEx() {
	}

	public SalesOrderItemsEx(ISalesOrder parent) {
		super(parent);
	}

	@Override
	public Class<?> getElementType() {
		return SalesOrderItemsEx.class;
	}
 
	@Override
	public ISalesOrderItem create() {
		ISalesOrderItem item = new SalesOrderItemEx();
		this.add(item);
		return item;
	}

}
