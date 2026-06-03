package org.colorcoding.ibas.bobas.serialization.jersey.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Random;

import org.colorcoding.ibas.bobas.common.Bytes;
import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.Decimals;
import org.colorcoding.ibas.bobas.common.IChildCriteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.ISort;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.common.SortType;
import org.colorcoding.ibas.bobas.common.Strings;
import org.colorcoding.ibas.bobas.data.DataTable;
import org.colorcoding.ibas.bobas.data.IDataTableColumn;
import org.colorcoding.ibas.bobas.data.IDataTableRow;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.i18n.I18N;
import org.colorcoding.ibas.bobas.serialization.ISerializer;
import org.colorcoding.ibas.bobas.serialization.SerializationFactory;
import org.colorcoding.ibas.bobas.serialization.SerializerXml;
import org.colorcoding.ibas.bobas.serialization.ValidateException;
import org.colorcoding.ibas.bobas.serialization.jersey.SerializerJson;
import org.colorcoding.ibas.bobas.serialization.jersey.SerializerManager;
import org.colorcoding.ibas.bobas.test.demo.SalesOrder;
import org.colorcoding.ibas.bobas.test.demo.SalesOrderItem;

import junit.framework.TestCase;

/**
 * Jersey序列化通用测试
 *
 * 测试范围：
 * 1. Criteria的标识字符串创建
 * 2. Criteria的JSON序列化/反序列化
 * 3. Criteria的JSON Schema生成
 * 4. OperationResult的JSON序列化（含DataTable）
 * 5. includeJsonRoot开关对比
 * 6. DataTable多格式输出（CSV/JSON/XML）
 */
public class TestCommon extends TestCase {

	// ==================== 辅助方法 ====================

	/**
	 * 创建标准测试用Criteria
	 */
	private ICriteria createCriteria() {
		ICriteria criteria = new Criteria();
		criteria.setResultCount(100);
		ICondition condition = criteria.getConditions().create();
		condition.setBracketOpen(1);
		condition.setAlias(SalesOrder.PROPERTY_DOCUMENTSTATUS.getName());
		condition.setValue(emDocumentStatus.PLANNED);
		condition = criteria.getConditions().create();
		condition.setBracketClose(1);
		condition.setAlias(SalesOrder.PROPERTY_DOCUMENTSTATUS.getName());
		condition.setValue(emDocumentStatus.RELEASED);
		condition.setRelationship(ConditionRelationship.OR);
		ISort sort = criteria.getSorts().create();
		sort.setAlias(SalesOrder.PROPERTY_DOCENTRY.getName());
		sort.setSortType(SortType.DESCENDING);
		sort = criteria.getSorts().create();
		sort.setAlias(SalesOrder.PROPERTY_CUSTOMERCODE.getName());
		sort.setSortType(SortType.ASCENDING);
		IChildCriteria childCriteria = criteria.getChildCriterias().create();
		condition = childCriteria.getConditions().create();
		condition.setAlias(SalesOrderItem.PROPERTY_ITEMCODE.getName());
		condition.setOperation(ConditionOperation.CONTAIN);
		condition.setValue("T000");
		return criteria;
	}

	// ==================== 1. 标识字符串创建 ====================

	/**
	 * 测试从标识字符串创建Criteria
	 * 覆盖：单条件、多条件、BusinessObject名称
	 */
	public void testFromString() {
		String identifiers1 = "{[CC_TT_SALESORDER].[DocEntry = 1]}";
		ICriteria criteria = Criteria.create(identifiers1);
		assertEquals("from identifiers faild.", 1, criteria.getConditions().size());
		assertEquals("from identifiers faild.", "CC_TT_SALESORDER", criteria.getBusinessObject());
		assertEquals("from identifiers faild.", "DocEntry", criteria.getConditions().get(0).getAlias());
		assertEquals("from identifiers faild.", "1", criteria.getConditions().get(0).getValue());

		String identifiers2 = "{[CC_TT_SALESORDER].[DocEntry = 1]&[LineId = 2]}";
		criteria = Criteria.create(identifiers2);
		assertEquals("from identifiers faild.", 2, criteria.getConditions().size());
		assertEquals("from identifiers faild.", "CC_TT_SALESORDER", criteria.getBusinessObject());
		assertEquals("from identifiers faild.", "DocEntry", criteria.getConditions().get(0).getAlias());
		assertEquals("from identifiers faild.", "1", criteria.getConditions().get(0).getValue());
		assertEquals("from identifiers faild.", "LineId", criteria.getConditions().get(1).getAlias());
		assertEquals("from identifiers faild.", "2", criteria.getConditions().get(1).getValue());
	}

