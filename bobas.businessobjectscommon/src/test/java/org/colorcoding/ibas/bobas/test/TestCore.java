package org.colorcoding.ibas.bobas.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.UUID;

import org.colorcoding.ibas.bobas.bo.BOFactory;
import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.bo.BusinessObjects;
import org.colorcoding.ibas.bobas.bo.IUserField;
import org.colorcoding.ibas.bobas.bo.IUserFields;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Decimals;
import org.colorcoding.ibas.bobas.common.Enums;
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

	public void testPropertyValue() throws ClassNotFoundException {
		// 测试基类的属性赋值
		Order order = new Order();
		order.setDocEntry(1);
		order.setDocumentDate(DateTimes.today());
		order.setDocumentStatus(Enums.valueOf(emDocumentStatus.class, "RELEASED"));
		order.setDocumentTotal(Decimals.valueOf("99.99"));
		order.setActivated(true);

		assertEquals("Property [DocEntry] faild. ", (int) order.getDocEntry(), 1);
		assertTrue("Property [DueDate] faild. ", order.getDocumentDate().equals(DateTimes.today()));
		assertEquals("Property [DocumentStatus] faild. ", order.getDocumentStatus(), emDocumentStatus.RELEASED);
		assertEquals("Property [Activted] faild. ", (boolean) order.getActivated(), true);
		assertTrue("Property [DocumentTotal] faild. ", order.getDocumentTotal().toString().equals("99.99"));

		order.setDocEntry(null);
		assertEquals("Property [DocEntry] default value faild. ", (int) order.getDocEntry(), 0);
		order.setDocumentStatus(null);
		assertEquals("Property [DocumentStatus] default value faild. ", order.getDocumentStatus(),
				emDocumentStatus.PLANNED);

		// 测试子类的属性赋值
		SalesOrder salesOrder = new SalesOrder();
		salesOrder.setDocEntry(1);
		salesOrder.setDocumentDate(DateTimes.today());
		salesOrder.setDocumentStatus(Enums.valueOf(emDocumentStatus.class, emDocumentStatus.FINISHED.ordinal()));
		salesOrder.setDocumentTotal(Decimals.valueOf("99.99"));
		salesOrder.setActivated(true);
		salesOrder.setCustomerCode("C00001");

		System.out.println(salesOrder.toString());

		assertEquals("Property [DocEntry] faild. ", (int) salesOrder.getDocEntry(), 1);
		assertEquals("Property [CustomerCode] faild. ", salesOrder.getCustomerCode(), "C00001");
		assertTrue("Property [DueDate] faild. ", salesOrder.getDocumentDate().equals(DateTimes.today()));
		assertEquals("Property [DocumentStatus] faild. ", salesOrder.getDocumentStatus(), emDocumentStatus.FINISHED);
		assertEquals("Property [Activted] faild. ", (boolean) salesOrder.getActivated(), true);
		assertTrue("Property [DocumentTotal] faild. ", salesOrder.getDocumentTotal().toString().equals("99.99"));

		Order salesOrder2 = salesOrder.clone();
		assertTrue("Object Instance faild. ", salesOrder2 != salesOrder);

		// 测试子项状态变化，父项状态
		salesOrder.markOld();
		salesOrder.getSalesOrderItems().create();
		assertEquals("Property [isDirty] faild. ", salesOrder.isDirty(), true);
		salesOrder.markOld();
		BOUtilities.traverse(salesOrder, c -> c.markOld());
		salesOrder.getSalesOrderItems().get(0).setItemCode(UUID.randomUUID().toString());
		assertEquals("Property [isDirty] faild. ", salesOrder.getSalesOrderItems().get(0).isDirty(), true);
		assertEquals("Property [isDirty] faild. ", salesOrder.isDirty(), true);
		salesOrder.markOld();
		BOUtilities.traverse(salesOrder, c -> c.markNew());
		salesOrder.getSalesOrderItems().deleteAll();
		assertEquals("Property [isDirty] faild. ", salesOrder.isDirty(), true);

		// 输出对象属性及默认值
		System.out.println(Strings.format("%s:", SalesOrder.class.getName()));
		for (IPropertyInfo<?> item : BOFactory.propertyInfos(SalesOrder.class)) {
			System.out.println(Strings.format("  %s: %s %s", item.getName(), item.getValueType().getSimpleName(),
					item.getDefaultValue()));
			if (BusinessObjects.class.isAssignableFrom(item.getValueType())) {
				Type genericType = item.getValueType().getGenericSuperclass();
				if (genericType instanceof ParameterizedType) {
					ParameterizedType pt = (ParameterizedType) genericType;
					Type[] actualTypeArgs = pt.getActualTypeArguments();
					for (IPropertyInfo<?> sItem : BOFactory
							.propertyInfos(Class.forName(actualTypeArgs[0].getTypeName()))) {
						System.out.println(Strings.format("    %s: %s %s", sItem.getName(),
								sItem.getValueType().getSimpleName(), sItem.getDefaultValue()));
					}
				}
			}
		}

		// 测试对象路径取值
		SalesOrderItem orderItem = salesOrder.getSalesOrderItems().create();
		orderItem.setItemCode("A00001");
		orderItem.setQuantity(Decimals.valueOf(10));
		orderItem.setPrice(Decimals.valueOf(99.99));
		orderItem = salesOrder.getSalesOrderItems().create();
		orderItem.setItemCode("A00002");
		orderItem.setQuantity(BigDecimal.valueOf(10));
		orderItem.setPrice(Decimals.valueOf(199.99123456789));
		String propertyPath = "CustomerCode";
		System.out.println(propertyPath);
		System.out.println(Strings.valueOf(BOUtilities.propertyValue(salesOrder, propertyPath)));
		propertyPath = "SalesOrderItems.ItemCode";
		System.out.println(propertyPath);
		System.out.println(Strings.valueOf(BOUtilities.propertyValue(salesOrder, propertyPath)));
		propertyPath = "SalesOrderItems[1].ItemCode";
		System.out.println(propertyPath);
		System.out.println(Strings.valueOf(BOUtilities.propertyValue(salesOrder, propertyPath)));

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
		salesOrder2.setDocumentTotal(Decimals.valueOf("99.99"));
		salesOrder2.setActivated(true);
		salesOrder2.setCustomerCode("C00001");
		salesOrder2.markOld();
		assertTrue("Property [isDitry] faild. ", !salesOrder2.isDirty());

		IUserField<String> userField = salesOrder2.getUserFields().get("U_String");
		userField.setValue("hello.");
		assertTrue("Property [isDitry] faild. ", salesOrder2.isDirty());

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
		order.setDocumentTotal(Decimals.valueOf("99.99"));
		order.getUserFields().register("U_OrderType", String.class);
		order.getUserFields().register("U_OrderId", Integer.class);
		order.getUserFields().register("U_OrderDate", DateTime.class);
		order.getUserFields().register("U_OrderTotal", BigDecimal.class);
		order.getUserFields().get("U_OrderType").setValue("S0000");
		order.getUserFields().get("U_OrderId").setValue(5768);
		order.getUserFields().get("U_OrderDate").setValue(DateTimes.today());
		order.getUserFields().get("U_OrderTotal").setValue(Decimals.valueOf("999.888"));
		SalesOrderItem orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00001");
		orderItem.setQuantity(Decimals.valueOf(10));
		orderItem.setPrice(Decimals.valueOf(99.99));
		orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00002");
		orderItem.setQuantity(BigDecimal.valueOf(10));
		orderItem.setPrice(Decimals.valueOf(199.99123456789));

		ISerializer serializer = new SerializerXml();
		ByteArrayOutputStream writer = new ByteArrayOutputStream();
		serializer.serialize(order, writer);
		System.out.println("data xml:");
		System.out.println(writer.toString());
		writer = new ByteArrayOutputStream();
		serializer.schema(SalesOrder.class, writer);
		System.out.println("schema xml:");
		System.out.println(writer.toString());

	}

}
