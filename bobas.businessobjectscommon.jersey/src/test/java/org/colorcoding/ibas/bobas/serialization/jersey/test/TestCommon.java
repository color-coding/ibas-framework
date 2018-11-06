package org.colorcoding.ibas.bobas.serialization.jersey.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.colorcoding.ibas.bobas.common.ConditionOperation;
import org.colorcoding.ibas.bobas.common.ConditionRelationship;
import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.IChildCriteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.ICriteria;
import org.colorcoding.ibas.bobas.common.ISort;
import org.colorcoding.ibas.bobas.common.OperationResult;
import org.colorcoding.ibas.bobas.common.SortType;
import org.colorcoding.ibas.bobas.data.DataTable;
import org.colorcoding.ibas.bobas.data.IDataTableColumn;
import org.colorcoding.ibas.bobas.data.IDataTableRow;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.serialization.ISerializer;
import org.colorcoding.ibas.bobas.serialization.SerializerFactory;
import org.colorcoding.ibas.bobas.serialization.ValidateException;
import org.colorcoding.ibas.bobas.serialization.jersey.SerializerJsonNoRoot;
import org.colorcoding.ibas.bobas.serialization.jersey.SerializerManager;
import org.colorcoding.ibas.bobas.test.bo.SalesOrder;
import org.colorcoding.ibas.bobas.test.bo.SalesOrderItem;

import junit.framework.TestCase;

public class TestCommon extends TestCase {
	private ICriteria createCriteria() {
		ICriteria criteria = new Criteria();
		criteria.setResultCount(100);
		// ("DocStatus" = 'P' OR "DocStatus" = 'F')
		ICondition condition = criteria.getConditions().create();
		condition.setBracketOpen(1);
		condition.setAlias(SalesOrder.PROPERTY_DOCUMENTSTATUS.getName());
		condition.setValue(emDocumentStatus.PLANNED);
		condition = criteria.getConditions().create();
		condition.setBracketClose(1);
		condition.setAlias(SalesOrder.PROPERTY_DOCUMENTSTATUS.getName());
		condition.setValue(emDocumentStatus.RELEASED);
		condition.setRelationship(ConditionRelationship.OR);
		// ORDER BY "DocEntry" DESC, "CardCode" ASC
		ISort sort = criteria.getSorts().create();
		sort.setAlias(SalesOrder.PROPERTY_DOCENTRY.getName());
		sort.setSortType(SortType.DESCENDING);
		sort = criteria.getSorts().create();
		sort.setAlias(SalesOrder.PROPERTY_CUSTOMERCODE.getName());
		sort.setSortType(SortType.ASCENDING);
		// 子项查询
		IChildCriteria childCriteria = criteria.getChildCriterias().create();
		condition = childCriteria.getConditions().create();
		condition.setAlias(SalesOrderItem.PROPERTY_ITEMCODE.getName());
		condition.setOperation(ConditionOperation.CONTAIN);
		condition.setValue("T000");
		return criteria;
	}

