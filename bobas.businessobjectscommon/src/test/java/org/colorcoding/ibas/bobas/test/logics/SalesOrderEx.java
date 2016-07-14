package org.colorcoding.ibas.bobas.test.logics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.colorcoding.ibas.bobas.mapping.BOCode;
import org.colorcoding.ibas.bobas.test.bo.ISalesOrderItems;
import org.colorcoding.ibas.bobas.test.bo.SalesOrder;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "SalesOrderEx")
@XmlRootElement(name = "SalesOrderEx")
@BOCode(SalesOrder.BUSINESS_OBJECT_CODE)
public class SalesOrderEx extends SalesOrder {

	/**
	 * 
	 */
	private static final long serialVersionUID = -243448784277376241L;

	/**
	 * 当前类型
	 */
	@SuppressWarnings("unused")
	private final static Class<?> MY_CLASS = SalesOrderEx.class;
	

	@Override
	@XmlElementWrapper(name = "SalesOrderItems")
	@XmlElement(name = "SalesOrderItem", type = SalesOrderItemEx.class, required = true)
	public ISalesOrderItems getSalesOrderItems() {
		return this.getProperty(SalesOrderItemsProperty);
	}

	/**
	 * 初始化数据
	 */
	@Override
	protected void initialize() {
		this.setSalesOrderItems(new SalesOrderItemsEx(this));
		super.initialize();

	}
}
