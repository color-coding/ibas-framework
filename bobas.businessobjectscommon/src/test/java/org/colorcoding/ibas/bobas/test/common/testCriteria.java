package org.colorcoding.ibas.bobas.test.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import org.colorcoding.ibas.bobas.common.SortType;
import org.colorcoding.ibas.bobas.data.emDocumentStatus;
import org.colorcoding.ibas.bobas.test.bo.SalesOrder;
import org.colorcoding.ibas.bobas.test.bo.SalesOrderItem;

import junit.framework.TestCase;

public class testCriteria extends TestCase {

	public void testToXML() throws JAXBException, IOException {
		ICriteria criteria = new Criteria();
		criteria.setResultCount(100);
		// ("DocStatus" = 'P' OR "DocStatus" = 'F')
		ICondition condition = criteria.getConditions().create();
		condition.setBracketOpenNum(1);
		condition.setAlias(SalesOrder.DocumentStatusProperty.getName());
		condition.setCondVal(emDocumentStatus.Planned);
		condition = criteria.getConditions().create();
		condition.setBracketCloseNum(1);
		condition.setAlias(SalesOrder.DocumentStatusProperty.getName());
		condition.setCondVal(emDocumentStatus.Released);
		condition.setRelationship(ConditionRelationship.cr_OR);
		// ORDER BY "DocEntry" DESC, "CardCode" ASC
		ISort sort = criteria.getSorts().create();
		sort.setAlias(SalesOrder.DocEntryProperty.getName());
		sort.setSortType(SortType.st_Descending);
		sort = criteria.getSorts().create();
		sort.setAlias(SalesOrder.CustomerCodeProperty.getName());
		sort.setSortType(SortType.st_Ascending);
		// 子项查询
		IChildCriteria childCriteria = criteria.getChildCriterias().create();
		condition = childCriteria.getConditions().create();
		condition.setAlias(SalesOrderItem.ItemCodeProperty.getName());
		condition.setOperation(ConditionOperation.co_CONTAIN);
		condition.setCondVal("T000");

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

	public void testToJSON() throws JAXBException {
		ICriteria criteria = new Criteria();
		criteria.setResultCount(100);
		// ("DocStatus" = 'P' OR "DocStatus" = 'F')
		ICondition condition = criteria.getConditions().create();
		condition.setBracketOpenNum(1);
		condition.setAlias(SalesOrder.DocumentStatusProperty.getName());
		condition.setCondVal(emDocumentStatus.Planned);
		condition = criteria.getConditions().create();
		condition.setBracketCloseNum(1);
		condition.setAlias(SalesOrder.DocumentStatusProperty.getName());
		condition.setCondVal(emDocumentStatus.Released);
		condition.setRelationship(ConditionRelationship.cr_OR);
		// ORDER BY "DocEntry" DESC, "CardCode" ASC
		ISort sort = criteria.getSorts().create();
		sort.setAlias(SalesOrder.DocEntryProperty.getName());
		sort.setSortType(SortType.st_Descending);
		sort = criteria.getSorts().create();
		sort.setAlias(SalesOrder.CustomerCodeProperty.getName());
		sort.setSortType(SortType.st_Ascending);
		// 子项查询
		IChildCriteria childCriteria = criteria.getChildCriterias().create();
		condition = childCriteria.getConditions().create();
		condition.setAlias(SalesOrderItem.ItemCodeProperty.getName());
		condition.setOperation(ConditionOperation.co_CONTAIN);
		condition.setCondVal("T000");

		// 设置系统默认工厂
		System.setProperty("javax.xml.bind.context.factory","org.eclipse.persistence.jaxb.JAXBContextFactory");
		Map<String, Object> properties = new HashMap<String, Object>(2);
		properties.put("eclipselink.media-type", "application/json");
		// json数组不要前缀类型
		properties.put("eclipselink.json.wrapper-as-array-name", true);
		//properties.put("eclipselink.json.include-root", true);
		//properties.put("eclipselink.json.attribute-prefix", "@");
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

		assertEquals("marshal and unmarshal not equal", oldJSON.replace("\r\n", "").replace(" ", ""), criteria.toString("json"));
	}
}