	// ==================== 2. Criteria JSON序列化 ====================

	/**
	 * 测试Criteria的JSON序列化与反序列化
	 * 覆盖：无根节点序列化、Schema生成
	 */
	public void testCriteriaJsonSchema() throws ValidateException {
		ICriteria criteria = createCriteria();

		ISerializer serializer = SerializationFactory.createManager().create(SerializerManager.TYPE_JSON);

		// 序列化
		System.out.println("-------------------no root---------------------");
		ByteArrayOutputStream writer = new ByteArrayOutputStream();
		serializer.serialize(criteria, writer);
		String jsonOutput = writer.toString();
		System.out.println(jsonOutput);
		assertNotNull("JSON output should not be null. ", jsonOutput);
		assertTrue("JSON should contain DocumentStatus. ", jsonOutput.contains("DocumentStatus"));

		// 反序列化
		ICriteria deserialized = (ICriteria) serializer.deserialize(jsonOutput, criteria.getClass());
		System.out.println(deserialized);
		assertNotNull("Deserialized criteria should not be null. ", deserialized);

		// Schema
		System.out.println("-------------------schema---------------------");
		writer = new ByteArrayOutputStream();
		serializer.schema(Criteria.class, writer);
		String schema = writer.toString();
		System.out.println(schema);
		assertNotNull("Schema should not be null. ", schema);
		assertTrue("Schema should contain $schema. ", schema.contains("$schema"));
	}

	/**
	 * 测试Criteria的JSON序列化/反序列化一致性
	 * 覆盖：含子项查询、排序、括号条件
	 */
	public void testCriteriaJsonRoundTrip() throws IOException {
		ICriteria criteria = new Criteria();
		criteria.setResultCount(100);
		ICondition condition = criteria.getConditions().create();
		condition.setBracketOpen(1);
		condition.setAlias(SalesOrder.PROPERTY_DOCUMENTSTATUS.getName());
		condition.setValue(emDocumentStatus.PLANNED);
		condition = criteria.getConditions().create();
		condition.setBracketClose(1);
		condition.setAlias(SalesOrder.PROPERTY_DOCUMENTSTATUS.getName());
		condition.setValue(emDocumentStatus.RELEASED);
		condition.setRelationship(ConditionRelationship.OR);
		ISort sort = criteria.getSorts().create();
		sort.setAlias(SalesOrder.PROPERTY_DOCENTRY.getName());
		sort.setSortType(SortType.DESCENDING);
		sort = criteria.getSorts().create();
		sort.setAlias(SalesOrder.PROPERTY_CUSTOMERCODE.getName());
		sort.setSortType(SortType.ASCENDING);
		IChildCriteria childCriteria = criteria.getChildCriterias().create();
		condition = childCriteria.getConditions().create();
		condition.setAlias(SalesOrderItem.PROPERTY_ITEMCODE.getName());
		condition.setOperation(ConditionOperation.CONTAIN);
		condition.setValue("T000");

		SerializerJson serializer = new SerializerJson();

		// 序列化
		ByteArrayOutputStream writer = new ByteArrayOutputStream();
		serializer.serialize(criteria, writer);
		String oldJSON = writer.toString();
		System.out.println(oldJSON);

		// 反序列化
		serializer = new SerializerJson();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(Bytes.valueOf(oldJSON));
		inputStream.reset();
		criteria = serializer.deserialize(inputStream, Criteria.class);

		// 再次序列化比较（忽略空白差异）
		String newJSON = Strings.toJsonString(criteria);
		System.out.println(newJSON);

		// 核心字段验证
		assertNotNull("Deserialized criteria should not be null. ", criteria);
		assertEquals("ResultCount should be 100. ", 100, criteria.getResultCount());
		assertEquals("Should have 2 conditions. ", 2, criteria.getConditions().size());
		assertEquals("Should have 2 sorts. ", 2, criteria.getSorts().size());
		assertEquals("Should have 1 child criteria. ", 1, criteria.getChildCriterias().size());
	}

