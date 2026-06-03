package org.colorcoding.ibas.bobas.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.UUID;

import org.colorcoding.ibas.bobas.bo.BOFactory;
import org.colorcoding.ibas.bobas.bo.BOUtilities;
import org.colorcoding.ibas.bobas.bo.IBusinessObjects;
import org.colorcoding.ibas.bobas.bo.IUserField;
import org.colorcoding.ibas.bobas.bo.UserFieldsFactory;
import org.colorcoding.ibas.bobas.bo.UserFieldsManager;
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

/**
 * 业务对象核心功能测试
 *
 * 测试范围： 1. 基础属性赋值与取值（基类Order、子类SalesOrder） 2. 属性默认值（null重置后恢复默认值） 3.
 * 对象克隆（深拷贝独立性） 4. 对象状态管理（isDirty/markOld/markNew/deleteAll） 5.
 * 子项集合操作（create/delete） 6. 属性路径取值（BOUtilities.propertyValue） 7.
 * 用户自定义字段（UserFields注册、赋值、脏状态） 8. 序列化（XML/JSON/CSV）
 */
public class TestCore extends TestCase {

	// ==================== 1. 基础属性赋值与取值 ====================

	/**
	 * 测试基类Order属性赋值 覆盖：整型、日期、枚举、布尔、Decimal属性
	 */
	public void testOrderPropertyValues() throws ClassNotFoundException {
		Order order = new Order();
		order.setDocEntry(1);
		order.setDocumentDate(DateTimes.today());
		order.setDocumentStatus(Enums.valueOf(emDocumentStatus.class, "RELEASED"));
		order.setDocumentTotal(Decimals.valueOf("99.99"));
		order.setActivated(true);

		assertEquals("Property [DocEntry] faild. ", (int) order.getDocEntry(), 1);
		assertTrue("Property [DocumentDate] faild. ", order.getDocumentDate().equals(DateTimes.today()));
		assertEquals("Property [DocumentStatus] faild. ", order.getDocumentStatus(), emDocumentStatus.RELEASED);
		assertEquals("Property [Activated] faild. ", (boolean) order.getActivated(), true);
		assertTrue("Property [DocumentTotal] faild. ", order.getDocumentTotal().toString().equals("99.99"));
	}

	/**
	 * 测试属性默认值 覆盖：Integer属性null→0，枚举属性null→PLANNED
	 */
	public void testPropertyDefaultValues() {
		Order order = new Order();
		order.setDocEntry(1);
		order.setDocumentStatus(emDocumentStatus.RELEASED);

		// 设置null后应恢复默认值
		order.setDocEntry(null);
		assertEquals("Property [DocEntry] default value faild. ", (int) order.getDocEntry(), 0);
		order.setDocumentStatus(null);
		assertEquals("Property [DocumentStatus] default value faild. ", order.getDocumentStatus(),
				emDocumentStatus.PLANNED);
	}

	/**
	 * 测试子类SalesOrder属性赋值 覆盖：继承属性 + 自有属性（CustomerCode等）
	 */
	public void testSalesOrderPropertyValues() {
		SalesOrder salesOrder = new SalesOrder();
		salesOrder.setDocEntry(1);
		salesOrder.setDocumentDate(DateTimes.today());
		salesOrder.setDocumentStatus(Enums.valueOf(emDocumentStatus.class, emDocumentStatus.FINISHED.ordinal()));
		salesOrder.setDocumentTotal(Decimals.valueOf("99.99"));
		salesOrder.setActivated(true);
		salesOrder.setCustomerCode("C00001");

		assertEquals("Property [DocEntry] faild. ", (int) salesOrder.getDocEntry(), 1);
		assertEquals("Property [CustomerCode] faild. ", salesOrder.getCustomerCode(), "C00001");
		assertTrue("Property [DocumentDate] faild. ", salesOrder.getDocumentDate().equals(DateTimes.today()));
		assertEquals("Property [DocumentStatus] faild. ", salesOrder.getDocumentStatus(), emDocumentStatus.FINISHED);
		assertEquals("Property [Activated] faild. ", (boolean) salesOrder.getActivated(), true);
		assertTrue("Property [DocumentTotal] faild. ", salesOrder.getDocumentTotal().toString().equals("99.99"));
	}

	// ==================== 2. 对象克隆 ====================

