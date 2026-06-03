package org.colorcoding.ibas.bobas.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.UUID;

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
import org.colorcoding.ibas.bobas.common.SortType;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.db.DbAdapter;
import org.colorcoding.ibas.bobas.db.SqlStatement;
import org.colorcoding.ibas.bobas.serialization.ISerializer;
import org.colorcoding.ibas.bobas.serialization.SerializerXml;
import org.colorcoding.ibas.bobas.test.demo.SalesOrder;
import org.colorcoding.ibas.bobas.test.demo.SalesOrderItem;

import junit.framework.TestCase;

/**
 * 查询条件（Criteria）功能测试
 *
 * 测试范围：
 * 1. Criteria的JAXB序列化/反序列化
 * 2. Condition/Sort/ChildCriteria的结构
 * 3. 从标识字符串创建Criteria
 * 4. Criteria的克隆与copyFrom
 * 5. 分页查询（next/previous）
 * 6. SQL语句生成（DbAdapter）
 */
public class TestCriteria extends TestCase {

	/**
	 * 创建标准测试用Criteria
	 * 包含：条件（带括号）、排序、子项查询
	 */
	private ICriteria createTestCriteria() {
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

	// ==================== 1. JAXB序列化/反序列化 ====================

	/**
	 * 测试Criteria的JAXB序列化与反序列化
	 * 覆盖：Condition/Sort/Criteria的marshal和unmarshal，序列化前后一致性
	 */
	public void testXml() throws JAXBException, IOException {
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
		condition = childCriteria.getConditions().create();
		condition.setAlias(SalesOrderItem.PROPERTY_ITEMCODE.getName());
		condition.setOperation(ConditionOperation.NOT_NULL);

		// 测试Condition
		JAXBContext context = JAXBContext.newInstance(condition.getClass());
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");// //编码格式
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);//
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);//
		StringWriter writer = new StringWriter();
		marshaller.marshal(condition, writer);
		System.out.println(writer.toString());
		// 测试Sort
		context = JAXBContext.newInstance(sort.getClass());
		marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");// //编码格式
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);//
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);//
		writer = new StringWriter();
		marshaller.marshal(sort, writer);
		System.out.println(writer.toString());
		// 测试Criteria
		context = JAXBContext.newInstance(Criteria.class);
		marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");// //编码格式
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);//
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);//
		writer = new StringWriter();
		marshaller.marshal(criteria, writer);
		String oldXML = writer.toString();
		System.out.println(oldXML);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		marshaller.marshal(criteria, outputStream);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		inputStream.reset();
		criteria = (Criteria) unmarshaller.unmarshal(inputStream);
		System.out.println(criteria.toString());

		writer = new StringWriter();
		marshaller.marshal(criteria, writer);
		String newXML = writer.toString();
		System.out.println(newXML);
		assertEquals("marshal and unmarshal not equal", oldXML, newXML);

	}

	public void testFromString() {
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

		ISerializer serializer = new SerializerXml();
		ByteArrayOutputStream writer = new ByteArrayOutputStream();
		serializer.serialize(criteria, writer);
		System.out.println(writer.toString());

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

		DbAdapter adapter = new DbAdapter() {
			@Override
			public Connection createConnection(String server, String dbName, String userName, String userPwd) {
				return null;
			}
		};
		SqlStatement sqlStatement = new SqlStatement(adapter.parsingSelect(SalesOrder.class, criteria));
		System.out.println(sqlStatement.getContent());
		sqlStatement.setObject(criteria.getConditions());
		System.out.println(adapter.parsing(sqlStatement));
	}

	public void testCopyFrom() {
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
		childCriteria.setPropertyPath(SalesOrder.PROPERTY_SALESORDERITEMS.getName());
		condition = childCriteria.getConditions().create();

		condition.setAlias(SalesOrderItem.PROPERTY_ITEMCODE.getName());
		condition.setOperation(ConditionOperation.CONTAIN);
		condition.setValue("T000");

		criteria = criteria.copyFrom(criteria.clone());
		ISerializer serializer = new SerializerXml();
		ByteArrayOutputStream writer = new ByteArrayOutputStream();
		serializer.serialize(criteria, writer);
		System.out.println(writer.toString());

		ICriteria nCriteria = criteria.clone();
		nCriteria.setResultCount(999);
		nCriteria.getChildCriterias().firstOrDefault().setBusinessObject(UUID.randomUUID().toString());
		nCriteria.getConditions().firstOrDefault().setValue(UUID.randomUUID().toString());
		nCriteria.getSorts().firstOrDefault().setAlias(UUID.randomUUID().toString());
		writer = new ByteArrayOutputStream();
		serializer.serialize(nCriteria, writer);
		System.out.println(writer.toString());
	}

	/**
	 * 测试Criteria基本结构
	 * 覆盖：条件数量、排序数量、子项查询、ResultCount
	 */
	public void testCriteriaStructure() {
		ICriteria criteria = createTestCriteria();
		assertEquals("Should have 2 conditions. ", 2, criteria.getConditions().size());
		assertEquals("Should have 2 sorts. ", 2, criteria.getSorts().size());
		assertEquals("Should have 1 child criteria. ", 1, criteria.getChildCriterias().size());
		assertEquals("ResultCount should be 100. ", 100, criteria.getResultCount());
	}

	/**
	 * 测试Condition操作符
	 * 覆盖：各种ConditionOperation设置
	 */
	public void testConditionOperations() {
		ICriteria criteria = new Criteria();
		// 等于
		ICondition condition = criteria.getConditions().create();
		condition.setAlias("Field1");
		condition.setOperation(ConditionOperation.EQUAL);
		condition.setValue("test");
		assertEquals("Operation should be EQUAL. ", ConditionOperation.EQUAL, condition.getOperation());

		// 包含
		condition = criteria.getConditions().create();
		condition.setAlias("Field2");
		condition.setOperation(ConditionOperation.CONTAIN);
		condition.setValue("abc");
		assertEquals("Operation should be CONTAIN. ", ConditionOperation.CONTAIN, condition.getOperation());

		// 大于等于
		condition = criteria.getConditions().create();
		condition.setAlias("Field3");
		condition.setOperation(ConditionOperation.GRATER_EQUAL);
		condition.setValue(100);
		assertEquals("Operation should be GRATER_EQUAL. ", ConditionOperation.GRATER_EQUAL, condition.getOperation());

		// 不为空
		condition = criteria.getConditions().create();
		condition.setAlias("Field4");
		condition.setOperation(ConditionOperation.NOT_NULL);
		assertEquals("Operation should be NOT_NULL. ", ConditionOperation.NOT_NULL, condition.getOperation());

		assertEquals("Should have 4 conditions. ", 4, criteria.getConditions().size());
	}

	/**
	 * 测试条件关系
	 * 覆盖：AND/OR关系设置
	 */
	public void testConditionRelationship() {
		ICriteria criteria = new Criteria();
		ICondition cond1 = criteria.getConditions().create();
		cond1.setAlias("Field1");
		cond1.setValue("A");
		assertEquals("Default relationship should be AND. ", ConditionRelationship.AND, cond1.getRelationship());

		ICondition cond2 = criteria.getConditions().create();
		cond2.setAlias("Field2");
		cond2.setValue("B");
		cond2.setRelationship(ConditionRelationship.OR);
		assertEquals("Relationship should be OR. ", ConditionRelationship.OR, cond2.getRelationship());
	}

	/**
	 * 测试子项查询条件
	 * 覆盖：PropertyPath、NoChilds、IncludingOtherChilds、OnlyHasChilds
	 */
	public void testChildCriteriaProperties() {
		ICriteria criteria = new Criteria();
		IChildCriteria childCriteria = criteria.getChildCriterias().create();
		childCriteria.setPropertyPath(SalesOrder.PROPERTY_SALESORDERITEMS.getName());
		childCriteria.setNoChilds(false);
		childCriteria.setIncludingOtherChilds(false);
		childCriteria.setOnlyHasChilds(false);

		assertEquals("PropertyPath should match. ", SalesOrder.PROPERTY_SALESORDERITEMS.getName(),
				childCriteria.getPropertyPath());
		assertFalse("NoChilds should be false. ", childCriteria.isNoChilds());
		assertFalse("IncludingOtherChilds should be false. ", childCriteria.isIncludingOtherChilds());
		assertFalse("OnlyHasChilds should be false. ", childCriteria.isOnlyHasChilds());
	}

	/**
	 * 测试分页查询（next/previous）
	 * 覆盖：基于已有结果构造下一页/上一页条件
	 */
	public void testResultCriteria() {
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

		SalesOrderItem order = new SalesOrderItem();
		order.setDocEntry(199);
		order.setLineId(78);
		order.markOld();
		ICriteria nextCriteria = criteria.next(order);
		ISerializer serializer = new SerializerXml();
		ByteArrayOutputStream writer = new ByteArrayOutputStream();
		serializer.serialize(nextCriteria, writer);
		System.out.println("下一个结果集：");
		System.out.println(writer.toString());
		ICriteria precriteria = criteria.previous(order);
		writer = new ByteArrayOutputStream();
		serializer.serialize(precriteria, writer);
		System.out.println("上一个结果集：");
		System.out.println(writer.toString());
	}
}
