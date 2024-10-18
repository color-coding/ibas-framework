package org.colorcoding.ibas.bobas.test.core;

import java.math.BigDecimal;

import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;

import junit.framework.TestCase;

public class TestCore extends TestCase {

	public void testOrder() {
		Order order = new Order();
		order.setDocEntry(1);
		order.setDocumentDate(DateTimes.getToday());
		order.setDocumentStatus(emDocumentStatus.RELEASED);
		order.setDocumentTotal(new BigDecimal("99.99"));
		order.setActivated(true);

		assertEquals("Property [DocEntry] faild. ", (int) order.getDocEntry(), 1);
		assertTrue("Property [DueDate] faild. ", order.getDocumentDate().equals(DateTimes.getToday()));
		assertEquals("Property [DocumentStatus] faild. ", order.getDocumentStatus(), emDocumentStatus.RELEASED);
		assertEquals("Property [Activted] faild. ", (boolean) order.getActivated(), true);
		assertTrue("Property [DocumentTotal] faild. ", order.getDocumentTotal().toString().equals("99.99"));

		order.setDocEntry(null);
		System.out.println(order.getDocEntry());

		SalesOrder salesOrder = new SalesOrder();
		salesOrder.setDocEntry(1);
		salesOrder.setDocumentDate(DateTimes.getToday());
		salesOrder.setDocumentStatus(emDocumentStatus.RELEASED);
		salesOrder.setDocumentTotal(new BigDecimal("99.99"));
		salesOrder.setActivated(true);
		salesOrder.setCustomerCode("C00001");

		System.out.println(salesOrder.toString());

		assertEquals("Property [DocEntry] faild. ", (int) salesOrder.getDocEntry(), 1);
		assertEquals("Property [CustomerCode] faild. ", salesOrder.getCustomerCode(), "C00001");
		assertTrue("Property [DueDate] faild. ", salesOrder.getDocumentDate().equals(DateTimes.getToday()));
		assertEquals("Property [DocumentStatus] faild. ", salesOrder.getDocumentStatus(), emDocumentStatus.RELEASED);
		assertEquals("Property [Activted] faild. ", (boolean) salesOrder.getActivated(), true);
		assertTrue("Property [DocumentTotal] faild. ", salesOrder.getDocumentTotal().toString().equals("99.99"));

		salesOrder = new SalesOrder();

		assertEquals("Property [DocumentStatus] faild. ", salesOrder.getDocumentStatus(), emDocumentStatus.PLANNED);
	}
}
