package org.colorcoding.ibas.bobas.test.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
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
import org.colorcoding.ibas.bobas.test.bo.SalesOrder;
import org.colorcoding.ibas.bobas.test.bo.SalesOrderItem;

import junit.framework.TestCase;

public class TestCriteria extends TestCase {

	public void testToXML() throws JAXBException, IOException {
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
		System.out.println(criteria.toString("xml"));

		ICriteria nCriteria = criteria.clone();
		nCriteria.setResultCount(999);
		nCriteria.getChildCriterias().firstOrDefault().setBusinessObject(UUID.randomUUID().toString());
		nCriteria.getConditions().firstOrDefault().setValue(UUID.randomUUID().toString());
		nCriteria.getSorts().firstOrDefault().setAlias(UUID.randomUUID().toString());
		System.out.println(nCriteria.toString("xml"));
	}

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
		System.out.println("下一个结果集：\r\n" + nextCriteria.toString("xml"));
		ICriteria precriteria = criteria.previous(order);
		System.out.println("上一个结果集：\r\n" + precriteria.toString("xml"));
	}
}
