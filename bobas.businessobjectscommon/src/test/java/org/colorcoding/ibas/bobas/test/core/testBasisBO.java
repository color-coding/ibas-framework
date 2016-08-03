package org.colorcoding.ibas.bobas.test.core;

import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;

import junit.framework.TestCase;

public class testBasisBO extends TestCase {

	public void testUseBO() {
		Order order = new Order();
		order.setDocEntry(1);
		order.setSuppler("C00001");
		order.setDueDate(DateTime.getToday());
		order.setDocumentStatus(emDocumentStatus.Released);
		order.setDocumentTotal(new Decimal("99.99"));
		order.setActivted(true);

		System.out.println(order.toString());

		assertEquals("Property [DocEntry] faild. ", (int) order.getDocEntry(), 1);
		assertEquals("Property [CustomerCode] faild. ", order.getSuppler(), "C00001");
		assertTrue("Property [DueDate] faild. ", order.getDueDate().equals(DateTime.getToday()));
		assertEquals("Property [DocumentStatus] faild. ", order.getDocumentStatus(), emDocumentStatus.Released);
		assertEquals("Property [Activted] faild. ", (boolean) order.getActivted(), true);
		assertTrue("Property [DocumentTotal] faild. ", order.getDocumentTotal().toString().equals("99.99"));

		order.setDocEntry(null);
		System.out.println(order.getDocEntry());
	}
}
