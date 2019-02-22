package org.colorcoding.ibas.bobas.test.bo;

import java.math.BigDecimal;

import org.colorcoding.ibas.bobas.bo.BOException;
import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.measurement.Time;
import org.colorcoding.ibas.bobas.data.measurement.emTimeUnit;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;

import junit.framework.TestCase;

public class TestBOUtilities extends TestCase {

	public void testGetBOPathValue() throws BOException {
		SalesOrder order = new SalesOrder();

		order.setDocEntry(1);
		order.setCustomerCode("C00001");
		order.setDeliveryDate(DateTime.getToday());
		order.setDocumentStatus(emDocumentStatus.RELEASED);
		order.setDocumentTotal(new BigDecimal("99.99"));
		order.setDocumentUser(new User());
		order.setTeamUsers(new User[] { new User(), new User() });
		order.setCycle(new Time(1.05, emTimeUnit.HOUR));
		order.getCycle().setValue(0.9988);

		order.getUserFields().register("U_OrderType", DbFieldType.ALPHANUMERIC);
		order.getUserFields().register("U_OrderId", DbFieldType.NUMERIC);
		order.getUserFields().register("U_OrderDate", DbFieldType.DATE);
		order.getUserFields().register("U_OrderTotal", DbFieldType.DECIMAL);

		order.getUserFields().get("U_OrderType").setValue("S0000");
		order.getUserFields().get("U_OrderId").setValue(5768);
		order.getUserFields().get("U_OrderDate").setValue(DateTime.getToday());
		order.getUserFields().get("U_OrderTotal").setValue(new BigDecimal("999.888"));

		ISalesOrderItem orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00001");
		orderItem.setQuantity(new BigDecimal(10));
		orderItem.setPrice(BigDecimal.valueOf(99.99));
		((SalesOrderItem) orderItem).getUserFields().register("U_LineType", DbFieldType.ALPHANUMERIC);
		((SalesOrderItem) orderItem).getUserFields().get("U_LineType").setValue("L0000");

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
		assertEquals(String.format("%s not equals.", path3), value3,
				order.getUserFields().get("U_LineType").getValue());

		orderItem = order.getSalesOrderItems().get(0);

		Object value2 = BOUtilities.getPropertyValue(order, path2, 0);
		assertEquals(String.format("%s not equals.", path2), value2, orderItem.getItemCode());

		Object value4 = BOUtilities.getPropertyValue(order, path4, 0);
		assertEquals(String.format("%s not equals.", path4), value4,
				((SalesOrderItem) orderItem).getUserFields().get("U_LineType").getValue());

		orderItem = order.getSalesOrderItems().get(1);

		value2 = BOUtilities.getPropertyValue(order, path2, 1);
		assertEquals(String.format("%s not equals.", path2), value2, orderItem.getItemCode());
	}
}
