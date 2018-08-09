package org.colorcoding.ibas.bobas.test.core;

import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;

import junit.framework.TestCase;

public class TestBasisBO extends TestCase {

	public void testUseBO() {
		Order order = new Order();
		order.setDocEntry(1);
		order.setCustomerCode("C00001");
		order.setDocumentDate(DateTime.getToday());
		order.setDocumentStatus(emDocumentStatus.RELEASED);
		order.setDocumentTotal(new Decimal("99.99"));
		order.setActivated(true);
		order.setSuppler("S00001");

		System.out.println(order.toString());

		assertEquals("Property [DocEntry] faild. ", (int) order.getDocEntry(), 1);
		assertEquals("Property [CustomerCode] faild. ", order.getCustomerCode(), "C00001");
		assertTrue("Property [DueDate] faild. ", order.getDocumentDate().equals(DateTime.getToday()));
		assertEquals("Property [DocumentStatus] faild. ", order.getDocumentStatus(), emDocumentStatus.RELEASED);
		assertEquals("Property [Activted] faild. ", (boolean) order.getActivated(), true);
		assertTrue("Property [DocumentTotal] faild. ", order.getDocumentTotal().toString().equals("99.99"));

		order.setDocEntry(null);
		System.out.println(order.getDocEntry());
	}
}
