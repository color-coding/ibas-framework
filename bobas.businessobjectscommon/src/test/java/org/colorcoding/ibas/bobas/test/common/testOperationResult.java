package org.colorcoding.ibas.bobas.test.common;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.colorcoding.ibas.bobas.common.Criteria;
import org.colorcoding.ibas.bobas.common.ICondition;
import org.colorcoding.ibas.bobas.common.OperationInformation;
import org.colorcoding.ibas.bobas.common.OperationResult;

import junit.framework.TestCase;

public class testOperationResult extends TestCase {

	public void testToXML() throws JAXBException {
		OperationResult<?> operationResult = new OperationResult<Object>();
		operationResult.setResultCode(0);
		operationResult.addInformations(new OperationInformation("信息"));
		Criteria criteria = new Criteria();
		ICondition condition = criteria.getConditions().create();
		condition.setAlias("ItemCode");
		condition.setCondVal("A00001");
		operationResult.addResultObjects(criteria);
		operationResult.addResultObjects(criteria.clone());

		JAXBContext context = JAXBContext.newInstance(operationResult.getClass(), Criteria.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");// //编码格式
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);// 是否格式化生成的xml串
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);// 是否省略xm头声明信息
		StringWriter writer = new StringWriter();
		marshaller.marshal(operationResult, writer);
		System.out.println(writer.toString());
	}

}