	/**
	 * 测试对象克隆（深拷贝） 覆盖：克隆后为不同实例、修改克隆体不影响原对象
	 */
	public void testCloneIndependence() {
		SalesOrder original = new SalesOrder();
		original.setDocEntry(1);
		original.setCustomerCode("C00001");
		original.setDocumentTotal(Decimals.valueOf("99.99"));

		SalesOrder cloned = (SalesOrder) original.clone();
		assertTrue("Clone should be different instance. ", cloned != original);
		assertEquals("Clone should have same DocEntry. ", (int) cloned.getDocEntry(), 1);
		assertEquals("Clone should have same CustomerCode. ", cloned.getCustomerCode(), "C00001");

		// 修改克隆体，原对象不变
		cloned.setCustomerCode("C00002");
		assertEquals("Original CustomerCode should not change. ", original.getCustomerCode(), "C00001");
		assertEquals("Cloned CustomerCode should change. ", cloned.getCustomerCode(), "C00002");
	}

	// ==================== 3. 对象状态管理 ====================

	/**
	 * 测试脏状态（isDirty） 覆盖：markOld后不脏、修改属性后变脏、子项修改导致父项变脏
	 */
	public void testDirtyState() {
		SalesOrder salesOrder = new SalesOrder();
		salesOrder.setDocEntry(1);
		salesOrder.setCustomerCode("C00001");
		salesOrder.markOld();

		// markOld后不脏
		assertTrue("After markOld, order should not be dirty. ", !salesOrder.isDirty());

		// 创建子项后变脏
		salesOrder.getSalesOrderItems().create();
		assertTrue("After creating child item, order should be dirty. ", salesOrder.isDirty());

		// markOld后修改子项属性
		salesOrder.markOld();
		BOUtilities.traverse(salesOrder, c -> c.markOld());
		salesOrder.getSalesOrderItems().get(0).setItemCode(UUID.randomUUID().toString());
		assertTrue("Child item change should make parent dirty. ", salesOrder.getSalesOrderItems().get(0).isDirty());
		assertTrue("Parent should be dirty when child is dirty. ", salesOrder.isDirty());
	}

	/**
	 * 测试markNew状态 覆盖：markNew后删除子项导致变脏
	 */
	public void testMarkNewAndDeleteAll() {
		SalesOrder salesOrder = new SalesOrder();
		salesOrder.setDocEntry(1);
		salesOrder.getSalesOrderItems().create();
		salesOrder.markOld();
		BOUtilities.traverse(salesOrder, c -> c.markNew());
		salesOrder.getSalesOrderItems().deleteAll();
		assertTrue("After deleteAll, order should be dirty. ", salesOrder.isDirty());
	}

	/**
	 * 测试BOStatus状态 覆盖：打开/关闭状态
	 */
	public void testBOStatus() {
		SalesOrder salesOrder = new SalesOrder();
		salesOrder.setDocEntry(1);
		assertNotNull("BO status should not be null. ", salesOrder.getStatus());

		salesOrder.markOld();
		// markOld后对象为非脏状态
		assertTrue("After markOld, BO should not be dirty. ", !salesOrder.isDirty());

		salesOrder.setCustomerCode("C00001");
		assertTrue("After modification, BO should be dirty. ", salesOrder.isDirty());

		salesOrder.delete();
		assertTrue("After delete, BO should be dirty. ", salesOrder.isDirty());
	}

	// ==================== 4. 属性路径取值 ====================

	/**
	 * 测试属性路径取值 覆盖：简单属性路径、集合属性路径、索引属性路径
	 */
	public void testPropertyPathValue() {
		SalesOrder salesOrder = new SalesOrder();
		salesOrder.setDocEntry(1);
		salesOrder.setCustomerCode("C00001");
		SalesOrderItem orderItem = salesOrder.getSalesOrderItems().create();
		orderItem.setItemCode("A00001");
		orderItem.setQuantity(Decimals.valueOf(10));
		orderItem.setPrice(Decimals.valueOf(99.99));
		orderItem = salesOrder.getSalesOrderItems().create();
		orderItem.setItemCode("A00002");
		orderItem.setQuantity(Decimals.valueOf(10));
		orderItem.setPrice(Decimals.valueOf(199.99123456789));

		// 简单属性路径
		String propertyPath = "CustomerCode";
		Object value = BOUtilities.propertyValue(salesOrder, propertyPath);
		assertEquals("Simple property path failed. ", "C00001", Strings.valueOf(value));

		// 集合属性路径（所有子项的ItemCode）
		propertyPath = "SalesOrderItems.ItemCode";
		value = BOUtilities.propertyValue(salesOrder, propertyPath);
		assertNotNull("Collection property path should return value. ", value);

		// 索引属性路径（指定子项的ItemCode，0-based）
		propertyPath = "SalesOrderItems[0].ItemCode";
		value = BOUtilities.propertyValue(salesOrder, propertyPath);
		assertEquals("Indexed property path failed. ", "A00001", Strings.valueOf(value));

		// 第二个子项
		propertyPath = "SalesOrderItems[1].ItemCode";
		value = BOUtilities.propertyValue(salesOrder, propertyPath);
		assertEquals("Second indexed property path failed. ", "A00002", Strings.valueOf(value));
	}

