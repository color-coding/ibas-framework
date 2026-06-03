package org.colorcoding.ibas.bobas.serialization.jersey.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

import org.colorcoding.ibas.bobas.bo.IBusinessObject;
import org.colorcoding.ibas.bobas.bo.UserFieldsFactory;
import org.colorcoding.ibas.bobas.bo.UserFieldsManager;
import org.colorcoding.ibas.bobas.common.DateTimes;
import org.colorcoding.ibas.bobas.common.Decimals;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.data.emBOStatus;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.data.emYesNo;
import org.colorcoding.ibas.bobas.serialization.ISerializer;
import org.colorcoding.ibas.bobas.serialization.SerializationFactory;
import org.colorcoding.ibas.bobas.serialization.ValidateException;
import org.colorcoding.ibas.bobas.serialization.jersey.SerializerJson;
import org.colorcoding.ibas.bobas.serialization.jersey.SerializerManager;
import org.colorcoding.ibas.bobas.test.demo.SalesOrder;
import org.colorcoding.ibas.bobas.test.demo.SalesOrderItem;

import junit.framework.TestCase;

/**
 * 业务对象JSON序列化测试
 *
 * 测试范围：
 * 1. SalesOrder → JSON序列化/反序列化（含UserFields）
 * 2. JSON → XML跨格式转换
 * 3. 反序列化状态校验（emBOStatus/emDocumentStatus/emYesNo）
 * 4. JSON Schema生成
 * 5. JSON数据校验（validate）
 */
public class TestBusinessObject extends TestCase {

	// ==================== 辅助方法 ====================

	/**
	 * 创建含UserFields的SalesOrder测试对象
	 */
	private SalesOrder createSalesOrderWithUserFields() {
		UserFieldsManager userFieldsManager = UserFieldsFactory.createManager();
		userFieldsManager.registerUserField(SalesOrder.class, "U_OrderType", String.class);
		userFieldsManager.registerUserField(SalesOrder.class, "U_OrderId", Integer.class);
		userFieldsManager.registerUserField(SalesOrder.class, "U_OrderDate", DateTime.class);
		userFieldsManager.registerUserField(SalesOrder.class, "U_OrderTotal", BigDecimal.class);

		SalesOrder order = new SalesOrder();
		order.setDocEntry(1);
		order.setCustomerCode("C00001");
		order.setDeliveryDate(DateTimes.today());
		order.setDocumentStatus(emDocumentStatus.RELEASED);
		order.setDocumentTotal(Decimals.valueOf("99.99"));

		order.getUserFields().get("U_OrderType").setValue("S0000");
		order.getUserFields().get("U_OrderId").setValue(5768);
		order.getUserFields().get("U_OrderDate").setValue(DateTimes.today());
		order.getUserFields().get("U_OrderTotal").setValue(Decimals.valueOf("999.888"));

		SalesOrderItem orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00001");
		orderItem.setQuantity(Decimals.valueOf(10));
		orderItem.setPrice(BigDecimal.valueOf(99.99));
		orderItem = order.getSalesOrderItems().create();
		orderItem.setItemCode("A00002");
		orderItem.setQuantity(Decimals.valueOf(10));
		orderItem.setPrice(Decimals.valueOf(199.99));
		return order;
	}

	// ==================== 1. JSON序列化/反序列化 ====================

	/**
	 * 测试SalesOrder的JSON序列化与反序列化
	 * 覆盖：含UserFields、含子项、格式化输出
	 */
	public void testJsonSerialization() throws IOException {
		SalesOrder order = createSalesOrderWithUserFields();

		// JSON序列化（带格式）
		ISerializer serializer = SerializationFactory.createManager().create("json");
		ByteArrayOutputStream writer = new ByteArrayOutputStream();
		serializer.serialize(order, writer, true);
		String jsonOutput = writer.toString();
		System.out.println(jsonOutput);
		assertNotNull("JSON output should not be null. ", jsonOutput);
		assertTrue("JSON should contain CustomerCode. ", jsonOutput.contains("C00001"));
		assertTrue("JSON should contain U_OrderType. ", jsonOutput.contains("S0000"));

		// JSON反序列化
		SalesOrder deserialized = serializer.deserialize(jsonOutput, order.getClass());
		assertNotNull("Deserialized order should not be null. ", deserialized);
		assertEquals("DocEntry should match. ", (int) deserialized.getDocEntry(), 1);
		assertEquals("CustomerCode should match. ", deserialized.getCustomerCode(), "C00001");
	}

	/**
	 * 测试JSON → XML跨格式转换
	 * 覆盖：先JSON序列化再反序列化后转XML
	 */
	public void testJsonToXmlCrossFormat() throws IOException {
		SalesOrder order = createSalesOrderWithUserFields();

		// JSON序列化
		ISerializer jsonSerializer = SerializationFactory.createManager().create("json");
		ByteArrayOutputStream writer = new ByteArrayOutputStream();
		jsonSerializer.serialize(order, writer, true);
		String jsonOutput = writer.toString();

		// JSON反序列化
		SalesOrder deserialized = jsonSerializer.deserialize(jsonOutput, order.getClass());

		// 转XML
		ISerializer xmlSerializer = SerializationFactory.createManager().create("xml");
		writer = new ByteArrayOutputStream();
		xmlSerializer.serialize(deserialized, writer, true);
		String xmlOutput = writer.toString();
		System.out.println(xmlOutput);
		assertNotNull("XML output should not be null. ", xmlOutput);
		assertTrue("XML should contain SalesOrder. ", xmlOutput.contains("SalesOrder"));
	}

	// ==================== 2. 反序列化状态校验 ====================

