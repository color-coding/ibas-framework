package org.colorcoding.ibas.bobas.jersey.test;

import javax.xml.bind.JAXBException;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.core.Serializer;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.Decimal;
import org.colorcoding.ibas.bobas.data.emBOStatus;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.data.measurement.Time;
import org.colorcoding.ibas.bobas.data.measurement.emTimeUnit;
import org.colorcoding.ibas.bobas.mapping.DbFieldType;
import org.colorcoding.ibas.bobas.test.bo.ISalesOrderItem;
import org.colorcoding.ibas.bobas.test.bo.SalesOrder;
import org.colorcoding.ibas.bobas.test.bo.User;

import junit.framework.TestCase;

public class testBusinessObject extends TestCase {

	public void testJSON() throws JAXBException {

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
		orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00002");
		orderItem.setQuantity(10);
		orderItem.setPrice(199.99);

		String json = order.toString("json");
		System.out.println(json);
		Serializer.deserializeString("json", json, order.getClass());
	}

	public void testSerializStatus() {
		// 测试反序列化的状态变化
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{\"SalesOrder\":{");
		stringBuilder.append("\"SalesOrderItems\":[");
		stringBuilder.append("{");
		stringBuilder.append(String.format("\"LineStatus\":\"%s\",", emDocumentStatus.Closed));
		stringBuilder.append(String.format("\"Status\":\"%s\",", emBOStatus.Closed));
		stringBuilder.append(String.format("\"Canceled\":\"%s\"", emYesNo.Yes));
		stringBuilder.append("},");
		stringBuilder.append("{");
		stringBuilder.append(String.format("\"LineStatus\":\"%s\",", emDocumentStatus.Closed));
		stringBuilder.append(String.format("\"Status\":\"%s\",", emBOStatus.Closed));
		stringBuilder.append(String.format("\"Canceled\":\"%s\"", emYesNo.Yes));
		stringBuilder.append("},");
		stringBuilder.append("{");
		stringBuilder.append(String.format("\"LineStatus\":\"%s\",", emDocumentStatus.Closed));
		stringBuilder.append(String.format("\"Status\":\"%s\",", emBOStatus.Closed));
		stringBuilder.append(String.format("\"Canceled\":\"%s\"", emYesNo.Yes));
		stringBuilder.append("}");
		stringBuilder.append("],");
		stringBuilder.append(String.format("\"DocumentStatus\":\"%s\",", emDocumentStatus.Closed));
		stringBuilder.append(String.format("\"Canceled\":\"%s\",", emYesNo.Yes));
		stringBuilder.append(String.format("\"Status\":\"%s\"", emBOStatus.Closed));
		stringBuilder.append("}}");
		IBusinessObject bo = (IBusinessObject) Serializer.deserializeString("json", stringBuilder.toString(),
				SalesOrder.class);
		System.out.println(Serializer.serializeString(bo, true));
	}
}
