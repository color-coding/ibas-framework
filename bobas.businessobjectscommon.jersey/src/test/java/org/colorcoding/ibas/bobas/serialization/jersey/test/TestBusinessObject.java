package org.colorcoding.ibas.bobas.serialization.jersey.test;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

import javax.xml.bind.JAXBException;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Decimals;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emBOStatus;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.serialization.ISerializer;
import org.colorcoding.ibas.bobas.serialization.SerializerFactory;
import org.colorcoding.ibas.bobas.serialization.ValidateException;
import org.colorcoding.ibas.bobas.serialization.jersey.SerializerJson;
import org.colorcoding.ibas.bobas.serialization.jersey.SerializerManager;
import org.colorcoding.ibas.bobas.test.demo.SalesOrder;
import org.colorcoding.ibas.bobas.test.demo.SalesOrderItem;

import junit.framework.TestCase;

public class TestBusinessObject extends TestCase {
	public void testJson() throws JAXBException {
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
		orderItem.setQuantity(Decimals.valueOf(10));
		orderItem.setPrice(Decimals.valueOf(199.99));

		ISerializer<?> serializer = SerializerFactory.create().createManager().create("json");
		ByteArrayOutputStream writer = new ByteArrayOutputStream();
		serializer.serialize(order, writer, true);
		System.out.println(writer.toString());
		order = (SalesOrder) serializer.deserialize(writer.toString(), order.getClass());

	}

	public void testSerializStatus() throws ValidateException {
		// 测试反序列化的状态变化
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{\"SalesOrder\":{");
		stringBuilder.append("\"SalesOrderItems\":[");
		stringBuilder.append("{");
		stringBuilder.append(String.format("\"LineStatus\":\"%s\",", emDocumentStatus.CLOSED));
		stringBuilder.append(String.format("\"Status\":\"%s\",", emBOStatus.CLOSED));
		stringBuilder.append(String.format("\"Canceled\":\"%s\"", emYesNo.YES));
		stringBuilder.append("},");
		stringBuilder.append("{");
		stringBuilder.append(String.format("\"LineStatus\":\"%s\",", emDocumentStatus.CLOSED));
		stringBuilder.append(String.format("\"Status\":\"%s\",", emBOStatus.CLOSED));
		stringBuilder.append(String.format("\"Canceled\":\"%s\"", emYesNo.YES));
		stringBuilder.append("},");
		stringBuilder.append("{");
		stringBuilder.append(String.format("\"LineStatus\":\"%s\",", emDocumentStatus.CLOSED));
		stringBuilder.append(String.format("\"Status\":\"%s\",", emBOStatus.CLOSED));
		stringBuilder.append(String.format("\"Canceled\":\"%s\"", emYesNo.YES));
		stringBuilder.append("}");
		stringBuilder.append("],");
		stringBuilder.append(String.format("\"DocumentStatus\":\"%s\",", emDocumentStatus.CLOSED));
		stringBuilder.append(String.format("\"Canceled\":\"%s\",", emYesNo.YES));
		stringBuilder.append(String.format("\"Status\":\"%s\"", emBOStatus.CLOSED));
		stringBuilder.append("}}");
		ISerializer<?> serializer = new SerializerJson();
		serializer.validate(SalesOrder.class, stringBuilder.toString());
		IBusinessObject bo = (IBusinessObject) serializer.deserialize(stringBuilder.toString(), SalesOrder.class);
		ByteArrayOutputStream writer = new ByteArrayOutputStream();
		serializer.serialize(bo, writer, true);
		System.out.println(writer.toString());
	}

	public void testJsonSchema() throws ValidateException {
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
		orderItem.setQuantity(Decimals.valueOf(10));
		orderItem.setPrice(Decimals.valueOf(199.99));

		System.out.println("-------------------has root--------------------");
		ISerializer<?> serializer = SerializerFactory.create().createManager()
				.create(SerializerManager.TYPE_JSON_HAS_ROOT);
		ByteArrayOutputStream writer = new ByteArrayOutputStream();
		serializer.getSchema(order.getClass(), writer);
		System.out.println(writer.toString());
		writer = new ByteArrayOutputStream();
		serializer.serialize(order, writer);
		System.out.println(writer.toString());
		serializer.validate(order.getClass(), writer.toString());
		System.out.println(serializer.deserialize(writer.toString(), order.getClass()));
		System.out.println("-------------------no root---------------------");
		serializer = SerializerFactory.create().createManager().create(SerializerManager.TYPE_JSON_NO_ROOT);
		writer = new ByteArrayOutputStream();
		serializer.getSchema(order.getClass(), writer);
		System.out.println(writer.toString());
		writer = new ByteArrayOutputStream();
		serializer.serialize(order, writer);
		System.out.println(writer.toString());
		serializer.validate(order.getClass(), writer.toString());
		System.out.println(serializer.deserialize(writer.toString(), order.getClass()));
	}
}
