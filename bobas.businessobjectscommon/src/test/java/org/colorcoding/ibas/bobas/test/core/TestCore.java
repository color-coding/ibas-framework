package org.colorcoding.ibas.bobas.test.core;

import java.math.BigDecimal;

import org.colorcoding.ibas.bobas.bo.BOFactory;
import org.colorcoding.ibas.bobas.bo.IUserField;
import org.colorcoding.ibas.bobas.bo.IUserFields;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.db.DbField;
import org.colorcoding.ibas.bobas.test.core.demo.Order;
import org.colorcoding.ibas.bobas.test.core.demo.SalesOrder;

import junit.framework.TestCase;

public class TestCore extends TestCase {

	public void testBasic() {
		Order order = new Order();
		order.setDocEntry(1);
		order.setDocumentDate(DateTimes.today());
		order.setDocumentStatus(emDocumentStatus.RELEASED);
		order.setDocumentTotal(new BigDecimal("99.99"));
		order.setActivated(true);

		assertEquals("Property [DocEntry] faild. ", (int) order.getDocEntry(), 1);
		assertTrue("Property [DueDate] faild. ", order.getDocumentDate().equals(DateTimes.today()));
		assertEquals("Property [DocumentStatus] faild. ", order.getDocumentStatus(), emDocumentStatus.RELEASED);
		assertEquals("Property [Activted] faild. ", (boolean) order.getActivated(), true);
		assertTrue("Property [DocumentTotal] faild. ", order.getDocumentTotal().toString().equals("99.99"));

		order.setDocEntry(null);
		System.out.println(order.getDocEntry());

		SalesOrder salesOrder = new SalesOrder();
		salesOrder.setDocEntry(1);
		salesOrder.setDocumentDate(DateTimes.today());
		salesOrder.setDocumentStatus(emDocumentStatus.RELEASED);
		salesOrder.setDocumentTotal(new BigDecimal("99.99"));
		salesOrder.setActivated(true);
		salesOrder.setCustomerCode("C00001");

		System.out.println(salesOrder.toString());

		assertEquals("Property [DocEntry] faild. ", (int) salesOrder.getDocEntry(), 1);
		assertEquals("Property [CustomerCode] faild. ", salesOrder.getCustomerCode(), "C00001");
		assertTrue("Property [DueDate] faild. ", salesOrder.getDocumentDate().equals(DateTimes.today()));
		assertEquals("Property [DocumentStatus] faild. ", salesOrder.getDocumentStatus(), emDocumentStatus.RELEASED);
		assertEquals("Property [Activted] faild. ", (boolean) salesOrder.getActivated(), true);
		assertTrue("Property [DocumentTotal] faild. ", salesOrder.getDocumentTotal().toString().equals("99.99"));

		salesOrder = new SalesOrder();

		assertEquals("Property [DocumentStatus] faild. ", salesOrder.getDocumentStatus(), emDocumentStatus.PLANNED);

		BOFactory.register(SalesOrder.class);
	}

	public void testUserFields() {
		SalesOrder salesOrder = new SalesOrder();
		IUserFields userFields = salesOrder.getUserFields();
		userFields.register("U_String", String.class);
		userFields.register("U_Integer", Integer.class);

		SalesOrder salesOrder2 = new SalesOrder();
		salesOrder2.setDocEntry(1);
		salesOrder2.setDocumentDate(DateTimes.today());
		salesOrder2.setDocumentStatus(emDocumentStatus.RELEASED);
		salesOrder2.setDocumentTotal(new BigDecimal("99.99"));
		salesOrder2.setActivated(true);
		salesOrder2.setCustomerCode("C00001");
		salesOrder2.markOld();

		IUserField<String> userField = salesOrder2.getUserFields().get("U_String");
		userField.setValue("hello.");
		salesOrder2.getUserFields().get("U_Integer").setValue(100);
		for (IUserField<?> item : salesOrder2.getUserFields()) {
			System.out.println(String.format("%s = %s", item.getName(), item.getValue()));
		}
		for (IPropertyInfo<?> item : salesOrder2.properties()) {
			DbField annotation = item.getAnnotation(DbField.class);
			if (annotation != null) {
				System.out.println(String.format("%s = %s", annotation.table(), annotation.name()));
			}
		}
	}
}