	/**
	 * 测试反序列化带状态字段的JSON
	 * 覆盖：emBOStatus.CLOSED/emDocumentStatus.CLOSED/emYesNo.YES
	 */
	public void testDeserializeStatus() throws ValidateException {
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

		SerializerJson serializer = new SerializerJson();
		serializer.setIncludeJsonRoot(true);

		// 校验
		serializer.validate(SalesOrder.class, stringBuilder.toString());

		// 反序列化
		IBusinessObject bo = serializer.deserialize(stringBuilder.toString(), SalesOrder.class);
		assertNotNull("Deserialized BO should not be null. ", bo);
		assertTrue("Deserialized BO should be SalesOrder. ", bo instanceof SalesOrder);

		// 重新序列化
		ByteArrayOutputStream writer = new ByteArrayOutputStream();
		serializer.serialize(bo, writer, true);
		String reJson = writer.toString();
		System.out.println(reJson);
		assertNotNull("Re-serialized JSON should not be null. ", reJson);
	}

	// ==================== 3. JSON Schema ====================

	/**
	 * 测试JSON Schema生成
	 * 覆盖：JSON/XML两种格式的Schema
	 */
	public void testJsonAndXmlSchema() throws ValidateException {
		SalesOrder order = createSalesOrderWithUserFields();

		// JSON Schema
		System.out.println("-------------------json--------------------");
		ISerializer serializer = SerializationFactory.createManager().create(SerializerManager.TYPE_JSON);
		ByteArrayOutputStream writer = new ByteArrayOutputStream();
		serializer.schema(order.getClass(), writer);
		String jsonSchema = writer.toString();
		System.out.println(jsonSchema);
		assertNotNull("JSON schema should not be null. ", jsonSchema);
		assertTrue("JSON schema should contain $schema. ", jsonSchema.contains("$schema"));

		// JSON序列化+反序列化
		writer = new ByteArrayOutputStream();
		serializer.serialize(order, writer);
		String jsonData = writer.toString();
		System.out.println(jsonData);
		SalesOrder deserialized = (SalesOrder) serializer.deserialize(jsonData, order.getClass());
		assertNotNull("Deserialized order should not be null. ", deserialized);

		// XML Schema
		System.out.println("-------------------xml---------------------");
		serializer = SerializationFactory.createManager().create(SerializerManager.TYPE_XML);
		writer = new ByteArrayOutputStream();
		serializer.schema(order.getClass(), writer);
		String xmlSchema = writer.toString();
		System.out.println(xmlSchema);
		assertNotNull("XML schema should not be null. ", xmlSchema);

		// XML序列化+校验+反序列化
		writer = new ByteArrayOutputStream();
		serializer.serialize(order, writer);
		String xmlData = writer.toString();
		System.out.println(xmlData);
		serializer.validate(order.getClass(), xmlData);
		deserialized = (SalesOrder) serializer.deserialize(xmlData, order.getClass());
		assertNotNull("Deserialized from XML should not be null. ", deserialized);
	}

	// ==================== 4. 数据校验（validate） ====================

	/**
	 * 测试JSON校验-合法数据
	 * 覆盖：合法的SalesOrder JSON数据通过校验
	 */
	public void testValidateValidJson() throws ValidateException {
		SerializerJson serializer = new SerializerJson();
		serializer.setIncludeJsonRoot(false);

		StringBuilder json = new StringBuilder();
		json.append("{");
		json.append("\"DocEntry\":1,");
		json.append("\"CustomerCode\":\"C00001\",");
		json.append("\"DocumentStatus\":\"RELEASED\"");
		json.append("}");

		// 合法数据应通过校验（不抛异常）
		serializer.validate(SalesOrder.class, json.toString());
	}

	/**
	 * 测试JSON校验-非法类型
	 * 覆盖：整数字段传入字符串应抛ValidateException
	 */
	public void testValidateInvalidType() {
		SerializerJson serializer = new SerializerJson();
		serializer.setIncludeJsonRoot(false);

		StringBuilder json = new StringBuilder();
		json.append("{");
		json.append("\"DocEntry\":\"not_a_number\",");
		json.append("\"CustomerCode\":\"C00001\"");
		json.append("}");

		try {
			serializer.validate(SalesOrder.class, json.toString());
			fail("Should throw ValidateException for invalid integer type.");
		} catch (ValidateException e) {
			// expected
			System.out.println("Expected validation error: " + e.getMessage());
		}
	}

	/**
	 * 测试JSON校验-非法枚举值
	 * 覆盖：枚举字段传入不存在的值应抛ValidateException
	 */
	public void testValidateInvalidEnum() {
		SerializerJson serializer = new SerializerJson();
		serializer.setIncludeJsonRoot(false);

		StringBuilder json = new StringBuilder();
		json.append("{");
		json.append("\"DocEntry\":1,");
		json.append("\"DocumentStatus\":\"INVALID_STATUS\"");
		json.append("}");

		try {
			serializer.validate(SalesOrder.class, json.toString());
			fail("Should throw ValidateException for invalid enum value.");
		} catch (ValidateException e) {
			// expected
			System.out.println("Expected validation error: " + e.getMessage());
		}
	}

	// ==================== 5. 兼容旧入口 ====================

	/**
	 * @deprecated 请使用 testJsonSerialization + testJsonToXmlCrossFormat 替代
	 */
	public void testJson() throws IOException {
		testJsonSerialization();
		testJsonToXmlCrossFormat();
	}

	/**
	 * @deprecated 请使用 testDeserializeStatus 替代
	 */
	public void testSerializStatus() throws ValidateException {
		testDeserializeStatus();
	}

	/**
	 * @deprecated 请使用 testJsonAndXmlSchema 替代
	 */
	public void testJsonSchema() throws ValidateException {
		testJsonAndXmlSchema();
	}
}
