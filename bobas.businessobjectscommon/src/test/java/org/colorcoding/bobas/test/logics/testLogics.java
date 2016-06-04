package org.colorcoding.bobas.test.logics;

import org.colorcoding.bobas.data.DateTime;
import org.colorcoding.bobas.data.Decimal;
import org.colorcoding.bobas.data.emDocumentStatus;
import org.colorcoding.bobas.data.measurement.Time;
import org.colorcoding.bobas.data.measurement.emTimeUnit;
import org.colorcoding.bobas.mapping.db.DbFieldType;
import org.colorcoding.bobas.test.bo.ISalesOrderItem;
import org.colorcoding.bobas.test.bo.SalesOrder;
import org.colorcoding.bobas.test.bo.User;

import junit.framework.TestCase;

public class testLogics extends TestCase {

	public void testBOInherit() {
		SalesOrder order = new SalesOrder2();

		order.setDocEntry(1);
		order.setCustomerCode("C00001");
		order.setDeliveryDate(DateTime.getToday());
		order.setDocumentStatus(emDocumentStatus.Released);
		order.setDocumentTotal(new Decimal("99.99"));
		order.setDocumentUser(new User());
		order.setTeamUsers(new User[] { new User(), new User() });
		order.setCycle(new Time(1.05, emTimeUnit.hour));
		order.getCycle().setValue(0.9988);

		order.getUserFields().addUserField("U_OrderType", DbFieldType.db_Alphanumeric);
		order.getUserFields().addUserField("U_OrderId", DbFieldType.db_Numeric);
		order.getUserFields().addUserField("U_OrderDate", DbFieldType.db_Date);
		order.getUserFields().addUserField("U_OrderTotal", DbFieldType.db_Decimal);

		order.getUserFields().setValue("U_OrderType", "S0000");
		order.getUserFields().setValue("U_OrderId", 5768);
		order.getUserFields().setValue("U_OrderDate", DateTime.getToday());
		order.getUserFields().setValue("U_OrderTotal", new Decimal("999.888"));

		ISalesOrderItem orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00001");
		orderItem.setQuantity(new Decimal(10));
		orderItem.setPrice(new Decimal(99.99));
		orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00002");
		orderItem.setQuantity(10);
		orderItem.setPrice(199.99);

		System.out.println(order.toString());
		assertEquals("Property [DocEntry] faild. ", (int) order.getDocEntry(), 1);
		assertEquals("Property [CustomerCode] faild. ", order.getCustomerCode(), "C00001");
		assertTrue("Property [DueDate] faild. ", order.getDeliveryDate().equals(DateTime.getToday()));
		assertEquals("Property [DocumentStatus] faild. ", order.getDocumentStatus(), emDocumentStatus.Released);
		assertTrue("Property [DocumentTotal] faild. ", order.getDocumentTotal().toString().equals("99.99"));

		System.out.println(orderItem.toString());
		assertEquals("Property [ItemCode] faild. ", orderItem.getItemCode(), "A00002");
		assertEquals("Property [Quantity] faild. ", orderItem.getQuantity().toString(), "10");
	}

	public void testSalesOrderLogics() {

	}
}
