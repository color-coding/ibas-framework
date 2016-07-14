package org.colorcoding.ibas.bobas.test.logics;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.test.bo.SalesOrderItem;

@XmlType(name = "SalesOrderItemEx")
@XmlRootElement(name = "SalesOrderItemEx")
public class SalesOrderItemEx extends SalesOrderItem implements IMaterialsOrderQuantityContract {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1398878790396616268L;

	
	
}