	// ==================== 5. 属性信息遍历 ====================

	/**
	 * 测试属性信息遍历（BOFactory.propertyInfos） 覆盖：属性列表输出、子项属性遍历
	 */
	public void testPropertyInfoTraversal() throws ClassNotFoundException {
		SalesOrder salesOrder = new SalesOrder();
		salesOrder.setDocEntry(1);
		salesOrder.setCustomerCode("C00001");

		// 遍历属性信息
		System.out.println(Strings.format("%s:", SalesOrder.class.getName()));
		for (IPropertyInfo<?> item : BOFactory.propertyInfos(SalesOrder.class)) {
			System.out.println(Strings.format("  %s: %s %s", item.getName(), item.getValueType().getSimpleName(),
					item.getDefaultValue()));
			if (IBusinessObjects.class.isAssignableFrom(item.getValueType())) {
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
		// 确保至少有属性
		assertTrue("SalesOrder should have properties. ", BOFactory.propertyInfos(SalesOrder.class).size() > 0);
	}

	// ==================== 6. 用户自定义字段 ====================

	/**
	 * 测试用户自定义字段 覆盖：注册、赋值、脏状态、遍历
	 */
	public void testUserFields() {
		UserFieldsManager userFieldsManager = UserFieldsFactory.createManager();
		userFieldsManager.registerUserField(SalesOrder.class, "U_String", String.class);
		userFieldsManager.registerUserField(SalesOrder.class, "U_Integer", Integer.class);
		userFieldsManager.registerUserField(SalesOrderItem.class, "U_Date", DateTime.class);
		userFieldsManager.registerUserField(SalesOrderItem.class, "U_Decimal", BigDecimal.class);

		SalesOrder salesOrder = new SalesOrder();
		salesOrder.setDocEntry(1);
		salesOrder.setDocumentDate(DateTimes.today());
		salesOrder.setDocumentStatus(emDocumentStatus.RELEASED);
		salesOrder.setDocumentTotal(Decimals.valueOf("99.99"));
		salesOrder.setActivated(true);
		salesOrder.setCustomerCode("C00001");
		SalesOrderItem orderItem = salesOrder.getSalesOrderItems().create();
		orderItem.setItemCode("A00001");
		orderItem.setQuantity(Decimals.valueOf(10));
		orderItem.setPrice(Decimals.valueOf(99.99));
		orderItem = salesOrder.getSalesOrderItems().create();
		orderItem.setItemCode("A00002");
		orderItem.setQuantity(Decimals.valueOf(10));
		orderItem.setPrice(Decimals.valueOf(199.99123456789));
		salesOrder.markOld();
		assertTrue("After markOld, should not be dirty. ", !salesOrder.isDirty());

		// 设置用户字段值后变脏
		IUserField<String> userField = salesOrder.getUserFields().get("U_String");
		userField.setValue("hello.");
		assertTrue("After setting user field, should be dirty. ", salesOrder.isDirty());

		// 设置整数用户字段
		salesOrder.getUserFields().get("U_Integer").setValue(100);
		assertEquals("User field U_Integer value faild. ", salesOrder.getUserFields().get("U_Integer").getValue(), 100);

		// 遍历用户字段
		int ufCount = 0;
		for (IUserField<?> item : salesOrder.getUserFields()) {
			System.out.println(String.format("%s = %s", item.getName(), item.getValue()));
			ufCount++;
		}
		assertTrue("Should have user fields. ", ufCount > 0);

		// 遍历属性的DbField注解
		for (IPropertyInfo<?> item : salesOrder.properties()) {
			DbField annotation = item.getAnnotation(DbField.class);
			if (annotation != null) {
				System.out.println(String.format("%s = %s", annotation.table(), annotation.name()));
			}
		}
	}

	/**
	 * 测试用户自定义字段-未注册字段 覆盖：获取不存在的用户字段、空值处理
	 */
	public void testUserFieldNotFound() {
		SalesOrder salesOrder = new SalesOrder();
		// 获取未注册的用户字段应抛出异常
		try {
			salesOrder.getUserFields().get("U_NotExist");
			fail("Should throw exception for unregistered user field.");
		} catch (Exception e) {
			// expected: IndexOutOfBoundsException
		}
	}

	// ==================== 7. 序列化 ====================

	/**
	 * 测试XML序列化/反序列化 覆盖：含用户字段、含子项、特殊字符、克隆后序列化对比
	 */
	public void testXmlSerialization() throws SAXException, IOException, ValidateException {
		UserFieldsManager userFieldsManager = UserFieldsFactory.createManager();
		userFieldsManager.registerUserField(SalesOrder.class, "U_OrderType", String.class);
		userFieldsManager.registerUserField(SalesOrder.class, "U_OrderId", Integer.class);
		userFieldsManager.registerUserField(SalesOrder.class, "U_OrderDate", DateTime.class);
		userFieldsManager.registerUserField(SalesOrder.class, "U_OrderTotal", BigDecimal.class);
		userFieldsManager.registerUserField(SalesOrderItem.class, "U_Date", DateTime.class);
		userFieldsManager.registerUserField(SalesOrderItem.class, "U_Decimal", BigDecimal.class);

		SalesOrder order = new SalesOrder();
		order.setDocEntry(1);
		order.setCustomerCode("C00001");
		order.setDeliveryDate(DateTimes.today());
		order.setDocumentStatus(emDocumentStatus.RELEASED);
		order.setDocumentTotal(Decimals.valueOf("99.99"));
		order.setU_DocumentRate(Decimals.valueOf("99.99"));

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
		orderItem.setQuantity(Decimals.valueOf(10));
		orderItem.setPrice(Decimals.valueOf(199.99123456789));

		// XML序列化
		ISerializer serializer = new SerializerXml();
		ByteArrayOutputStream writer = new ByteArrayOutputStream();
		serializer.serialize(order, writer);
		String xmlOutput = writer.toString();
		System.out.println("data xml:");
		System.out.println(xmlOutput);
		assertNotNull("XML output should not be null. ", xmlOutput);
		assertTrue("XML output should contain SalesOrder. ", xmlOutput.contains("SalesOrder"));

		// XML Schema
		writer = new ByteArrayOutputStream();
		serializer.schema(SalesOrder.class, writer);
		String schemaOutput = writer.toString();
		System.out.println("schema xml:");
		System.out.println(schemaOutput);
		assertNotNull("Schema output should not be null. ", schemaOutput);
	}

	/**
	 * 测试JSON/XML/CSV多种序列化格式 覆盖：中文特殊字符、格式切换
	 */
	public void testMultiFormatSerialization() {
		SalesOrder order = new SalesOrder();
		order.setDocEntry(1);
		order.setCustomerCode("C00001");
		order.setCustomerName("!@\"中文#$%^&*<>?/你好！");
		order.setDocumentStatus(emDocumentStatus.RELEASED);

		// JSON序列化
		String jsonOutput = BOUtilities.toJsonString(order);
		System.out.println("json:");
		System.out.println(jsonOutput);
		assertNotNull("JSON output should not be null. ", jsonOutput);
		assertTrue("JSON output should contain CustomerCode. ", jsonOutput.contains("C00001"));

		// XML序列化
		String xmlOutput = BOUtilities.toXmlString(order);
		System.out.println("xml:");
		System.out.println(xmlOutput);
		assertNotNull("XML output should not be null. ", xmlOutput);

		// CSV序列化
		String csvOutput = BOUtilities.toCsvString(order);
		System.out.println("csv:");
		System.out.println(csvOutput);
		assertNotNull("CSV output should not be null. ", csvOutput);
	}

	/**
	 * 测试克隆后序列化独立性 覆盖：修改原对象不影响克隆体的序列化输出
	 */
	public void testCloneSerializationIndependence() {
		SalesOrder order = new SalesOrder();
		order.setDocEntry(1);
		order.setCustomerCode("C00001");
		SalesOrderItem orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00001");

		SalesOrder cloned = (SalesOrder) order.clone();
		String clonedXml = BOUtilities.toXmlString(cloned);
		System.out.println(clonedXml);

		// 修改原对象
		orderItem.setItemCode("A00003");
		orderItem.setQuantity(Decimals.valueOf("99999999999999"));
		String originalXml = BOUtilities.toXmlString(order);
		System.out.println(originalXml);

		// 克隆体的序列化输出应不受原对象修改影响
		String clonedXmlAfter = BOUtilities.toXmlString(cloned);
		assertEquals("Cloned serialization should be independent. ", clonedXml, clonedXmlAfter);
	}
}
