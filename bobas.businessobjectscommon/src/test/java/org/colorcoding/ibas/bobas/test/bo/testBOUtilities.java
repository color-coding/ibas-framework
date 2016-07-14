package org.colorcoding.ibas.bobas.test.bo;

import org.colorcoding.ibas.bobas.bo.BOException;
import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.measurement.Time;
import org.colorcoding.ibas.bobas.data.measurement.emTimeUnit;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;

import junit.framework.TestCase;

public class testBOUtilities extends TestCase {

	public void testGetBOPathValue() throws BOException {
		SalesOrder order = new SalesOrder();

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
		((SalesOrderItem) orderItem).getUserFields().addUserField("U_LineType", DbFieldType.db_Alphanumeric);
		((SalesOrderItem) orderItem).getUserFields().setValue("U_LineType", "L0000");

		orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00002");
		orderItem.setQuantity(10);
		orderItem.setPrice(199.99);

		String path1 = "CustomerCode";
		String path2 = "SalesOrderItems.ItemCode";
		String path3 = "U_OrderType";
		String path4 = "SalesOrderItems.U_LineType";

		Object value1 = BOUtilities.getPropertyValue(order, path1);
		assertEquals(String.format("%s not equals.", path1), value1, order.getCustomerCode());

		Object value3 = BOUtilities.getPropertyValue(order, path3);
		assertEquals(String.format("%s not equals.", path3), value3, order.getUserFields().getValue("U_OrderType"));

		orderItem = order.getSalesOrderItems().get(0);

		Object value2 = BOUtilities.getPropertyValue(order, path2, 0);
		assertEquals(String.format("%s not equals.", path2), value2, orderItem.getItemCode());

		Object value4 = BOUtilities.getPropertyValue(order, path4, 0);
		assertEquals(String.format("%s not equals.", path4), value4,
				((SalesOrderItem) orderItem).getUserFields().getValue("U_LineType"));
		
		orderItem = order.getSalesOrderItems().get(1);

		value2 = BOUtilities.getPropertyValue(order, path2, 1);
		assertEquals(String.format("%s not equals.", path2), value2, orderItem.getItemCode());
	}
}
