package org.colorcoding.ibas.bobas.test.bo;

public class SalesOrderItems2 extends SalesOrderItems {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4062784903911615013L;

	public SalesOrderItems2() {
	}

	public SalesOrderItems2(ISalesOrder parent) {
		super(parent);
	}

	@Override
	public Class<?> getElementType() {
		return SalesOrderItem2.class;
	}

	@Override
	public ISalesOrderItem create() {
		ISalesOrderItem item = new SalesOrderItem2();
		this.add(item);
		return item;
	}

}