	public void testFromString() {
		ICriteria criteria = this.createCriteria();
		ICriteria jsonCriteria = Criteria.create(criteria.toString("json"));
		assertEquals("json marshal and unmarshal not equal", criteria.toString("json"), jsonCriteria.toString("json"));

		ICriteria xmlCriteria = Criteria.create(criteria.toString("xml"));
		assertEquals("xml marshal and unmarshal not equal", criteria.toString("xml"), xmlCriteria.toString("xml"));

		String identifiers1 = "{[CC_TT_SALESORDER].[DocEntry = 1]}";
		criteria = Criteria.create(identifiers1);
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

	public void testJsonSchema() throws ValidateException {
		ICriteria criteria = this.createCriteria();
		System.out.println("-------------------has root--------------------");
		ISerializer<?> serializer = SerializerFactory.create().createManager()
				.create(SerializerManager.TYPE_JSON_HAS_ROOT);
		ByteArrayOutputStream writer = new ByteArrayOutputStream();
		serializer.serialize(criteria, writer);
		System.out.println(writer.toString());
		System.out.println(serializer.deserialize(writer.toString(), criteria.getClass()));
		System.out.println("-------------------no root---------------------");
		serializer = SerializerFactory.create().createManager().create(SerializerManager.TYPE_JSON_NO_ROOT);
		writer = new ByteArrayOutputStream();
		serializer.serialize(criteria, writer);
		System.out.println(writer.toString());
		System.out.println(serializer.deserialize(writer.toString(), criteria.getClass()));
	}

	public void testToJSON() throws JAXBException {
		ICriteria criteria = new Criteria();
		criteria.setResultCount(100);
		// ("DocStatus" = 'P' OR "DocStatus" = 'F')
		ICondition condition = criteria.getConditions().create();
		condition.setBracketOpen(1);
		condition.setAlias(SalesOrder.PROPERTY_DOCUMENTSTATUS.getName());
		condition.setValue(emDocumentStatus.PLANNED);
		condition = criteria.getConditions().create();
		condition.setBracketClose(1);
		condition.setAlias(SalesOrder.PROPERTY_DOCUMENTSTATUS.getName());
		condition.setValue(emDocumentStatus.RELEASED);
		condition.setRelationship(ConditionRelationship.OR);
		// ORDER BY "DocEntry" DESC, "CardCode" ASC
		ISort sort = criteria.getSorts().create();
		sort.setAlias(SalesOrder.PROPERTY_DOCENTRY.getName());
		sort.setSortType(SortType.DESCENDING);
		sort = criteria.getSorts().create();
		sort.setAlias(SalesOrder.PROPERTY_CUSTOMERCODE.getName());
		sort.setSortType(SortType.ASCENDING);
		// 子项查询
		IChildCriteria childCriteria = criteria.getChildCriterias().create();
		condition = childCriteria.getConditions().create();
		condition.setAlias(SalesOrderItem.PROPERTY_ITEMCODE.getName());
		condition.setOperation(ConditionOperation.CONTAIN);
		condition.setValue("T000");

		// 设置系统默认工厂
		System.setProperty("javax.xml.bind.context.factory", "org.eclipse.persistence.jaxb.JAXBContextFactory");
		Map<String, Object> properties = new HashMap<String, Object>(2);
		properties.put("eclipselink.media-type", "application/json");
		// json数组不要前缀类型
		properties.put("eclipselink.json.wrapper-as-array-name", true);
		// properties.put("eclipselink.json.include-root", true);
		// properties.put("eclipselink.json.attribute-prefix", "@");
		JAXBContext jc = JAXBContext.newInstance(new Class[] { Criteria.class }, properties);

		StringWriter writer = new StringWriter();
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(criteria, writer);
		String oldJSON = writer.toString();
		System.out.println(oldJSON);

		Unmarshaller unmarshaller = jc.createUnmarshaller();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(oldJSON.getBytes());
		inputStream.reset();
		criteria = (Criteria) unmarshaller.unmarshal(inputStream);

		assertEquals("marshal and unmarshal not equal",
				oldJSON.replace(System.getProperty("line.separator"), "").replace(" ", ""),
				criteria.toString("json").replace(System.getProperty("line.separator"), "").replace(" ", ""));

	}

	public void testOperationRuslut() {
		OperationResult<DataTable> operationResult = new OperationResult<>();
		DataTable dataTable = new DataTable();
		IDataTableColumn column = dataTable.getColumns().create();
		column.setName("Test");
		column.setDataType(String.class);
		IDataTableRow row = dataTable.getRows().create();
		row.setValue(column, "你好，世界！");
		operationResult.getResultObjects().add(dataTable);

		ByteArrayOutputStream writer = new ByteArrayOutputStream();
		ISerializer<?> serializer = new SerializerJsonNoRoot();
		serializer.serialize(operationResult, writer, OperationResult.class, DataTable.class);
		System.out.println(writer.toString());
		serializer = new SerializerJsonNoRoot();
		Object data = serializer.deserialize(writer.toString(), OperationResult.class, DataTable.class);
		System.out.println(data);
	}
}
