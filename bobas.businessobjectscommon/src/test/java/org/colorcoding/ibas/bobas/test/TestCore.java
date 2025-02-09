package org.colorcoding.ibas.bobas.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.bo.IUserField;
import org.colorcoding.ibas.bobas.bo.IUserFields;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.core.IPropertyInfo;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.db.DbField;
import org.colorcoding.ibas.bobas.serialization.ISerializer;
import org.colorcoding.ibas.bobas.serialization.SerializerXml;
import org.colorcoding.ibas.bobas.serialization.ValidateException;
import org.colorcoding.ibas.bobas.test.demo.Order;
import org.colorcoding.ibas.bobas.test.demo.SalesOrder;
import org.colorcoding.ibas.bobas.test.demo.SalesOrderItem;
import org.xml.sax.SAXException;

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

		Order salesOrder2 = salesOrder.clone();

		System.out.println(salesOrder2.toString());

		salesOrder = new SalesOrder();
		assertEquals("Property [DocumentStatus] faild. ", salesOrder.getDocumentStatus(), emDocumentStatus.PLANNED);

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

	public void testXml() throws SAXException, IOException, ValidateException {
		SalesOrder order = new SalesOrder();
		order.setDocEntry(1);
		order.setCustomerCode("C00001");
		order.setDeliveryDate(DateTimes.today());
		order.setDocumentStatus(emDocumentStatus.RELEASED);
		order.setDocumentTotal(new BigDecimal("99.99"));
		order.getUserFields().register("U_OrderType", String.class);
		order.getUserFields().register("U_OrderId", Integer.class);
		order.getUserFields().register("U_OrderDate", DateTime.class);
		order.getUserFields().register("U_OrderTotal", BigDecimal.class);
		order.getUserFields().get("U_OrderType").setValue("S0000");
		order.getUserFields().get("U_OrderId").setValue(5768);
		order.getUserFields().get("U_OrderDate").setValue(DateTimes.today());
		order.getUserFields().get("U_OrderTotal").setValue(new BigDecimal("999.888"));
		SalesOrderItem orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00001");
		orderItem.setQuantity(new BigDecimal(10));
		orderItem.setPrice(BigDecimal.valueOf(99.99));
		orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00002");
		orderItem.setQuantity(BigDecimal.valueOf(10));
		orderItem.setPrice(BigDecimal.valueOf(199.99));
		ISerializer<?> serializer = new SerializerXml();
		ByteArrayOutputStream writer = new ByteArrayOutputStream();
		serializer.serialize(order, writer);
		System.out.println(writer.toString());

		System.out.println(Strings.valueOf(BOUtilities.propertyValue(order, "CustomerCode")));
		System.out.println(Strings.valueOf(BOUtilities.propertyValue(order, "SalesOrderItems.ItemCode")));
	}

}