	// ==================== 3. OperationResult序列化 ====================

	/**
	 * 创建测试用DataTable
	 */
	private DataTable createTestDataTable() {
		DataTable dataTable = new DataTable();
		IDataTableColumn column = dataTable.getColumns().create();
		column.setName("Name");
		column.setDataType(String.class);
		column = dataTable.getColumns().create();
		column.setName("Value");
		column.setDataType(BigDecimal.class);
		IDataTableRow row = dataTable.getRows().create();
		row.setValue(0, "你好，世界！");
		row.setValue(1, Decimals.VALUE_LONG_MIN_VALUE);
		return dataTable;
	}

	/**
	 * 测试OperationResult的JSON序列化（includeJsonRoot=true）
	 * 覆盖：含根节点、含DataTable、反序列化
	 */
	public void testOperationResultWithRoot() throws IOException {
		DataTable dataTable = createTestDataTable();
		OperationResult<DataTable> operationResult = new OperationResult<>();
		operationResult.setResultCode(new Random().nextInt());
		operationResult.setMessage(I18N.prop("msg_bobas_unknown_exception"));
		operationResult.getResultObjects().add(dataTable);

		try (ByteArrayOutputStream writer = new ByteArrayOutputStream()) {
			SerializerJson serializer = new SerializerJson();
			serializer.setIncludeJsonRoot(true);
			serializer.serialize(operationResult, writer, OperationResult.class, DataTable.class);
			String jsonOutput = writer.toString();
			System.out.println(jsonOutput);
			assertNotNull("JSON output should not be null. ", jsonOutput);

			// 反序列化
			operationResult = serializer.deserialize(jsonOutput, OperationResult.class, DataTable.class);
			assertNotNull("Deserialized result should not be null. ", operationResult);
			System.out.println(operationResult);
		}
	}

	/**
	 * 测试OperationResult的JSON序列化（includeJsonRoot=false）
	 * 覆盖：无根节点、含DataTable、反序列化
	 */
	public void testOperationResultWithoutRoot() throws IOException {
		DataTable dataTable = createTestDataTable();
		OperationResult<DataTable> operationResult = new OperationResult<>();
		operationResult.setResultCode(new Random().nextInt());
		operationResult.setMessage(I18N.prop("msg_bobas_unknown_exception"));
		operationResult.getResultObjects().add(dataTable);

		try (ByteArrayOutputStream writer = new ByteArrayOutputStream()) {
			SerializerJson serializer = new SerializerJson();
			serializer.setIncludeJsonRoot(false);
			serializer.serialize(operationResult, writer, OperationResult.class, DataTable.class);
			String jsonOutput = writer.toString();
			System.out.println(jsonOutput);
			assertNotNull("JSON output should not be null. ", jsonOutput);

			// 反序列化
			operationResult = serializer.deserialize(jsonOutput, OperationResult.class, DataTable.class);
			assertNotNull("Deserialized result should not be null. ", operationResult);
			System.out.println(operationResult);
		}
	}

	/**
	 * 测试OperationResult的XML序列化
	 * 覆盖：XML格式、含DataTable、反序列化
	 */
	public void testOperationResultXml() throws IOException {
		DataTable dataTable = createTestDataTable();
		OperationResult<DataTable> operationResult = new OperationResult<>();
		operationResult.setResultCode(0);
		operationResult.setMessage("test message");
		operationResult.getResultObjects().add(dataTable);

		try (ByteArrayOutputStream writer = new ByteArrayOutputStream()) {
			SerializerXml serializer = new SerializerXml();
			serializer.serialize(operationResult, writer, OperationResult.class, DataTable.class);
			String xmlOutput = writer.toString();
			System.out.println(xmlOutput);
			assertNotNull("XML output should not be null. ", xmlOutput);
			assertTrue("XML should contain OperationResult. ", xmlOutput.contains("OperationResult"));

			// 反序列化
			operationResult = serializer.deserialize(xmlOutput, OperationResult.class, DataTable.class);
			assertNotNull("Deserialized result should not be null. ", operationResult);
			assertEquals("ResultCode should be 0. ", 0, operationResult.getResultCode());
			System.out.println(operationResult);
		}
	}

	// ==================== 4. DataTable多格式输出 ====================

	/**
	 * 测试DataTable的多格式输出
	 * 覆盖：CSV/JSON/XML格式
	 */
	public void testDataTableFormats() {
		DataTable dataTable = createTestDataTable();

		String csv = dataTable.toString("csv");
		System.out.println(csv);
		assertNotNull("CSV output should not be null. ", csv);

		String json = dataTable.toString("json");
		System.out.println(json);
		assertNotNull("JSON output should not be null. ", json);

		String xml = dataTable.toString("xml");
		System.out.println(xml);
		assertNotNull("XML output should not be null. ", xml);
	}

	// ==================== 5. includeJsonRoot开关对比 ====================

	/**
	 * 测试includeJsonRoot开关
	 * 覆盖：true时包含根节点名称，false时不包含
	 */
	public void testIncludeJsonRootToggle() throws IOException {
		ICriteria criteria = createCriteria();

		// includeJsonRoot = false (默认)
		SerializerJson serializerNoRoot = new SerializerJson();
		serializerNoRoot.setIncludeJsonRoot(false);
		ByteArrayOutputStream writer = new ByteArrayOutputStream();
		serializerNoRoot.serialize(criteria, writer);
		String jsonNoRoot = writer.toString();

		// includeJsonRoot = true
		SerializerJson serializerWithRoot = new SerializerJson();
		serializerWithRoot.setIncludeJsonRoot(true);
		writer = new ByteArrayOutputStream();
		serializerWithRoot.serialize(criteria, writer);
		String jsonWithRoot = writer.toString();

		System.out.println("No root: " + jsonNoRoot);
		System.out.println("With root: " + jsonWithRoot);

		// 两种模式输出应该不同
		assertFalse("includeJsonRoot toggle should produce different output. ",
				jsonNoRoot.equals(jsonWithRoot));
	}

	// ==================== 兼容旧入口 ====================

	/**
	 * @deprecated 请使用 testCriteriaJsonRoundTrip 替代
	 */
	public void testJson() throws IOException {
		testCriteriaJsonRoundTrip();
	}

	/**
	 * @deprecated 请使用 testCriteriaJsonSchema 替代
	 */
	public void testJsonSchema() throws ValidateException {
		testCriteriaJsonSchema();
	}

	/**
	 * @deprecated 请使用 testOperationResultWithRoot + testOperationResultWithoutRoot + testOperationResultXml 替代
	 */
	public void testOperationRuslut() throws IOException {
		// 保留旧入口，调用新方法
		DataTable dataTable = createTestDataTable();
		System.out.println(dataTable.toString("csv"));
		System.out.println(dataTable.toString("json"));
		System.out.println(dataTable.toString("xml"));

		OperationResult<DataTable> operationResult = new OperationResult<>();
		operationResult.setResultCode(new Random().nextInt());
		operationResult.setMessage(I18N.prop("msg_bobas_unknown_exception"));
		operationResult.getResultObjects().add(dataTable);

		try (ByteArrayOutputStream writer = new ByteArrayOutputStream()) {
			SerializerJson serializer = new SerializerJson();
			serializer.setIncludeJsonRoot(true);
			serializer.serialize(operationResult, writer, OperationResult.class, DataTable.class);
			System.out.println(writer.toString());
			operationResult = serializer.deserialize(writer.toString(), OperationResult.class, DataTable.class);
			System.out.println(operationResult);
		}

		try (ByteArrayOutputStream writer = new ByteArrayOutputStream()) {
			SerializerJson serializer = new SerializerJson();
			serializer.setIncludeJsonRoot(false);
			serializer.serialize(operationResult, writer, OperationResult.class, DataTable.class);
			System.out.println(writer.toString());
			operationResult = serializer.deserialize(writer.toString(), OperationResult.class, DataTable.class);
			System.out.println(operationResult);
		}

		try (ByteArrayOutputStream writer = new ByteArrayOutputStream()) {
			SerializerXml serializer = new SerializerXml();
			serializer.serialize(operationResult, writer, OperationResult.class, DataTable.class);
			System.out.println(writer.toString());
			operationResult = serializer.deserialize(writer.toString(), OperationResult.class, DataTable.class);
			System.out.println(operationResult);
		}
	}
}
